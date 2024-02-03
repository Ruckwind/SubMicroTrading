package com.rr.core.admin;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class JMXListBeans {

    // ref https://docs.oracle.com/javase/tutorial/jmx/remote/custom.html

    public static void main( String... args ) throws Exception {

        if ( args.length != 2 ) {
            System.out.println( "JMXListBeans USAGE : [user] [url]" );
            System.out.println( "JMXListBeans USAGE : roseri service:jmx:rmi:///jndi/rmi://:15751/jmxrmi" );
        }

        String user = args[ 0 ];
        String url  = args[ 1 ];

        try {
            System.out.println( "JMXListBeans User " + user + " url=" + url );

            JMXServiceURL target    = new JMXServiceURL( url );
            JMXConnector  connector = JMXConnectorFactory.connect( target );

            MBeanServerConnection mbsc = connector.getMBeanServerConnection();

            // Get domains from MBeanServer
            //
            System.out.println( "\nDomains:" );
            String domains[] = mbsc.getDomains();
            Arrays.sort( domains );
            for ( String domain : domains ) {
                System.out.println( "\tDomain = " + domain );
            }

            // Get MBeanServer's default domain
            //
            System.out.println( "\nMBeanServer default domain = " + mbsc.getDefaultDomain() );

            // Get MBean count
            //
            System.out.println( "\nMBean count = " + mbsc.getMBeanCount() );

            // Query MBean names
            //
            System.out.println( "\nQuery MBeanServer MBeans:" );
            Set<ObjectName> names =
                    new TreeSet<ObjectName>( mbsc.queryNames( null, null ) );
            for ( ObjectName name : names ) {
                System.out.println( "\tObjectName = " + name );
            }

            connector.close();

        } catch( Exception e ) {
            System.err.println( "JMXInvoke " + e.getMessage() );
            e.printStackTrace();

            System.exit( 10 );
        }
    }
}