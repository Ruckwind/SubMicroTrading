package com.rr.core.recovery.impl;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTComponent;
import com.rr.core.component.SMTInitialisableComponent;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.recovery.*;
import com.rr.core.recovery.json.JSONClassDefinitionCache;
import com.rr.core.recovery.json.JSONException;
import com.rr.core.recovery.json.JSONReaderImpl;
import com.rr.core.recovery.json.Resolver;
import com.rr.core.thread.RunState;
import com.rr.core.utils.FileException;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.file.BufferedRollingFileReader;
import com.rr.core.utils.file.RollableFileNameGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.rr.core.recovery.SnapshotUtils.SNAPSHOT_EXT;

@SuppressWarnings( "unchecked" )

/**
 * Reader to read data into prod that was exported from BackTest
 *
 * in future could use splitable streams and concurrent JSONWriters to write
 */
public class ComponentMFileImportReader implements ComponentImportReader {

    private static final Logger _log = LoggerFactory.create( ComponentMFileImportReader.class );

    private static final int       MAX_DAYS_IN_PAST = 365;
    private static final ErrorCode ERR_IMPORT       = new ErrorCode( "CMI100", "Exception during import" );

    private final     String          _id;
    private final     SnapshotUtils   _snapshotUtils;
    private           int             _blockSize = 4 * 1024 * 1024; // 4MB block size
    private           SMTStartContext _ctx;
    private transient RunState        _runState  = RunState.Unknown;

    /**
     * @param id
     * @param snapshotRootPath root dir for all snapshots "/snapshot/"
     */
    public ComponentMFileImportReader( final String id, String snapshotRootPath ) throws FileException {
        _id            = id;
        _snapshotUtils = new SnapshotUtils( snapshotRootPath );
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState() {
        return _runState;
    }

    @Override public void init( final SMTStartContext ctx, CreationPhase phase ) {
        _ctx = ctx;
    }

    @Override public RunState setRunState( final RunState newState ) {
        return _runState = newState;
    }

    @Override public long getTimeLastSnapshot( final SnapshotDefinition def, final int maxAgeDays ) throws Exception {
        long snapshotStartTime = 0;

        long start = ClockFactory.get().currentTimeMillis();

        _log.info( "Import Restore starting" );

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

    @Override public synchronized long importLastSnapshot( SnapshotDefinition def, Collection<Object> restoredComponents, int maxAgeDays, long minTimestamp ) throws Exception {
        long snapshotStartTime = 0;

        long start = ClockFactory.get().currentTimeMillis();

        _log.info( "Import Restore starting" );

        String baseSnapFileName = _snapshotUtils.getLastSnapshotFile( AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ), def.getType(), maxAgeDays );

        if ( baseSnapFileName == null ) {
            _log.info( getComponentId() + " no import snapshot file found for " + def.getType() + " at " + baseSnapFileName );
            return snapshotStartTime;
        }

        String dirName = FileUtils.getDirName( baseSnapFileName );

        final List<String> files = FileUtils.getDirEntries( dirName );

        snapshotStartTime = importFiles( restoredComponents, minTimestamp, snapshotStartTime, start, baseSnapFileName, dirName, files );

        return snapshotStartTime;
    }

    @Override public synchronized long importLastSnapshot( SnapshotDefinition def, Collection<Object> restoredComponents, long minTimestamp ) throws Exception {
        return importLastSnapshot( def, restoredComponents, Constants.UNSET_INT, minTimestamp );
    }

    public long importFiles( Collection<Object> restoredComponents, long minTimestamp, long snapshotStartTime, long start, String baseSnapFileName, String dirName, List<String> files ) throws JSONException {

        Collections.sort( files );

        RollableFileNameGenerator fGen = SnapshotUtils::formRollableFileName;

        JSONClassDefinitionCache cache = new JSONClassDefinitionCache( _ctx );

        final Resolver resolver = new SMTComponentResolver( _ctx.getComponentManager() );

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

                        if ( snapshotStartTime > minTimestamp ) {
                            _log.info( getComponentId() + " importing from " + reader.toString() );

                            Object s = null;

                            final Collection<ExportContainer> importers = new ArrayList<>();

                            do {
                                s = jsonReader.jsonToObject();

                                if ( s != null ) {
                                    if ( s instanceof Collection ) {
                                        Collection<ExportContainer> c = Collection.class.cast( s );

                                        for ( ExportContainer o : c ) {
                                            importers.add( o );
                                        }

                                    } else if ( s instanceof ExportContainer ) {
                                        importers.add( (ExportContainer) s );
                                    } else {
                                        _log.info( "Import unexpected object " + s.toString() );
                                    }
                                }

                            } while( s != null );

                            for ( ExportContainer exportContainer : importers ) {

                                doImport( snapshotStartTime, exportContainer, restoredComponents );
                            }

                            _log.info( getComponentId() + " import complete from " + reader.toString() );

                        } else {
                            _log.info( "importing SKIPPING file " +
                                       baseSnapFileName +
                                       " as snapshotTimeStamp=" +
                                       TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( snapshotStartTime ) +
                                       ", is older than specified minTimestamp=" +
                                       TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( minTimestamp ) );
                        }

                    } catch( Exception e ) {
                        throw new JSONException( e.getMessage() + " in " + reader.getCurFileName(), e );
                    } finally {
                        reader.close();
                    }
                }
            }
        }

        resolver.resolveMissing();

        long end = ClockFactory.get().currentTimeMillis();

        _log.info( "Import complete took " + (Math.abs( end - start ) / 1000) + " secs" );

        return snapshotStartTime;
    }

    protected void doImport( final long snapshotStartTime, final ExportContainer exportContainer, final Collection<Object> restoredComponents ) {
        String       componentId = exportContainer.getIdOfExportComponent();
        SMTComponent c           = _ctx.getComponentManager().getComponentOrNull( componentId );

        if ( c != null ) {
            if ( c instanceof ComponentExportClient ) {

                ComponentExportClient importComp = (ComponentExportClient) c;

                try {

                    boolean doImport = true;

                    if ( c instanceof SMTInitialisableComponent ) {

                        if ( ((SMTInitialisableComponent) c).getRunState().isDead() ) {
                            doImport = false;
                        }
                    }

                    if ( doImport ) {
                        _log.info( "Importing for " + componentId );

                        importComp.importData( exportContainer, snapshotStartTime );

                        restoredComponents.add( importComp );

                    } else {
                        _log.info( "skipping dead component " + componentId );
                    }

                } catch( Exception ex ) {
                    _log.error( ERR_IMPORT, c.getComponentId() + " : " + ex.getMessage(), ex );
                }

            } else {
                _log.warn( "Importing for " + componentId + " UNABLE TO IMPORT AS COMPONENT " + c.getClass().getSimpleName() + " doesnt implement ComponentExportClient" );
            }

        } else {
            _log.warn( "Importing for " + componentId + " UNABLE TO IMPORT AS COMPONENT DOESNT EXIST" );
        }
    }
}
