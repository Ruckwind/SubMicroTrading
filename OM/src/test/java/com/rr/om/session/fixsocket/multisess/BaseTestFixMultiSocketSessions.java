/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket.multisess;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.logger.Level;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.ThreadUtilsFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class BaseTestFixMultiSocketSessions extends BaseTestCase {

    void doTestMultiSessionSocket( String id, int ordersPerClient, int numClients ) {

        LoggerFactory.setForceConsole( false );
        LoggerFactory.initLogging( "./logs/TstFixMultiSocketSession" + id + ".log", 1000000 * numClients, Level.info );

        try {
            MultiFixSocketSessionEmul sess = MultiFixSocketSessionEmul.create( id, ordersPerClient, numClients );

            sess.setMaxRunTime( 10000 + 10000 * numClients );
            sess.setEventLogging( true );
            sess.warmup();

            LoggerFactory.flush();

            ThreadUtilsFactory.get().sleep( 1000 );

            sess.logDetails();

            assertEquals( ordersPerClient * numClients, sess.getSent() );
            assertEquals( ordersPerClient * numClients, sess.getReceived() );
            assertEquals( ordersPerClient * numClients, sess.getExchangeSent() );
            assertEquals( sess.getSent(), sess.getReceived() );

        } catch( Exception e ) {

            fail( e.getMessage() );
        }
    }
}

