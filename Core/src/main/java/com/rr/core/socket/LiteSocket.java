/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.socket;

import com.rr.core.lang.ReusableString;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public interface LiteSocket extends Closeable {

    /**
     * bind to local port
     *
     * @throws IOException
     */
    void bind( InetSocketAddress local ) throws IOException;

    void configureBlocking( boolean isBlocking ) throws IOException;

    boolean connect( SocketAddress addr ) throws IOException;

    boolean finishConnect() throws IOException;

    boolean getKeepAlive() throws SocketException;

    void setKeepAlive( boolean b ) throws SocketException;

    boolean getReuseAddress() throws SocketException;

    void setReuseAddress( boolean reuseAddr ) throws SocketException;

    int getSoLinger() throws SocketException;

    int getSoTimeout() throws SocketException;

    void setSoTimeout( int soTimeout ) throws SocketException;

    void info( ReusableString out );

    /**
     * create a new instance of the socket using the same socket/FD but with new buffer
     * <p>
     * once invoked then the old instance should not be used again
     * <p>
     * its only use is to pass the socket to new child session
     * <p>
     * would be simpler to allow the in/out buffer to be changed, but this code is critical and final buffer allows JIT optimisations
     *
     * @param inBuf
     * @param outBuf
     * @return
     */
    LiteSocket newInstance( ByteBuffer inBuf, ByteBuffer outBuf );

    int read() throws IOException;

    void setSoLinger( boolean b, int soLinger ) throws SocketException;

    void setTcpNoDelay( boolean tcpNoDelay ) throws SocketException;

    int write() throws IOException;
}
