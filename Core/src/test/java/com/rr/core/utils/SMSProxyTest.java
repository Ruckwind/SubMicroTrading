/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.dummy.warmup.DummyAppProperties;
import com.rr.core.dummy.warmup.TestStats;
import com.rr.core.lang.Env;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;

import java.util.Collections;

import static org.junit.Assert.fail;

public class SMSProxyTest {

    public static void main( String[] args ) {

        Env _env = Env.DEV;

        LoggerFactory.setForceConsole( true );
        StatsMgr.setStats( new TestStats() );

        try {
            DummyAppProperties.testInit( Collections.singletonMap( CoreProps.RUN_ENV, _env.name() ) );
        } catch( Exception e ) {
            fail( e.getMessage() );
        }

        AppProps props = AppProps.instance();
        props.override( CoreProps.APP_NAME, "smtTestApp" );
        props.override( CoreProps.SMS_TO_PHONES,   "+449999999999" );
        props.override( CoreProps.SMS_FROM_PHONE,  "+449999999999" );
        props.override( CoreProps.SMS_ACCOUNT_SID, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" );
        props.override( CoreProps.SMS_AUTH_TOKEN,  "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" );

        SMSProxy.setRestrictToProd( false );

        SMSProxy p = new SMSProxy();
        p.init();

        p.sendText( "test message" );
    }
}