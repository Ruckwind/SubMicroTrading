package com.rr.core.admin;

import com.sun.jmx.interceptor.MBeanServerInterceptor;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Logger;
import com.rr.core.utils.ReflectUtils;
import com.rr.core.utils.StringUtils;

import javax.management.*;
import javax.management.loading.ClassLoaderRepository;
import java.io.ObjectInputStream;
import java.util.Set;

public class LoggingMBeanServerInterceptor implements MBeanServerInterceptor {

    private static final Logger _log = ConsoleFactory.console( LoggingMBeanServerInterceptor.class );

    private final MBeanServer _proxy;

    public LoggingMBeanServerInterceptor( final MBeanServer proxy ) {
        _proxy = proxy;
    }

    @Override public ObjectInstance createMBean( final String className, final ObjectName name ) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
        return _proxy.createMBean( className, name );
    }

    @Override public ObjectInstance createMBean( final String className, final ObjectName name, final ObjectName loaderName )
            throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
        return _proxy.createMBean( className, name, loaderName );
    }

    @Override public ObjectInstance createMBean( final String className, final ObjectName name, final Object[] params, final String[] signature )
            throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
        return _proxy.createMBean( className, name, params, signature );
    }

    @Override public ObjectInstance createMBean( final String className, final ObjectName name, final ObjectName loaderName, final Object[] params, final String[] signature )
            throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
        return _proxy.createMBean( className, name, loaderName, params, signature );
    }

    @Override public ObjectInstance registerMBean( final Object object, final ObjectName name ) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException { return _proxy.registerMBean( object, name ); }

    @Override public void unregisterMBean( final ObjectName name ) throws InstanceNotFoundException, MBeanRegistrationException                                                               { _proxy.unregisterMBean( name ); }

    @Override public ObjectInstance getObjectInstance( final ObjectName name ) throws InstanceNotFoundException                                                                               { return _proxy.getObjectInstance( name ); }

    @Override public Set<ObjectInstance> queryMBeans( final ObjectName name, final QueryExp query )                                                                                           { return _proxy.queryMBeans( name, query ); }

    @Override public Set<ObjectName> queryNames( final ObjectName name, final QueryExp query )                                                                                                { return _proxy.queryNames( name, query ); }

    @Override public boolean isRegistered( final ObjectName name )                                                                                                                            { return _proxy.isRegistered( name ); }

    @Override public Integer getMBeanCount()                                                                                                                                                  { return _proxy.getMBeanCount(); }

    @Override public Object getAttribute( final ObjectName name, final String attribute ) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
        return _proxy.getAttribute( name, attribute );
    }

    @Override public AttributeList getAttributes( final ObjectName name, final String[] attributes ) throws InstanceNotFoundException, ReflectionException {
        return _proxy.getAttributes( name, attributes );
    }

    @Override public void setAttribute( final ObjectName name, final Attribute attribute ) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        _proxy.setAttribute( name, attribute );
    }

    @Override public AttributeList setAttributes( final ObjectName name, final AttributeList attributes ) throws InstanceNotFoundException, ReflectionException {
        return _proxy.setAttributes( name, attributes );
    }

    @Override public Object invoke( final ObjectName name, final String operationName, Object[] params, final String[] signature ) throws InstanceNotFoundException, MBeanException, ReflectionException {

        long start = ClockFactory.getLiveClock().currentTimeMillis();

        ReusableString s = TLC.strPop();

        s.copy( "JMX invoking " ).append( name.getCanonicalName() ).append( ":" ).append( operationName ).append( ", params=" ).append( params ).append( ", sig=" ).append( signature );

        _log.info( s );

        Object reply = null;
        try {
            reply = _proxy.invoke( name, operationName, params, signature );

            long end = ClockFactory.getLiveClock().currentTimeMillis();

            s.copy( "JMX invoked (" )
             .append( Math.abs( end - start ) ).append( "ms) " )
             .append( name.getCanonicalName() ).append( ":" )
             .append( operationName )
             .append( ", response=" ).append( (reply == null) ? "null" : reply.toString() );
        } catch( ReflectionException e ) {

            String msg = e.getMessage();

            if ( msg != null ) {
                String ptn = "should be (";
                int    idx = msg.indexOf( ptn );

                String args = msg.substring( idx + ptn.length() );

                idx = args.indexOf( ")" );

                if ( idx != -1 ) {
                    args = args.substring( 0, idx );

                    String[] paramTypes = StringUtils.split( args, ',' );

                    params = ReflectUtils.makeArgs( paramTypes, params );

                    try {
                        reply = _proxy.invoke( name, operationName, params, paramTypes );

                        long end = ClockFactory.getLiveClock().currentTimeMillis();

                        s.copy( "JMX invoked (" )
                         .append( Math.abs( end - start ) ).append( "ms) " )
                         .append( name.getCanonicalName() ).append( ":" )
                         .append( operationName )
                         .append( ", response=" ).append( (reply == null) ? "null" : reply.toString() );

                    } catch( Exception e2 ) {

                        s.copy( "JMX exception " ).append( e2.getMessage() );

                        reply = e2.getMessage();
                    }
                }
            }

        } catch( Exception e ) {

            s.copy( "JMX exception " ).append( e.getMessage() );

            reply = e.getMessage();
        }

        _log.info( s );

        TLC.strPush( s );

        return reply;
    }

    @Override public String getDefaultDomain() { return _proxy.getDefaultDomain(); }

    @Override public String[] getDomains()     { return _proxy.getDomains(); }

    @Override public void addNotificationListener( final ObjectName name, final NotificationListener listener, final NotificationFilter filter, final Object handback ) throws InstanceNotFoundException {
        _proxy.addNotificationListener( name, listener, filter, handback );
    }

    @Override public void addNotificationListener( final ObjectName name, final ObjectName listener, final NotificationFilter filter, final Object handback ) throws InstanceNotFoundException {
        _proxy.addNotificationListener( name, listener, filter, handback );
    }

    @Override public void removeNotificationListener( final ObjectName name, final ObjectName listener ) throws InstanceNotFoundException, ListenerNotFoundException {
        _proxy.removeNotificationListener( name, listener );
    }

    @Override public void removeNotificationListener( final ObjectName name, final ObjectName listener, final NotificationFilter filter, final Object handback ) throws InstanceNotFoundException, ListenerNotFoundException {
        _proxy.removeNotificationListener( name, listener, filter, handback );
    }

    @Override public void removeNotificationListener( final ObjectName name, final NotificationListener listener ) throws InstanceNotFoundException, ListenerNotFoundException {
        _proxy.removeNotificationListener( name, listener );
    }

    @Override public void removeNotificationListener( final ObjectName name, final NotificationListener listener, final NotificationFilter filter, final Object handback ) throws InstanceNotFoundException, ListenerNotFoundException {
        _proxy.removeNotificationListener( name, listener, filter, handback );
    }

    @Override public MBeanInfo getMBeanInfo( final ObjectName name ) throws InstanceNotFoundException, IntrospectionException, ReflectionException {
        return _proxy.getMBeanInfo( name );
    }

    @Override public boolean isInstanceOf( final ObjectName name, final String className ) throws InstanceNotFoundException {
        return _proxy.isInstanceOf( name, className );
    }

    @Override public ClassLoader getClassLoaderFor( final ObjectName mbeanName ) throws InstanceNotFoundException { return _proxy.getClassLoaderFor( mbeanName ); }

    @Override public ClassLoader getClassLoader( final ObjectName loaderName ) throws InstanceNotFoundException   { return _proxy.getClassLoaderFor( loaderName ); }

    @Override public Object instantiate( final String className ) throws ReflectionException, MBeanException {
        return _proxy.instantiate( className );
    }

    @Override public Object instantiate( final String className, final ObjectName loaderName ) throws ReflectionException, MBeanException, InstanceNotFoundException {
        return _proxy.instantiate( className, loaderName );
    }

    @Override public Object instantiate( final String className, final Object[] params, final String[] signature ) throws ReflectionException, MBeanException {
        return _proxy.instantiate( className, params, signature );
    }

    @Override public Object instantiate( final String className, final ObjectName loaderName, final Object[] params, final String[] signature ) throws ReflectionException, MBeanException, InstanceNotFoundException {
        return _proxy.instantiate( className, loaderName, params, signature );
    }

    @Override public ObjectInputStream deserialize( final ObjectName name, final byte[] data ) throws InstanceNotFoundException, OperationsException {
        return _proxy.deserialize( name, data );
    }

    @Override public ObjectInputStream deserialize( final String className, final byte[] data ) throws OperationsException, ReflectionException {
        return _proxy.deserialize( className, data );
    }

    @Override public ObjectInputStream deserialize( final String className, final ObjectName loaderName, final byte[] data ) throws InstanceNotFoundException, OperationsException, ReflectionException {
        return _proxy.deserialize( className, loaderName, data );
    }

    @Override public ClassLoaderRepository getClassLoaderRepository()                                             { return _proxy.getClassLoaderRepository(); }
}
