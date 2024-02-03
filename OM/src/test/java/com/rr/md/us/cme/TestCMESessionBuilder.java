/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme;

import com.rr.algo.t1.DummyExchangeSession;
import com.rr.core.codec.binary.fastfix.NonBlockingFastFixSocketSession;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ViewString;
import com.rr.core.model.EventHandler;
import com.rr.core.session.MultiSessionThreadedReceiver;
import com.rr.core.thread.ControlThread;
import com.rr.core.thread.SingleElementControlThread;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ThreadPriority;
import com.rr.md.fastfix.FastSocketConfig;
import com.rr.md.us.cme.builder.CMERoundRobinSessionBuilder;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.om.session.SessionManager;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestCMESessionBuilder extends BaseTestCase {

    private CMEConfig _cfg;

    @Before
    public void setUp() {
        XMLCMEConfigLoader loader = new XMLCMEConfigLoader( "./data/cme/config.xml" );
        _cfg = loader.load();
    }

    @Test
    public void testFiveChannels() {

        SessionManager                 sessMgr            = new SessionManager( "testSM" );
        EventHandler[]                 handlers           = makeHandlers( 1 );
        MultiSessionThreadedReceiver[] multiplexReceivers = makeMultiplexReceivers( 1 );
        FastSocketConfig               socketConfig       = new FastSocketConfig( AllEventRecycler.class, false, new ViewString( "localhost" ), null, 0 );
        CMERoundRobinSessionBuilder    b                  = new CMERoundRobinSessionBuilder( "CMETestSessBldr", _cfg, "7, 10, 9, 13, 121", sessMgr, handlers, multiplexReceivers, socketConfig, null );

        List<NonBlockingFastFixSocketSession> sessions = b.create();

        assertEquals( 15, sessions.size() );
        assertEquals( 15, multiplexReceivers[ 0 ].getNumSessions() );
    }

    @Test
    public void testFourHandlers() {
        SessionManager                 sessMgr            = new SessionManager( "testSM" );
        EventHandler[]                 handlers           = makeHandlers( 4 );
        MultiSessionThreadedReceiver[] multiplexReceivers = makeMultiplexReceivers( 1 );
        FastSocketConfig               socketConfig       = new FastSocketConfig( AllEventRecycler.class, false, new ViewString( "localhost" ), null, 0 );
        CMERoundRobinSessionBuilder    b                  = new CMERoundRobinSessionBuilder( "CMETestSessBldr", _cfg, "7, 10, 9, 13, 121", sessMgr, handlers, multiplexReceivers, socketConfig, null );

        List<NonBlockingFastFixSocketSession> sessions = b.create();

        assertEquals( 15, sessions.size() );
        assertEquals( 15, multiplexReceivers[ 0 ].getNumSessions() );
    }

    @Test
    public void testInvalidChannel() {

        try {
            SessionManager                 sessMgr            = new SessionManager( "testSM" );
            EventHandler[]                 handlers           = makeHandlers( 1 );
            MultiSessionThreadedReceiver[] multiplexReceivers = makeMultiplexReceivers( 1 );
            FastSocketConfig               socketConfig       = new FastSocketConfig( AllEventRecycler.class, false, new ViewString( "localhost" ), null, 0 );
            CMERoundRobinSessionBuilder    b                  = new CMERoundRobinSessionBuilder( "CMETestSessBldr", _cfg, "9999999", sessMgr, handlers, multiplexReceivers, socketConfig, null );

            b.create();

            fail( "Should of thrown exception" );

        } catch( SMTRuntimeException e ) {
            // expected
        }
    }

    @Test
    public void testNoHandlers() {

        try {
            SessionManager                 sessMgr            = new SessionManager( "testSM" );
            EventHandler[]                 handlers           = new EventHandler[ 0 ];
            MultiSessionThreadedReceiver[] multiplexReceivers = makeMultiplexReceivers( 1 );
            FastSocketConfig               socketConfig       = new FastSocketConfig( AllEventRecycler.class, false, new ViewString( "localhost" ), null, 0 );
            CMERoundRobinSessionBuilder    b                  = new CMERoundRobinSessionBuilder( "CMETestSessBldr", _cfg, "7,10", sessMgr, handlers, multiplexReceivers, socketConfig, null );

            b.create();

            fail( "Should of thrown exception" );

        } catch( SMTRuntimeException e ) {
            // expected
        }
    }

    @Test
    public void testOneChannels() {

        SessionManager                 sessMgr            = new SessionManager( "testSM" );
        EventHandler[]                 handlers           = makeHandlers( 1 );
        MultiSessionThreadedReceiver[] multiplexReceivers = makeMultiplexReceivers( 1 );
        FastSocketConfig               socketConfig       = new FastSocketConfig( AllEventRecycler.class, false, new ViewString( "localhost" ), null, 0 );
        CMERoundRobinSessionBuilder    b                  = new CMERoundRobinSessionBuilder( "CMETestSessBldr", _cfg, "7", sessMgr, handlers, multiplexReceivers, socketConfig, null );

        List<NonBlockingFastFixSocketSession> sessions = b.create();

        assertEquals( 3, sessions.size() );
        assertEquals( 3, multiplexReceivers[ 0 ].getNumSessions() );
    }

    @Test
    public void testThreeMultiplexReceivers() {
        SessionManager                 sessMgr            = new SessionManager( "testSM" );
        EventHandler[]                 handlers           = makeHandlers( 1 );
        MultiSessionThreadedReceiver[] multiplexReceivers = makeMultiplexReceivers( 3 );
        FastSocketConfig               socketConfig       = new FastSocketConfig( AllEventRecycler.class, false, new ViewString( "localhost" ), null, 0 );
        CMERoundRobinSessionBuilder    b                  = new CMERoundRobinSessionBuilder( "CMETestSessBldr", _cfg, "7, 10, 9, 13, 121", sessMgr, handlers, multiplexReceivers, socketConfig, null );

        List<NonBlockingFastFixSocketSession> sessions = b.create();

        assertEquals( 15, sessions.size() );
        assertEquals( 6, multiplexReceivers[ 0 ].getNumSessions() );
        assertEquals( 6, multiplexReceivers[ 1 ].getNumSessions() );
        assertEquals( 3, multiplexReceivers[ 2 ].getNumSessions() );
    }

    @Test
    public void testThreeMultiplexReceiversAndHandlers() {
        SessionManager                 sessMgr            = new SessionManager( "testSM" );
        EventHandler[]                 handlers           = makeHandlers( 3 );
        MultiSessionThreadedReceiver[] multiplexReceivers = makeMultiplexReceivers( 3 );
        FastSocketConfig               socketConfig       = new FastSocketConfig( AllEventRecycler.class, false, new ViewString( "localhost" ), null, 0 );
        CMERoundRobinSessionBuilder    b                  = new CMERoundRobinSessionBuilder( "CMETestSessBldr", _cfg, "7, 10, 9, 13, 121", sessMgr, handlers, multiplexReceivers, socketConfig, null );

        List<NonBlockingFastFixSocketSession> sessions = b.create();

        assertEquals( 15, sessions.size() );
        assertEquals( 6, multiplexReceivers[ 0 ].getNumSessions() );
        assertEquals( 6, multiplexReceivers[ 1 ].getNumSessions() );
        assertEquals( 3, multiplexReceivers[ 2 ].getNumSessions() );
    }

    @Test
    public void testTwoChannels() {

        SessionManager                 sessMgr            = new SessionManager( "testSM" );
        EventHandler[]                 handlers           = makeHandlers( 1 );
        MultiSessionThreadedReceiver[] multiplexReceivers = makeMultiplexReceivers( 1 );
        FastSocketConfig               socketConfig       = new FastSocketConfig( AllEventRecycler.class, false, new ViewString( "localhost" ), null, 0 );
        CMERoundRobinSessionBuilder    b                  = new CMERoundRobinSessionBuilder( "CMETestSessBldr", _cfg, "7, 10", sessMgr, handlers, multiplexReceivers, socketConfig, null );

        List<NonBlockingFastFixSocketSession> sessions = b.create();

        assertEquals( 6, sessions.size() );
        assertEquals( 6, multiplexReceivers[ 0 ].getNumSessions() );
    }

    @Test
    public void testTwoHandlers() {
        SessionManager                 sessMgr            = new SessionManager( "testSM" );
        EventHandler[]                 handlers           = makeHandlers( 2 );
        MultiSessionThreadedReceiver[] multiplexReceivers = makeMultiplexReceivers( 1 );
        FastSocketConfig               socketConfig       = new FastSocketConfig( AllEventRecycler.class, false, new ViewString( "localhost" ), null, 0 );
        CMERoundRobinSessionBuilder    b                  = new CMERoundRobinSessionBuilder( "CMETestSessBldr", _cfg, "7, 10, 9, 13, 121", sessMgr, handlers, multiplexReceivers, socketConfig, null );

        List<NonBlockingFastFixSocketSession> sessions = b.create();

        assertEquals( 15, sessions.size() );
        assertEquals( 15, multiplexReceivers[ 0 ].getNumSessions() );
    }

    @Test
    public void testTwoMultiplexReceivers() {
        SessionManager                 sessMgr            = new SessionManager( "testSM" );
        EventHandler[]                 handlers           = makeHandlers( 1 );
        MultiSessionThreadedReceiver[] multiplexReceivers = makeMultiplexReceivers( 2 );
        FastSocketConfig               socketConfig       = new FastSocketConfig( AllEventRecycler.class, false, new ViewString( "localhost" ), null, 0 );
        CMERoundRobinSessionBuilder    b                  = new CMERoundRobinSessionBuilder( "CMETestSessBldr", _cfg, "7, 10, 9, 13, 121", sessMgr, handlers, multiplexReceivers, socketConfig, null );

        List<NonBlockingFastFixSocketSession> sessions = b.create();

        assertEquals( 15, sessions.size() );
        assertEquals( 9, multiplexReceivers[ 0 ].getNumSessions() );
        assertEquals( 6, multiplexReceivers[ 1 ].getNumSessions() );
    }

    private EventHandler[] makeHandlers( int tot ) {

        EventHandler[] h = new EventHandler[ tot ];

        for ( int i = 0; i < tot; i++ ) {
            h[ i ] = new DummyExchangeSession();
        }

        return h;
    }

    private MultiSessionThreadedReceiver[] makeMultiplexReceivers( int tot ) {
        MultiSessionThreadedReceiver[] h = new MultiSessionThreadedReceiver[ tot ];

        for ( int i = 0; i < tot; i++ ) {
            ControlThread t = new SingleElementControlThread( "CTL" + i, ThreadPriority.Other );
            h[ i ] = new MultiSessionThreadedReceiver( "MREC" + i, t );
        }

        return h;
    }
}
