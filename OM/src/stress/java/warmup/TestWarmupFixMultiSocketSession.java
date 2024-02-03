/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package warmup;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.session.socket.PortOffset;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.om.warmup.WarmupMultiFixSocketSession;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestWarmupFixMultiSocketSession extends BaseTestCase {

    private static final int MAX_ITER = 30;

    // TEST FAILS RANDOMLY ON GitLab - disabled until can find why
    @Ignore
    @Test
    public void testWarmupFixMultiSessionSocket() {
        LoggerFactory.setForceConsole( false );
        LoggerFactory.initLogging( "./logs/TstWarmupFixSocketMultiSession.log", 1000000, Level.info );

        Logger l = ConsoleFactory.console( TestWarmupFixMultiSocketSession.class, Level.info );

        WarmupMultiFixSocketSession sess = null;

        try {
            int count = 500;

            sess = WarmupMultiFixSocketSession.create( "testWFMSS", PortOffset.getNext(), false, count );

            sess.setMaxRunTime( 30000 );
            sess.setEventLogging( true );
            sess.warmup();

            int idx = 0;

            while( idx++ <= MAX_ITER ) {
                ThreadUtilsFactory.get().sleep( 1000 );

                if ( count == sess.getSent() && sess.getSent() == sess.getReceived() ) {
                    break;
                }
            }

            assertEquals( count, sess.getSent() );
            assertEquals( sess.getSent(), sess.getReceived() );

        } catch( Exception e ) {

            fail( e.getMessage() );
        } finally {
            if ( sess != null ) {
                try {
                    sess.close();
                } catch( Exception e ) {
                    // dont care
                }
            }

            LoggerFactory.flush();
        }
    }
}

