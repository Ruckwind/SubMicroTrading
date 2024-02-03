/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.admin;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTInitialisableComponent;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.*;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Logger;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.recovery.json.JSONUtils;
import com.rr.core.thread.RunState;
import com.rr.core.utils.*;
import com.sun.jdmk.TraceFilter;
import com.sun.jdmk.comm.HtmlAdaptorServer;
import com.sun.jdmk.trace.Trace;
import com.sun.jmx.mbeanserver.JmxMBeanServer;

import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.BufferedWriter;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * AdminAgent
 * <p>
 * admin wrapper to JMX, client code should not use any JMX as this may change in future
 *
 * @NOTE use the html adapter as doesnt spam create temp objs
 * @NOTE AVOID JConsole it generates 16MB temp objs in the app in 5mins (while jconsole connected) !
 * <p>
 * if using JConsole run with
 * -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=1616  -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false
 * <p>
 * https://docs.oracle.com/javase/tutorial/jmx/remote/custom.html
 * https://docs.oracle.com/javase/8/docs/technotes/guides/jmx/tutorial/security.html#wp997065
 * <p>
 * DONT !! USE JVM ARGS -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false
 */
public class AdminAgent implements SMTInitialisableComponent {

    static final Logger _log = ConsoleFactory.console( AdminAgent.class );
    static final         ErrorCode FAIL_START = new ErrorCode( "ADA200", "Unable to start HTML admin adapter" );
    private static final ErrorCode FAIL_REG   = new ErrorCode( "ADA100", "Unable to register admin command" );
    private static final int ADMIN_DISABLED = -1;
    static         HtmlAdaptorServer  _htmlConnector;
    private static MBeanServer        _mbs;
    private static JMXConnectorServer _rmiConnector;
    private static Registry           _rmiRegistry;
    private static BufferedWriter     _traceFile;
    private static int                _htmlPort;
    private static String             _serviceUrl;
    private static String             _serverHost;
    private static int                _rmiPort;
    private static int                _maxHTMLClients = 100;

    private final              String   _id;
    private transient volatile RunState _runState = RunState.Unknown;

    public synchronized static void init( int htmlPort ) throws AdminException {
        init( htmlPort, htmlPort + 1 );
    }

    public synchronized static void init( int htmlPort, int rmiPort ) throws AdminException {
        if ( _mbs == null && htmlPort > 0 ) {

            _log.info( "AdminAgent starting JMX html port " + htmlPort + ", rmiPort=" + rmiPort );
            _htmlPort = htmlPort;
            _rmiPort  = rmiPort;

            // Register and start the HTML adaptor
            try {

                System.setProperty( "com.sun.management.jmxremote.port", "" + rmiPort );

                _mbs = MBeanServerFactory.createMBeanServer();

                // setup trace logging for JMX
                AppProps     props     = AppProps.instance();
                String       logRoot   = props.getProperty( CoreProps.LOG_ROOT, false, "/logs/smt" );
                String       appName   = props.getProperty( CoreProps.APP_NAME );
                final String traceFile = logRoot + "/" + appName + ".jmx.log";
                _maxHTMLClients = props.getIntProperty( "MAX_HTML_JMX_CONNECTIONS", false, _maxHTMLClients );

                try {
                    _traceFile = FileUtils.bufFileWriter( traceFile, true );
                } catch( Exception e ) {
                    _traceFile = null;
                }

                NotificationListener tl = new NotificationListener() {

                    @Override public void handleNotification( final Notification notification, final Object handback ) {

                        final String lastHttpAddr = (_htmlConnector == null) ? "unknown" : _htmlConnector.getLastConnectedClient();

                        ReusableString r = TLC.strPop();

                        TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( r, TimeUtilsFactory.safeTimeUtils().getLocalTimeZone(), ClockFactory.getLiveClock().currentTimeMillis() );

                        r.append( " handleNotification lastHttpAddr=" ).append( lastHttpAddr ).append( ", notif=" ).append( JSONUtils.objectToJSON( notification ) ).append( "\n" );

                        if ( _traceFile == null ) {
                            _log.info( r.toString() );
                        } else {
                            try {
                                _traceFile.write( r.toString() );
                                _traceFile.flush();
                            } catch( IOException e ) {
                                _log.info( r.toString() );
                            }
                        }

                        if ( r.contains( "HtmlAdaptorServer" ) && (r.contains( "waitIfTooManyClients" ) || r.contains( "Waiting for a client to terminate" )) ) {

                            try {
                                _log.warn( "JMX HTML adapter reached max clients ... stop and start new one" );
                                createHTMLConnector();
                                _log.info( "JMX HTML recreated HTML adapter" );
                            } catch( Exception e ) {
                                _log.warn( "JMX HTML failed recreate " + e.getMessage() );
                                e.printStackTrace();
                            }
                        }

                        TLC.strPush( r );
                    }
                };

                TraceFilter tf = new TraceFilter( Trace.LEVEL_TRACE, Trace.INFO_ALL );
                com.sun.jdmk.TraceManager.addNotificationListener( tl, tf, null );

                if ( _mbs instanceof JmxMBeanServer ) {
                    _log.info( "installing JMX log interceptor ... force hacking JmxMBeanServer . interceptorsEnabled" );
                    ReflectUtils.setMember( _mbs, "interceptorsEnabled", true );
                    JmxMBeanServer    mBeanServer = (JmxMBeanServer) _mbs;
                    final MBeanServer defaultInt  = mBeanServer.getMBeanServerInterceptor();
                    final MBeanServer loggingInt  = new LoggingMBeanServerInterceptor( defaultInt );
                    mBeanServer.setMBeanServerInterceptor( loggingInt );
                } else {
                    _log.info( "Unknown JMS server class " + _mbs.getClass().getName() + " cannot install JMX log interceptor" );
                }

                createRMIConnector();

                createHTMLConnector();

                ShutdownManager.instance().register( "CloseAdminAgent", AdminAgent::close, ShutdownManager.Priority.Low );

            } catch( Exception e ) {
                if ( ! Env.isBacktest() ) {
                    throw new AdminException( e );
                }
                _htmlPort = ADMIN_DISABLED;
            }
        } else {
            _htmlPort = ADMIN_DISABLED;
        }
    }

