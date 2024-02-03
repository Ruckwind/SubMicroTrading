/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.os;

import com.rr.core.lang.JavaSystemProperties;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.socket.LiteMulticastSocket;
import com.rr.core.socket.LiteServerSocket;
import com.rr.core.socket.LiteSocket;
import com.rr.core.utils.ReflectUtils;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class SocketFactory implements ISocketFactory {

    private static final Logger _log = LoggerFactory.create( SocketFactory.class );

    private final static SocketFactory  _instance = new SocketFactory();
    private final        ISocketFactory _factory;

    public static SocketFactory instance() { return _instance; }

    private SocketFactory() {
        String sockFacClass = JavaSystemProperties.getSocketFactoryClass();

        if ( sockFacClass == null )
            sockFacClass = AppProps.instance().getProperty( CoreProps.SOCKET_FACTORY, false, "com.rr.core.os.StandardSocketFactory" );

        _log.info( "SocketFactoryClass " + sockFacClass );

        _factory = ReflectUtils.create( sockFacClass );
    }

    @Override
    public LiteSocket createClientSocket( SocketConfig socketConfig, ByteBuffer inBuf, ByteBuffer outBuf ) throws IOException {
        return _factory.createClientSocket( socketConfig, inBuf, outBuf );
    }

    @Override
    public LiteMulticastSocket createMulticastSocket( SocketConfig socketConfig, ByteBuffer inBuf, ByteBuffer outBuf ) throws IOException {
        return _factory.createMulticastSocket( socketConfig, inBuf, outBuf );
    }

    @Override
    public LiteServerSocket createServerSocket( SocketConfig socketConfig, ByteBuffer inBuf, ByteBuffer outBuf ) throws IOException {
        return _factory.createServerSocket( socketConfig, inBuf, outBuf );
    }

    @Override
    public String getNicIp( SocketConfig socketConfig ) throws SocketException {
        return _factory.getNicIp( socketConfig );
    }
}
