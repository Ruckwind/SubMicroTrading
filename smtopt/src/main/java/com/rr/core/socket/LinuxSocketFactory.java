/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.socket;

import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.os.ISocketFactory;
import com.rr.core.session.socket.SocketConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.util.Enumeration;

public class LinuxSocketFactory implements ISocketFactory {

    private static final Logger _log = LoggerFactory.create( LinuxSocketFactory.class );

    private static final ErrorCode LSF100 = new ErrorCode( "LSF100", "LinuxSocketFactory exception" );

    static {
        _log.info( "SocketFactory() USING non thread safe LinuxHackedSocketChannelImpl" );

        try { // force sun library load

            ServerSocketChannel.open();
        } catch( IOException e ) {
            _log.info( "SocketFactory() failed to force library load " + e.getMessage() );
        }

        try {
            System.loadLibrary( "submicroopt" );

            LinuxSocketInit.init();

        } catch( Exception e ) {
            _log.error( LSF100, "Failed setup native sockets", e );
        }
    }

    @Override
    public LiteServerSocket createServerSocket( SocketConfig socketConfig, ByteBuffer inBuf, ByteBuffer outBuf ) {
        return new LinuxLiteServerSocketImpl( inBuf, outBuf );
    }

    @Override
    public LiteSocket createClientSocket( SocketConfig socketConfig, ByteBuffer inBuf, ByteBuffer outBuf ) {
        return new LinuxLiteSocketImpl( inBuf, outBuf );
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

        if ( nicIP == null ) {
            _log.info( "Default NIC to loopback 127.0.0.1" );

            nicIP = "127.0.0.1";
        }

        adapter = new LinuxLiteMulticastSocket( socketConfig.getPort(),
                                                socketConfig.isDisableLoopback(),
                                                socketConfig.getQOS(),
                                                socketConfig.getTTL(),
                                                inBuf,
                                                outBuf,
                                                nicIP,
                                                sendMcastGrp
        );

        _log.info( "LINUX Multicast port=" + socketConfig.getPort() + ", CONNECTED=" + sendMcastGrp + ", nicIP=" + nicIP );

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
