/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.os;

import com.rr.core.session.socket.SocketConfig;
import com.rr.core.socket.LiteMulticastSocket;
import com.rr.core.socket.LiteServerSocket;
import com.rr.core.socket.LiteSocket;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;

public interface ISocketFactory {

    LiteSocket createClientSocket( SocketConfig socketConfig, ByteBuffer inBuf, ByteBuffer outBuf ) throws IOException;

    /**
     * create a multicast socket and connect to first grp
     * <p>
     * if not server socket then subscribe to all grps
     *
     * @param socketConfig
     * @param inBuf
     * @param outBuf
     * @return
     * @throws IOException
     */
    LiteMulticastSocket createMulticastSocket( SocketConfig socketConfig, ByteBuffer inBuf, ByteBuffer outBuf ) throws IOException;

    LiteServerSocket createServerSocket( SocketConfig socketConfig, ByteBuffer inBuf, ByteBuffer outBuf ) throws IOException;

    String getNicIp( SocketConfig socketConfig ) throws SocketException;
}