    private static void createHTMLConnector() throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {

        if ( _htmlConnector != null ) {
            _htmlConnector.stop();
        }

        _htmlConnector = new HtmlAdaptorServer();

        String     id          = "AdminAgent:name=htmladapter,port=" + _htmlPort;
        ObjectName adapterName = new ObjectName( id );
        _htmlConnector.setPort( _htmlPort );

        _htmlConnector.setMaxActiveClientCount( _maxHTMLClients );

        int maxCnt = _htmlConnector.getMaxActiveClientCount();

        _log.info( "HtmlAdaptorServer max connections is " + maxCnt );

        _mbs.registerMBean( _htmlConnector, adapterName );

        // must start the HTML adapter under a daemon thread for it to inherit isDaemon property

        Thread adStart = new Thread( () -> {
            try {
                _htmlConnector.start();
                _log.info( "HTML Admin Adapter started" );
            } catch( Exception e ) {
                _log.error( FAIL_START, "", e );
            }

        }, "AdinHTMLAdapterStarter" );

        adStart.setDaemon( true );
        adStart.start();
    }

    private static void createRMIConnector() {

        try {
            _rmiRegistry = LocateRegistry.createRegistry( _rmiPort );
            Map<String, Object> env = new HashMap<String, Object>();

            env.put( "com.sun.management.jmxremote.authenticate", "false" );
            env.put( "com.sun.management.jmxremote.ssl", "false" );
            env.put( "com.sun.management.jmxremote.port", "" + _rmiPort );
            env.put( "com.sun.management.jmxremote.rmi.port", "" + _rmiPort );

            _log.info( "Create an RMI connector server" );

            JMXServiceURL url = new JMXServiceURL( "service:jmx:rmi:///jndi/rmi://0.0.0.0:" + _rmiPort + "/jmxrmi" );

            Thread adRMIThread = new Thread( () -> {
                try {
                    _log.info( "AdminAgent : Start the RMI connector server on port " + _rmiPort );

                    _rmiConnector = JMXConnectorServerFactory.newJMXConnectorServer( url, env, _mbs );

                    _rmiConnector.start();

                    _log.info( "RMI Admin Adapter started" );
                } catch( Exception e ) {
                    _log.error( FAIL_START, "", e );
                }

            }, "AdminRMIAdapterStarter" );

            adRMIThread.setDaemon( true );
            adRMIThread.start();

        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public static void restartJMXWeb() throws AdminException {
        if ( _mbs != null ) {

            // Register and start the HTML adaptor
            try {

                createHTMLConnector();

            } catch( Exception e ) {
                throw new AdminException( e );
            }
        }
    }

    public synchronized static void close() {
        _log.info( "AdminAgent closing" );

        if ( _htmlConnector != null ) {
            try {
                _htmlConnector.stop();
            } catch( Exception e ) {
                _log.info( "AdminAgent exception on close : " + e.getMessage() );
            }
            _htmlConnector = null;
        }
        if ( _rmiConnector != null ) {
            try {
                _rmiConnector.stop();
            } catch( IOException e ) {
                _log.info( "closing JMX exception " + e.getMessage() );
            }
            _rmiConnector = null;
        }
        if ( _rmiRegistry != null ) {
            try {
                UnicastRemoteObject.unexportObject( _rmiRegistry, true );
            } catch( Exception e ) {
                _log.info( "closing RMI registry " + e.getMessage() );
            } finally {
                _rmiRegistry = null;
            }
        }

        Utils.close( _traceFile );

        _log.info( "AdminAgent closed" );
    }

    public static void register( AdminCommand handler ) {
        if ( _mbs != null && _htmlPort != ADMIN_DISABLED ) {
            ObjectName name;

            try {
                // Uniquely identify the MBeans and register them with the platform MBeanServer
                name = formName( handler.getName() );
                _mbs.registerMBean( handler, name );

            } catch( Exception e ) {
                _log.error( FAIL_REG, "command " + handler.getName(), e );
            }
        }
    }

    private static ObjectName formName( String beanName ) throws MalformedObjectNameException {
        ObjectName name;
        name = new ObjectName( "AdminAgent:name=" + beanName );
        return name;
    }

    public static ObjectInstance find( String beanName ) throws AdminException {
        if ( _htmlPort == ADMIN_DISABLED ) return null;

        ObjectName name;
        try {
            name = formName( beanName );
            return _mbs.getObjectInstance( name );
        } catch( MalformedObjectNameException | InstanceNotFoundException e ) {
            throw new AdminException( e );
        }
    }

    public static Object invokeOperation( String beanName, String operationName, Object params[], String signature[] ) throws AdminException {
        if ( _htmlPort == ADMIN_DISABLED ) return null;

        ReusableString msg = new ReusableString( "AdminAgent : invokeOperation on " );
        msg.append( beanName ).append( " " );
        msg.append( operationName ).append( " with " );
        for ( Object param : params ) {
            msg.append( " [" ).append( param.toString() ).append( "]" );
        }

        msg.append( ", signature " );

        for ( Object param : signature ) {
            msg.append( " [" ).append( param.toString() ).append( "]" );
        }
        _log.info( msg );

        ObjectName name;
        try {
            name = formName( beanName );
            return _mbs.invoke( name, operationName, params, signature );
        } catch( MalformedObjectNameException | MBeanException | ReflectionException | InstanceNotFoundException e ) {
            throw new AdminException( e );
        }
    }

    public static Object getAttribute( String beanName, String attrName ) throws AdminException {
        if ( _htmlPort == ADMIN_DISABLED ) return null;

        ObjectName name;
        try {
            name = formName( beanName );
            return _mbs.getAttribute( name, attrName );
        } catch( MalformedObjectNameException | AttributeNotFoundException | MBeanException | ReflectionException | InstanceNotFoundException e ) {
            throw new AdminException( e );
        }
    }

    public static MBeanInfo info( String beanName ) throws AdminException {
        if ( _htmlPort == ADMIN_DISABLED ) return null;

        ObjectName name;
        try {
            name = formName( beanName );
            return _mbs.getMBeanInfo( name );
        } catch( MalformedObjectNameException | ReflectionException | IntrospectionException | InstanceNotFoundException e ) {
            throw new AdminException( e );
        }
    }

    public AdminAgent( String id, int port ) {
        _id       = id;
        _htmlPort = port;
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState()                          { return _runState; }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    @Override
    public void init( SMTStartContext ctx, CreationPhase creationPhase ) {
        try {
            init( _htmlPort, _htmlPort + 1 );
        } catch( AdminException e ) {
            throw new SMTRuntimeException( "Exception initialising JXM", e );
        }
    }

    @Override
    public void prepare() {
        // nothing
    }
}
