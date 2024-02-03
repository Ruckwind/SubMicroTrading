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
import com.rr.core.recovery.json.JSONException;
import com.rr.core.recovery.json.JSONReaderImpl;
import com.rr.core.recovery.json.Resolver;
import com.rr.core.thread.RunState;
import com.rr.core.utils.FileException;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.file.BufferedRollingFileReader;
import com.rr.core.utils.file.RollableFileNameGenerator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.rr.core.recovery.SnapshotUtils.SNAPSHOT_EXT;

@SuppressWarnings( "unchecked" )

/**
 * in future could use splitable streams and concurrent JSONWriters to write
 */
public class SnapshotMultiFileReaderImpl implements SnapshotReader {

    public static final  String SNAPSHOT_C       = "snapshot_C";
    private static final Logger _log = LoggerFactory.create( SnapshotMultiFileReaderImpl.class );
    private static final int    MAX_DAYS_IN_PAST = 365;
    private final     String          _id;
    private final     SnapshotUtils   _snapshotUtils;
    private           int             _blockSize = 4 * 1024 * 1024; // 4MB block size
    private           SMTStartContext _ctx;
    private transient RunState        _runState  = RunState.Unknown;

    /**
     * @param id
     * @param snapshotRootPath root dir for all snapshots "/snapshot/"
     */
    public SnapshotMultiFileReaderImpl( final String id, String snapshotRootPath ) throws FileException {
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

        String dirName = FileUtils.getDirName( baseSnapFileName );

        final List<String> files = FileUtils.getDirEntries( dirName );

        Collections.sort( files, this::sortFileNamesComparator );

        RollableFileNameGenerator fGen = SnapshotUtils::formRollableFileName;

        JSONClassDefinitionCache cache = new JSONClassDefinitionCache( _ctx );

        final Resolver resolver = new SMTComponentResolver( _ctx.getComponentManager() );
        ReusableString logMsg   = TLC.strPop();

        for ( String entry : files ) {
            if ( entry.contains( SNAPSHOT_EXT ) && entry.contains( "001" ) ) {
                int lastUnder = entry.lastIndexOf( "_" );
                if ( lastUnder > 0 ) {
                    String baseName = dirName + "/" + entry.substring( 0, lastUnder );

                    BufferedRollingFileReader reader = new BufferedRollingFileReader( baseName, SNAPSHOT_EXT, fGen, _blockSize );

                    JSONReaderImpl jsonReader = new JSONReaderImpl( reader, resolver, cache );

                    try {
                        reader.open();

                        snapshotStartTime = _snapshotUtils.getSnapshotStartTime( baseSnapFileName, entry );

                        Object s = null;

                        do {

                            long startObject = ClockFactory.get().currentTimeMillis();
                            s = jsonReader.jsonToObject();

                            if ( s != null ) {
                                if ( s instanceof Collection ) {
                                    Collection<Object> c = Collection.class.cast( s );

                                    for ( Object o : c ) {
                                        restoredComponents.add( o );
                                    }

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

                    } catch( Exception e ) {
                        throw new JSONException( e.getMessage() + " in " + reader.getCurFileName(), e );
                    } finally {
                        reader.close();
                    }
                }
            } else if ( entry.contains( "001" ) && !entry.contains( "gz" ) ) {
                throw new SMTRuntimeException( getComponentId() + " SNAPSHOT FILES MUST BE COMPRESSED with .json.gz  EXTENSION NOT " + entry );
            }
        }
        TLC.strPush( logMsg );

        resolver.resolveMissing();

        long end = ClockFactory.get().currentTimeMillis();

        _log.info( "Snapshotting Restore complete took " + (Math.abs( end - start ) / 1000) + " secs" );

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

        String dirName = FileUtils.getDirName( baseSnapFileName );

        final List<String> files = FileUtils.getDirEntries( dirName );

        RollableFileNameGenerator fGen = SnapshotUtils::formRollableFileName;

        for ( String entry : files ) {
            if ( entry.contains( SNAPSHOT_EXT ) ) {
                int lastUnder = entry.lastIndexOf( "_" );
                if ( lastUnder > 0 ) {
                    String baseName = dirName + "/" + entry.substring( 0, lastUnder );

                    BufferedRollingFileReader reader = new BufferedRollingFileReader( baseName, SNAPSHOT_EXT, fGen, _blockSize );

                    JSONReaderImpl jsonReader = new JSONReaderImpl( reader, null, null );

                    try {
                        reader.open();

                        snapshotStartTime = _snapshotUtils.getSnapshotStartTime( baseSnapFileName, entry );

                        return snapshotStartTime;

                    } catch( Exception e ) {
                        // ignore
                    } finally {
                        reader.close();
                    }
                }
            }
        }

        return 0;
    }

    private int sortFileNamesComparator( final String f1, final String f2 ) {
        int idx1a = f1.indexOf( SNAPSHOT_C );
        int idx2a = f2.indexOf( SNAPSHOT_C );

        try {
            if ( idx1a != -1 && idx2a != -1 ) {
                int idx1b = f1.indexOf( '_', idx1a + SNAPSHOT_C.length() );
                int idx2b = f2.indexOf( '_', idx2a + SNAPSHOT_C.length() );

                if ( idx1b != -1 && idx2b != -1 ) {

                    String n1 = f1.substring( idx1a + SNAPSHOT_C.length(), idx1b );
                    String n2 = f2.substring( idx2a + SNAPSHOT_C.length(), idx2b );

                    int i1 = Integer.parseInt( n1 );
                    int i2 = Integer.parseInt( n2 );

                    return Integer.compare( i1, i2 );
                }
            }
        } catch( NumberFormatException e ) {
            _log.warn( getComponentId() + " " + e.getMessage() + " on " + f1 + " and " + f2 );
        }

        return f1.compareTo( f2 );
    }
}
