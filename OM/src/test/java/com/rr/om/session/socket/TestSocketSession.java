/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.socket;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.dispatch.DirectDispatcher;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.ConnectionListener;
import com.rr.core.session.EventRouter;
import com.rr.core.session.PassThruRouter;
import com.rr.core.session.RecoverableSession;
import com.rr.core.session.socket.PortOffset;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.session.socket.SocketSession;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderSingleImpl;
import com.rr.om.processor.BaseProcessorTestCase;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class TestSocketSession extends BaseProcessorTestCase {

    private static final Logger _log = LoggerFactory.create( TestSocketSession.class );

    private static final int MAX_RECONNECT = 3;

    private final byte[] _buf1 = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
    private final byte[] _buf2 = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];

    private AtomicBoolean _clientConnected = new AtomicBoolean();
    private AtomicBoolean _serverConnected = new AtomicBoolean();

    public void doSend( boolean nio, int serverPort, int clientLocalPort ) throws PersisterException {

        int offset = PortOffset.getNext();

        serverPort += offset;
        if ( clientLocalPort > 0 ) clientLocalPort += offset;

        int connects = 0;

        while( ++connects <= MAX_RECONNECT ) {

            SocketSession client = createClient( nio, serverPort, clientLocalPort );
            SocketSession server = createServer( nio, serverPort );

            try {
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

                while( !_clientConnected.get() || !_serverConnected.get() ) {

                    ThreadUtilsFactory.get().sleep( 200 );

                    if ( ClockFactory.get().currentTimeMillis() - start > maxWait ) {
                        break;
                    }
                }

                if ( _clientConnected.get() && _serverConnected.get() ) {
                    sendEvents( client, server, maxWait );

                    return;
                }

                _log.info( "Failed to connect try next port, ATTEMPT #" + connects );

                ++serverPort;

                if ( clientLocalPort > 0 ) ++clientLocalPort;

            } catch( Exception e ) {
                fail( e.getMessage() );
            } finally {
                client.stop();

                ThreadUtilsFactory.get().sleep( 100 );

                server.stop();

                ThreadUtilsFactory.get().sleep( 100 );

                while( _clientConnected.get() || _serverConnected.get() ) {
                    ThreadUtilsFactory.get().sleep( 5000 );

                    _log.info( "TestSocketSession.doSend waiting for socket to fully disconnect" );
                }

                _proc.clear();
            }
        }

        fail( "Failed to connect" );
    }

    @Test
    public void testSendBlockingWithClientLocalBind() throws PersisterException {
        doSend( false, 14226, 14227 );
    }

    @Test
    public void testSendNIO() throws PersisterException {
        doSend( true, 14223, 0 );
    }

    @Ignore
    @Test
    public void testSendNIOWithClientLocalBind() throws PersisterException {
        // set to ignore as too many false failures

        doSend( true, 14220, 14221 );
    }

    private SocketSession createClient( boolean nio, int serverPort, int clientLocalPort ) {
        String         name             = "TCLIENT";
        EventRouter    inboundRouter    = new PassThruRouter( "passThru", _proc );
        int            logHdrOut        = SocketSession.getDataOffset( name, false );
        Encoder        encoder          = FixTestUtils.getEncoder44( _buf1, logHdrOut );
        Decoder        decoder          = _decoder;
        Decoder        fullDecoder      = FixTestUtils.getFullDecoder44();
        ThreadPriority receiverPriority = ThreadPriority.Other;
        SocketConfig   socketConfig     = new SocketConfig( AllEventRecycler.class, false, new ViewString( "localhost" ), null, serverPort );

        if ( clientLocalPort != 0 ) {
            socketConfig.setLocalPort( clientLocalPort );
            setLoopback( socketConfig );
        }

        //MessageDispatcher dispatcher        = new SessionThreadedDispatcher( new BlockingSyncQueue(), "CLIENT_DISPATCHER", ThreadPriority.Other );
        EventDispatcher dispatcher = new DirectDispatcher();

        socketConfig.setUseNIO( nio );
        socketConfig.setSoDelayMS( 0 );

        SocketSession sess = new SocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder, fullDecoder, receiverPriority );

        dispatcher.setHandler( sess );

        sess.registerConnectionListener( new ConnectionListener() {

            @Override
            public void connected( RecoverableSession session ) { _clientConnected.set( true ); }

            @Override
            public void disconnected( RecoverableSession session ) { _clientConnected.set( false ); }
        } );

        return sess;
    }

    private SocketSession createServer( boolean nio, int serverPort ) {
        String      name          = "TSERVER2";
        EventRouter inboundRouter = new PassThruRouter( "passThru", _proc );
        int         logHdrOut     = SocketSession.getDataOffset( name, false );
        Encoder     encoder       = FixTestUtils.getEncoder44( _buf2, logHdrOut );
        Decoder     decoder       = FixTestUtils.getOMSDecoder44();
        Decoder     fullDecoder   = FixTestUtils.getFullDecoder44();

        ThreadPriority receiverPriority = ThreadPriority.Other;
        SocketConfig   socketConfig     = new SocketConfig( AllEventRecycler.class, true, new ViewString( "localhost" ), null, serverPort );

        socketConfig.setUseNIO( nio );
        socketConfig.setSoDelayMS( 0 );

//        MessageDispatcher dispatcher        = new SessionThreadedDispatcher( new BlockingSyncQueue(), "SRV_DISPATCHER", ThreadPriority.Other );
        EventDispatcher dispatcher = new DirectDispatcher();

        SocketSession sess = new SocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder, fullDecoder, receiverPriority );

        dispatcher.setHandler( sess );

        sess.registerConnectionListener( new ConnectionListener() {

            @Override
            public void connected( RecoverableSession session ) { _serverConnected.set( true ); }

            @Override
            public void disconnected( RecoverableSession session ) { _serverConnected.set( false ); }
        } );

        return sess;
    }

    private void sendEvents( SocketSession client, SocketSession server, long maxWait ) throws PersisterException {

        synchronized( _downQ ) {
            _downQ.clear();
        }

        int numMsgsFromClient = 5;

        for ( int i = 0; i < numMsgsFromClient; ++i ) {
            // send fix messages

            ClientNewOrderSingleImpl cnos = FixTestUtils.getClientNOS( _decoder, "TST000000" + i, 100 + i, 25.12, _upMsgHandler );

            client.handle( cnos );
        }

        // check received fix message

        boolean                  found = false;
        long                     start = ClockFactory.get().currentTimeMillis();
        MarketNewOrderSingleImpl mnos  = null;

        while( !found ) {

            ThreadUtilsFactory.get().sleep( 200 );

            synchronized( _downQ ) {

                if ( ClockFactory.get().currentTimeMillis() - start > maxWait ) {
                    assertTrue( "OUT OF TIME : Failed to find required msgs, found=" + _downQ.size() + ", expected=" + numMsgsFromClient, false );
                }

                if ( _downQ.size() == numMsgsFromClient ) {
                    mnos  = (MarketNewOrderSingleImpl) getMessage( _downQ, MarketNewOrderSingleImpl.class );
                    found = true;
                }
            }
        }

        assertNotNull( mnos );
    }
}
