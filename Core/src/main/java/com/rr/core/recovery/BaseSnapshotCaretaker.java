package com.rr.core.recovery;

import com.rr.core.admin.AdminAgent;
import com.rr.core.admin.AdminCommand;
import com.rr.core.annotations.OptionalReference;
import com.rr.core.component.*;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ZConsumer2Args;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.recovery.impl.*;
import com.rr.core.thread.RunState;
import com.rr.core.utils.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * persistTime eg   23:59:59.999
 */
public class BaseSnapshotCaretaker implements SnapshotCaretaker {

    private static final Logger _log     = LoggerFactory.create( BaseSnapshotCaretaker.class );
    private static final Logger _console = ConsoleFactory.console( BaseSnapshotCaretaker.class );

    private static final ErrorCode ERR_TAKE_SNAP = new ErrorCode( "DSC100", "Error taking snapshot" );
    private static final ErrorCode ERR_RESTORE   = new ErrorCode( "DSC200", "Error restoring snapshot" );
    private static final ErrorCode ERR_FILTER    = new ErrorCode( "DSC300", "Error in filter list" );
    private final     String                  _persistRoot;
    private final     String                  _exportRoot;
    private final     String[]                _importRoots;
    private final     SnapshotWriter          _snapshotWriter;
    private final     SnapshotReader          _snapshotReader;
    private final     ComponentExportWriter   _exportWriter;
    private final     ComponentImportReader[] _importReaders;
    private final     SnapshotDefinitionImpl  _snapDefDaily    = new SnapshotDefinitionImpl( SnapshotType.daily, new LinkedHashSet<>() );
    private final     SnapshotDefinitionImpl  _snapDefFinal    = new SnapshotDefinitionImpl( SnapshotType.intraday, new LinkedHashSet<>() );
    private final     SnapshotDefinitionImpl  _snapDefExport   = new SnapshotDefinitionImpl( SnapshotType.exportBT, new LinkedHashSet<>() );
    private final     SnapshotDefinitionImpl  _snapDefImport   = new SnapshotDefinitionImpl( SnapshotType.importBT, new LinkedHashSet<>() );
    @OptionalReference
    private List<SMTComponent> _snapshotMembers;
    private Set<SMTSnapshotMember> _extraMembers = new LinkedHashSet<>();
    private           String                  _id;
    private           SMTCoreContext          _ctx;
    private           boolean                 _forceSingleFile = false;
    private           int                     _maxAgeDays;
    private transient RunState                _runState        = RunState.Created;
    private           boolean                 _disabledSnapshot;

    public BaseSnapshotCaretaker( String id, String persistRoot, String exportRoot, String importRoots, int maxDaysOld ) throws FileException {
        this( id, persistRoot, exportRoot, importRoots, maxDaysOld, null );
    }

    public BaseSnapshotCaretaker( String id, String persistRoot, String exportRoot, String importRoots, int maxDaysOld, List<SMTComponent> snapshotMembers ) throws FileException {
        _id              = id;
        _persistRoot     = persistRoot;
        _snapshotMembers = snapshotMembers;
        _maxAgeDays      = maxDaysOld;
        _exportRoot      = exportRoot;
        _snapshotWriter  = (_forceSingleFile) ? new SnapshotSingleFileWriterImpl( id + "Writer", persistRoot ) : new SnapshotMultiFileWriterImpl( id + "Writer", persistRoot );
        _snapshotReader  = (_forceSingleFile) ? new SnapshotSingleFileReaderImpl( id + "Reader", persistRoot ) : new SnapshotMultiFileReaderImpl( id + "Reader", persistRoot );

        if ( exportRoot == null || exportRoot.length() == 0 ) throw new SMTRuntimeException( getComponentId() + " missing exportRoot" );
        if ( importRoots == null || importRoots.length() == 0 ) throw new SMTRuntimeException( getComponentId() + " missing importRoots" );

        _exportWriter = new ComponentMFileExportWriter( id + "ExportWriter", exportRoot );

        setFilters( _exportWriter );

        _importRoots = importRoots.split( "," );

        _importReaders = new ComponentMFileImportReader[ _importRoots.length ];

        for ( int i = 0; i < _importRoots.length; i++ ) {
            _importReaders[ i ] = new ComponentMFileImportReader( id + "ImportReader" + i, _importRoots[ i ] );
        }

        AdminCommand bean = new SnapshotCaretakerAdmin( this );
        AdminAgent.register( bean );
    }

