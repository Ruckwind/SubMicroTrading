/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

import com.rr.core.lang.Env;
import com.rr.core.lang.TLC;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.utils.FileUtils;
import com.rr.core.utils.StringUtils;
import com.rr.core.utils.ThreadUtilsFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class LoggerFactory {

    private static Level                        _level            = Level.info; // MUST BE FIRST
    private static Logger                       _console;
    private static boolean                      _forceConsole     = false; // in debug mode all loggers are console
    private static LinkedHashMap<String, Level> _overridePatterns = new LinkedHashMap<>();
    private static List<Logger>                 _allLoggers       = Collections.synchronizedList( new ArrayList<>() );
    private static LogEventEmailAppender        _emailAppender;
    private static GoogleLogAppender            _googleApender;
    private static long                         _maxFileSize;
    private static int                          _maxLoggerQueue;
    private static String                       _dirName;
    private static int                          _logFlushMS       = Logger.DEFAULT_FLUSH_INT_MS;
    private static ConcurrentHashMap<String, AsyncAppenderWrapper> _appenderMap = new ConcurrentHashMap<>( 128 );
    private static AsyncAppenderWrapper _rootAppender = null;

    private static class AsyncAppenderWrapper {

        private boolean              _init    = false;
        private boolean              _running = false;
        private String               _fileName;
        private LogEventFileAppender _fileAppender;
        private AsyncAppender        _asyncAppender;
    }

    public static Level forceLevel( Level level ) {
        ApacheLogger.forceLevel( level );
        final Level was = level;
        _level = level;
        return was;
    }

    public static synchronized void initLogging( String fileName, long maxFileSize, Level level ) {
        initLogging( fileName, maxFileSize, level, 131072, null, Logger.DEFAULT_FLUSH_INT_MS );
    }

    public static synchronized void initLogging( String fileName, long maxFileSize, Level level, int maxQueue ) {
        initLogging( fileName, maxFileSize, level, maxQueue, null, Logger.DEFAULT_FLUSH_INT_MS );
    }

    public static synchronized void initLogging( String fileName, long maxFileSize, Level level, int maxQueue, String[] overrides ) { initLogging( fileName, maxFileSize, level, maxQueue, overrides, Logger.DEFAULT_FLUSH_INT_MS ); }

    public static synchronized void initLogging( String fileName, long maxFileSize, Level level, int maxQueue, String[] overrides, int logFlushMS ) {

        _level      = level;
        _logFlushMS = logFlushMS;

        if ( _console == null ) _console = ConsoleFactory.console( LoggerFactory.class, level );

        _dirName = FileUtils.getDirName( fileName );

        forceLevel( level );
        setOverrides( overrides );

        _maxFileSize    = maxFileSize;
        _maxLoggerQueue = maxQueue;

        _rootAppender = getAppenderWrapper( fileName, true, _maxLoggerQueue, logFlushMS, fileName.endsWith( ".gz" ) );
    }

    private static synchronized AsyncAppenderWrapper getAppenderWrapper( String fileName, boolean forceReOpen, int maxLogQueueSize, int logFlushMS, final boolean compress ) {

        AppProps props           = AppProps.instance();
        boolean  forceOneLogFile = props.getBooleanProperty( CoreProps.FORCE_SINGLE_LOG, false, false );

        if ( forceOneLogFile && _rootAppender != null ) {
            return _rootAppender;
        }

        AsyncAppenderWrapper appenderWrapper = _appenderMap.get( fileName );

        if ( appenderWrapper == null ) {
            appenderWrapper = new AsyncAppenderWrapper();

            if ( !fileName.contains( "/" ) ) {
                fileName = _dirName + "/" + fileName;
            }

            appenderWrapper._fileName = fileName;

            _appenderMap.put( fileName, appenderWrapper );

        } else {
            if ( forceReOpen && appenderWrapper._init ) {

                appenderWrapper._asyncAppender.wakeup();
                ThreadUtilsFactory.get().sleep( 10 ); // give queue chance to flush
                appenderWrapper._fileAppender.open();

                return appenderWrapper;
            }
        }

        if ( !appenderWrapper._init ) {
            appenderWrapper._init = true;

            Level fileAppenderLevel = getLevel( LogEventFileAppender.class );

            appenderWrapper._fileAppender = new LogEventFileAppender( fileName, _maxFileSize, compress );
            appenderWrapper._fileAppender.init( fileAppenderLevel );

            Level asyncAppenderLevel = getLevel( AsyncAppender.class );

            final String baseFileName = FileUtils.getBaseName( fileName );

            appenderWrapper._asyncAppender = new AsyncAppender( appenderWrapper._fileAppender, "AsyncAppender" + baseFileName, true, maxLogQueueSize, logFlushMS );
            appenderWrapper._asyncAppender.init( asyncAppenderLevel );

            attachEmailAppender( appenderWrapper );

            if ( !appenderWrapper._running ) {

                appenderWrapper._asyncAppender.open();
                appenderWrapper._running = true;
            }
        }

        return appenderWrapper;
    }

    public static void dateRoll() {
        for ( AsyncAppenderWrapper appenderWrapper : _appenderMap.values() ) {
            if ( appenderWrapper._init ) {
                appenderWrapper._fileAppender.dateRoll();
            }
        }
    }

    public static void prepare() {
        // email appender uses scheduler so have to wait for that to be init before used
        if ( _emailAppender != null ) _emailAppender.open();
    }

    private static void attachEmailAppender( final AsyncAppenderWrapper appenderWrapper ) {
        AppProps props             = AppProps.instance();
        boolean  enableEmailErrors = props.getBooleanProperty( "app.emailErrors", false, false );

        Env env = props.getProperty( CoreProps.RUN_ENV, Env.class );
        if ( enableEmailErrors && (env.isProd() || Env.UAT == env) ) {
            if ( _emailAppender == null ) {
                final String   receivers     = props.getProperty( "run.emailRecipients" );
                final String[] receiverList  = receivers.split( "," );
                final int      batchSizeSecs = props.getIntProperty( "run.emailBatchSizeSecs", false, 60 );
                String         appName       = props.getProperty( CoreProps.APP_NAME );
                String         envStr        = env.toString();
                final String   subject       = appName + "." + envStr + "." + "Mailer";

                _emailAppender = new LogEventEmailAppender( receiverList, batchSizeSecs, subject );
                _emailAppender.init( Level.vhigh );

                if ( AppProps.instance().getBooleanProperty( "run.cloudLoggingEnabled", false, false ) ) {
                    final String cloudLogName  = appName + "." + envStr;
                    final String cloudAuthJSON = AppProps.instance().getProperty( "run.cloudLogAuthJSON" );
                    _googleApender = new GoogleLogAppender( cloudLogName, cloudAuthJSON );
                    _googleApender.init( Level.WARN );

                    _emailAppender.chain( _googleApender );
                }
            }
            appenderWrapper._fileAppender.chain( _emailAppender );
        }
    }

    /**
     * create a thread safe logger
     *
     * @param aClass
     * @return
     */
    public static synchronized com.rr.core.logger.Logger createSync( Class<?> aClass ) {

        if ( _forceConsole ) return ConsoleFactory.console( aClass, _level );

        if ( _rootAppender == null || !_rootAppender._init ) throw new RuntimeException( "LoggerFactory() must initialise before creating non console loggers" );

        Level lvl = getLevel( aClass );

        Logger l = new com.rr.core.logger.LogDelegator( _rootAppender._asyncAppender, lvl, aClass );
        _allLoggers.add( l );
        return l;
    }

    /**
     * should be invoked withing context of required control thread, eg within run method or threadedInit call
     *
     * @param aClass
     * @return instance of logger for specified class, obtained using current threads TLC
     */
    public static synchronized com.rr.core.logger.Logger getThreadLocal( Class<?> aClass ) {
        if ( _forceConsole ) return ConsoleFactory.console( aClass, _level );

        if ( _rootAppender == null || !_rootAppender._init ) throw new RuntimeException( "LoggerFactory() must initialise before creating non console loggers" );

        final Level lvl = getLevel( aClass );

        Class<?>[] pClass = { Appender.class, Level.class, Class.class };
        Object[]   pArgs  = { _rootAppender._asyncAppender, lvl, aClass };

        String className = aClass.getSimpleName();
        String id        = "Logger" + className.substring( className.lastIndexOf( '.' ) + 1 );

        Logger l = TLC.instance().getInstanceOf( id, LogDelegator.class, pClass, pArgs );
        _allLoggers.add( l );

        return l;
    }

    public static synchronized com.rr.core.logger.Logger create( Class<?> aClass ) {
        if ( _forceConsole ) return ConsoleFactory.console( aClass, _level );

        if ( _rootAppender == null || !_rootAppender._init ) {
            throw new RuntimeException( "LoggerFactory() must initialise before creating non console loggers" );
        }

        Level lvl = getLevel( aClass );

        Logger l = new com.rr.core.logger.LogDelegator( _rootAppender._asyncAppender, lvl, aClass );
        _allLoggers.add( l );
        return l;
    }

    public static synchronized com.rr.core.logger.Logger create( Class<?> aClass, String fileName ) {
        return create( aClass, fileName, _maxLoggerQueue, false );
    }

    public static synchronized com.rr.core.logger.Logger create( Class<?> aClass, String fileName, boolean compress ) {
        return create( aClass, fileName, _maxLoggerQueue, compress );
    }

    public static synchronized com.rr.core.logger.Logger create( Class<?> aClass, String fileName, int maxLogQueueSize ) {
        return create( aClass, fileName, maxLogQueueSize, false );
    }

    public static synchronized com.rr.core.logger.Logger create( Class<?> aClass, String fileName, int maxLogQueueSize, boolean compress ) {

        if ( _forceConsole ) return ConsoleFactory.console( aClass, _level );

        if ( _rootAppender == null || !_rootAppender._init ) throw new RuntimeException( "LoggerFactory() must initialise before creating non console loggers" );

        fileName = getFileName( fileName );

        final AsyncAppenderWrapper appender = getAppenderWrapper( fileName, false, maxLogQueueSize, _logFlushMS, compress );

        Level lvl = getLevel( aClass );

        Logger l = new com.rr.core.logger.LogDelegator( appender._asyncAppender, lvl, aClass );
        _allLoggers.add( l );
        return l;
    }

    private static String getFileName( String fileName ) {
        String dirName  = FileUtils.getDirName( fileName );
        String baseName = FileUtils.getBaseName( fileName );

        if ( dirName == null ) dirName = _dirName;

        fileName = dirName + "/" + AppProps.instance().getProperty( CoreProps.APP_NAME, false, "Unknown" ) + "_" + baseName;

        return fileName;
    }

    public static boolean setForceConsole( boolean isDebug ) {
        boolean was = _forceConsole;
        _forceConsole = isDebug;
        return was;
    }

    public static void flush() {
        for ( AsyncAppenderWrapper appenderWrapper : _appenderMap.values() ) {
            if ( appenderWrapper._asyncAppender != null ) appenderWrapper._asyncAppender.flush();
        }

        ThreadUtilsFactory.getLive().sleep( 200 ); // chance to drain logger queue

        for ( AsyncAppenderWrapper appenderWrapper : _appenderMap.values() ) {
            if ( appenderWrapper._fileAppender != null ) appenderWrapper._fileAppender.flush();
        }
    }

    private static Level getLevel( final Class<?> c ) {

        if ( c == null ) return Level.info;

        String name = c.getName().toLowerCase();

        if ( Appender.class.isAssignableFrom( c ) ) {
            if ( _overridePatterns != null ) {
                for ( Map.Entry<String, Level> e : _overridePatterns.entrySet() ) {
                    if ( name.equals( e.getKey() ) ) {
                        return e.getValue();
                    }
                }
            }
            return Level.xtrace; // default allow everything thru without explicit rule
        }

        if ( _overridePatterns != null ) {
            for ( Map.Entry<String, Level> e : _overridePatterns.entrySet() ) {
                if ( name.startsWith( e.getKey() ) ) {
                    return e.getValue();
                }
            }
        }

        return _level;
    }

    private static void setOverrides( final String[] overridePatterns ) {

        if ( overridePatterns == null ) return;

        final TreeSet<String> tmpSet = new TreeSet<>( ( o1, o2 ) -> {

            int lenComp = o2.length() - o1.length();

            if ( lenComp != 0 ) return lenComp;

            return o1.compareTo( o2 );
        } );

        for ( String s : overridePatterns ) {
            tmpSet.add( s );
        }

        for ( String s : tmpSet ) {

            try {
                String[] parts = s.split( "=" );

                if ( parts.length == 2 ) {
                    Level l = StringUtils.getEnum( parts[ 1 ], Level.class );

                    String key = parts[ 0 ];

                    if ( key.startsWith( CoreProps.APP_LOG_OVERRIDES ) ) key = key.substring( CoreProps.APP_LOG_OVERRIDES.length() );

                    _overridePatterns.put( key, l );
                } else {
                    Level l = AppProps.instance().getProperty( s, Level.class );

                    if ( s.startsWith( CoreProps.APP_LOG_OVERRIDES ) ) s = s.substring( CoreProps.APP_LOG_OVERRIDES.length() );

                    _overridePatterns.put( s, l );
                }

            } catch( Exception e ) {
                _console.warn( "skipping bad log override " + s + " : " + e.getMessage() );
            }
        }

        synchronized( _allLoggers ) {
            for ( Logger l : _allLoggers ) {
                Level lvl = getLevel( l.getTagClass() );
                l.setLevel( lvl );
            }
        }
    }

    public static void shutdown() {
        flush();

        for ( AsyncAppenderWrapper appenderWrapper : _appenderMap.values() ) {
            appenderWrapper._asyncAppender.forceClose();
        }
    }
}
