/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session.socket;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.Utils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * test using selector for reading from channel
 * <p>
 * Failed as randomly fails to connect when run via Gradle
 * Also selectors NOT used in currect socket comms so not worth further investigation to fix.
 */

@SuppressWarnings( "NonAtomicOperationOnVolatileField" ) @Ignore
public class TestSelector extends BaseTestCase {

    private static final int MSG_SIZE = 16;  // 8 bytes for long pingId, 8 bytes for nanoSent
    private final ByteBuffer          _inNativeByteBuffer    = ByteBuffer.allocateDirect( MSG_SIZE + 1 );
    private final ByteBuffer    _outNativeByteBuffer = ByteBuffer.allocateDirect( MSG_SIZE + 1 );
    int _runSize;
    private Logger _log = LoggerFactory.create( TestSelector.class );
    private       ServerSocketChannel _inServerSocketChannel = null;
    private       SocketChannel       _inChannel             = null;
    private       ServerSocket        _inServerSocket        = null;
    private       int                 _port;
    private       Selector            _inSelector;
    private       SelectionKey        _inSelectionKey;
    private       String        _host                = "127.0.0.1";
    private       int           _msDelay;
    private       SocketChannel _outChannel          = null;
    private volatile int _in;
    private volatile int _out;

    public void inClose() {
        try { if ( _inChannel != null ) _inChannel.close(); } catch( Exception e ) { /* NADA */ }
        try { if ( _inServerSocket != null ) _inServerSocket.close(); } catch( Exception e ) { /* NADA */ }
        try { if ( _inServerSocketChannel != null ) _inServerSocketChannel.close(); } catch( Exception e ) { /* NADA */ }
    }

    public void inConnect() throws IOException {

        _inServerSocketChannel = ServerSocketChannel.open();
        _inServerSocketChannel.configureBlocking( true );
        SocketAddress addr = new InetSocketAddress( _port );
        _inServerSocket = _inServerSocketChannel.socket();
        _inServerSocket.setReuseAddress( true );
        _inServerSocket.bind( addr );
        _inChannel = _inServerSocketChannel.accept();

        _log.log( Level.info, "IN accepted socket" );

        _inChannel.setOption( StandardSocketOptions.SO_REUSEADDR, true );
        _inChannel.configureBlocking( false );

        Socket socket = _inChannel.socket();
        socket.setKeepAlive( false );
        socket.setSoLinger( false, 0 );
        socket.setTcpNoDelay( true );

        while( !_inChannel.finishConnect() ) {
            try { Thread.sleep( 200 ); } catch( Exception e ) {  /* dont care */ }
        }

        _log.log( Level.info, "IN Connected " );

        _inSelector     = Selector.open();
        _inSelectionKey = _inChannel.register( _inSelector, SelectionKey.OP_READ );
    }

    public void outClose() {
        try { if ( _outChannel != null ) _outChannel.close(); } catch( Exception e ) { /* NADA */ }
    }

    public void outConnect() throws IOException {
        _outChannel = SocketChannel.open();

        Socket socket = _outChannel.socket();
        socket.setTcpNoDelay( true );
        socket.setKeepAlive( false );
        socket.setSoLinger( false, 0 );

        SocketAddress addr = new InetSocketAddress( _host, _port );

        _outChannel.connect( addr );

        _log.log( Level.info, "OUT initial connect" );

        _outChannel.setOption( StandardSocketOptions.SO_REUSEADDR, true );
        _outChannel.configureBlocking( false );
        while( !_outChannel.finishConnect() ) {
            try { Thread.sleep( 200 ); } catch( Exception e ) {  /* dont care */ }
        }

        _log.log( Level.info, "OUT Connected " );
    }

    @Test
    public void testOneWayNIO() throws Exception {
        _port    = 15880;
        _runSize = 100;
        _msDelay = 1;

        int maxTime = 30000;

        final CountDownLatch latch    = new CountDownLatch( 2 );
        final CountDownLatch received = new CountDownLatch( _runSize );

        Thread consumer = new Thread( () -> {
            try {
                inConnect();

                latch.countDown();
                latch.await();

                consume( _runSize, received );

                inClose();
            } catch( Exception e ) {
                fail( e.getMessage() );
            }
        }, "CONSUMER" );

        Thread producer = new Thread( () -> {
            try {
                outConnect();

                latch.countDown();
                latch.await();

                produce( _runSize );

                outClose();
            } catch( Exception e ) {
                fail( e.getMessage() );
            }
        }, "PRODUCER" );

        consumer.start();
        producer.start();

        received.await( maxTime, TimeUnit.MILLISECONDS );

        assertEquals( _runSize, _in );
        assertEquals( _runSize, _out );
    }

    protected final void writeSocket() throws IOException {

        long failWriteCount = 0;

        do {
            if ( _outChannel.write( _outNativeByteBuffer ) == 0 ) {
                if ( failWriteCount++ >= 5 ) {
                    try { Thread.sleep( 1 ); } catch( Exception e ) {  /* dont care */ }

                    _log.log( Level.info, "WARN: Delayed Write : possible slow consumer" );
                }
            }
        } while( _outNativeByteBuffer.hasRemaining() );
    }

    void consume( int max, final CountDownLatch received ) throws Exception {
        _in = 0;

        while( _in < max ) {
            int read = getMessage( MSG_SIZE );

            if ( read > 0 ) {
                long now = Utils.nanoTime();

                long id   = _inNativeByteBuffer.getLong();
                long sent = _inNativeByteBuffer.getLong();

                long delay = Math.abs( (sent - now) / 1000 );

                _log.log( Level.info, "Read id=" + id + ", usecs=" + delay );

                ++_in;

                received.countDown();
            }
        }
    }

    void produce( int max ) throws Exception {
        _out = 0;

        while( _out < max ) {
            send( _out, Utils.nanoTime() );

            ++_out;

            _log.log( Level.info, "Sent id=" + _out );

            try { Thread.sleep( _msDelay ); } catch( Exception e ) {  /* dont care */ }
        }
    }

    private int getMessage( int msgSize ) throws Exception {
        int totalRead = 0;
        int curRead;

        _inNativeByteBuffer.position( 0 );
        _inNativeByteBuffer.limit( msgSize );

        while( totalRead < msgSize ) {

            if ( _inSelector.select( 1000 ) <= 0 ) continue;
            if ( !_inSelectionKey.isValid() || !_inSelectionKey.isReadable() ) continue;
            Set<SelectionKey> readyKeys = _inSelector.selectedKeys();
            if ( !readyKeys.isEmpty() ) {
                Iterator<SelectionKey> iterator = readyKeys.iterator();
                while( iterator.hasNext() ) {
                    iterator.next();
                    iterator.remove();
                }

                curRead = _inChannel.read( _inNativeByteBuffer );

                if ( curRead == -1 ) throw new Exception( "Detected socket disconnect" );

                // spurious wakeup

                if ( curRead == 0 && totalRead == 0 ) {
                    return 0;
                }

                totalRead += curRead;
            }
        }

        _inNativeByteBuffer.flip();

        return totalRead;
    }

    private void send( long idx, long nanoTime ) throws IOException {

        _outNativeByteBuffer.clear();
        _outNativeByteBuffer.putLong( idx );
        _outNativeByteBuffer.putLong( nanoTime );

        _outNativeByteBuffer.flip();

        writeSocket();
    }
}
