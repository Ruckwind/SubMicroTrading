/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.socket;

import com.rr.core.lang.ReusableString;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public final class SocketChannelProxy implements LiteSocket {

    private final SocketChannel _sc;
    private final ByteBuffer    _inBuf;
    private final ByteBuffer    _outBuf;

    public SocketChannelProxy( SocketChannel sc, ByteBuffer inBuf, ByteBuffer outBuf ) {
        _sc     = sc;
        _inBuf  = inBuf;
        _outBuf = outBuf;
    }

    @Override public void close() throws IOException {
        _sc.close();
    }

    @Override public int read() throws IOException {
        return _sc.read( _inBuf );
    }

    @Override public int write() throws IOException {
        return _sc.write( _outBuf );
    }

    @Override public void configureBlocking( boolean isBlocking ) throws IOException {
        _sc.configureBlocking( isBlocking );
    }

    @Override public void setTcpNoDelay( boolean tcpNoDelay ) throws SocketException {
        _sc.socket().setTcpNoDelay( tcpNoDelay );
    }

    @Override public boolean getKeepAlive() throws SocketException {
        return _sc.socket().getKeepAlive();
    }

    @Override public void setKeepAlive( boolean b ) throws SocketException {
        _sc.socket().setKeepAlive( b );
    }

    @Override public int getSoLinger() throws SocketException {
        return _sc.socket().getSoLinger();
    }

    @Override public void setSoLinger( boolean b, int soLinger ) throws SocketException {
        _sc.socket().setSoLinger( b, soLinger );
    }

    @Override public boolean getReuseAddress() throws SocketException {
        return _sc.socket().getReuseAddress();
    }

    @Override public void setReuseAddress( boolean reuseAddr ) throws SocketException {
        _sc.socket().setReuseAddress( reuseAddr );
    }

    @Override public int getSoTimeout() throws SocketException {
        return _sc.socket().getSoTimeout();
    }

    @Override public void setSoTimeout( int soTimeout ) throws SocketException {
        _sc.socket().setSoTimeout( soTimeout );
    }

    @Override public void bind( InetSocketAddress sa ) throws IOException {
        _sc.socket().bind( sa );
    }

    @Override public boolean connect( SocketAddress addr ) throws IOException {
        return _sc.connect( addr );
    }

    @Override public boolean finishConnect() throws IOException {
        return _sc.finishConnect();
    }

    @Override public void info( ReusableString out ) {
        // nothing
    }

    @Override public LiteSocket newInstance( ByteBuffer inBuf, ByteBuffer outBuf ) {
        SocketChannelProxy newInst = new SocketChannelProxy( _sc, inBuf, outBuf );
        return newInst;
    }
}
