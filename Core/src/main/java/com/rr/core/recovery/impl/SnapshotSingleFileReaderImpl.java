package com.rr.core.recovery.impl;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTComponent;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.recovery.SMTComponentResolver;
import com.rr.core.recovery.SnapshotDefinition;
import com.rr.core.recovery.SnapshotReader;
import com.rr.core.recovery.SnapshotUtils;
import com.rr.core.recovery.json.JSONClassDefinitionCache;
import com.rr.core.recovery.json.JSONReaderImpl;
import com.rr.core.recovery.json.Resolver;
import com.rr.core.thread.RunState;
import com.rr.core.utils.FileException;
import com.rr.core.utils.file.BufferedRollingFileReader;
import com.rr.core.utils.file.RollableFileNameGenerator;

import java.util.Collection;

import static com.rr.core.recovery.SnapshotUtils.SNAPSHOT_EXT;

@SuppressWarnings( "unchecked" )

/**
 * in future could use splitable streams and concurrent JSONWriters to write
 */
public class SnapshotSingleFileReaderImpl implements SnapshotReader {

    private static final Logger _log = LoggerFactory.create( SnapshotSingleFileReaderImpl.class );

    private static final int MAX_DAYS_IN_PAST = 365;

    private final     String          _id;
    private final     SnapshotUtils   _snapshotUtils;
    private           int             _blockSize = 4 * 1024 * 1024; // 4MB block size
    private           SMTStartContext _ctx;
    private transient RunState        _runState  = RunState.Unknown;

    /**
     * @param id
     * @param snapshotRootPath root dir for all snapshots "/snapshot/"
     */
    public SnapshotSingleFileReaderImpl( final String id, String snapshotRootPath ) throws FileException {
        _id            = id;
        _snapshotUtils = new SnapshotUtils( snapshotRootPath );
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState() {
        return _runState;
    }

    @Override public RunState setRunState( final RunState newState ) {
        return _runState = newState;
    }

    @Override public void init( final SMTStartContext ctx, CreationPhase phase ) {
        _ctx = ctx;
    }

    @Override public synchronized long restoreLastSnapshot( SnapshotDefinition def, Collection<Object> restoredComponents ) throws Exception {
        return restoreLastSnapshot( def, restoredComponents, Constants.UNSET_INT );
    }

    @Override public synchronized long restoreLastSnapshot( SnapshotDefinition def, Collection<Object> restoredComponents, int maxAgeDays ) throws Exception {
        long snapshotStartTime = 0;

        long start = ClockFactory.get().currentTimeMillis();

        _log.info( "Snapshotting Restore starting" );

        String baseSnapFileName = _snapshotUtils.getLastSnapshotFile( AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ), def.getType(), maxAgeDays );

        if ( baseSnapFileName == null ) {
            _log.info( getComponentId() + " no snapshot file found for " + def.getType() );
            return snapshotStartTime;
        }

        RollableFileNameGenerator fGen = SnapshotUtils::formRollableFileName;

        BufferedRollingFileReader reader = new BufferedRollingFileReader( baseSnapFileName, SNAPSHOT_EXT, fGen, _blockSize );

        JSONClassDefinitionCache cache = new JSONClassDefinitionCache( _ctx );

        final Resolver resolver = new SMTComponentResolver( _ctx.getComponentManager() );

        JSONReaderImpl jsonReader = new JSONReaderImpl( reader, resolver, cache );
        ReusableString logMsg     = TLC.strPop();

        try {
            reader.open();

            snapshotStartTime = _snapshotUtils.getSnapshotStartTime( baseSnapFileName, reader.getCurFileName() );

            Object s = null;

            do {

                long startObject = ClockFactory.get().currentTimeMillis();
                s = jsonReader.jsonToObject();

                if ( s != null ) {
                    if ( s instanceof Collection ) {
                        Collection<Object> c = Collection.class.cast( s );

                        for ( Object o : c ) restoredComponents.add( o );

                    } else {
                        restoredComponents.add( s );
                    }

                    if ( s instanceof SMTComponent ) {
                        long finishObject = ClockFactory.get().currentTimeMillis();
                        _log.info(
                                logMsg.copy( getClass().getSimpleName() )
                                      .append( ": Snapshotting Restored " )
                                      .append( SMTComponent.class.cast( s ).getComponentId() )
                                      .append( ", took " )
                                      .append( Math.abs( finishObject - startObject ) / 1000 )
                                      .append( " seconds" ) );
                    }
                }

            } while( s != null );

        } finally {
            reader.close();
            TLC.strPush( logMsg );
        }

        resolver.resolveMissing();

        long end = ClockFactory.get().currentTimeMillis();

        _log.info( "Snapshotting complete took " + (Math.abs( end - start ) / 1000) + " secs" );

        return snapshotStartTime;
    }

    @Override public long getTimeLastSnapshot( final SnapshotDefinition def, final int maxAgeDays ) throws Exception {
        long snapshotStartTime = 0;

        long start = ClockFactory.get().currentTimeMillis();

        _log.info( "Snapshotting Restore starting" );

        String baseSnapFileName = _snapshotUtils.getLastSnapshotFile( AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ), def.getType(), maxAgeDays );

        if ( baseSnapFileName == null ) {
            _log.info( getComponentId() + " no snapshot file found for " + def.getType() );
            return snapshotStartTime;
        }

        RollableFileNameGenerator fGen = SnapshotUtils::formRollableFileName;

        BufferedRollingFileReader reader = new BufferedRollingFileReader( baseSnapFileName, SNAPSHOT_EXT, fGen, _blockSize );

        JSONClassDefinitionCache cache = new JSONClassDefinitionCache( _ctx );

        JSONReaderImpl jsonReader = new JSONReaderImpl( reader, null, cache );

        try {
            reader.open();

            snapshotStartTime = _snapshotUtils.getSnapshotStartTime( baseSnapFileName, reader.getCurFileName() );

        } catch( Exception e ) {
            // ignore
        } finally {
            reader.close();
        }

        return snapshotStartTime;
    }
}
