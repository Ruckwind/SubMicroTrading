package com.rr.core.recovery.impl;

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
import com.rr.core.recovery.*;
import com.rr.core.recovery.json.*;
import com.rr.core.thread.RunState;
import com.rr.core.utils.FileException;
import com.rr.core.utils.file.BufferedRollingFileWriter;
import com.rr.core.utils.file.RollableFileNameGenerator;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.rr.core.recovery.SnapshotUtils.SNAPSHOT_EXT;

/**
 * Writer to export data from BackTest that needs to be imported to Prod
 * <p>
 * in future could use splitable streams and concurrent JSONWriters to write
 */
public class ComponentMFileExportWriter implements ComponentExportWriter {

    private static final Logger    _log            = LoggerFactory.create( ComponentMFileExportWriter.class );
    private static final ErrorCode SNAP_ERR        = new ErrorCode( "CME100", "Error in component export" );
    private static final ErrorCode ERR_MISSING_REF = new ErrorCode( "CME200", "Object referenced in component export missed as not top level object" );

    private final     String          _id;
    private final     SnapshotUtils   _snapshotUtils;
    private           long            _maxFileSize = 1024 * 1024 * 1024; // 1GB max file size
    private           int             _blockSize   = 4 * 1024 * 1024; // 4MB block size
    private           SMTStartContext _ctx;
    private           Set<Class>      _filterClasses;
    private transient RunState        _runState    = RunState.Unknown;

    /**
     * @param id
     * @param snapshotRootPath root dir for all snapshots "/snapshot/"
     */
    public ComponentMFileExportWriter( final String id, String snapshotRootPath ) throws FileException {
        _id            = id;
        _snapshotUtils = new SnapshotUtils( snapshotRootPath );
    }

    @Override public synchronized void exportSnapshot( SnapshotDefinition snapshot ) throws Exception {
        double start = ClockFactory.getLiveClock().currentTimeMillis();

        _log.info( "Export starting for id " + snapshot.getType() );

        _snapshotUtils.removeTempSnapshots( AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ), snapshot.getType() );

        LinkedHashSet<SMTSnapshotMember> components = snapshot.getComponents();

        JSONWriteSharedState jsonWriteSharedState = new JSONWriteSharedStateWithRefs( components );

        _log.info( getComponentId() + " Exporting " + components.size() + " top level components" );

        String baseFileName = _snapshotUtils.createSnapshotFile( AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ), snapshot.getType() );

        int idx = 0;

        for ( SMTSnapshotMember rootComponent : components ) {
            final boolean verboseSpaces = true;

            if ( rootComponent instanceof ComponentExportClient ) {
                jsonWriteSharedState.prepWrite( rootComponent ); // mark root object as being persisted so self refs dont break

                String fileName = baseFileName + "_" + "E" + (++idx) + "_" + rootComponent.getComponentId(); // add C# so can sort files in correct processing order

                RollableFileNameGenerator fGen = SnapshotUtils::formRollableFileName;

                BufferedRollingFileWriter writer = new BufferedRollingFileWriter( fileName, SNAPSHOT_EXT, fGen, _maxFileSize, _blockSize );

                JSONClassDefinitionCache cache = new JSONClassDefinitionCache( _ctx );

                JSONWriterImpl jsonWriter = new JSONWriterImpl( writer, cache, _ctx.getComponentManager(), verboseSpaces, jsonWriteSharedState );

                jsonWriter.setExcludeNullFields( true );
                jsonWriter.setWriteContext( WriteContext.Export );
                jsonWriter.setFilterClasses( _filterClasses );

                try {
                    ComponentExportClient client = (ComponentExportClient) rootComponent;

                    ExportContainer exportContainer = new ExportContainer();
                    exportContainer.setIdOfExportComponent( rootComponent.getComponentId() );
                    exportContainer.setFilterClasses( _filterClasses );

                    client.exportData( exportContainer );

                    writer.open();

                    _log.info( getComponentId() + " Exporting top level component " + rootComponent.getComponentId() );

                    jsonWriter.objectToJson( exportContainer );

                } catch( Exception e ) {
                    _log.error( SNAP_ERR, "component " + rootComponent.getComponentId() + " file " + fileName + " " + e.getMessage(), e );
                } finally {
                    writer.close();
                }
            }
        }

        checkMissingRefs( jsonWriteSharedState, baseFileName );

        if ( _snapshotUtils.finaliseSnapshot( AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ), snapshot.getType() ) ) {
            _log.info( getComponentId() + " successfully finalised export" );
        } else {
            _log.warn( getComponentId() + " nothing to finalise for export ! fileName=" + baseFileName );
        }

        double end = ClockFactory.getLiveClock().currentTimeMillis();

        _log.info( "Snapshotting complete took " + (Math.abs( end - start ) / 1000) + " secs" );
    }

    @Override public void setFilterClasses( final Set<Class> filterClasses ) { _filterClasses = filterClasses; }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState()                                  { return _runState; }

    @Override public void init( final SMTStartContext ctx, CreationPhase time ) {
        _ctx = ctx;
    }

    @Override public RunState setRunState( final RunState newState )         { return _runState = newState; }

    public int getBlockSize()                            { return _blockSize; }

    public void setBlockSize( final int blockSize )      { _blockSize = blockSize; }

    public long getMaxFileSize()                         { return _maxFileSize; }

    public void setMaxFileSize( final long maxFileSize ) { _maxFileSize = maxFileSize; }

    protected void checkMissingRefs( final JSONWriteSharedState jsonWriteSharedState, final String baseFileName ) {
        JSONClassDefinitionCache cache = new JSONClassDefinitionCache( _ctx );

        final Set<Object> missing = new LinkedHashSet<>();
        jsonWriteSharedState.checkMissing( missing, cache );
    }
}
