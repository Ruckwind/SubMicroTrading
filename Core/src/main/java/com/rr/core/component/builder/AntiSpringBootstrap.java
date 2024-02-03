/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.component.builder;

import com.rr.core.admin.AdminAgent;
import com.rr.core.annotations.SMTPreRestore;
import com.rr.core.annotations.StandAloneThreadedInit;
import com.rr.core.component.*;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.lang.stats.StatsCfgFile;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.os.NativeHooksImpl;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.recovery.SnapshotCaretaker;
import com.rr.core.tasks.SchedulerFactory;
import com.rr.core.thread.RunState;
import com.rr.core.time.StandardTimeUtils;
import com.rr.core.utils.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Bootstraps an SMT set of components forming an application from a property file
 * <p>
 * use -Dapp.forceConsole=true   to force console logging
 * <p>
 * Sequence is as follows
 * <p>
 * instantiate all components wether via loader or direct
 * <p>
 * for each component that can : INITIALISE
 * <p>
 * for each component that can : PREPARE
 * (prepare is used for second phase initialisation that requires other components to have had phase 1 initialisation)
 * components should use this to verify required references have all been populated
 * <p>
 * for each component that can : WARMUP
 * (components may have their own warmup or register with warmupController to avoid duplication)
 * <p>
 * for each component that can : START
 */
public class AntiSpringBootstrap {

    private static final Logger    _console     = ConsoleFactory.console( AntiSpringBootstrap.class, Level.info );
    private static final ErrorCode FAILED       = new ErrorCode( "SPB100", "Exception in main" );
    private static final ErrorCode ERR_PRE_STOP = new ErrorCode( "SPB200", "Exception in preStop" );
    private static final ErrorCode ERR_STOP     = new ErrorCode( "SPB300", "Exception in stop" );
    private static final ErrorCode ERR_WARM     = new ErrorCode( "SPB400", "Exception in warmup" );
    private static final ErrorCode ERR_ON_EXIT  = new ErrorCode( "SPB500", "Processing exiting with error" );
    private static final int       ERR_EXIT     = 101;

    static Logger _log;

    private static boolean _runWarmup = true;

    private static SMTComponentManager _componentManager;

    /**
     * _restoredSnapshotStartTime is the time that the start of the restored snapshot was taken
     */
    private static long          _restoredSnapshotStartTime;
    private static AtomicBoolean _ranStopCode = new AtomicBoolean();

    public static void main( String[] args ) {

        StandardThreadUtils standardThreadUtils = new StandardThreadUtils();

        try {
            long start = System.currentTimeMillis();

            basicSetup( args );

            AppProps props   = AppProps.instance();
            String   appName = props.getProperty( CoreProps.APP_NAME );

            _componentManager = new SMTComponentManager( "componentManager" );

            SMTPropertyComponentBuilder componentBuilder = new SMTPropertyComponentBuilder( AppProps.instance(), _componentManager );

            componentBuilder.init();

            NativeHooksImpl.instance();

            initComponents();

            prepareComponents();

            if ( Utils.getExitCode() == 0 ) {

                ShutdownManager.instance().register( "AntiSpringBootstrap", AntiSpringBootstrap::preAndStopComponents, ShutdownManager.Priority.High );

                warmupComponents();

                SuperpoolManager.instance().resetPoolStats();

                Utils.invokeGC();

                startComponents();

                standaloneThreadedInit();

                _console.info( "Completed" );

                _console.info( "ENTERING MAIN LOOP - ctrl-C to stop program" );

                System.out.flush();
                System.err.flush();

                _log.info( "ENTERING MAIN LOOP - ctrl-C to stop program" );

                AppState.setState( AppState.State.Running );

                int i = 0;
                while( _componentManager.anyComponentsActive() ) {
                    standardThreadUtils.sleep( 1000 ); // REAL SLEEP

                    if ( AppState.isTerminal() && (++i % 10) == 0 ) {
                        _componentManager.logComponentsActive(); // 10 secs after stopping log active components
                    }
                }

                _log.warn( "CLOSING  " + appName + " exitCode=" + Utils.getExitCode() );
            } else {

                _log.warn( "CLOSING  " + appName + " exitCode=" + Utils.getExitCode() + " after failed init" );

                preAndStopComponents();
            }

            AdminAgent.close();

            LoggerFactory.shutdown();

            long end = System.currentTimeMillis();

            _log.info( "EXITING MAIN LOOP" );

            _console.info( "\nNo active thread components so terminating, duration " + ((end - start) / 1000) + " secs\n" );

            if ( Utils.getExitCode() != 0 ) {
                _log.error( ERR_ON_EXIT, "Process terminating with error code " + Utils.getExitCode() );
            }

            Utils.exit( 0 );

        } catch( Exception e ) {

            _console.error( FAILED, e.getMessage(), e );

            if ( _log != null ) {
                _log.error( FAILED, e.getMessage(), e );
            }

            Utils.exit( ERR_EXIT );
        }
    }

