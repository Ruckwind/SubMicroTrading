/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package warmup;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.logger.Level;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.session.socket.PortOffset;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.main.OMProps;
import com.rr.om.warmup.WarmupCMENonBlockFastFixSession;
import com.rr.om.warmup.sim.FixSimParams;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestWarmupCMENonBlockFastFixSession extends BaseTestCase {

    private static final int PORT_OFFSET_CME2 = PortOffset.getNext();

    @Test
    public void testWarmupCMENonBlockFastFixSession() throws Exception {

        LoggerFactory.setForceConsole( false );
        LoggerFactory.initLogging( "./logs/TstWarmupCMENonBlockFastFixSession.log", 1000000, Level.info );
        AppProps.instance().init( null, OMProps.instance() );

        try {
            int orders      = get( "ORDERS", 3000 );
            int delayMicros = get( "DELAY", 100 );
            int batchSize   = get( "BATCH", 1 );
            int maxTime     = get( "MAX_TIME", 30000 );

            WarmupCMENonBlockFastFixSession sess = WarmupCMENonBlockFastFixSession.create( "Test", PORT_OFFSET_CME2, false, orders, new DummyInstrumentLocator() );

            FixSimParams p = sess.getParams();

            p.setBatchSize( batchSize );
            p.setDelayMicros( delayMicros );
            p.setDisableNanoStats( false );
            p.setDisableEventLogging( false );
            p.setUpHost( "224.0.0.1" );
            p.setUpPort( 30011 );
            p.setDebug( false );

            sess.setMaxRunTime( maxTime );
            sess.setEventLogging( true );
            sess.warmup();

            LoggerFactory.flush();

            ThreadUtilsFactory.get().sleep( 5000 );

            assertEquals( 3000, sess.getSentByClientSim() );
            assertEquals( 1514, sess.getOrdersReceived() );

        } catch( Exception e ) {

            fail( e.getMessage() );
        }
    }

    private int get( String key, int def ) {
        int val = def;

        Properties p = System.getProperties();

        String sVal = p.getProperty( key );

        if ( sVal != null ) {
            try {
                val = Integer.parseInt( sVal );
            } catch( NumberFormatException e ) {
                //
            }
        }

        return val;
    }
}

