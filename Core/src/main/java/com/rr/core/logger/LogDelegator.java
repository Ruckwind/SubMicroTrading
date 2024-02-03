/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

import com.rr.core.factories.LogEventHugeFactory;
import com.rr.core.factories.LogEventLargeFactory;
import com.rr.core.factories.LogEventSmallFactory;
import com.rr.core.lang.*;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;

import java.nio.ByteBuffer;
import java.util.TimeZone;

/**
 * a thread safe logger delegator
 * <p>
 * pay the overhead of the ThreadLocal map lookup as otherwise each LogDelegator would need its own LogEvent pool
 * which would be somewhat inefficient memory wise
 *
 * @author Richard Rose
 */
public class LogDelegator implements Logger {

    private static final ViewString NULL_MSG = new ViewString( "" );
    private static ThreadLocal<ReusableString> _stackTrace = ThreadLocal.withInitial( () -> {
        ReusableString str = new ReusableString( 1024 );
        return str;
    } );
    private static ThreadLocal<LogEventSmallFactory> _localEventSmallPool = ThreadLocal.withInitial( () -> {
        SuperPool<LogEventSmall> sp   = SuperpoolManager.instance().getSuperPool( LogEventSmall.class );
        LogEventSmallFactory     pool = new LogEventSmallFactory( sp );
        return pool;
    } );
    private static ThreadLocal<LogEventLargeFactory> _localEventLargePool = ThreadLocal.withInitial( () -> {
        SuperPool<LogEventLarge> sp   = SuperpoolManager.instance().getSuperPool( LogEventLarge.class );
        LogEventLargeFactory     pool = new LogEventLargeFactory( sp );
        return pool;
    } );
    private static ThreadLocal<LogEventHugeFactory> _localEventHugePool = ThreadLocal.withInitial( () -> {
        SuperPool<LogEventHuge> sp   = SuperpoolManager.instance().getSuperPool( LogEventHuge.class );
        LogEventHugeFactory     pool = new LogEventHugeFactory( sp );
        return pool;
    } );
    private final transient Appender _appender;
    private final Class<?> _tagClass;
    private       Level    _lvl;
    private       TimeZone _localTZ;

    public LogDelegator( Appender appender, Level lvl, Class<?> tagClass ) {
        _appender = appender;
        _lvl      = lvl;
        _tagClass = tagClass;
    }

    @Override public void error( ErrorCode code, ZString msg ) {

        if ( ErrorMsgFilter.shouldDowngradeToTrace( msg ) ) {
            warn( msg );
        } else {
            LogEvent e = _localEventHugePool.get().get();

            populateErrorEvent( e, code, msg );

            dispatchEvent( e );

            ErrorController.error( msg );
        }
    }

    @Override public void error( ErrorCode code, ZString msg, Throwable t ) {

        if ( ErrorMsgFilter.shouldDowngradeToTrace( msg ) ) {
            warn( msg );
        } else {
            LogEvent e = _localEventHugePool.get().get();

            populateThrowableEvent( e, code, msg, t );

            dispatchEvent( e );

            ErrorController.error( msg, t );
        }
    }

    @Override public void error( ErrorCode code, String msg ) {

        if ( ErrorMsgFilter.shouldDowngradeToTrace( msg ) ) {
            warn( msg );
        } else {
            LogEvent e = _localEventHugePool.get().get();

            e.setError( code, msg );

            dispatchEvent( e );

            ErrorController.error( msg );
        }
    }

    @Override public void error( ErrorCode code, String msg, Throwable t ) {
        if ( ErrorMsgFilter.shouldDowngradeToTrace( msg ) ) {
            warn( msg );
        } else {
            LogEvent e = _localEventHugePool.get().get();

            populateThrowableEvent( e, code, msg, t );

            dispatchEvent( e );

            ErrorController.error( msg, t );
        }
    }

    @Override public void errorHuge( ErrorCode code, String msg ) {
        if ( ErrorMsgFilter.shouldDowngradeToTrace( msg ) ) {
            warn( msg );
        } else {
            LogEvent e = _localEventHugePool.get().get();

            populateErrorEvent( e, code, msg );

            dispatchEvent( e );

            ErrorController.error( msg );
        }
    }

