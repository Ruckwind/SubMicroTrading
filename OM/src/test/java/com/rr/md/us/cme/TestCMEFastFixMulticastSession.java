/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.binary.fastfix.FastFixSocketSession;
import com.rr.core.dispatch.DirectDispatcher;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.*;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;
import com.rr.md.us.cme.reader.CMEFastFixDecoder;
import com.rr.md.us.cme.writer.CMEFastFixEncoder;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.om.processor.BaseProcessorTestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestCMEFastFixMulticastSession extends BaseProcessorTestCase {

    private static class StoreHandler implements EventHandler {

        private final Logger _log = LoggerFactory.create( StoreHandler.class );
        private final String      _name;
        private       List<Event> _store = new ArrayList<>();

        public StoreHandler( String name ) {
            super();
            _name = name;
        }

        @Override
        public String getComponentId() {
            return _name;
        }

        @Override
        public void handle( Event msg ) {
            handleNow( msg );
        }

        @Override
        public synchronized void handleNow( Event msg ) {
            _log.info( "Storing " + msg.toString() );
            if ( !(msg instanceof SessionStatusEvent) ) {
                _store.add( msg );
            }
        }

        @Override
        public boolean canHandle() {
            return true;
        }

        @Override
        public void threadedInit() {
            // nothing
        }

        public synchronized List<Event> getMessages() {
            return _store;
        }

        public synchronized int size() {
            return _store.size();
        }
    }

    volatile boolean _clientConnected = false;
    volatile boolean _serverConnected = false;

    private ZString[] _grps = { new ViewString( Utils.getLoopbackMultiCastGroup() ) };

    public void doSend( boolean nio, int serverPort ) throws PersisterException {
        StoreHandler storeHandler = new StoreHandler( "StoreHandler" );

        FastFixSocketSession client = createClient( nio, serverPort );
        FastFixSocketSession server = createServer( nio, serverPort, storeHandler );

        try {
            doActualSend( client, server, storeHandler );
        } finally {
            client.stop();
            server.stop();
        }
    }

    @Test
    public void testSendBlocking() throws PersisterException {
        doSend( false, 10001 );
    }

    @Test
    public void testSendNIO() throws PersisterException {
        doSend( true, 10001 );
    }

    private FastFixSocketSession createClient( boolean nio, int serverPort ) {
        String            name             = "TCLIENT";
        EventRouter       inboundRouter    = new PassThruRouter( "passThru", _proc );
        CMEFastFixEncoder encoder          = new CMEFastFixEncoder( "CMETstWriter", "data/cme/templates.xml", true );
        Decoder           decoder          = new CMEFastFixDecoder( "TstReader", "data/cme/templates.xml", -1, true );
        ThreadPriority    receiverPriority = ThreadPriority.Other;
        SocketConfig      socketConfig     = new SocketConfig( AllEventRecycler.class, true, new ViewString( "localhost" ), null, serverPort );

        setMCastSocketParams( socketConfig );

//        MessageDispatcher dispatcher        = new ThreadedDispatcher( new BlockingSyncQueue(), "CLIENT_DISPATCHER", ThreadPriority.Other );
        EventDispatcher dispatcher = new DirectDispatcher();

        socketConfig.setUseNIO( nio );
        socketConfig.setSoDelayMS( 0 );

        FastFixSocketSession sess = new CMEFastFixSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder, receiverPriority );

        dispatcher.setHandler( sess );

        sess.registerConnectionListener( new ConnectionListener() {

            @Override
            public void connected( RecoverableSession session ) { _clientConnected = true; }

            @Override
            public void disconnected( RecoverableSession session ) { _clientConnected = false; }
        } );

        return sess;
    }

    private FastFixSocketSession createServer( boolean nio, int serverPort, StoreHandler store ) {
        String            name          = "TSERVER2";
        EventRouter       inboundRouter = new PassThruRouter( "passThru", store );
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

        dispatcher.setHandler( sess );

        sess.registerConnectionListener( new ConnectionListener() {

            @Override
            public void connected( RecoverableSession session ) { _serverConnected = true; }

            @Override
            public void disconnected( RecoverableSession session ) { _serverConnected = false; }
        } );

        return sess;
    }

    private void doActualSend( FastFixSocketSession client, FastFixSocketSession server, StoreHandler storeHandler ) throws PersisterException {
        client.setLogEvents( true );
        server.setLogEvents( true );

        // start the sockets

        server.init();
        client.init();

        server.connect();
        client.connect();

        // check connected state at both ends
        long maxWait = 2000;
        long start   = ClockFactory.get().currentTimeMillis();

        while( !_clientConnected || !_serverConnected ) {

            ThreadUtilsFactory.get().sleep( 200 );

            if ( ClockFactory.get().currentTimeMillis() - start > maxWait ) {
                assertTrue( "Failed to connect", false );
            }
        }

        int numMsgsFromClient = 5;

        for ( int i = 0; i < numMsgsFromClient; ++i ) {
            // send fix messages

            MDIncRefreshImpl inc = FastFixTstUtils.makeMDIncRefresh( i, 3 );

            client.handle( inc );
        }

        // check received fix message

        boolean found = false;
        start = ClockFactory.get().currentTimeMillis();

        while( !found ) {

            ThreadUtilsFactory.get().sleep( 200 );

            if ( ClockFactory.get().currentTimeMillis() - start > maxWait ) {
                assertTrue( "Failed to find required msgs, found=" + _downQ.size(), false );
            }

            if ( storeHandler.size() == numMsgsFromClient ) {
                found = true;
            }
        }

        for ( int i = 0; i < numMsgsFromClient; ++i ) {
            // send fix messages

            MDIncRefreshImpl inc     = FastFixTstUtils.makeMDIncRefresh( i, 3 );
            MDIncRefreshImpl readInc = (MDIncRefreshImpl) storeHandler.getMessages().get( i );

            FastFixTstUtils.checkEqualsA( inc, readInc );
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
