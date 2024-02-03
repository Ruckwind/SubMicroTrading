package com.rr.core.admin;

import com.rr.core.utils.StringUtils;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.ArrayList;

public class JMXInvoker {

    public static void main( String... args ) throws Exception {

        if ( args.length < 4 ) {
            System.out.println( "USAGE : [user] [url] [beanName] [method] [commaDelimArgs]" );
            System.out.println( "USAGE : roseri service:jmx:rmi:///jndi/rmi://:15751/jmxrmi AdminAgent:name=SnapshotCaretakerMBean takeSnapshot" );
            System.out.println( "USAGE : roseri service:jmx:rmi:///jndi/rmi://:15751/jmxrmi AdminAgent:name=StrategyManagerAdmin1 addRealPnlForStrat tradeGwy,ZL.SMK1.202105.XCBT,-5990,MANUAL_1" );
        }

        String user       = args[ 0 ];
        String url        = args[ 1 ];
        String beanName   = args[ 2 ];
        String method     = args[ 3 ];
        String methodArgs = "";

        for ( int idx = 4; idx < args.length; ++idx ) {
            if ( idx > 4 ) methodArgs = methodArgs + " ";

            methodArgs = methodArgs + args[ idx ];
        }

        try {
            JMXInvoker ji = new JMXInvoker();

            ji.remoteInvoke( user, url, beanName, method, methodArgs );

        } catch( Exception e ) {
            System.err.println( "JMXInvoke " + e.getMessage() );
            e.printStackTrace();

            System.exit( 10 );
        }
    }

    public void remoteInvoke( final String user, final String url, final String beanName, final String method, final String methodArgs )
            throws IOException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
        System.out.println( "INVOKING User " + user + " url=" + url + ", bean=" + beanName + ", method=" + method + ", args=" + methodArgs );

        Object[] mArgs = {};

        if ( methodArgs != null && methodArgs.length() > 0 ) {
            final ArrayList<String> bits = new ArrayList<>();
            StringUtils.split( methodArgs, ',', bits );

            mArgs = new Object[ bits.size() ];

            for ( int i = 0; i < bits.size(); ++i ) {
                mArgs[ i ] = bits.get( i );
            }
        }

        JMXServiceURL target    = new JMXServiceURL( url );
        JMXConnector  connector = JMXConnectorFactory.connect( target );

        MBeanServerConnection mbsc = connector.getMBeanServerConnection();

        ObjectName bean = new ObjectName( beanName );
        mbsc.invoke( bean, method, mArgs, new String[] {} );

        System.out.println( "COMPLETED User " + user + " url=" + url + ", bean=" + bean + ", method=" + method + ", args=" + methodArgs );

        connector.close();
    }
}