    @Override public void errorLarge( ErrorCode code, String msg ) {
        if ( ErrorMsgFilter.shouldDowngradeToTrace( msg ) ) {
            warn( msg );
        } else {
            LogEvent e = _localEventLargePool.get().get();

            populateErrorEvent( e, code, msg );

            dispatchEvent( e );

            ErrorController.error( msg );
        }
    }

    @Override public void errorLarge( ErrorCode code, ZString msg ) {
        if ( ErrorMsgFilter.shouldDowngradeToTrace( msg ) ) {
            warn( msg );
        } else {
            LogEvent e = _localEventLargePool.get().get();

            populateErrorEvent( e, code, msg );

            dispatchEvent( e );

            ErrorController.error( msg );
        }
    }

    @Override public Level getLowestLevel() {
        return _lvl;
    }

    @Override public Class<?> getTagClass() { return _tagClass; }

    @Override public void info( String msg ) {
        if ( isEnabledFor( Level.info ) ) {
            LogEvent e = _localEventSmallPool.get().get();

            populateEvent( e, Level.info, msg );

            dispatchEvent( e );
        }
    }

    @Override public void info( ZString msg ) {
        if ( isEnabledFor( Level.info ) ) {
            LogEvent e = _localEventSmallPool.get().get();

            populateEvent( e, Level.info, msg );

            dispatchEvent( e );
        }
    }

    @Override public void infoHuge( ZString msg ) {
        if ( isEnabledFor( Level.info ) ) {
            LogEvent e = _localEventHugePool.get().get();

            populateEvent( e, Level.info, msg );

            dispatchEvent( e );
        }
    }

    @Override public void infoHuge( byte[] buf, int offset, int len ) {
        if ( isEnabledFor( Level.info ) ) {
            LogEvent e = _localEventHugePool.get().get();

            e.set( Level.info, buf, offset, len );

            dispatchEvent( e );
        }
    }

    @Override public void infoHuge( ByteBuffer buf ) {
        if ( isEnabledFor( Level.info ) ) {
            LogEvent e = _localEventHugePool.get().get();

            e.set( Level.info, buf );

            dispatchEvent( e );
        }
    }

    @Override public void infoLarge( ZString msg ) {
        if ( isEnabledFor( Level.info ) ) {
            LogEvent e = _localEventLargePool.get().get();

            populateEvent( e, Level.info, msg );

            dispatchEvent( e );
        }
    }

    @Override public void infoLarge( byte[] buf, int offset, int len ) {
        if ( isEnabledFor( Level.info ) ) {
            LogEvent e = _localEventLargePool.get().get();

            e.set( Level.info, buf, offset, len );

            dispatchEvent( e );
        }
    }

    @Override public void infoLargeAsHex( ZString msg, int hexStartOffset ) {
        if ( isEnabledFor( Level.info ) ) {
            LogEventLarge e = _localEventLargePool.get().get();

            e.set( Level.info, msg, hexStartOffset );

            dispatchEvent( e );
        }
    }

    @Override public boolean isEnabledFor( final Level level ) {
        return (level.ordinal() >= _lvl.ordinal());
    }

    @Override public void log( Level level, String msg ) {
        if ( isEnabledFor( level ) ) {
            LogEvent e = _localEventSmallPool.get().get();

            populateEvent( e, level, msg );

            dispatchEvent( e );

            if ( level == Level.ERROR ) {
                ErrorController.error( msg );
            }
        }
    }

    @Override public void log( Level level, ZString msg ) {
        if ( isEnabledFor( level ) ) {
            LogEvent e = _localEventSmallPool.get().get();

            populateEvent( e, level, msg );

            dispatchEvent( e );
        }
    }

    @Override public void log( final Level level, final String msg, final LoggerArgs customLogArgs ) {
        ReusableString s = TLC.strPop();
        s.copy( msg );
        log( level, s, customLogArgs );
        TLC.strPush( s );
    }

