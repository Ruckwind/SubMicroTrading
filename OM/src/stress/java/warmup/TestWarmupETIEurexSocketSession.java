/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package warmup;

import com.rr.core.logger.Level;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.session.socket.PortOffset;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.om.BaseOMTestCase;
import com.rr.om.warmup.WarmupETIEurexSocketSession;
import com.rr.om.warmup.sim.FixSimParams;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestWarmupETIEurexSocketSession extends BaseOMTestCase {

    private static final int PORT_OFFSET_ETI_EUREX = PortOffset.getNext();

    @Test
    public void testWarmupETISocket() {

        WarmupETIEurexSocketSession.TRACE = true;

        loadExchanges();

        LoggerFactory.setForceConsole( false );
        LoggerFactory.initLogging( "./logs/TstWarmupETISocketSession.log", 30000000, Level.info );

        try {
            int orders      = get( "ORDERS", 5 );
            int delayMicros = get( "DELAY", 100 );
            int batchSize   = get( "BATCH", 1 );
            int maxTime     = get( "MAX_TIME", 30000 );

            WarmupETIEurexSocketSession sess = WarmupETIEurexSocketSession.create( "Test", PORT_OFFSET_ETI_EUREX, false, orders );

            FixSimParams p = sess.getParams();

            p.setBatchSize( batchSize );
            p.setDelayMicros( delayMicros );
            p.setDisableNanoStats( false );
            p.setDisableEventLogging( false );
            p.setLogPojoEvents( true );

            sess.setMaxRunTime( maxTime );
            sess.setEventLogging( true );
            sess.warmup();

            LoggerFactory.flush();

            ThreadUtilsFactory.get().sleep( 1000 );

            assertEquals( sess.getSentByClientSim(), sess.getReceived() );

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

