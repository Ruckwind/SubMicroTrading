/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.socket;

import com.rr.core.dummy.warmup.DummyAppProperties;
import com.rr.core.dummy.warmup.TestStats;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.os.SocketFactory;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.utils.Percentiles;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.Utils;
import org.junit.Assert;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

// test using selector for reading from channel

public class MulticastPingServer {

    static String  _nicIP;
    static boolean _spin;
    static boolean _disableLookback;
    static boolean _debug = false;
    static int     _qos;
    static int     _msgSize;
    String _mcastGroupAddressOut = "226.1.1.1";
    String _mcastGroupAddressIn  = "226.1.1.2";

    // args count, delayUSEC, NIC  
    public static void main( String args[] ) {

        LoggerFactory.setDebug( true );
        StatsMgr.setStats( new TestStats() );
        Map<String, String> p = new HashMap<String, String>();

        try {
            DummyAppProperties.testInit( p );
        } catch( Exception e ) {
            Assert.fail( e.getMessage() );
        }

        if ( args.length != 9 ) {
            System.out.println( "Args : msgsToSend  delayUSEC  NIC  cpuMask   spin{1|0}  disableLoopback{1|0} debug{1|0} qos msgSize" );
            Utils.exit( -1 );
        }

        int msgsToSend = Integer.parseInt( args[ 0 ] );
        int usecDelay  = Integer.parseInt( args[ 1 ] );
        _nicIP = args[ 2 ];
        int mask = Integer.parseInt( args[ 3 ] );
        _spin            = Integer.parseInt( args[ 4 ] ) == 1;
        _disableLookback = Integer.parseInt( args[ 5 ] ) == 1;
        _debug           = Integer.parseInt( args[ 6 ] ) == 1;
        _qos             = Integer.parseInt( args[ 7 ] );
        _msgSize         = Integer.parseInt( args[ 8 ] );

        try {
            MulticastPingServer ps = new MulticastPingServer();
            ps.doRun( 1, msgsToSend, 14880, 14981, mask, usecDelay );
            ps.doRun( 1, msgsToSend, 14880, 14981, mask, usecDelay );
            ps.doRun( 1, msgsToSend, 14880, 14981, mask, usecDelay );
            ps.doRun( 1, msgsToSend, 14880, 14981, mask, usecDelay );
            ps.doRun( 1, msgsToSend, 14880, 14981, mask, usecDelay );
        } catch( IOException e ) {
            System.out.println( "ERROR " + e.getMessage() );
            e.printStackTrace();
        }
    }

    private void doRun( int numProducers, int sendPerProducer, int port, int echoPort, int mask, int delayUSEC ) throws IOException {

        CyclicBarrier cb = new CyclicBarrier( numProducers );

        Producer[] producers = new Producer[ numProducers ];
        Thread[]   tProd     = new Thread[ numProducers ];

        for ( int i = 0; i < numProducers; ++i ) {
            Consumer consumer = new Consumer( "C" + i, echoPort + i, sendPerProducer );
            producers[ i ] = new Producer( "P" + i, sendPerProducer, port + i, cb, consumer, mask, delayUSEC );
            tProd[ i ]     = new Thread( producers[ i ], "PRODUCER" + i );
        }

        for ( int i = 0; i < numProducers; ++i ) {
            tProd[ i ].start();
        }

        for ( int i = 0; i < numProducers; ++i ) {
            try {
                tProd[ i ].join();
            } catch( InterruptedException e ) {
                // dont care
            }
        }

        for ( int i = 0; i < numProducers; ++i ) {
            producers[ i ].logStats();
        }
    }

    private class Producer implements Runnable {

        private final String              _id;
        private final ByteBuffer          _inBuf     = ByteBuffer.allocateDirect( _msgSize );
        private final ByteBuffer          _outBuf    = ByteBuffer.allocateDirect( _msgSize );
        private final int                 _send;
        private final int                 _port;
        private final Consumer            _consumer;
        private final int                 _mask;
        private final int                 _delayUSEC;
        private       LiteMulticastSocket _outSocket = null;
        private       CyclicBarrier       _cb;
        private volatile int _out;

        public Producer( String id, int send, int port, CyclicBarrier cb, Consumer consumer, int mask, int delayUSEC ) {
            _id        = id;
            _send      = send;
            _port      = port;
            _cb        = cb;
            _consumer  = consumer;
            _mask      = mask;
            _delayUSEC = delayUSEC;
        }

