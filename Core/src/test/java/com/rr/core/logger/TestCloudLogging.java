package com.rr.core.logger;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Env;
import org.junit.Ignore;
import org.junit.Test;

public class TestCloudLogging extends BaseTestCase {

    @Ignore // re-enable to test cloud logging working
    @Test
    public void testCloudMail() {
        final String      cloudLogName  = "DummyApp" + "." + Env.TEST.toString();
        final String      cloudAuthJSON = "./gcloud/Astro-Logger.json";
        GoogleLogAppender googleApender = new GoogleLogAppender( cloudLogName, cloudAuthJSON );

        final LogEvent event = new LogEventSmall();
        event.set( Level.WARN, "This is a sample warning" );

        googleApender.init( Level.WARN );
        googleApender.open();
        googleApender.handle( event );
        googleApender.close();
    }
}
