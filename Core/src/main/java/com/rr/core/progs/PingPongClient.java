/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.progs;

import com.rr.core.session.DisconnectedException;
import com.rr.core.utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PingPongClient {

    private static final int MSG_SIZE = 16;  // 8 bytes for long pingId, 8 bytes for nanoSent
    protected final ByteBuffer _nativeByteBuffer = ByteBuffer.allocateDirect( MSG_SIZE + 1 );
    private String _host;
    private int    _msDelay;
    private SocketChannel _channel = null;
    private int           _port;

    public static void main( String[] args ) {

        if ( args.length != 2 ) {
            System.out.println( "Usage : [pingServerIP] [port]" );
            Utils.exit( 99 );
        }

        PingPongClient tsl = new PingPongClient( args[ 0 ], Integer.parseInt( args[ 1 ] ), 1000 );

        try {

            tsl.connect();

            tsl.ping();

        } catch( Exception e ) {
            System.out.println( "TstSocketListener : exception " + e.getMessage() );
        }

        tsl.close();
    }

    public PingPongClient( String host, int port, int msDelay ) {
        System.out.println( "Connect to host=" + host + " port=" + port );
        _host    = host;
        _port    = port;
        _msDelay = msDelay;
    }

    public void close() {
        try { if ( _channel != null ) _channel.close(); } catch( Exception e ) { /* NADA */ }
    }

    public void connect() throws IOException {
        _channel = SocketChannel.open();

        Socket socket = _channel.socket();
        socket.setTcpNoDelay( true );
        socket.setKeepAlive( false );
        socket.setSoLinger( false, 0 );

        SocketAddress addr = new InetSocketAddress( _host, _port );

        _channel.connect( addr );
        _channel.configureBlocking( false );
        while( !_channel.finishConnect() ) {
            try { Thread.sleep( 200 ); } catch( Exception e ) {  /* dont care */ }
        }

        System.out.println( "Connected " );
    }

    protected final void writeSocket() throws IOException {

        long failWriteCount = 0;

        do {
            if ( _channel.write( _nativeByteBuffer ) == 0 ) {
                if ( failWriteCount++ >= 5 ) {
                    try { Thread.sleep( 1 ); } catch( Exception e ) {  /* dont care */ }

                    System.out.println( "WARN Delayed Write : possible slow consumer" );
                }
            }
        } while( _nativeByteBuffer.hasRemaining() );
    }

    private int getMessage( int msgSize ) throws Exception {
        int totalRead = 0;
        int curRead;

        _nativeByteBuffer.position( 0 );
        _nativeByteBuffer.limit( msgSize );

        while( totalRead < msgSize ) {

            curRead = _channel.read( _nativeByteBuffer );

            if ( curRead == -1 ) throw new DisconnectedException( "Detected socket disconnect" );

            // spurious wakeup

            if ( curRead == 0 && totalRead == 0 ) {
                return 0;
            }

            totalRead += curRead;
        }

        _nativeByteBuffer.flip();

        return totalRead;
    }

    private void ping() throws Exception {

        long idx = 0;

        while( true ) {
            send( idx, Utils.nanoTime() );

            int read;

            do {
                read = getMessage( MSG_SIZE );

                // if read==0 and using selectors then would invoke select again
            } while( read == 0 );

            long now = Utils.nanoTime();

            long id   = _nativeByteBuffer.getLong();
            long sent = _nativeByteBuffer.getLong();

            long delay = (sent - now) / 1000;

            System.out.println( "Ping id=" + id + ", usecs=" + delay );

            try { Thread.sleep( _msDelay ); } catch( Exception e ) {  /* dont care */ }
        }
    }

    private void send( long idx, long nanoTime ) throws IOException {

        _nativeByteBuffer.clear();
        _nativeByteBuffer.putLong( idx );
        _nativeByteBuffer.putLong( nanoTime );

        _nativeByteBuffer.flip();

        writeSocket();
    }
}
