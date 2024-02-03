/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package warmup;

import com.rr.core.logger.Level;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.session.socket.PortOffset;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.om.BaseOMTestCase;
import com.rr.om.warmup.WarmupUTPSocketSession;
import com.rr.om.warmup.sim.FixSimParams;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestWarmupUTPSocketSession extends BaseOMTestCase {

    private static final int PORT_OFFSET_UTP = PortOffset.getNext();

    @Test
    public void testWarmupUTPSocket() {

        loadExchanges();

        LoggerFactory.setForceConsole( false );
        LoggerFactory.initLogging( "./logs/TstWarmupUTPSocketSession.log", 30000000, Level.info );

        try {
            int orders      = get( "ORDERS", 3000 );
            int delayMicros = get( "DELAY", 100 );
            int batchSize   = get( "BATCH", 1 );
            int maxTime     = get( "MAX_TIME", 30000 );

            WarmupUTPSocketSession sess = WarmupUTPSocketSession.create( "Test", PORT_OFFSET_UTP, false, orders );

            FixSimParams p = sess.getParams();

            p.setBatchSize( batchSize );
            p.setDelayMicros( delayMicros );
            p.setDisableNanoStats( false );
            p.setDisableEventLogging( false );

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

