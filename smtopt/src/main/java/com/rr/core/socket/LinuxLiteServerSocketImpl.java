/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetBoundException;

/**
 * lite server socket
 *
 * @NOTE not threadsafe (expected to be used from single thread for read and single thread for write)
 */

public final class LinuxLiteServerSocketImpl implements LiteServerSocket {

    private final Object _stateLock = new Object();
    private final ByteBuffer _inBuf;
    private final ByteBuffer _outBuf;
    private          int     _fd;
    private volatile boolean _open  = true;
    private          boolean _bound = false;

    public LinuxLiteServerSocketImpl( ByteBuffer inBuf, ByteBuffer outBuf ) {
        _inBuf  = inBuf;
        _outBuf = outBuf;

        _fd = LinuxSocketImpl.socket( true, true );
    }

    @Override
    public LiteSocket accept() throws IOException {
        if ( !isOpen() )
            throw new ClosedChannelException();
        if ( !isBound() )
            throw new NotYetBoundException();

        LiteSocket sc;

        int                 acceptedSocketFD = 0;
        InetSocketAddress[] isaa             = new InetSocketAddress[ 1 ];

        try {
            for ( ; ; ) {
                acceptedSocketFD = LinuxSocketImpl.accept( _fd, isaa );
                if ( (acceptedSocketFD == LinuxSocketImpl.INTERRUPTED) && isOpen() )
                    continue;
                break;
            }
        } finally {
            assert LinuxSocketImpl.check( acceptedSocketFD );
        }

        if ( acceptedSocketFD < 0 )
            return null;

        InetSocketAddress isa = isaa[ 0 ];
        sc = new LinuxLiteSocketImpl( acceptedSocketFD, isa, _inBuf, _outBuf );
        SecurityManager sm = System.getSecurityManager();
        if ( sm != null ) {
            try {
                sm.checkAccept( isa.getAddress().getHostAddress(), isa.getPort() );
            } catch( SecurityException e ) {
                sc.close();
                throw e;
            }
        }
        return sc;
    }

    @Override
    public synchronized void bind( SocketAddress local ) throws IOException {
        int backlog = 50;

        if ( local == null )
            local = new InetSocketAddress( 0 );

        try {
            if ( !isOpen() )
                throw new ClosedChannelException();
            if ( isBound() )
                throw new SocketException( "Already bound" );
            InetSocketAddress isa = LinuxSocketImpl.checkAddress( local );
            SecurityManager   sm  = System.getSecurityManager();
            if ( sm != null )
                sm.checkListen( isa.getPort() );
            LinuxSocketImpl.bind( _fd, isa.getAddress(), isa.getPort() );
            LinuxSocketImpl.listen( _fd, backlog < 1 ? 50 : backlog );
        } catch( Exception e ) {
            LinuxSocketImpl.throwIOException( e );
        }
        _bound = true;
    }

    @Override
    public void configureBlocking( boolean isBlocking ) {
        LinuxSocketImpl.configureBlocking( _fd, isBlocking );
    }

    @Override public void configureReuseAddress( final boolean isReuse ) throws IOException {
        LinuxSocketImpl.setOption( _fd, SocketOptions.SO_REUSEADDR, 1 );
    }

    @Override
    public void close() {
        _open = false;
        synchronized( _stateLock ) {
            if ( _fd >= 0 ) {
                LinuxSocketImpl.close( _fd );
                _fd = -1;
            }
        }
    }

    private boolean isBound() {
        return _bound;
    }

    private boolean isOpen() {
        return _open;
    }
}
