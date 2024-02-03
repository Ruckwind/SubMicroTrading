/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ZString;
import com.rr.core.utils.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

import java.nio.ByteBuffer;
import java.util.TimeZone;

public class ApacheLogger implements com.rr.core.logger.Logger {

    static {
        org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
//        Inlined from BasicConfigurator.configure();
//        changed to use %d so we get a date timestamp, this should all be in a log4j file though
//        root.addAppender(new ConsoleAppender( new PatternLayout( "%r [%t] %p %c %x - %m%n")));
        root.addAppender( new ConsoleAppender( new PatternLayout( "%d [%t] %p %c %x - %m%n" ) ) );
    }

    private final Class<?> _tagClass;
    private final byte[]     _tmpData = new byte[ 8192 ];
    private final ByteBuffer _tmpBuf  = ByteBuffer.wrap( _tmpData );
    private org.apache.log4j.Logger _log;

    public static ApacheLogger getApacheLogger( Class<?> aClass, Level lvl ) {
        return new ApacheLogger( org.apache.log4j.Logger.getLogger( aClass ), lvl, aClass );
    }

    public static void forceLevel( final Level level ) {
        org.apache.log4j.Level al = getApacheLevel( level );
        org.apache.log4j.Logger.getRootLogger().setLevel( al );
    }

    private static org.apache.log4j.Level getApacheLevel( final Level level ) {
        org.apache.log4j.Level al = org.apache.log4j.Level.INFO;

        switch( level ) {
        case xtrace:
        case trace:
            al = org.apache.log4j.Level.TRACE;
            break;
        case debug:
            al = org.apache.log4j.Level.DEBUG;
            break;
        case info:
            al = org.apache.log4j.Level.INFO;
            break;
        case WARN:
            al = org.apache.log4j.Level.WARN;
            break;
        case ERROR:
            al = org.apache.log4j.Level.ERROR;
            break;
        }
        return al;
    }

    public ApacheLogger( org.apache.log4j.Logger logger, Level lvl, final Class<?> aClass ) {
        _log = logger;
        _log.setLevel( getApacheLevel( lvl ) );
        _tagClass = aClass;
    }

    @Override public void error( ErrorCode code, ZString msg ) {
        _log.error( code.getError() + " " + msg.toString() );
        ErrorController.error( msg );
    }

    @Override public void error( ErrorCode code, ZString msg, Throwable t ) {
        _log.error( code.getError() + " " + msg.toString(), t );
        ErrorController.error( msg, t );
    }

    @Override public void error( ErrorCode code, String msg ) {
        _log.error( code.getError() + " " + msg );
        ErrorController.error( msg );
    }

    @Override public void error( ErrorCode code, String msg, Throwable t ) {
        _log.error( code.getError() + " " + msg, t );
        ErrorController.error( msg, t );
    }

    @Override public void errorHuge( ErrorCode code, String msg ) {
        _log.error( code.getError() + msg );
        ErrorController.error( msg );
    }

    @Override public void errorLarge( ErrorCode code, String msg ) {
        _log.error( code.getError() + msg );
        ErrorController.error( msg );
    }

    @Override public void errorLarge( ErrorCode code, ZString msg ) {
        _log.error( code.getError() + msg.toString() );
        ErrorController.error( msg );
    }

    @Override public Level getLowestLevel() {
        if ( _log.isTraceEnabled() ) return Level.trace;
        if ( _log.isDebugEnabled() ) return Level.debug;
        if ( _log.isInfoEnabled() ) return Level.info;

        return Level.high;
    }

    @Override public Class<?> getTagClass() { return _tagClass; }

    @Override public void info( String msg ) {
        _log.info( msg );
    }

    @Override public void info( ZString msg ) {
        _log.info( msg );
    }

    @Override public void infoHuge( ZString msg ) {
        _log.info( msg );
    }

    @Override public void infoHuge( byte[] buf, int offset, int len ) {
        _log.info( new String( buf, offset, len ) );
    }

    @Override public void infoHuge( ByteBuffer buf ) {
        String text = StringUtils.byteBufToString( buf );
        _log.info( text );
    }

    @Override public void infoLarge( ZString msg ) {
        _log.info( msg );
    }

    @Override public void infoLarge( byte[] buf, int offset, int len ) {
        _log.info( new String( buf, offset, len ) );
    }

    @Override public void infoLargeAsHex( ZString msg, int hexStartOffset ) {
        LogEventLarge e = new LogEventLarge();

        e.set( Level.info, msg, hexStartOffset );

        String s;

        synchronized( ApacheLogger.class ) {
            _tmpBuf.clear();
            _tmpBuf.put( (byte) 0x0A );
            e.encode( _tmpBuf );
            _tmpBuf.put( (byte) 0x0A );
            s = new String( _tmpData, 0, _tmpBuf.limit() );
            _log.info( s );
        }
    }

    @Override public boolean isEnabledFor( final Level level ) {
        switch( level ) {
        case xtrace:
        case trace:
            return _log.isTraceEnabled();
        case debug:
            return _log.isDebugEnabled();
        case info:
            return _log.isInfoEnabled();
        }
        return true;
    }

    @Override public void log( final Level level, final String msg ) {
        switch( level ) {
        case trace:
            _log.trace( msg );
            break;
        case debug:
            _log.debug( msg );
            break;
        case info:
            _log.info( msg );
            break;
        case high:
        case WARN:
            _log.warn( msg );
            break;
        case ERROR:
            _log.error( msg );
            ErrorController.error( msg );
            break;
        }
    }

    @Override public void log( final Level level, final ZString msg ) {
        switch( level ) {
        case xtrace:
        case trace:
            _log.trace( msg );
            break;
        case debug:
            _log.debug( msg );
            break;
        case info:
            _log.info( msg );
            break;
        case WARN:
            _log.warn( msg );
            break;
        case ERROR:
            _log.error( msg );
            ErrorController.error( msg );
            break;
        }
    }

    @Override public void log( final Level level, final String msg, final LoggerArgs customLogArgs ) {
        log( level, msg );
    }

    @Override public void log( final Level level, final ZString msg, final LoggerArgs customLogArgs ) {
        log( level, msg );
    }

    @Override public void logHuge( final Level level, final ZString msg ) {
        log( level, msg );
    }

    @Override public void logLarge( final Level level, final ZString msg ) {
        log( level, msg );
    }

    @Override public void setLevel( final Level lvl ) {
        _log.setLevel( getApacheLevel( lvl ) );
    }

    @Override public void setTimeZone( final TimeZone timeZone ) { /* not supported */ }

    @Override public void warn( ZString msg ) {
        _log.warn( msg );
    }

    @Override public void warn( String msg ) {
        _log.warn( msg );
    }

    @Override public void warnHuge( String msg ) {
        _log.warn( msg );
    }

    @Override public void warnLarge( String msg ) {
        _log.warn( msg );
    }

    @Override public String toString() {
        return "ApacheLogger{" + "_tagClass=" + _tagClass.getSimpleName() + '}';
    }
}
