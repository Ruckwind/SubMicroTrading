/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils.file;

import com.rr.core.lang.CoreErrorCode;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public abstract class BaseFileAppender implements FileLog {

    private static final Logger _log = LoggerFactory.create( BaseFileAppender.class );

    private static final ZString FILE_APPENDER_ALREADY_CLOSED = new ViewString( "FileAppender already closed" );
    private static final long    MIN_FILE_SIZE                = 10000000;
    private static final int     BUF_SIZE                     = 4 * 1024 * 1024;
    protected final ByteBuffer _directBlockBuf;
    private final String      _baseFileName;
    private final long _maxFileSize;
    private       FileChannel _channel;
    private boolean _init = false;
    private File _file;
    private FileOutputStream _stream;
    private       long _fileSize = 0;
    private int _rollNumber;

    public BaseFileAppender( String fileName, long maxFileSize ) {
        this( fileName, maxFileSize, true );
    }

    public BaseFileAppender( String fileName, long maxFileSize, boolean enforceMinFileSize ) {
        _baseFileName = fileName;

        int blockSize = AppProps.instance().getIntProperty( CoreProps.LOG_BUF_SIZE, false, BUF_SIZE );

        if ( maxFileSize < MIN_FILE_SIZE ) {
            if ( enforceMinFileSize ) {
                maxFileSize = MIN_FILE_SIZE;
            } else {
                blockSize = (int) maxFileSize;
            }
        }

        _directBlockBuf = ByteBuffer.allocateDirect( blockSize );

        _maxFileSize = maxFileSize;
    }

    @Override
    public synchronized void close() {

        if ( _channel != null ) {
            flush();

            try {
                if ( _stream != null && _stream.getFD().valid() ) _stream.close();
            } catch( Throwable t ) { /* NADA */ }

            try {
                _channel.close();
            } catch( Exception e ) { /* NADA */ }

            _channel = null;
            _stream  = null;
            _file    = null;
        }
    }

    @Override
    public final void log( byte[] buf, int offset, int len ) {
        int startPos = _directBlockBuf.position();

        try {
            doLog( buf, offset, len );

        } catch( Exception e ) {

            _log.info( "Failed to append -> will flush and retry" +
                       ", startPos=" + startPos +
                       ", curPos=" + _directBlockBuf.position() +
                       ", limit=" + _directBlockBuf.limit() +
                       ", capacity=" + _directBlockBuf.capacity() +
                       ", eventSize=" + len +
                       ", errMsg=" + e.getMessage() );

            try {
                _directBlockBuf.position( startPos );
                blockWriteToDisk();
                doLog( buf, offset, len );

            } catch( Exception e2 ) {
                _log.error( CoreErrorCode.ERR_RETRY, e.getMessage(), e );
            }
        }
    }

    @Override
    public synchronized void open() {

        if ( !_init ) {
            throw new RuntimeException( "FileAppender.open must invoke init first" );
        }

        if ( _channel != null ) {
            close();
        }

        String fileName = FileUtils.formRollableFileName( _baseFileName, ++_rollNumber, ".log" );

        try {
            FileUtils.mkDirIfNeeded( fileName );

            _file = new File( fileName );

            _stream = new FileOutputStream( _file, true );
        } catch( Exception e ) {
            throw new RuntimeException( "FileAppender.open error creating log file " + fileName, e );
        }

        _channel = _stream.getChannel();
    }

    public abstract void doLog( byte[] buf, int offset, int len );

    public final synchronized void flush() {

        if ( _directBlockBuf.position() > 0 ) {
            blockWriteToDisk();
        }

        if ( _channel != null ) {
            try {
                _channel.force( false );
            } catch( Exception e ) {
                _log.warn( "FileAppender : Failed to flush logfile :" + e.getMessage() );
            }
        }
    }

    protected final int blockWriteToDisk() {

        int eventSize = 0;

        try {
            _directBlockBuf.flip();
            eventSize = _directBlockBuf.limit();

            if ( eventSize > 0 ) {
                if ( _channel != null ) {
                    do {
                        _channel.write( _directBlockBuf );
                    } while( _directBlockBuf.hasRemaining() );
                } else {
                    _log.info( FILE_APPENDER_ALREADY_CLOSED );
                    _log.infoHuge( _directBlockBuf );
                }
            }

            _fileSize += eventSize;

            _directBlockBuf.clear();

            if ( _fileSize > _maxFileSize ) {
                synchronized( this ) {

                    close();
                    open();

                    _fileSize = 0;
                }
            }

        } catch( Exception e ) {
            _log.warn( "FileAppender: error to log : " + e.getMessage() );
            _log.infoHuge( _directBlockBuf );

            _directBlockBuf.clear();
        }

        return eventSize;
    }
}