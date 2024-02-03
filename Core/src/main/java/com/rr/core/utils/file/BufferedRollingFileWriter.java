/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils.file;

import com.rr.core.lang.*;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.ShutdownManager;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Non ThreadSafe buffered file writer
 * <p>
 * Writes a byte[] stream with optional line feed after each write
 * <p>
 * Supports file rolling
 * <p>
 * taken off FileOutputStream and channel with direct write to allow for gzip output
 */
public final class BufferedRollingFileWriter extends OutputStream {

    private static final Logger _console = ConsoleFactory.console( BufferedRollingFileWriter.class, Level.info );

    private static final ErrorCode ERR_WRITE       = new ErrorCode( "BFW100", "BufferedRollingFileWriter: failed on log write : " );
    private static final ErrorCode ERR_BLOCK_WRITE = new ErrorCode( "BFW200", "BufferedRollingFileWriter: error in blockWriteToDisk with file : " );
    private static final ErrorCode ERR_CLOSE       = new ErrorCode( "BFW300", "BufferedRollingFileWriter: failed on close file : " );

    private static final ZString FILE_APPENDER_ALREADY_CLOSED = new ViewString( "BufferedRollingFileWriter already closed" );
    private static final int     SAFETY_BYTES                 = 256;    // leave space for extra formatting around the event
    private final long _maxFileSize;
    private final boolean                   _retryOnFail;
    private final String                    _fileExtension;
    private final RollableFileNameGenerator _fileNameGenerator;
    private String _baseFileName;
    private int _rollNumber = 0;
    private BufferedOutputStream _stream;
    private ByteBuffer _directBlockBuf;
    private byte[]     _singleByteArr = new byte[ 1 ];
    private       long _fileSize = 0;
    private       boolean                   _dateRoll         = false;
    private       boolean                   _addNewLine;
    private       String                    _curFileName;
    private final ShutdownManager.Callback  _shutdownCallback = () -> closeStream();
    private       boolean                   _zipOnRoll        = true;

    public BufferedRollingFileWriter( String fileName, String fileExtension, RollableFileNameGenerator fileNameGenerator, long maxFileSize, int blockSize ) {
        this( fileName, fileExtension, fileNameGenerator, maxFileSize, blockSize, false, false );
    }

    public BufferedRollingFileWriter( String fileName,
                                      String fileExtension,
                                      RollableFileNameGenerator fileNameGenerator,
                                      long maxFileSize,
                                      int blockSize,
                                      boolean addNewLine,
                                      boolean retryOnFail ) {
        _baseFileName      = fileName;
        _addNewLine        = addNewLine;
        _retryOnFail       = retryOnFail;
        _fileExtension     = fileExtension;
        _fileNameGenerator = fileNameGenerator;

        _directBlockBuf = ByteBuffer.allocate( blockSize );

        // assume compression is at least 10x
        _maxFileSize = (fileExtension.endsWith( "gz" )) ? maxFileSize * 10 : maxFileSize;

        /**
         * priority is Low to allow other shutdown hooks that could be using the file to complete first
         */
        ShutdownManager.instance().register( "BufferedRollingFileWriterClose:" + fileName, _shutdownCallback, ShutdownManager.Priority.Low );
    }

    @Override public String toString() {
        return "BufferedRollingFileWriter {" + _curFileName + ", pos=" + _directBlockBuf.position() + "}";
    }

    @Override public void write( final int b ) throws IOException {
        _singleByteArr[ 0 ] = (byte) b;
        doHandle( _singleByteArr, 0, 1 );
    }

    @Override public void write( byte buf[], int off, int len ) throws IOException {
        doHandle( buf, off, len );
    }

    @Override public void flush() {
        if ( _directBlockBuf.position() > 0 ) {
            blockWriteToDisk();
        }

        if ( _stream != null ) {
            try {
                _stream.flush();
            } catch( Exception e ) {
                _console.warn( "BufferedRollingFileWriter : Failed to flush file " + _curFileName + " : " + e.getMessage() );
            }
        }
    }

    @Override public void close() {
        // this method is callable from outside e.g try-with-resources
        // if we close before the application is shutdown then we don't want to hold onto buffers by being reachable from shutdown manager
        ShutdownManager.instance().deregister( _shutdownCallback );
        closeStream();
    }

    public void dateRoll() {
        _dateRoll = true;
    }

    public String getFileName() { return (_curFileName == null) ? "null" : _curFileName; }

    public void handle( ZConsumer<ByteBuffer> encoder, int roughLen ) {
        doHandle( () -> writeBuf( encoder, roughLen ), roughLen );
    }

    public void open() {

        if ( _stream != null ) {
            closeStream();
        }

        if ( _dateRoll ) {
            _dateRoll   = false;
            _rollNumber = 0;
        }

        _curFileName = _fileNameGenerator.formName( _baseFileName, ++_rollNumber, _fileExtension );

        try {

            _console.info( "BufferedRollingFileWriter opening " + _curFileName );

            _stream = FileUtils.bufFileOutStream( _curFileName, 256 * 1024 );

        } catch( Exception e ) {
            throw new RuntimeException( "BufferedFileWriter.open error creating file " + _curFileName, e );
        }
    }

