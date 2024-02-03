/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.progs;

import com.rr.core.session.DisconnectedException;
import com.rr.core.utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class PingPongServer {

    private static final int MSG_SIZE = 16;  // 8 bytes for long pingId, 8 bytes for nanoSent
    protected final ByteBuffer _nativeByteBuffer = ByteBuffer.allocateDirect( MSG_SIZE + 1 );
    private ServerSocketChannel _serverSocketChannel = null;
    private SocketChannel       _channel             = null;
    private ServerSocket        _serverSocket        = null;
    private int                 _port;

    public static void main( String[] args ) {
        if ( args.length != 1 ) {
            System.out.println( "Usage : port to listen too" );
            Utils.exit( 99 );
        }

        PingPongServer tsl = new PingPongServer( Integer.parseInt( args[ 0 ] ) );

        try {

            tsl.connect();

            tsl.pong();

        } catch( Exception e ) {
            System.out.println( "TstSocketListener : exception " + e.getMessage() );
        }

        tsl.close();
    }

    public PingPongServer( int port ) {
        System.out.println( "Listen to " + port );
        _port = port;
    }

    public void close() {
        try { if ( _channel != null ) _channel.close(); } catch( Exception e ) { /* NADA */ }
        try { if ( _serverSocket != null ) _serverSocket.close(); } catch( Exception e ) { /* NADA */ }
        try { if ( _serverSocketChannel != null ) _serverSocketChannel.close(); } catch( Exception e ) { /* NADA */ }
    }

    public void connect() throws IOException {
        _serverSocketChannel = ServerSocketChannel.open();
        _serverSocketChannel.configureBlocking( true );
        SocketAddress addr = new InetSocketAddress( _port );
        _serverSocket = _serverSocketChannel.socket();
        _serverSocket.bind( addr );
        _channel = _serverSocketChannel.accept();
        _channel.configureBlocking( false );

        Socket socket = _channel.socket();
        _channel.socket().setTcpNoDelay( true );

        while( !_channel.finishConnect() ) {
            try { Thread.sleep( 200 ); } catch( Exception e ) {  /* dont care */ }
        }

        System.out.println( "Connected " );

        socket.setKeepAlive( false );
        socket.setSoLinger( false, 0 );
    }

    protected final void writeSocket() throws IOException {

        long failWriteCount = 0;

        do {
            if ( _channel.write( _nativeByteBuffer ) == 0 ) {
                if ( failWriteCount++ >= 5 ) {
                    try { Thread.sleep( 1 ); } catch( Exception e ) {  /* dont care */ }

                    System.out.println( "WARN: Delayed Write : possible slow consumer" );
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

    private void pong() throws Exception {

        while( true ) {
            int read = getMessage( MSG_SIZE );

            if ( read > 0 ) {
                sendBack( MSG_SIZE );
            } else {
                // if using selectors then would invoke select again
            }
        }
    }

    private void sendBack( int msgSize ) throws IOException {
        writeSocket();
    }
}