    @Override public void log( final Level level, final ZString msg, final LoggerArgs customLogArgs ) {
        if ( isEnabledFor( level ) ) {

            if ( customLogArgs == null ) {
                log( level, msg, customLogArgs );
                return;
            }

            LogEvent e = _localEventSmallPool.get().get();

            populateEvent( e, level, msg );

            e.setCustomLogArgs( customLogArgs );

            dispatchEvent( e );
        }
    }

    @Override public void logHuge( Level level, ZString msg ) {
        if ( isEnabledFor( level ) ) {
            LogEvent e = _localEventHugePool.get().get();

            populateEvent( e, level, msg );

            dispatchEvent( e );
        }
    }

    @Override public void logLarge( Level level, ZString msg ) {
        if ( isEnabledFor( level ) ) {
            LogEvent e = _localEventLargePool.get().get();

            populateEvent( e, level, msg );

            dispatchEvent( e );
        }
    }

    @Override public void setLevel( final Level lvl ) {
        _lvl = lvl;
    }

    @Override public void setTimeZone( final TimeZone timeZone ) { _localTZ = timeZone; }

    @Override public void warn( ZString msg ) {
        LogEvent e = _localEventSmallPool.get().get();

        populateEvent( e, Level.WARN, msg );

        dispatchEvent( e );
    }

    @Override public void warn( String msg ) {
        LogEvent e = _localEventSmallPool.get().get();

        populateEvent( e, Level.WARN, msg );

        dispatchEvent( e );
    }

    @Override public void warnHuge( String msg ) {
        LogEvent e = _localEventHugePool.get().get();

        populateEvent( e, Level.WARN, msg );

        dispatchEvent( e );
    }

    @Override public void warnLarge( String msg ) {
        LogEvent e = _localEventLargePool.get().get();

        populateEvent( e, Level.WARN, msg );

        dispatchEvent( e );
    }

    private void dispatchEvent( final LogEvent e ) {
        if ( _localTZ != null ) {
            e.setLocalTimeZone( _localTZ );
        }

        _appender.handle( e );
    }

    private void populateErrorEvent( LogEvent e, ErrorCode code, String msg ) {

        if ( msg == null ) {
            e.setError( code, NULL_MSG.getBytes(), 0, NULL_MSG.length() );
        } else {
            e.setError( code, msg.getBytes(), 0, msg.length() );
        }
    }

    private void populateErrorEvent( LogEvent e, ErrorCode code, ZString msg ) {

        if ( msg == null ) {
            e.setError( code, NULL_MSG.getBytes(), 0, NULL_MSG.length() );
        } else {
            e.setError( code, msg.getBytes(), msg.getOffset(), msg.length() );
        }
    }

    private void populateEvent( LogEvent e, Level lvl, ZString msg ) {

        if ( msg == null ) {
            e.set( lvl, NULL_MSG.getBytes(), 0, NULL_MSG.length() );
        } else {
            e.set( lvl, msg.getBytes(), msg.getOffset(), msg.length() );
        }
    }

    private void populateEvent( LogEvent e, Level lvl, String msg ) {

        if ( msg == null ) {
            e.set( lvl, NULL_MSG.getBytes(), 0, NULL_MSG.length() );
        } else {
            e.set( lvl, msg );
        }
    }

    private void populateThrowableEvent( LogEvent e, ErrorCode code, ZString msg, Throwable t ) {

        ReusableString stackTrace = _stackTrace.get();

        stackTrace.reset();

        ExceptionTrace.getStackTrace( stackTrace, t );

        if ( msg == null ) {
            e.setError( code, stackTrace.getBytes(), 0, stackTrace.length() );
        } else {
            e.setError( code, msg.getBytes(), msg.getOffset(), msg.length(), stackTrace.getBytes(), 0, stackTrace.length() );
        }
    }

    private void populateThrowableEvent( LogEvent e, ErrorCode code, String msg, Throwable t ) {

        ReusableString stackTrace = _stackTrace.get();

        stackTrace.reset();

        ExceptionTrace.getStackTrace( stackTrace, t );

        if ( msg == null ) {
            e.setError( code, stackTrace.getBytes(), 0, stackTrace.length() );
        } else {
            e.setError( code, msg.getBytes(), 0, msg.length(), stackTrace.getBytes(), 0, stackTrace.length() );
        }
    }
}