    public void write( ZString buf ) throws IOException {
        doHandle( buf.getBytes(), buf.getOffset(), buf.length() );
    }

    private int blockWriteToDisk() {

        int eventSize = 0;

        try {
            _directBlockBuf.flip();
            eventSize = _directBlockBuf.limit();

            if ( eventSize > 0 ) {
                if ( _stream != null ) {
                    _stream.write( _directBlockBuf.array(), 0, eventSize );
                } else {
                    _console.log( Level.debug, FILE_APPENDER_ALREADY_CLOSED + " " + _curFileName + ", bytes=" + eventSize );
                    if ( !_curFileName.contains( "gz" ) ) {
                        _console.infoHuge( _directBlockBuf );
                    }
                }
            }

            _fileSize += eventSize;

            _directBlockBuf.clear();

            if ( _fileSize > _maxFileSize ) {
                rollFile();
            }

        } catch( Exception e ) {
            _console.error( ERR_BLOCK_WRITE, " " + _curFileName + " : exception=" + e.getClass().getName() + ", errMsg=" + e.getMessage(), e );

            if ( !_curFileName.contains( ".gz" ) ) {
                _console.infoHuge( _directBlockBuf );
            }

            _directBlockBuf.clear();
        }

        return eventSize;
    }

    private void closeStream() {
        if ( _stream != null ) {
            flush();

            try {
                if ( _stream != null ) _stream.close();
            } catch( Throwable t ) {
                _console.error( ERR_CLOSE, _curFileName + ": " + t.getMessage(), t );
            }

            _console.info( "BufferedRollingFileWriter closing " + _curFileName );
            _stream = null;
        }
    }

    private void doHandle( byte buf[], int off, int len ) {

        int startPos = _directBlockBuf.position();

        try {
            if ( _dateRoll ) {
                rollFile();
            }

            while( len > 0 ) {
                if ( (len + _directBlockBuf.position()) >= _directBlockBuf.capacity() ) {
                    blockWriteToDisk();
                    startPos = _directBlockBuf.position();
                }

                int copyBytes = Math.min( _directBlockBuf.remaining(), len );

                _directBlockBuf.put( buf, off, copyBytes );

                len -= copyBytes;
                off += copyBytes;
            }

            if ( _addNewLine ) _directBlockBuf.put( (byte) 0x0A );

        } catch( Exception e ) {
            _console.error( ERR_WRITE, e.getMessage(), e );
        }
    }

    private void doHandle( Procedure p, int estimatedLen ) {
        int startPos = _directBlockBuf.position();

        try {
            if ( _dateRoll ) {
                rollFile();
            }

            if ( (estimatedLen + _directBlockBuf.position() + SAFETY_BYTES) >= _directBlockBuf.capacity() ) {
                blockWriteToDisk();
            }

            p.invoke();

        } catch( Exception e ) {

            if ( _retryOnFail ) {
                _console.info( "Failed to append log event -> will flush and retry" +
                               ", startPos=" + startPos +
                               ", curPos=" + _directBlockBuf.position() +
                               ", limit=" + _directBlockBuf.limit() +
                               ", capacity=" + _directBlockBuf.capacity() +
                               ", eventSize=" + estimatedLen +
                               ", errMsg=" + e.getMessage() );

                try {
                    _directBlockBuf.position( startPos );
                    blockWriteToDisk();
                    p.invoke();

                } catch( Exception e2 ) {
                    _console.error( CoreErrorCode.ERR_RETRY, e.getMessage(), e );
                }
            } else {
                _console.error( ERR_WRITE, e.getMessage(), e );
            }
        }
    }

    private void resize( final int size ) {
        _console.warn( "BufferedRollingFileWriter " + _baseFileName + " forced to grow buf to " + size + " bytes" );
        _directBlockBuf = ByteBuffer.allocate( size );
    }

    private void rollFile() {

        closeStream();
        String prevFile = _curFileName;
        open();

        if ( _zipOnRoll && !prevFile.endsWith( ".gz" ) ) {
            BackgroundZipper.instance().zip( prevFile );
        }

        _fileSize = 0;
    }

    private void writeBuf( ZConsumer<ByteBuffer> encoder, int len ) {
        if ( (len + _directBlockBuf.position() + SAFETY_BYTES) >= _directBlockBuf.capacity() ) {
            blockWriteToDisk();
        }

        if ( (len + SAFETY_BYTES) >= _directBlockBuf.capacity() ) {
            resize( len + SAFETY_BYTES );
        }

        encoder.accept( _directBlockBuf );

        if ( _addNewLine ) _directBlockBuf.put( (byte) 0x0A );
    }
}