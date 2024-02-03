/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.socket;

import com.rr.core.lang.ReusableString;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;

/**
 * lite socket
 *
 * @NOTE not threadsafe (expected to be used from single thread for read and single thread for write)
 */

public final class LinuxLiteSocketImpl implements LiteSocket {

    private enum State {
        Unconnected, Pending, Connected, Closed
    }

    private final ByteBuffer _inBuf;
    private final ByteBuffer _outBuf;
    private final Object     _stateLock = new Object();
    private final Object     _regLock   = new Object();

    private int               _fd;
    private InetSocketAddress _remoteAddress;
    private State             _state;
    private boolean           _blocking = true;

    /**
     * constructor invoked on an accepted socket
     */
    public LinuxLiteSocketImpl( int fd, InetSocketAddress isa, ByteBuffer inBuf, ByteBuffer outBuf ) {
        _inBuf         = inBuf;
        _outBuf        = outBuf;
        _fd            = fd;
        _remoteAddress = isa;
        _state         = State.Connected;
    }

    public LinuxLiteSocketImpl( ByteBuffer inBuf, ByteBuffer outBuf ) {
        _fd     = LinuxSocketImpl.socket( true, false );
        _state  = State.Unconnected;
        _inBuf  = inBuf;
        _outBuf = outBuf;
    }

    @Override public void close() {
        synchronized( _stateLock ) {
            switch( _state ) {
            case Closed:
                return;
            case Connected:
            case Pending:
                LinuxSocketImpl.close( _fd );
                _state = State.Closed;
                _fd = -1;
                break;
            case Unconnected:
                _state = State.Closed;
            }
        }
    }

    @Override public int read() throws IOException {

        if ( !isOpen() ) return -1;

        return (_blocking) ? readBlocking() : readNonBlock();
    }

    @Override public int write() throws IOException {

        if ( !isOpen() ) return -1;

        return (_blocking) ? writeBlocking() : writeNonBlock();
    }

    @Override public void configureBlocking( boolean isBlocking ) throws IOException {
        if ( !isOpen() )
            throw new ClosedChannelException();

        synchronized( _regLock ) {

            int res = LinuxSocketImpl.configureBlocking( _fd, isBlocking );

            if ( res != 0 ) {
                throw new IOException( "Failed to set socket to blocking, res=" + res );
            }

            _blocking = isBlocking;
        }
    }

    @Override public void setTcpNoDelay( boolean tcpNoDelay ) throws SocketException {
        LinuxSocketImpl.setOption( _fd, SocketOptions.TCP_NODELAY, (tcpNoDelay) ? 1 : 0 );
    }

    @Override public boolean getKeepAlive() throws SocketException {
        int v = LinuxSocketImpl.getOption( _fd, SocketOptions.SO_KEEPALIVE );

        return v != 0;
    }

    @Override public void setKeepAlive( boolean alive ) throws SocketException {
        LinuxSocketImpl.setOption( _fd, SocketOptions.SO_KEEPALIVE, (alive) ? 1 : 0 );
    }

    @Override public int getSoLinger() throws SocketException {
        return LinuxSocketImpl.getOption( _fd, SocketOptions.SO_LINGER );
    }

    @Override public void setSoLinger( boolean b, int soLinger ) throws SocketException {
        LinuxSocketImpl.setOption( _fd, SocketOptions.SO_LINGER, soLinger );
    }

    @Override public boolean getReuseAddress() throws SocketException {
        int v = LinuxSocketImpl.getOption( _fd, SocketOptions.SO_REUSEADDR );
        return v != 0;
    }

    @Override public void setReuseAddress( final boolean reuseAddr ) throws SocketException {
        LinuxSocketImpl.setOption( _fd, SocketOptions.SO_REUSEADDR, (reuseAddr) ? 1 : 0 );
    }

    @Override public int getSoTimeout() {
        return 0; // non blocking
    }

    @Override public void setSoTimeout( int soTimeout ) {
        // nothing to do - non blocking
    }

    @Override public void bind( InetSocketAddress local ) {
        if ( local.isUnresolved() )
            throw new UnresolvedAddressException();
        LinuxSocketImpl.bind( _fd, local.getAddress(), local.getPort() );
    }

    @Override public boolean connect( SocketAddress addr ) throws IOException {
        int trafficClass = 0;

        ensureOpenAndUnconnected();
        InetSocketAddress isa = LinuxSocketImpl.checkAddress( addr );
        SecurityManager   sm  = System.getSecurityManager();

        if ( sm != null )
            sm.checkConnect( isa.getAddress().getHostAddress(), isa.getPort() );

        synchronized( _regLock ) {
            int res;

            try {
                synchronized( _stateLock ) {
                    if ( !isOpen() ) {
                        return false;
                    }
                }

                for ( ; ; ) {
                    InetAddress ia = isa.getAddress();
                    if ( ia.isAnyLocalAddress() ) ia = InetAddress.getLocalHost();
                    res = LinuxSocketImpl.connect( _fd, ia, isa.getPort(), trafficClass );
                    if ( (res == LinuxSocketImpl.INTERRUPTED) && isOpen() )
                        continue;
                    break;
                }
            } catch( IOException e ) {
                close();
                throw e;
            }

            synchronized( _stateLock ) {
                _remoteAddress = isa;
                if ( res > 0 ) {
                    _state = State.Connected;
                    return true;
                }
                if ( !isBlocking() )
                    _state = State.Pending;
                else
                    assert false;
            }
        }

        return false;
    }

