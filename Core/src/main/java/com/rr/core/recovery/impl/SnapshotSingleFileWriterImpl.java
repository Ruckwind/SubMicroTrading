package com.rr.core.recovery.impl;

import com.rr.core.annotations.PostSnapshot;
import com.rr.core.annotations.PreSnapshot;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTSnapshotMember;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.Env;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.recovery.SnapshotDefinition;
import com.rr.core.recovery.SnapshotUtils;
import com.rr.core.recovery.SnapshotWriter;
import com.rr.core.recovery.json.JSONClassDefinitionCache;
import com.rr.core.recovery.json.JSONWriterImpl;
import com.rr.core.thread.RunState;
import com.rr.core.utils.FileException;
import com.rr.core.utils.ReflectUtils;
import com.rr.core.utils.file.BufferedRollingFileWriter;
import com.rr.core.utils.file.RollableFileNameGenerator;

import java.util.LinkedHashSet;

import static com.rr.core.recovery.SnapshotUtils.SNAPSHOT_EXT;

/**
 * in future could use splitable streams and concurrent JSONWriters to write
 */
public class SnapshotSingleFileWriterImpl implements SnapshotWriter {

    private static final Logger _log = LoggerFactory.create( SnapshotSingleFileWriterImpl.class );

    private final     String          _id;
    private final     SnapshotUtils   _snapshotUtils;
    private           long            _maxFileSize = 1024 * 1024 * 1024; // 1GB max file size
    private           int             _blockSize   = 4 * 1024 * 1024; // 4MB block size
    private           SMTStartContext _ctx;
    private transient RunState        _runState    = RunState.Unknown;

    /**
     * @param id
     * @param snapshotRootPath root dir for all snapshots "/snapshot/"
     */
    public SnapshotSingleFileWriterImpl( final String id, String snapshotRootPath ) throws FileException {
        _id            = id;
        _snapshotUtils = new SnapshotUtils( snapshotRootPath );
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState()                          { return _runState; }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    @Override public void init( final SMTStartContext ctx, CreationPhase time ) {
        _ctx = ctx;
    }

    @Override public synchronized void takeSnapshot( SnapshotDefinition snapshot ) throws Exception {
        long start = ClockFactory.get().currentTimeMillis();

        _log.info( "Snapshotting starting" );

        _snapshotUtils.removeTempSnapshots( AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ), snapshot.getType() );

        String fileName = _snapshotUtils.createSnapshotFile( AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ), snapshot.getType() );

        RollableFileNameGenerator fGen = SnapshotUtils::formRollableFileName;

        final boolean verboseSpaces = true;

        BufferedRollingFileWriter writer = new BufferedRollingFileWriter( fileName, SNAPSHOT_EXT, fGen, _maxFileSize, _blockSize );

        JSONClassDefinitionCache cache = new JSONClassDefinitionCache( _ctx );

        JSONWriterImpl jsonWriter = new JSONWriterImpl( writer, cache, _ctx.getComponentManager(), verboseSpaces );

        LinkedHashSet<SMTSnapshotMember> components = snapshot.getComponents();

        try {
            writer.open();

            _log.info( getComponentId() + " Snapshotting " + components.size() + " components" );

            components.forEach( ( c ) -> ReflectUtils.invokeAnnotatedMethod( c, PreSnapshot.class ) );

            jsonWriter.objectsToJson( components );

        } finally {
            writer.close();

            components.forEach( ( c ) -> ReflectUtils.invokeAnnotatedMethod( c, PostSnapshot.class ) );
        }

        if ( _snapshotUtils.finaliseSnapshot( AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ), snapshot.getType() ) ) {
            _log.info( getComponentId() + " successfully finalised snapshot" );
        } else {
            _log.warn( getComponentId() + " nothing to finalise for snapshot ! fileName=" + fileName );
        }

        long end = ClockFactory.get().currentTimeMillis();

        _log.info( "Snapshotting complete took " + (Math.abs( end - start ) / 1000) + " secs" );
    }

    public int getBlockSize()                            { return _blockSize; }

    public void setBlockSize( final int blockSize )      { _blockSize = blockSize; }

    public long getMaxFileSize()                         { return _maxFileSize; }

    public void setMaxFileSize( final long maxFileSize ) { _maxFileSize = maxFileSize; }
}
