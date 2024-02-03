/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.socket;

import com.rr.core.dummy.warmup.DummyAppProperties;
import com.rr.core.dummy.warmup.TestStats;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.stats.StatsMgr;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.os.SocketFactory;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.utils.Percentiles;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.Utils;
import org.junit.Assert;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

// test using selector for reading from channel

public class TCPPingClient {

    static String _serverIP;

    static boolean _debug = false;
    static int     _qos;
    static int     _msgSize;
    final    int        _port        = 4321;
    final    ByteBuffer _inBuf       = ByteBuffer.allocateDirect( _msgSize );
    final    ByteBuffer _outBuf      = ByteBuffer.allocateDirect( _msgSize );
    LiteSocket _socket = null;
    volatile boolean    _isConnected = false;

    public static void main( String args[] ) {

        LoggerFactory.setDebug( true );
        StatsMgr.setStats( new TestStats() );
        Map<String, String> p = new HashMap<String, String>();

        try {
            DummyAppProperties.testInit( p );
        } catch( Exception e ) {
            Assert.fail( e.getMessage() );
        }

        if ( args.length != 6 ) {
            System.out.println( "Args : msgsToSend  serverIP  cpuMask  debug{1|0}  qos  msgSize" );
            Utils.exit( -1 );
        }

        int msgsToSend = Integer.parseInt( args[ 0 ] );
        _serverIP = args[ 1 ];
        int mask = Integer.parseInt( args[ 2 ] );
        _debug   = Integer.parseInt( args[ 3 ] ) == 1;
        _qos     = Integer.parseInt( args[ 4 ] );
        _msgSize = Integer.parseInt( args[ 5 ] );

        try {
            TCPPingClient ps = new TCPPingClient();
            ps.connect();
            ps.doRun( msgsToSend, mask );
            ps.close();
        } catch( IOException e ) {
            System.out.println( "ERROR " + e.getMessage() );
            e.printStackTrace();
        }
    }

    {
        Map<String, String> p = new HashMap<String, String>();

        try {
            DummyAppProperties.testInit( p );
        } catch( Exception e ) {
            Assert.fail( e.getMessage() );
        }
    }

    public void close() {
        try { if ( _socket != null ) _socket.close(); } catch( Exception e ) { /* NADA */ }
    }

    public void connect() throws IOException {
        SocketConfig sc = new SocketConfig();
        sc.setPort( _port );
        sc.setQOS( _qos );
        sc.setTTL( 1 );
        sc.setHostname( new ViewString( _serverIP ) );
        sc.setUseNIO( true );
        sc.setSoDelayMS( 0 );

        _socket = SocketFactory.instance().createClientSocket( sc, _inBuf, _outBuf );

        InetAddress   ia   = InetAddress.getByName( _serverIP );
        SocketAddress addr = new InetSocketAddress( ia, _port );

        _socket.configureBlocking( true );
        _socket.connect( addr );
        _socket.configureBlocking( false );

        _socket.setTcpNoDelay( true );
        _socket.setKeepAlive( false );

//        _socket.setSoLinger( true, soLinger );
//        _socket.setSoTimeout( soTimeout );

        _isConnected = true;

        System.out.println( "Connected " );
    }

    private void doRun( int sendPerProducer, int mask ) {
        int totalConsume = sendPerProducer;

        Thread tCons;

        CyclicBarrier cb = new CyclicBarrier( 1 );

        EchoBack echo = new EchoBack( "ECHO" );

        Consumer c = new Consumer( "C", totalConsume, cb, echo, mask );
        tCons = new Thread( c, "CONSUMER" );

        tCons.start();
        try {
            tCons.join();
        } catch( InterruptedException e ) {
            // dont care
        }
    }

    private class EchoBack {

        private final String _id;

        public EchoBack( String id ) {
            _id = id;
        }

        public void send( int idx, long nanoTime ) throws IOException {

            _outBuf.clear();
            _outBuf.putInt( idx );
            _outBuf.putLong( nanoTime );
            _outBuf.position( _msgSize );

            _outBuf.flip();

            writeSocket();

            if ( _debug ) System.out.println( _id + " ECHO Sent id=" + idx + ", time=" + nanoTime );
        }

        protected final void writeSocket() throws IOException {

            _socket.write();
        }
    }

    private class Consumer implements Runnable {

        private final int           _consume;
        private final CyclicBarrier _cb;
        private final EchoBack      _echo;
        private final int           _mask;
        private       String        _id;
        private volatile int _in;

        public Consumer( String id, int consume, CyclicBarrier cb, EchoBack echo, int mask ) {
            _id      = id;
            _consume = consume;
            _cb      = cb;
            _echo    = echo;
            _mask    = mask;
        }

        @Override
        public void run() {
            try {
                _cb.await();

                ThreadUtilsFactory.get().setPriority( Thread.currentThread(), ThreadPriority.HIGHEST, _mask );

                consume( _consume );

            } catch( Exception e ) {
                Assert.fail( e.getMessage() );
            }
        }

        void consume( int max ) throws Exception {
            _in = 0;

            while( !_isConnected ) {
                ThreadUtilsFactory.get().sleep( 100 );
            }

            int[] dur = new int[ max ];

            while( _in < max ) {
                getMessage( _msgSize );
                _inBuf.flip();

                while( _inBuf.hasRemaining() ) {

                    int startPos = _inBuf.position();

                    int  id   = _inBuf.getInt();
                    long sent = _inBuf.getLong();

                    long ts = Utils.nanoTime();
                    _echo.send( id, sent );
                    long sendNanos = Math.abs( Utils.nanoTime() - ts );

                    dur[ _in ] = (int) sendNanos;

                    if ( _debug ) System.out.println( "Consumer " + _id + ", Read id=" + id + ", time=" + sent + ", sendNanos=" + sendNanos );

                    ++_in;

                    _inBuf.position( startPos + _msgSize );
                }
            }

            Percentiles    p = new Percentiles( dur );
            ReusableString s = new ReusableString();

            p.logStats( s );

            System.out.println( s.toString() );
        }

        private void getMessage( int msgSize ) throws Exception {
            _inBuf.clear();

            while( true ) {

                _socket.read();

                if ( _inBuf.position() >= msgSize ) {
                    break;
                }
            }
        }
    }
}
