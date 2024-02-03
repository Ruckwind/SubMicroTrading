/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.fastfix.cme;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.binary.fastfix.FastFixSocketSession;
import com.rr.core.dispatch.DirectDispatcher;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.ConnectionListener;
import com.rr.core.session.EventRouter;
import com.rr.core.session.PassThruRouter;
import com.rr.core.session.RecoverableSession;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.md.us.cme.CMEFastFixSession;
import com.rr.md.us.cme.reader.CMEFastFixDecoder;
import com.rr.md.us.cme.writer.CMEFastFixEncoder;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.om.processor.BaseProcessorTestCase;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CMEFastFixMulticastSessionTest extends BaseProcessorTestCase {

    private static class EventHandler implements com.rr.core.model.EventHandler {

        private final String _name;
        private       int    _count = 0;

        public EventHandler( String name ) {
            super();
            _name = name;
        }

        @Override
        public boolean canHandle() {
            return true;
        }

        @Override
        public void handle( Event msg ) {
            handleNow( msg );
        }

        @Override
        public synchronized void handleNow( Event msg ) {
            ++_count;
        }

        @Override
        public String getComponentId() {
            return _name;
        }

        @Override
        public void threadedInit() {
            // nothing
        }

        public synchronized int count() {
            return _count;
        }
    }

    volatile boolean _clientConnected = false;
    volatile boolean _serverConnected = false;

    private ZString[] _grps = { new ViewString( "224.000.026.001" ) };

    public void doSend( boolean nio, int serverPort, int count ) throws PersisterException {
        EventHandler eventHandler = new EventHandler( "StoreHandler" );

        FastFixSocketSession server = createServer( nio, serverPort, eventHandler );

        try {
            doRead( server, eventHandler, count );
        } finally {
            server.stop();
        }
    }

    @Test
    public void testSendBlocking() throws PersisterException {
        doSend( false, 10001, 10000 );
    }

    @Test
    public void testSendNIO() throws PersisterException {
        doSend( true, 10001, 10000 );
    }

    private FastFixSocketSession createServer( boolean nio, int serverPort, EventHandler store ) {
        String            name          = "TSERVER2";
        EventRouter       inboundRouter = new PassThruRouter( "tst", store );
        CMEFastFixEncoder encoder       = new CMEFastFixEncoder( "CMETstWriter", "data/cme/templates.xml", true );
        Decoder           decoder       = new CMEFastFixDecoder( "TstReader", "data/cme/templates.xml", -1, true );

        ThreadPriority receiverPriority = ThreadPriority.Other;
        SocketConfig   socketConfig     = new SocketConfig( AllEventRecycler.class, false, new ViewString( "localhost" ), null, serverPort );

        socketConfig.setUseNIO( nio );
        socketConfig.setSoDelayMS( 0 );

        setMCastSocketParams( socketConfig );

        EventDispatcher dispatcher = new DirectDispatcher();
//        MessageDispatcher dispatcher        = new ThreadedDispatcher( new BlockingSyncQueue(), "SRV_DISPATCHER", ThreadPriority.Other );

        FastFixSocketSession sess = new CMEFastFixSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder, receiverPriority );

        sess.setLogEvents( true );
        sess.setLogPojos( true );

        dispatcher.setHandler( sess );

        sess.registerConnectionListener( new ConnectionListener() {

            @Override
            public void connected( RecoverableSession session ) { _serverConnected = true; }

            @Override
            public void disconnected( RecoverableSession session ) { _serverConnected = false; }
        } );

        return sess;
    }

    private void doRead( FastFixSocketSession server, EventHandler storeHandler, int events ) throws PersisterException {
        server.setLogEvents( true );

        // start the sockets

        server.init();

        server.connect();

        // check connected state at both ends
        long maxWait = 30000;
        long start   = ClockFactory.get().currentTimeMillis();

        while( !_clientConnected || !_serverConnected ) {

            ThreadUtilsFactory.get().sleep( 200 );

            if ( ClockFactory.get().currentTimeMillis() - start > maxWait ) {
                assertTrue( "Failed to connect", false );
            }
        }

        // check received fix message

        boolean found = false;
        start = ClockFactory.get().currentTimeMillis();

        while( !found ) {

            ThreadUtilsFactory.get().sleep( 200 );

            if ( ClockFactory.get().currentTimeMillis() - start > maxWait ) {
                assertTrue( "Failed to find required msgs, found=" + _downQ.size(), false );
            }

            if ( storeHandler.count() == events ) {
                found = true;
            }
        }
    }

    private void setMCastSocketParams( SocketConfig socketConfig ) {
        socketConfig.setDisableLoopback( false );
        socketConfig.setQOS( 2 );
        socketConfig.setTTL( 1 );
        socketConfig.setMulticast( true );
        socketConfig.setMulticastGroups( _grps );
    }
}