    @Override public boolean finishConnect() throws IOException {
        synchronized( _stateLock ) {
            if ( !isOpen() )
                throw new ClosedChannelException();
            if ( _state == State.Connected )
                return true;
            if ( _state != State.Pending )
                throw new NoConnectionPendingException();
        }
        int res = 0;
        try {
            try {
                synchronized( _regLock ) {
                    synchronized( _stateLock ) {
                        if ( !isOpen() ) {
                            return false;
                        }
                    }
                    if ( !isBlocking() ) {
                        res = nonBlockingConnect( res );
                    } else {
                        res = blockingConnect( res );
                    }
                }
            } finally {
                assert LinuxSocketImpl.check( res );
            }
        } catch( IOException e ) {
            close();
            throw e;
        }

        if ( res > 0 ) {
            synchronized( _stateLock ) {
                _state = State.Connected;
            }
            return true;
        }

        return false;
    }

    @Override public void info( ReusableString out ) {
        int flags = LinuxSocketImpl.getFlags( _fd );

        out.append( ", fd=" ).append( _fd ).append( ", flags=" ).append( flags );
    }

    @Override public LiteSocket newInstance( ByteBuffer inBuf, ByteBuffer outBuf ) {
        LinuxLiteSocketImpl newInst = new LinuxLiteSocketImpl( _fd, _remoteAddress, inBuf, outBuf );
        newInst._blocking = _blocking;
        newInst._state    = _state;
        return newInst;
    }

    public InetSocketAddress getRemoteAddress() {
        return _remoteAddress;
    }

    public boolean isBlocking() {
        synchronized( _regLock ) {
            return _blocking;
        }
    }

    public boolean isConnected() {
        synchronized( _stateLock ) {
            return (_state == State.Connected);
        }
    }

    public boolean isConnectionPending() {
        synchronized( _stateLock ) {
            return (_state == State.Pending);
        }
    }

    private int blockingConnect( int res ) throws IOException {
        for ( ; ; ) {
            res = LinuxSocketImpl.checkConnect( _fd, true, false );
            if ( res == 0 ) {
                continue;
            }
            if ( (res == LinuxSocketImpl.INTERRUPTED) && isOpen() )
                continue;
            break;
        }
        return res;
    }

    private void ensureOpenAndUnconnected() throws IOException {
        synchronized( _stateLock ) {
            if ( !isOpen() )
                throw new ClosedChannelException();
            if ( _state == State.Connected )
                throw new AlreadyConnectedException();
            if ( _state == State.Pending )
                throw new ConnectionPendingException();
        }
    }

    private boolean isOpen() {
        return _fd >= 0;
    }

    private int nonBlockingConnect( int res ) throws IOException {
        for ( ; ; ) {
            res = LinuxSocketImpl.checkConnect( _fd, false, false );
            if ( (res == LinuxSocketImpl.INTERRUPTED) && isOpen() )
                continue;
            break;
        }
        return res;
    }

    private int readBlocking() throws IOException {
        int n;

        for ( ; ; ) {
            n = LinuxSocketImpl.readIntoNativeBuffer( _fd, _inBuf );

            if ( (n == LinuxSocketImpl.INTERRUPTED) && isOpen() ) {
                continue;
            }

            return LinuxSocketImpl.normalize( n );
        }
    }

    private int readNonBlock() throws IOException {
        int n = LinuxSocketImpl.readIntoNativeBuffer( _fd, _inBuf );

        if ( n == LinuxSocketImpl.INTERRUPTED ) {
            return 0;
        }

        return LinuxSocketImpl.normalize( n );
    }

    private int writeBlocking() throws IOException {
        int n;

        for ( ; ; ) {
            n = LinuxSocketImpl.writeFromNativeBuffer( _fd, _outBuf );

            if ( (n == LinuxSocketImpl.INTERRUPTED) && isOpen() )
                continue;

            return LinuxSocketImpl.normalize( n );
        }
    }

    private int writeNonBlock() throws IOException {
        int n = LinuxSocketImpl.writeFromNativeBuffer( _fd, _outBuf );

        if ( n == LinuxSocketImpl.INTERRUPTED )
            return 0;

        return LinuxSocketImpl.normalize( n );
    }
}
