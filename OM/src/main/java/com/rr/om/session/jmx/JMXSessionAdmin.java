/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.jmx;

import com.rr.core.utils.ThreadUtilsFactory;

public class JMXSessionAdmin implements JMXSessionAdminMBean {

    private final JMXSession _session;

    public JMXSessionAdmin( JMXSession jmxSession ) {
        _session = jmxSession;
    }

    @Override
    public String getName() {
        return _session.getComponentId() + "Admin";
    }

    @Override
    public String injectMessage( String rawMessage ) {
        return injectMessage( rawMessage, null );
    }

    @Override
    public String injectMessage( String rawMessage, String sessionName ) {
        String[]      msgs = rawMessage.split( "#" );
        StringBuilder ret  = null;

        for ( String msg : msgs ) {
            String lcMsg = msg.toLowerCase();

            String r;

            if ( lcMsg.startsWith( "sleep" ) || lcMsg.startsWith( "pause" ) ) {
                String[] bits = msg.split( " " );

                try {
                    int delay = Integer.parseInt( bits[ 1 ] );

                    if ( delay < 0 || delay > 30000 ) {
                        delay = 1000;
                        r     = "sleep override [" + delay + " ms]";
                    } else {
                        r = "sleep [" + delay + " ms]";
                    }

                    ThreadUtilsFactory.get().sleep( delay );
                } catch( NumberFormatException e ) {
                    r = "Override bad sleep param [" + msg + "]";
                    ThreadUtilsFactory.get().sleep( 1000 );
                }
            } else {
                r = _session.injectMessage( msg, sessionName );
            }

            if ( ret == null ) {
                ret = new StringBuilder( r );
            } else {
                ret.append( "\n" ).append( r );
            }

            ThreadUtilsFactory.get().sleep( 100 );
        }

        return ret.toString();
    }
}