        public void logStats() {
            _consumer.logStats();
        }

        public void outClose() {
            try { if ( _outSocket != null ) _outSocket.close(); } catch( Exception e ) { /* NADA */ }
            _consumer.inClose();
        }

        public void outConnect() throws IOException {
            SocketConfig sc = new SocketConfig();
            sc.setServer( true );
            sc.setPort( _port );
            sc.setDisableLoopback( _disableLookback );
            sc.setQOS( _qos );
            sc.setTTL( 1 );
            sc.setNic( new ViewString( _nicIP ) );
            ZString[] mcastAddrs = { new ViewString( _mcastGroupAddressOut ) };
            sc.setMulticastGroups( mcastAddrs );

            _outSocket = SocketFactory.instance().createMulticastSocket( sc, _inBuf, _outBuf );
            _outSocket.configureBlocking( _spin == false );
        }

        @Override
        public void run() {
            try {
                _cb.await();

                ThreadUtilsFactory.get().setPriority( Thread.currentThread(), ThreadPriority.HIGHEST, _mask );

                outConnect();

                produce( _send );

                outClose();
            } catch( Exception e ) {
                Assert.fail( e.getMessage() );
            }
        }

        protected final void writeSocket() throws IOException {

            _outSocket.write();
        }

        void produce( int max ) throws Exception {
            _out = 0;

            while( _out < max ) {
                long ts = Utils.nanoTime();
                send( _out, ts );

                ++_out;

                if ( _debug ) System.out.println( _id + " Sent id=" + _out + ", time=" + ts );

                _consumer.waitEcho();

                if ( _delayUSEC > 0 ) {
                    ThreadUtilsFactory.get().sleepMicros( _delayUSEC );
                }
            }
        }

        private void send( long idx, long nanoTime ) throws IOException {

            _outBuf.clear();
            _outBuf.putLong( idx );
            _outBuf.putLong( nanoTime );
            _outBuf.position( _msgSize );
            _outBuf.flip();

            writeSocket();
        }
    }

    private class Consumer {

        private final ByteBuffer          _inBuf    = ByteBuffer.allocateDirect( _msgSize );
        private final ByteBuffer          _outBuf   = ByteBuffer.allocateDirect( _msgSize );
        private final int                 _port;
        private       LiteMulticastSocket _inSocket = null;
        private       String              _id;
        private volatile int   _in;
        private          int[] _times;

        public Consumer( String id, int port, int cnt ) throws IOException {
            _id    = id;
            _port  = port;
            _times = new int[ cnt ];
            inConnect();
        }

        public void inClose() {
            try { if ( _inSocket != null ) _inSocket.close(); } catch( Exception e ) { /* NADA */ }
        }

        public void inConnect() throws IOException {

            SocketConfig sc = new SocketConfig();
            sc.setServer( false );
            sc.setPort( _port );
            sc.setDisableLoopback( _disableLookback );
            sc.setQOS( _qos );
            sc.setTTL( 1 );
            sc.setNic( new ViewString( _nicIP ) );
            ZString[] mcastAddrs = { new ViewString( _mcastGroupAddressIn ) };
            sc.setMulticastGroups( mcastAddrs );

            _inSocket = SocketFactory.instance().createMulticastSocket( sc, _inBuf, _outBuf );
            _inSocket.configureBlocking( _spin == false );
        }

        public void logStats() {
            Percentiles    p   = new Percentiles( _times );
            ReusableString buf = new ReusableString();
            p.logStats( buf );
            System.out.println( buf.toString() );
        }

        public void waitEcho() throws Exception {

            getMessage( _msgSize );
            _inBuf.flip();

            while( _inBuf.hasRemaining() ) {

                long now      = Utils.nanoTime();
                int  startPos = _inBuf.position();

                long id   = _inBuf.getLong();
                long sent = _inBuf.getLong();

                int delay = (int) Math.abs( (sent - now) / 1000 );

                if ( _in < _times.length ) _times[ _in ] = delay;

                if ( _debug ) System.out.println( "Consumer " + _id + ", Read id=" + id + ", usecs=" + delay );

                ++_in;

                _inBuf.position( startPos + _msgSize );
            }
        }

        private void getMessage( int msgSize ) throws Exception {
            _inBuf.clear();

            while( true ) {

                _inSocket.read();

                if ( _inBuf.position() >= msgSize ) {
                    break;
                }
            }
        }
    }
}