    private static void standaloneThreadedInit() {
        _console.info( "startComponents start" );

        _componentManager.setCreationPhase( CreationPhase.Runtime );

        Set<SMTComponent> components = new LinkedHashSet<>();
        _componentManager.getComponentSet( SMTComponentManager.CREATION_TIME_PRE_RUNTIME, components );

        for ( SMTComponent component : components ) {

            ReflectUtils.invokeAnnotatedMethod( component, StandAloneThreadedInit.class );
        }
    }

    public static void saveAndExit() {
        _console.info( "AntiSpringBootstrap::saveAndExit INVOKED" );
        long start = System.currentTimeMillis();
        preAndStopComponents();
        long end      = System.currentTimeMillis();
        long duration = Math.abs( end - start );
        _console.info( "AntiSpringBootstrap::saveAndExit took " + duration );
        Utils.exit( 0 );
    }

    public synchronized static void preAndStopComponents() {
        if ( _ranStopCode.compareAndSet( false, true ) ) {

            AppState.setState( AppState.State.Stopping );

            _log.info( "preAndStopComponents STARING from " + Thread.currentThread().getName() + ", isDaemon=" + Thread.currentThread().isDaemon() );
            preStopComponents( _componentManager );
            stopComponents( _componentManager );
            _log.info( "preAndStopComponents COMPLETED from " + Thread.currentThread().getName() );

        } else {
            _log.info( "preAndStopComponents IGNORED from " + Thread.currentThread().getName() + ", isDaemon=" + Thread.currentThread().isDaemon() );
        }
    }

