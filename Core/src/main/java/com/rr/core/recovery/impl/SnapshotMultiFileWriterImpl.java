package com.rr.core.recovery.impl;

import com.rr.core.annotations.PostSnapshot;
import com.rr.core.annotations.PreSnapshot;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTSnapshotMember;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.Env;
import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.recovery.SnapshotDefinition;
import com.rr.core.recovery.SnapshotUtils;
import com.rr.core.recovery.SnapshotWriter;
import com.rr.core.recovery.json.JSONClassDefinitionCache;
import com.rr.core.recovery.json.JSONWriteSharedState;
import com.rr.core.recovery.json.JSONWriteSharedStateWithRefs;
import com.rr.core.recovery.json.JSONWriterImpl;
import com.rr.core.thread.RunState;
import com.rr.core.utils.FileException;
import com.rr.core.utils.ReflectUtils;
import com.rr.core.utils.Utils;
import com.rr.core.utils.file.BufferedRollingFileWriter;
import com.rr.core.utils.file.RollableFileNameGenerator;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.rr.core.recovery.SnapshotUtils.SNAPSHOT_EXT;

/**
 * in future could use splitable streams and concurrent JSONWriters to write
 */
public class SnapshotMultiFileWriterImpl implements SnapshotWriter {

    private static final Logger    _log     = LoggerFactory.create( SnapshotMultiFileWriterImpl.class );
    private static final ErrorCode SNAP_ERR = new ErrorCode( "SMF100", "Error in snapshot" );

    private final     String          _id;
    private final     SnapshotUtils   _snapshotUtils;
    private           long            _maxFileSize = 1024 * 1024 * 1024; // 1GB max file size
    private           int             _blockSize   = 4 * 1024 * 1024; // 4MB block size
    private           SMTStartContext _ctx;
    private transient RunState        _runState    = RunState.Unknown;
    private           boolean         _batchNotif  = AppProps.instance().getBooleanProperty( "BATCH_SNAPSHOT_NOTIF", false, false );

    /**
     * @param id
     * @param snapshotRootPath root dir for all snapshots "/snapshot/"
     */
    public SnapshotMultiFileWriterImpl( final String id, String snapshotRootPath ) throws FileException {
        _id            = id;
        _snapshotUtils = new SnapshotUtils( snapshotRootPath );
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState()                          { return _runState; }

    @Override public void init( final SMTStartContext ctx, CreationPhase time ) {
        _ctx = ctx;
    }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    @Override public synchronized void takeSnapshot( SnapshotDefinition snapshot ) throws Exception {
        long start = ClockFactory.get().currentTimeMillis();

        _log.info( getComponentId() + " snapshotting starting for id " + snapshot.getType() );

        _snapshotUtils.removeTempSnapshots( AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ), snapshot.getType() );

        LinkedHashSet<SMTSnapshotMember> components = snapshot.getComponents();

        JSONWriteSharedState jsonWriteSharedState = new JSONWriteSharedStateWithRefs( components );

        _log.info( getComponentId() + " snapshotting " + components.size() + " top level components" );

        String baseFileName = _snapshotUtils.createSnapshotFile( AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ), snapshot.getType() );

        int idx = 0;

        try {
            if ( _batchNotif ) components.forEach( ( c ) -> ReflectUtils.invokeAnnotatedMethod( c, PreSnapshot.class ) );

            JSONClassDefinitionCache cache = new JSONClassDefinitionCache( _ctx );

            for ( SMTSnapshotMember rootComponent : components ) {
                final boolean verboseSpaces = true;

                String fileName = String.format( "%s_C%03d_%s", baseFileName, (++idx), rootComponent.getComponentId() );

                RollableFileNameGenerator fGen = SnapshotUtils::formRollableFileName;

                BufferedRollingFileWriter writer = new BufferedRollingFileWriter( fileName, SNAPSHOT_EXT, fGen, _maxFileSize, _blockSize );

                JSONWriterImpl jsonWriter = new JSONWriterImpl( writer, cache, _ctx.getComponentManager(), verboseSpaces, jsonWriteSharedState );

                try {
                    writer.open();

                    if ( !_batchNotif ) ReflectUtils.invokeAnnotatedMethod( rootComponent, PreSnapshot.class );

                    _log.info( getComponentId() + " snapshotting top level component " + rootComponent.getComponentId() );

                    jsonWriter.objectToJson( rootComponent );

                } catch( Exception e ) {
                    _log.error( SNAP_ERR, "component " + rootComponent.getComponentId() + " file " + fileName + " " + e.getMessage(), e );
                } finally {
                    Utils.close( writer );

                    if ( !_batchNotif ) ReflectUtils.invokeAnnotatedMethod( rootComponent, PostSnapshot.class );
                }
            }

            snapMissingRefs( jsonWriteSharedState, baseFileName );

            if ( _snapshotUtils.finaliseSnapshot( AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ), snapshot.getType() ) ) {
                _log.info( getComponentId() + " successfully finalised snapshot" );
            } else {
                _log.warn( getComponentId() + " nothing to finalise for snapshot ! fileName=" + baseFileName );
            }

        } finally {
            if ( _batchNotif ) components.forEach( ( c ) -> ReflectUtils.invokeAnnotatedMethod( c, PostSnapshot.class ) );
        }

        long end = ClockFactory.get().currentTimeMillis();

        _log.info( getComponentId() + " snapshotting complete took " + (Math.abs( end - start ) / 1000) + " secs" );
    }

    public int getBlockSize()                            { return _blockSize; }

    public void setBlockSize( final int blockSize )      { _blockSize = blockSize; }

    public long getMaxFileSize()                         { return _maxFileSize; }

    public void setMaxFileSize( final long maxFileSize ) { _maxFileSize = maxFileSize; }

    protected void snapMissingRefs( final JSONWriteSharedState jsonWriteSharedState, final String baseFileName ) {
        JSONClassDefinitionCache cache = new JSONClassDefinitionCache( _ctx );

        final Set<Object> missing = new LinkedHashSet<>();
        jsonWriteSharedState.checkMissing( missing, cache );

        if ( missing.size() > 0 ) {
            String fileName = baseFileName + "_MISSED";

            RollableFileNameGenerator fGen = SnapshotUtils::formRollableFileName;

            final boolean verboseSpaces = true;

            BufferedRollingFileWriter writer = new BufferedRollingFileWriter( fileName, SNAPSHOT_EXT, fGen, _maxFileSize, _blockSize );

            JSONWriterImpl jsonWriter = new JSONWriterImpl( writer, cache, _ctx.getComponentManager(), verboseSpaces, jsonWriteSharedState );

            try {
                writer.open();

                _log.info( getComponentId() + " snapshotting " + missing.size() + " component(s) which didnt get its own file" );

                jsonWriter.objectToJson( missing );

            } catch( Exception e ) {
                _log.error( SNAP_ERR, "missing components, file " + fileName + " " + e.getMessage(), e );
            } finally {
                writer.close();
            }
        }
    }
}
