/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.cloud.MonitoredResource;
//import com.google.cloud.logging.*;

import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.SMTRuntimeException;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;

public final class GoogleLogAppender implements Appender {

    private static final Logger _log = ConsoleFactory.console( GoogleLogAppender.class, Level.info );

    private static final ErrorCode ERR_ENCODING = new ErrorCode( "GLA100", "Error encoding / sending event for gmail " );

    private final String  _fileName;
    private final String  _jsonCred;
    private       boolean _init = false;

    private ReusableString _msgBody = new ReusableString( 1024 );
    private Appender       _chainAppender;
    private ByteBuffer     _directBlockBuf;
//    private Logging        _logging;

    public GoogleLogAppender( String fileName, String jsonGCloudCred ) {

        _fileName = fileName;

        _jsonCred = jsonGCloudCred;

        _directBlockBuf = ByteBuffer.allocate( 8192 );
    }

    @Override
    public synchronized void init( Level level ) {
//        if ( !_init ) {
//            try ( InputStream stream = FileUtils.bufFileInpStream( _jsonCred ) ) {
//
//                GoogleCredentials credentials = GoogleCredentials.fromStream( stream );
//
//                final LoggingOptions.Builder builder = LoggingOptions.newBuilder();
//                builder.setCredentials( credentials );
//                LoggingOptions loggingOptions = builder.build();
//
//                _logging = loggingOptions.getService();
//            } catch( Exception e ) {
//                throw new SMTRuntimeException( "GoogleLogAppender error " + e.getMessage(), e );
//            }
//
//            // Writes the logger entry asynchronously
//            _init = true;
//        }
    }

    @Override public synchronized void open()                  { /* nothing */ }

    @Override public synchronized void close() {
//        _logging.flush();
    }

    @Override public synchronized void flush() {
//        _logging.flush();
        if ( _chainAppender != null ) _chainAppender.flush();
    }

    @Override public void handle( LogEvent curEvent ) {
//        Level level = curEvent.getLevel();
//        if ( level.ordinal() >= Level.WARN.ordinal() ) {
//
//            _msgBody.reset();
//            _directBlockBuf.clear();
//
//            try {
//                curEvent.encode( _directBlockBuf );
//
//                LogEntry entry;
//
//                synchronized( this ) {
//                    _msgBody.append( _directBlockBuf.array(), 0, _directBlockBuf.position() ).append( "\n\n" );
//
//                    entry = LogEntry.newBuilder( Payload.StringPayload.of( _msgBody.toString() ) )
//                                    .setSeverity( (level == Level.ERROR) ? Severity.ERROR : Severity.WARNING )
//                                    .setLogName( _fileName )
//                                    .setResource( MonitoredResource.newBuilder( "global" ).build() )
//                                    .build();
//                }
//
//                _logging.write( Collections.singleton( entry ) );
//            } catch( Exception e ) {
//                _log.error( ERR_ENCODING, " event : " + curEvent.toString(), e );
//            }
//        }

        if ( _chainAppender != null ) {
            _chainAppender.handle( curEvent );
        } else {
            LogEventRecyler.recycle( curEvent );
        }
    }

    @Override public boolean isEnabledFor( final Level level ) { return (level.ordinal() >= Level.WARN.ordinal()); }

    @Override public void chain( final Appender dest )         { _chainAppender = dest; }

    @Override public String toString() {
        return "GoogleLogAppender " + _fileName;
    }

    public String getFileName() { return _fileName; }
}