    private static void initComponents() {
        AppState.setState( AppState.State.Initialising );

        Set<SMTComponent> components = new LinkedHashSet<>();

        _componentManager.getComponentSet( SMTComponentManager.CREATION_TIME_PRE_RUNTIME, components );

        _console.info( "initComponents started" );

        SMTStartContext context = _componentManager.getComponent( "appContext" );
        context.setComponentManager( _componentManager );

        Set<String> ids = new HashSet<>();

        int idx = 0;

        Class[]  argClasses = { SMTStartContext.class };
        Object[] argVals    = { context };

        for ( SMTComponent component : components ) {
            if ( component instanceof SMTInitialisableComponent ) {
                _console.info( "initComponents:  preRestore [" + (++idx) + "] " + component.getComponentId() );

                SMTInitialisableComponent ic = (SMTInitialisableComponent) component;

                if ( ic.getRunState() != RunState.Dead ) {

                    ReflectUtils.invokeAnnotatedMethod( component, SMTPreRestore.class, argClasses, argVals );
                }
            } else {
                ReflectUtils.invokeAnnotatedMethod( component, SMTPreRestore.class, argClasses, argVals );
            }
        }

        restore( context, components );

        _componentManager.setCreationPhase( CreationPhase.Initialisation );

        idx = 0;

        for ( SMTComponent component : components ) {
            if ( component instanceof SMTInitialisableComponent ) {
                _console.info( "initComponents:  initialising [" + (++idx) + "] " + component.getComponentId() );

                SMTInitialisableComponent ic = (SMTInitialisableComponent) component;

                if ( ic.getRunState() != RunState.Dead ) {
                    ic.init( context, CreationPhase.Initialisation );
                }

                if ( ic.getRunState() != RunState.Dead ) {
                    ids.add( component.getComponentId() );
                } else {
                    checkDropDeadComponent( ic, context );
                }

                _console.info( "initComponents:  initised [" + (++idx) + "] " + component.getComponentId() );
            }
        }

        // init components created in init phase

        Set<SMTComponent> latestComponents = new LinkedHashSet<>();
        _componentManager.getComponentSet( SMTComponentManager.CREATION_TIME_PRE_RUNTIME, latestComponents );

        for ( SMTComponent component : latestComponents ) {
            if ( !ids.contains( component.getComponentId() ) ) {
                if ( component instanceof SMTInitialisableComponent ) {
                    _console.info( "initComponents: second change : init " + component.getComponentId() );

                    SMTInitialisableComponent ic = (SMTInitialisableComponent) component;

                    ic.init( context, CreationPhase.Initialisation );

                    if ( ic.getRunState() != RunState.Dead ) {
                        ids.add( component.getComponentId() );
                    } else {
                        if ( ic.dropComponentWhenDead() ) {

                            ic.preDeadRemoval( context );

                            _componentManager.remove( ic );
                        }
                    }
                }
            }
        }

        _console.info( "initComponents completed" );
    }

    private static void checkDropDeadComponent( final SMTInitialisableComponent ic, SMTStartContext ctx ) {
        if ( ic.dropComponentWhenDead() ) {

            ic.preDeadRemoval( ctx );

            _componentManager.remove( ic );
        }
    }

    private static void restore( final SMTStartContext context, final Set<SMTComponent> components ) {
        _componentManager.setCreationPhase( CreationPhase.Recovery );

        SnapshotCaretaker ct = context.getSnapshotCaretaker();

        if ( ct != null ) {

            ct.init( context, CreationPhase.Recovery );

            Collection<Object> restoredComps = new ArrayList<>();

            _console.info( "AntiSprintBootstrap : SNAPSHOT CARETAKER " + ct.getComponentId() + " INVOKING restoreLast" );

            _restoredSnapshotStartTime = ct.restoreLast( restoredComps, context );

            context.setRestoredTimestamp( _restoredSnapshotStartTime );

            _console.info( "AntiSprintBootstrap : SNAPSHOT CARETAKER " + ct.getComponentId() + " COMPLETED restoreLast" );

        } else {
            _console.info( "AntiSprintBootstrap : NO SNAPSHOT CARETAKER CONFIGURED - no persistence/recovery" );
        }
    }

    private static void prepareComponents() {
        _console.info( "prepareComponents start" );

        AppState.setState( AppState.State.Initialising );

        LoggerFactory.prepare();

        Set<SMTComponent> components = new LinkedHashSet<>();
        _componentManager.getComponentSet( SMTComponentManager.CREATION_TIME_PRE_RUNTIME, components );

        for ( SMTComponent component : components ) {
            if ( component instanceof SMTInitialisableComponent ) {
                _console.info( "prepareComponents:  prepare " + component.getComponentId() );

                SMTInitialisableComponent ic = (SMTInitialisableComponent) component;

                ic.prepare();
            }
        }

        _console.info( "prepareComponents:  finished" );
    }

