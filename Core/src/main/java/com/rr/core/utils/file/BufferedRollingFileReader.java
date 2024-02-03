/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils.file;

import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Non ThreadSafe buffered file reader
 * <p>
 * Reads a byte[] stream with optional line feed after each write
 * <p>
 * Supports file rolling
 */
public final class BufferedRollingFileReader extends InputStream {

    private static final Logger _log = LoggerFactory.create( BufferedRollingFileReader.class );
    private final String                    _fileExtension;
    private final RollableFileNameGenerator _fileNameGenerator;
    private       String                    _baseFileName;
    private       String                    _curFileName;
    private       int                       _rollNumber = 0;
    private       BufferedInputStream       _stream;
    private       ByteBuffer                _directBlockBuf;
    private boolean _allDone;

    public BufferedRollingFileReader( String fileName, String fileExtension, RollableFileNameGenerator fileNameGenerator, int blockSize ) {
        _baseFileName      = fileName;
        _fileExtension     = fileExtension;
        _fileNameGenerator = fileNameGenerator;

        _directBlockBuf = ByteBuffer.allocate( blockSize );
        _directBlockBuf.flip();
    }

    @Override public int read() throws IOException {

        if ( _directBlockBuf.position() == _directBlockBuf.limit() ) {

            fill();

            if ( _allDone && _directBlockBuf.position() == _directBlockBuf.limit() ) {
                return -1; // NO MORE DATA
            }
        }

        return _directBlockBuf.get();
    }

    @Override public void close() {

        if ( _stream != null ) {

            _log.info( "BufferedRollingFileReader closing " + _curFileName );

            try {
                if ( _stream != null ) _stream.close();
            } catch( Throwable t ) { /* NADA */ }

            _stream = null;
        }
    }

    @Override public String toString() {
        return "BufferedRollingFileReader {" + _curFileName + ", pos=" + _directBlockBuf.position() + "}";
    }

    public String getCurFileName() { return _curFileName; }

    public boolean open() {

        if ( _stream != null ) {
            close();
        }

        _curFileName = _fileNameGenerator.formName( _baseFileName, ++_rollNumber, _fileExtension );

        try {
            if ( FileUtils.isFile( _curFileName ) ) {

                _log.info( "BufferedRollingFileReader opening " + _curFileName );

                _stream = FileUtils.bufFileInpStream( _curFileName, 256 * 1024 );

            } else {
                _allDone = true;

                return false;
            }
        } catch( Exception e ) {
            throw new RuntimeException( "BufferedFileReader.open error creating file " + _curFileName, e );
        }

        return true;
    }

    private void fill() throws IOException {
        _directBlockBuf.clear();

        while( !_allDone ) {
            final int res = _stream.read( _directBlockBuf.array(), 0, _directBlockBuf.capacity() );

            if ( res == -1 ) {
                close();

                if ( !open() ) {
                    _allDone = true;
                }
            } else if ( res > 0 ) {
                _directBlockBuf.position( res );

                break;
            }
        }

        _directBlockBuf.flip();
    }
}