    @Override public String getComponentId() { return _id; }

    @Override public RunState getRunState() { return _runState; }

    @Override public final synchronized RunState setRunState( RunState newState ) {
        RunState old = _runState;

        if ( old != newState ) {
            _log.info( getComponentId() + " change state from " + old + " to " + newState );
            _runState = newState;
        }

        return old;
    }

    @Override public void init( final SMTStartContext ctx, final CreationPhase creationPhase ) {

        if ( _runState.shouldInitialise() ) {
            _ctx = SMTCoreContext.class.cast( ctx );

            setTimer();

            _snapshotReader.init( ctx, creationPhase );
            _snapshotWriter.init( ctx, creationPhase );

            _exportWriter.init( ctx, creationPhase );

            for ( int i = 0; i < _importReaders.length; i++ ) {
                _importReaders[ i ].init( ctx, creationPhase );
            }

            setRunState( RunState.Initialised );
        }
    }

    @Override public void preStop() {
        final int exitCode = Utils.getExitCode();

        if ( exitCode != 0 ) {

            _console.info( getComponentId() + " preStop : exit code is " + exitCode + " so dont takeSnapshot" );
            _log.info( getComponentId() + " preStop : exit code is " + exitCode + " so dont takeSnapshot" );

        } else {
            _console.info( getComponentId() + " preStop : invoke takeSnapshot" );
            _log.info( getComponentId() + " preStop : invoke takeSnapshot" );

            takeSnapshot( _snapDefFinal, ( doneFlag, def ) -> doFinalSnap( doneFlag, def ) );

            _console.info( getComponentId() + " preStop : finished takeSnapshot" );
            _log.info( getComponentId() + " preStop : finished takeSnapshot" );
        }
    }

    @Override public synchronized long restoreLast( Collection<Object> restoredComponents, final SMTStartContext context ) {
        long restoreTime = 0;

        try {
            _log.info( getComponentId() + " about to restore from last final snapshot" );

            restoreTime = _snapshotReader.restoreLastSnapshot( _snapDefFinal, restoredComponents, _maxAgeDays );

            if ( restoreTime == 0 ) {
                _log.info( getComponentId() + " about to restore from last EOD snapshot" );

                restoreTime = _snapshotReader.restoreLastSnapshot( _snapDefDaily, restoredComponents, _maxAgeDays );
            }

            // now import last import snapshot if date is AFTER last snapshot
            for ( ComponentImportReader importReader : _importReaders ) {
                importReader.importLastSnapshot( _snapDefImport, restoredComponents, restoreTime );
            }

            postRestore( restoredComponents, restoreTime, context );

        } catch( Exception e ) {
            _log.error( ERR_RESTORE, " ABORT ... UNSAFE TO CONTINUE AS IN UNKNOWN STATE : " + e.getMessage(), e );

            Utils.exit( -1, e );
        }

        return restoreTime;
    }

    @Override public synchronized void takeSnapshot() {
        final SnapshotDefinitionImpl def = _snapDefDaily;

        takeSnapshot( def, ( a, b ) -> doSnap( a, b ) );
    }

    @Override public void addExtra( final SMTSnapshotMember extra ) {
        _extraMembers.add( extra );
    }

    @Override public long getTimeLastSnapshot() {
        long restoreTime = 0;

        try {
            _log.info( getComponentId() + " about to getTimeLastSnapshot from last final snapshot" );

            restoreTime = _snapshotReader.getTimeLastSnapshot( _snapDefFinal, _maxAgeDays );

            if ( restoreTime == 0 ) {
                _log.info( getComponentId() + " about to getTimeLastSnapshot from last EOD snapshot" );

                restoreTime = _snapshotReader.getTimeLastSnapshot( _snapDefDaily, _maxAgeDays );
            }

        } catch( Exception e ) {
            // ignore
        }

        return restoreTime;
    }

    @Override public void disableSnapshot( final boolean isDisableSnapshot ) { _disabledSnapshot = true; }

    protected void postRestore( final Collection<Object> restoredComponents, final long restoreTime, final SMTStartContext context ) {
        for ( Object o : restoredComponents ) {
            if ( o instanceof PostRestorePatchup ) {
                PostRestorePatchup sm = PostRestorePatchup.class.cast( o );

                sm.postRestore( restoreTime, context );
            }
        }
    }

    protected void setTimer() { /* default no timer */ }

