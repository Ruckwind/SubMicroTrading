package com.rr.core.utils;

import com.rr.core.dummy.warmup.DummyAppProperties;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.fail;

@SuppressWarnings( "unchecked" )
public class EmailProxyTest extends BaseTestCase {

    static {
        try {
            DummyAppProperties.testInit( "env.core.DEV.properties" );

            AppProps.instance().override( CoreProps.APP_NAME, "unitTest" );

        } catch( Exception e ) {
            fail( e.getMessage() );
        }
    }

    // ignore test for now ... its kept for use if any mail problems occur
    @Ignore
    @Test
    public void sendMail() {
        final String[] receiverList = { "gjhgj@nowhere.nowhere.sghjsk" };
        final String   subjectLine  = "SMT Test Email";
        final String   msgBody      = "zis is ze body";
        EmailProxy.instance().sendMail( receiverList, subjectLine, msgBody );
    }
}