    private static void warmupComponents() {
        _runWarmup = AppProps.instance().getBooleanProperty( CoreProps.ENABLE_WARMUP, false, true );

        if ( _runWarmup ) {
            _console.info( "warmupComponents start" );

            Set<SMTComponent> components = new LinkedHashSet<>();
            _componentManager.getComponentSet( SMTComponentManager.CREATION_TIME_PRE_RUNTIME, components );

            for ( SMTComponent component : components ) {
                if ( component instanceof SMTWarmableComponent ) {
                    if ( _runWarmup ) {
                        _console.info( "warmupComponents:  prepare " + component.getComponentId() );

                        SMTWarmableComponent wc = (SMTWarmableComponent) component;

                        try {
                            wc.warmup();
                        } catch( Exception e ) {
                            _log.error( ERR_WARM, " on component " + component.getComponentId() + " : " + e.getMessage(), e );
                        }
                    }
                }
            }
        } else {
            _console.info( "warmupComponents SKIPPED" );
        }
    }

    private static void startComponents() {
        _console.info( "startComponents start" );

        AppState.setState( AppState.State.Starting );

        _componentManager.setCreationPhase( CreationPhase.Runtime );

        Set<SMTComponent> components = new LinkedHashSet<>();
        _componentManager.getComponentSet( SMTComponentManager.CREATION_TIME_PRE_RUNTIME, components );

        for ( SMTComponent component : components ) {
            if ( component instanceof SMTControllableComponent ) {
                _console.info( "startComponents:  start " + component.getComponentId() );

                SMTControllableComponent cc = (SMTControllableComponent) component;

                cc.startWork();
            }
        }

        SystemStatus.instance().initialised( _componentManager );
    }

    public static void preStopComponents( SMTComponentManager loader ) {
        _console.info( "stopComponents preStop ENTRY" );

        Set<SMTComponent> components = new LinkedHashSet<>();
        _componentManager.getComponentSet( SMTComponentManager.CREATION_TIME_PRE_RUNTIME, components );

        for ( SMTComponent component : components ) {
            if ( component instanceof SMTControllableComponent ) {
                _console.info( "stopComponents:  preStop " + component.getComponentId() );

                SMTControllableComponent cc = (SMTControllableComponent) component;

                try {
                    cc.preStop();
                } catch( Exception e ) {
                    _log.error( ERR_PRE_STOP, " on component " + component.getComponentId() + " : " + e.getMessage(), e );
                }
            }
        }

        _console.info( "stopComponents preStop EXIT" );
    }

    public static void stopComponents( SMTComponentManager loader ) {
        _console.info( "stopComponents stop ENTRY " );

        Set<SMTComponent> components = new LinkedHashSet<>();
        _componentManager.getComponentSet( SMTComponentManager.CREATION_TIME_PRE_RUNTIME, components );

        for ( SMTComponent component : components ) {
            if ( component instanceof SMTControllableComponent ) {
                _console.info( "stopComponents:  stop " + component.getComponentId() );

                SMTControllableComponent cc = (SMTControllableComponent) component;

                try {
                    cc.stopWork();
                } catch( Throwable e ) {
                    _log.error( ERR_STOP, " on component " + component.getComponentId() + " : " + e.getMessage(), e );
                }

                _console.info( "stopComponents:  stopped " + component.getComponentId() );
            }
        }

        LoggerFactory.shutdown();

        AppState.setState( AppState.State.Stopped );

        _console.info( "stopComponents stop EXIT " );
    }

