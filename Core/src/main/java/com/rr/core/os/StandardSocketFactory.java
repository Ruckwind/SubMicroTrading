/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.os;

import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.session.socket.SocketConfig;
import com.rr.core.socket.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;

public class StandardSocketFactory implements ISocketFactory {

    private static final Logger _log = LoggerFactory.create( StandardSocketFactory.class );

    static {
        NativeHooksImpl.instance(); // force native library load if appropriate
        _log.info( "SocketFactory() USING standard java sockets" );
    }

    @Override
    public LiteServerSocket createServerSocket( SocketConfig socketConfig, ByteBuffer inBuf, ByteBuffer outBuf ) throws IOException {
        return new ServerSocketChannelProxy( ServerSocketChannel.open(), inBuf, outBuf );
    }

    @Override
    public LiteSocket createClientSocket( SocketConfig socketConfig, ByteBuffer inBuf, ByteBuffer outBuf ) throws IOException {
        return new SocketChannelProxy( SocketChannel.open(), inBuf, outBuf );
    }

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
    @Override
    public LiteMulticastSocket createMulticastSocket( SocketConfig socketConfig, ByteBuffer inBuf, ByteBuffer outBuf ) throws IOException {

        String nicIP        = getNicIp( socketConfig );
        String sendMcastGrp = socketConfig.getMulticastGroups()[ 0 ].toString();

        LiteMulticastSocket adapter;

        adapter = new MulticastSocketAdapter( socketConfig.getPort(),
                                              socketConfig.isDisableLoopback(),
                                              socketConfig.getQOS(),
                                              socketConfig.getTTL(),
                                              inBuf,
                                              outBuf );

        InetSocketAddress group = new InetSocketAddress( sendMcastGrp, socketConfig.getPort() );

        adapter.connect( group );

        _log.info( "Multicast port=" + socketConfig.getPort() + ", CONNECTED=" + sendMcastGrp + ", nicIP=" + nicIP );

        if ( socketConfig.isServer() == false ) {
            ZString[] grps = socketConfig.getMulticastGroups();

            for ( ZString grp : grps ) {
                adapter.joinGroup( grp.toString(), nicIP );

                _log.info( "Multicast port=" + socketConfig.getPort() + ", JOINED=" + grp + ", nicIP=" + nicIP );
            }
        }

        return adapter;
    }

    @Override
    public String getNicIp( SocketConfig socketConfig ) throws SocketException {
        String nicIP = null;
        if ( socketConfig.getNic() != null ) {
            String nic = socketConfig.getNic().toString();

            NetworkInterface nif = NetworkInterface.getByName( nic );
            if ( nif != null ) {
                Enumeration<InetAddress> nifAddresses = nif.getInetAddresses();
                InetAddress              nicAddr      = nifAddresses.nextElement();
                nicIP = nicAddr.getHostAddress();
            } else {
                nicIP = nic;
            }
        }
        return nicIP;
    }
}
