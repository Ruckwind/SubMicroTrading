/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

import com.rr.core.lang.ErrorCode;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.file.BufferedRollingFileWriter;

public final class LogEventFileAppender implements Appender {

    private static final Logger    _console  = ConsoleFactory.console( LogEventFileAppender.class );
    private static final ErrorCode ERR_FLUSH = new ErrorCode( "LFA100", "Error in flush" );

    private static final long MIN_FILE_SIZE = 100000000;
    private static final int  BUF_SIZE      = 4 * 1024 * 1024;

    private int     _blockSize;
    private String  _baseFileName;
    private boolean _init = false;

    private BufferedRollingFileWriter _stream;

    private Level    _logLevel = Level.info;
    private Appender _chainAppender;

    public LogEventFileAppender( String fileName, long maxFileSize, boolean compress ) {
        this( fileName, maxFileSize, true, compress );
    }

    public LogEventFileAppender( String fileName, long maxFileSize, boolean enforceMinFileSize, boolean compress ) {
        _baseFileName = fileName;

        if ( _baseFileName.endsWith( ".gz" ) ) compress = true;

        _blockSize = BUF_SIZE;

        if ( maxFileSize < MIN_FILE_SIZE ) {
            if ( enforceMinFileSize ) {
                maxFileSize = MIN_FILE_SIZE;
            } else {
                _blockSize = (int) maxFileSize;
            }
        }

        String fileExtension = (compress) ? ".log.gz" : ".log";

        _stream = new BufferedRollingFileWriter( _baseFileName, fileExtension, FileUtils::formRollableFileName, maxFileSize, _blockSize, true, true );
    }

    @Override public void chain( final Appender dest ) {
        _chainAppender = dest;
    }

    @Override
    public synchronized void close() {
        _stream.close();
    }

    @Override public synchronized void flush() {
        try {
            _stream.flush();
        } catch( Exception e ) {
            _console.error( ERR_FLUSH, _baseFileName + " " + e.getClass().getSimpleName() + " " + e.getMessage(), e );
        }

        if ( _chainAppender != null ) _chainAppender.flush();
    }

    @Override public void handle( LogEvent curEvent ) {
        if ( isLoggingEnabled( curEvent ) ) {
            _stream.handle( ( buf ) -> curEvent.encode( buf ), curEvent.length() );

            if ( curEvent.getLevel() == Level.ERROR || curEvent.getLevel() == Level.WARN ) {
                flush(); // FORCE FLUSH ON ERROR
            }
        }

        if ( _chainAppender != null ) {
            _chainAppender.handle( curEvent );
        } else {
            LogEventRecyler.recycle( curEvent );
        }
    }

    @Override
    public synchronized void init( Level level ) {
        if ( !_init ) {
            _logLevel = level;
            _init     = true;
        }
    }

    @Override public boolean isEnabledFor( final Level level ) {
        return (level.ordinal() >= _logLevel.ordinal());
    }

    @Override public synchronized void open() { _stream.open(); }

    @Override public String toString() {
        return "LogEventFileAppender " + _stream.toString();
    }

    public void dateRoll() { _stream.dateRoll(); }

    public String getFileName() { return _stream.getFileName(); }

    private boolean isLoggingEnabled( final LogEvent event ) {
        return (event.getLevel().ordinal() >= _logLevel.ordinal());
    }
}