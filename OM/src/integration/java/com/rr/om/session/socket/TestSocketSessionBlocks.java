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
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.model.NullEvent;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.*;
import com.rr.core.session.socket.PortOffset;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.session.socket.SocketSession;
import com.rr.core.utils.Percentiles;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.core.utils.Utils;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.om.processor.BaseProcessorTestCase;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @TODO refactor tests to use threads with CyclicBarrier / CountdownLatch .. and put check back in for non nio socket block
 */
public class TestSocketSessionBlocks extends BaseProcessorTestCase {

    private static final Logger _log = ConsoleFactory.console( TestSocketSessionBlocks.class, Level.info );

    static class ServerHandler implements EventHandler {

        public List<Event> _list = Collections.synchronizedList( new ArrayList<>() );

        @Override
        public String getComponentId() {
            return null;
        }

        @Override
        public void handle( Event msg ) {
            handleNow( msg );
        }

        @Override
        public void handleNow( Event msg ) {
            ThreadUtilsFactory.get().sleep( 1 );
            if ( msg != null && msg.getClass() != NullEvent.class && !(msg instanceof SessionStatusEvent) ) {
                _list.add( msg );
            }
        }

        @Override public boolean canHandle() { return true; }

        @Override public void threadedInit() { /* nada */ }
    }
    private final byte[] _buf1 = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
    private final byte[] _buf2 = new byte[ SizeConstants.DEFAULT_MAX_SESSION_BUFFER ];
    volatile boolean _clientConnected = false;
    volatile boolean _serverConnected = false;
    private ServerHandler _server = new ServerHandler();

    public void doSend( boolean nio ) throws PersisterException {
        int port = 9901 + PortOffset.getNext();

        SocketSession client = createClient( nio, port );
        SocketSession server = createServer( nio, port );

        try {
            sendOrders( client, server );
        } finally {
            client.stop();
            server.stop();
        }

        _log.info( "Finished" );
    }

    @Test
    public void testNIODelayedWritesOnFullSocket() throws PersisterException {
        doSend( true );
    }

    private SocketSession createClient( boolean nio, int port ) {
        String         name             = "TCLIENT";
        EventRouter    inboundRouter    = new PassThruRouter( "passThru", _proc );
        int            logHdrOut        = SocketSession.getDataOffset( name, false );
        Encoder        encoder          = FixTestUtils.getEncoder44( _buf1, logHdrOut );
        Decoder        decoder          = _decoder;
        Decoder        fullDecoder      = FixTestUtils.getFullDecoder44();
        ThreadPriority receiverPriority = ThreadPriority.Other;
        SocketConfig   socketConfig     = new SocketConfig( AllEventRecycler.class, false, new ViewString( "localhost" ), null, port );

        EventDispatcher dispatcher = new DirectDispatcher();

        socketConfig.setUseNIO( nio );
        socketConfig.setSoDelayMS( 0 );

        SocketSession sess = new SocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder, fullDecoder, receiverPriority );

        sess.setLogEvents( false );
        sess.setLogPojos( false );

        dispatcher.setHandler( sess );

        sess.registerConnectionListener( new ConnectionListener() {

            @Override
            public void connected( RecoverableSession session ) { _clientConnected = true; }

            @Override
            public void disconnected( RecoverableSession session ) { _clientConnected = false; }
        } );

        return sess;
    }

    private SocketSession createServer( boolean nio, int port ) {
        String      name          = "TSERVER3";
        EventRouter inboundRouter = new PassThruRouter( "passThru", _server );
        int         logHdrOut     = SocketSession.getDataOffset( name, false );
        Encoder     encoder       = FixTestUtils.getEncoder44( _buf2, logHdrOut );
        Decoder     decoder       = FixTestUtils.getOMSDecoder44();
        Decoder     fullDecoder   = FixTestUtils.getFullDecoder44();

        ThreadPriority receiverPriority = ThreadPriority.Other;
        SocketConfig   socketConfig     = new SocketConfig( AllEventRecycler.class, true, new ViewString( "localhost" ), null, port );

        socketConfig.setUseNIO( nio );
        socketConfig.setSoDelayMS( 0 );

        EventDispatcher dispatcher = new DirectDispatcher();

        SocketSession sess = new SocketSession( name, inboundRouter, socketConfig, dispatcher, encoder, decoder, fullDecoder, receiverPriority );

        sess.setLogEvents( false );
        sess.setLogPojos( false );

        dispatcher.setHandler( sess );

        sess.registerConnectionListener( new ConnectionListener() {

            @Override
            public void connected( RecoverableSession session ) {
                _serverConnected = true;
                ThreadUtilsFactory.get().sleep( 1000 );
            }

            @Override
            public void disconnected( RecoverableSession session ) { _serverConnected = false; }
        } );

        return sess;
    }

    private void sendOrders( SocketSession client, SocketSession server ) throws PersisterException {
        // start the sockets

        server.init();
        client.init();

        server.connect();
        client.connect();

        // check connected state at both ends
        long maxWait = 1000;

        long startConnect = ClockFactory.get().currentTimeMillis();

        while( !_clientConnected || !_serverConnected ) {

            ThreadUtilsFactory.get().sleep( 100 );

            if ( ClockFactory.get().currentTimeMillis() - startConnect > maxWait ) {
                assertTrue( "Failed to connect", false );
            }
        }

        int    numMsgsFromClient = 50000;
        long[] times             = new long[ numMsgsFromClient ];

        _log.info( "Send " + numMsgsFromClient );

        for ( int i = 0; i < numMsgsFromClient; ++i ) {
            // send fix messages

            ClientNewOrderSingleImpl cnos = FixTestUtils.getClientNOS( _decoder, "TST000000" + i, 100 + i, 25.12, _upMsgHandler );

            long start = Utils.nanoTime();
            client.handle( cnos );
            long end = Utils.nanoTime();

            times[ i ] = (int) ((end - start) / 1000);

            if ( client.getDelayedWriteCount() > 1 ) {
                numMsgsFromClient = i;
                break;
            }
        }

        _log.info( "Sent All, delayed=" + client.getDelayedWriteCount() + ", cnt=" + numMsgsFromClient );

        assertTrue( client.getDelayedWriteCount() > 0 );

        // check received fix message

        boolean found = false;

        long last      = ClockFactory.get().currentTimeMillis();
        int  lastCount = 0;

        while( !found ) {

            int count;

            synchronized( _server._list ) {
                count = _server._list.size();
            }

            if ( count >= numMsgsFromClient ) {
                found = true;
            } else if ( count == lastCount ) {
                if ( ClockFactory.get().currentTimeMillis() - last > maxWait ) {
                    assertTrue( "Failed to find required msgs, found=" + count, false );
                }

                ThreadUtilsFactory.get().sleep( 100 );

            } else {
                last      = ClockFactory.get().currentTimeMillis();
                lastCount = count;
            }
        }

        Percentiles p = new Percentiles( times );

        _log.info( "MicroSecond stats " + " count=" + times.length +
                   ", med=" + p.median() + ", ave=" + p.getAverage() +
                   ", min=" + p.getMinimum() + ", max=" + p.getMaximum() +
                   "\n" +
                   ", p99=" + p.calc( 99 ) + ", p95=" + p.calc( 95 ) +
                   ", p90=" + p.calc( 90 ) + ", p80=" + p.calc( 80 ) +
                   ", p70=" + p.calc( 70 ) + ", p50=" + p.calc( 50 ) + "\n" );
    }
}