    private void doFinalSnap( final AtomicBoolean doneFlag, final SnapshotDefinitionImpl def ) {
        try {
            if ( !_disabledSnapshot ) {

                updateSnapDef( def );

                _log.info( getComponentId() + " about to take snapshot" );

                _snapshotWriter.takeSnapshot( def );
            }

        } catch( Throwable e ) {
            _log.error( ERR_TAKE_SNAP, " " + e.getMessage(), e );

        } finally {
            doneFlag.set( true );

            synchronized( doneFlag ) {
                doneFlag.notifyAll();
            }
        }
    }

    private void doSnap( AtomicBoolean doneFlag, SnapshotDefinitionImpl def ) {
        try {
            updateSnapDef( def );

            _snapshotWriter.takeSnapshot( def );

            _log.info( getComponentId() + " snapshot " + def.id() + " completed" );

        } catch( Throwable e ) {
            _log.error( ERR_TAKE_SNAP, id() + " " + e.getMessage(), e );
        } finally {
            doneFlag.set( true );

            synchronized( doneFlag ) {
                doneFlag.notifyAll();
            }
        }
    }

    private void setFilters( final ComponentExportWriter exportWriter ) {
        String filterExport = AppProps.instance().getProperty( "EXPORT_FILTER", false, null );

        _log.log( Level.debug, getComponentId() + " setting EXPORT_FILTER FROM " + filterExport );

        if ( filterExport != null && filterExport.length() > 0 ) {
            String[] names = StringUtils.split( filterExport, ',' );

            Set<Class> classes = new HashSet<>( names.length );

            for ( String name : names ) {
                try {
                    Class c = ReflectUtils.getClass( name );

                    classes.add( c );
                } catch( Exception e ) {
                    _log.error( ERR_FILTER, e.getMessage(), e );
                }
            }

            exportWriter.setFilterClasses( classes );
        }
    }

    private void takeSnapshot( final SnapshotDefinitionImpl def, ZConsumer2Args<AtomicBoolean, SnapshotDefinitionImpl> snapper ) {
        Thread  thread   = Thread.currentThread();
        boolean isDaemon = thread.isDaemon();

        final AtomicBoolean doneFlag = new AtomicBoolean( false );

        if ( isDaemon && AppProps.instance().isProdOrUAT() ) {
            _log.info( getComponentId() + " about to take snapshot " + def.id() + " on DAEMON thread " + thread.getName() + ", creating non daemon thread for snapshot" );

            Thread t = new Thread( () -> snapper.accept( doneFlag, def ), "SnapshotTaker" );
            t.setDaemon( false );
            t.start();

            while( !doneFlag.get() ) {
                _log.info( getComponentId() + " waiting for snapshot to complete" );

                synchronized( doneFlag ) {
                    try { doneFlag.wait( 1000 ); } catch( InterruptedException e ) { /* ignore */ }
                }
            }

        } else {
            _log.info( getComponentId() + " about to take snapshot " + def.id() + " on NON daemon thread " + thread.getName() );

            snapper.accept( doneFlag, def );
        }
    }

    private void updateSnapDef( final SnapshotDefinitionImpl snapDef ) {
        LinkedHashSet<SMTSnapshotMember> smSet = new LinkedHashSet<>();

        for ( SMTSnapshotMember s : _extraMembers ) {
            if ( !smSet.contains( s ) ) {
                _log.info( getComponentId() + " adding explicitly additional SMTSnapshotMember " + s.getComponentId() );
                smSet.add( s );
            }
        }

        if ( _snapshotMembers != null ) {
            for ( SMTComponent s : _snapshotMembers ) {
                if ( s instanceof SMTSnapshotMember ) {
                    if ( !smSet.contains( s ) ) {
                        _log.info( getComponentId() + " adding explicitly selected SMTSnapshotMember " + s.getComponentId() );
                        smSet.add( (SMTSnapshotMember) s );
                    }
                }
            }
        } else {
            final Set<SMTComponent> allComponents = new LinkedHashSet<>();
            _ctx.getComponentManager().getComponentSet( allComponents );
            for ( SMTComponent s : allComponents ) {
                if ( s instanceof SMTSnapshotMember ) {
                    if ( !smSet.contains( s ) ) {
                        _log.info( getComponentId() + " adding default SMTSnapshotMember " + s.getComponentId() );
                        smSet.add( (SMTSnapshotMember) s );
                    }
                }
            }
        }

        snapDef.getComponents().clear();
        snapDef.getComponents().addAll( smSet );
    }
}
