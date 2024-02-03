package com.rr.core.pubsub.codecs;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTInitialisableComponent;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ReusableString;
import com.rr.core.model.Event;
import com.rr.core.pubsub.Codec;
import com.rr.core.thread.RunState;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ShutdownManager;

import java.io.BufferedOutputStream;

public class FileJSONWriter implements SMTInitialisableComponent, SMTExporter {

    private transient final ReusableString       _buf = new ReusableString();
    private String  _id;
    private boolean _appendBarFile = false;
    private String  _logFileName;
    private int     _blockSize     = 4 * 1024 * 1024;
    private transient       Codec                _codec;
    private transient       RunState             _runState;
    private transient       BufferedOutputStream _writer;

    public FileJSONWriter( final String id ) {
        _id = id;
    }

    public FileJSONWriter( final String id, String logFileName ) {
        _id          = id;
        _logFileName = logFileName;
    }

    public void close() {
        if ( _writer != null ) {
            FileUtils.flush( _writer );
            FileUtils.close( _writer );
            _writer = null;
        }
    }

    @Override public String getComponentId()                  { return _id; }

    @Override public RunState getRunState()                   { return _runState; }

    @Override public RunState setRunState( final RunState newState ) {
        final RunState oldState = _runState;
        _runState = newState;
        return oldState;
    }

    @Override public void init( final SMTStartContext ctx, final CreationPhase creationPhase ) {
        _codec = new JSONCodec( ctx, true, true );

        if ( _logFileName == null || _logFileName.length() == 0 ) throw new SMTRuntimeException( getComponentId() + " missing logName" );

        try {
            _writer = FileUtils.bufFileOutStream( _logFileName, _blockSize, _appendBarFile );

            ShutdownManager.instance().register( "FileJSONWriter" + _id, () -> close(), ShutdownManager.Priority.Low );

        } catch( Exception e ) {
            throw new SMTRuntimeException( e.getMessage() + " on " + _logFileName, e );
        }
    }

    @Override public void write( Event obj ) throws Exception {

        _codec.encode( obj, _buf );

        _buf.replace( (byte) '\n', (byte) ' ' ); // shouldnt be necessary ... precaution against naughty custom codecs

        _buf.append( (byte) 0x0A );

        if ( _writer != null ) {
            _writer.write( _buf.getBytes(), _buf.getOffset(), _buf.length() );
        }
    }

    public int getBlockSize()                                 { return _blockSize; }

    @Override public void setBlockSize( final int blockSize ) { _blockSize = blockSize; }

    @Override public String getExportId() { return getFileName(); }

    public String getFileName() {
        return _logFileName;
    }

    public void setAppendFile( final boolean appendBarFile ) {
        _appendBarFile = appendBarFile;
    }
}
