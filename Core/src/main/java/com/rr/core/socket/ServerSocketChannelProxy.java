/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.socket;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;

public final class ServerSocketChannelProxy implements LiteServerSocket {

    private final ServerSocketChannel _ssc;
    private final ByteBuffer          _inBuf;
    private final ByteBuffer          _outBuf;

    public ServerSocketChannelProxy( ServerSocketChannel ssc, ByteBuffer inBuf, ByteBuffer outBuf ) {
        _ssc    = ssc;
        _inBuf  = inBuf;
        _outBuf = outBuf;
    }

    @Override public LiteSocket accept() throws IOException {
        return new SocketChannelProxy( _ssc.accept(), _inBuf, _outBuf );
    }

    @Override public void bind( SocketAddress saddr ) throws IOException {
        _ssc.socket().bind( saddr );
    }

    @Override public void configureBlocking( boolean isBlocking ) throws IOException {
        _ssc.configureBlocking( true );
    }

    @Override public void configureReuseAddress( boolean isReuse ) throws IOException {
        _ssc.setOption( StandardSocketOptions.SO_REUSEADDR, Boolean.valueOf( isReuse ) );
    }

    @Override public void close() throws IOException {
        _ssc.close();
    }
}