    private static void basicSetup( String[] args ) throws Exception {
        if ( args.length != 2 ) {
            _console.info( "Error : missing property file arguments" );
            _console.info( "Usage: {prog} envPropertyFile appPropertyFile" );
            Utils.exit( 99 );
        }

        String envPropFile = args[ 0 ];
        String appPropFile = args[ 1 ];

        AppProps.instance().init( envPropFile, appPropFile );

        AppProps props   = AppProps.instance();
        String   appName = props.getProperty( CoreProps.APP_NAME );

        boolean appendUTC = props.getBooleanProperty( CoreProps.LOG_UTC, false, true );
        StandardTimeUtils.setAppendUTC( appendUTC );

        String tzStr = AppProps.instance().getProperty( CoreProps.APP_TIMEZONE, false, null );
        setLocalTimeZone( tzStr );

        StatsMgr.setStats( new StatsCfgFile( props.getProperty( CoreProps.STATS_CFG_FILE, false, null ) ) );
        StatsMgr.instance().initialise();

        ThreadUtilsFactory.get().init( props.getProperty( CoreProps.CPU_MASK_FILE, false, null ) );

        ThreadPriority priority = PropertyHelper.getProperty( props, CoreProps.MAIN_THREAD_PRI, ThreadPriority.class, ThreadPriority.Main );

        ThreadUtilsFactory.get().setPriority( Thread.currentThread(), priority );

        Level level = PropertyHelper.getProperty( props, CoreProps.LOG_LEVEL, Level.class, Level.info );

        boolean forceConsole = "true".equalsIgnoreCase( System.getProperty( "app.forceConsole" ) ) || props.getBooleanProperty( "app.forceConsole", false, false );

        LoggerFactory.setForceConsole( forceConsole );
        String logFile                = getLogFile();
        int    minFlushPeriodMS       = props.getIntProperty( CoreProps.MIN_LOG_FLUSH_SECS, false, 5 ) * 1000;
        int    defaultLogMaxQueueSize = SizeConstants.DEFAULT_LOG_MAX_QUEUE_SIZE;
        LoggerFactory.initLogging( logFile,
                                   props.getIntProperty( CoreProps.MAX_LOG_SIZE, false, 10000000 ),
                                   level,
                                   props.getIntProperty( CoreProps.MAX_LOGQ_SIZE, false, defaultLogMaxQueueSize ),
                                   props.getMatchedKeys( CoreProps.APP_LOG_OVERRIDES ),
                                   minFlushPeriodMS
        );

        log4jOverrides();
        Utils.logInit();

        TimeUtilsFactory.safeTimeUtils(); // init the time utils

        _log = LoggerFactory.create( AntiSpringBootstrap.class );

        int htmlAdminPort   = props.getIntProperty( CoreProps.ADMIN_HTML_PORT, false, 8000 );
        int serverAdminPort = props.getIntProperty( CoreProps.ADMIN_RMI_PORT, false, htmlAdminPort + 1 );

        AdminAgent.init( htmlAdminPort, serverAdminPort );

        AntiSpringAdmin antiSpringAdmin = new AntiSpringAdmin( AntiSpringBootstrap::saveAndExit );
        AdminAgent.register( antiSpringAdmin );

        if ( tzStr != null ) _log.info( "AntiSpringBootstrap set default TimeZone to " + tzStr );

        _log.info( "STARTING " + appName );

        startupOverrides();

        ClockFactory.init();

        setScheduler();
    }

    private static String getLogFile() {
        AppProps props = AppProps.instance();

        String logRoot = props.getProperty( CoreProps.LOG_ROOT, false, "/logs/smt" );

        String appName = props.getProperty( CoreProps.APP_NAME );

        return props.getProperty( CoreProps.LOG_FILE_NAME, false, logRoot + "/" + appName );
    }

    private static void log4jOverrides() {
// didnt work
//        org.apache.log4j.Logger.getLogger("io.netty").setLevel( org.apache.log4j.Level.WARN );
//        org.apache.log4j.Logger.getLogger("io.grpc.netty").setLevel( org.apache.log4j.Level.WARN );
    }

    private static void startupOverrides() {
    }

    private static void setLocalTimeZone( final String tzStr ) {

        if ( tzStr != null ) {
            TimeZone tz = TimeZone.getTimeZone( tzStr );

            TimeZone.setDefault( tz );
        }
    }

    public static void setScheduler() {
        SchedulerFactory.get().initDaily( ( e ) -> dailyRoll() );
    }

    private static void dailyRoll() {
        LoggerFactory.dateRoll();
        Utils.invokeGC();
    }

    AntiSpringBootstrap() {
        super();
    }
}
