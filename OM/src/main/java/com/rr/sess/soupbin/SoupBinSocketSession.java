/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.sess.soupbin;

import com.rr.core.codec.*;
import com.rr.core.collections.EventQueue;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.os.SocketFactory;
import com.rr.core.persister.PersisterException;
import com.rr.core.session.*;
import com.rr.core.socket.LiteServerSocket;
import com.rr.core.socket.LiteSocket;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.om.session.AbstractNonBlockingSocketSession;

/**
 * Session for SOUP BINARY V3
 */

public class SoupBinSocketSession extends AbstractNonBlockingSocketSession<SoupBinSessionController, SoupBinSocketConfig> {

    private static final int INITIAL_READ_BYTES = 30;

    private final boolean _isChildSession;

    private final boolean _allowMultipleChildren;

    private int _nextChildId;

    public SoupBinSocketSession( String name,
                                 EventRouter inboundRouter,
                                 SoupBinSocketConfig fixConfig,
                                 MultiSessionThreadedDispatcher dispatcher,
                                 MultiSessionThreadedReceiver receiver,
                                 FixEncoder encoder,
                                 FixDecoder decoder,
                                 Decoder fullDecoder,
                                 EventQueue dispatchQueue ) throws SessionException, PersisterException {

        super( name, inboundRouter, fixConfig, dispatcher, receiver, encoder, decoder, fullDecoder, INITIAL_READ_BYTES, dispatchQueue );

        _allowMultipleChildren = fixConfig.isAllowMultipleChildren();

        _isChildSession = false;
    }

    @SuppressWarnings( "unchecked" )
    public SoupBinSocketSession( final SoupBinSocketSession parent, final LiteSocket socketChannel ) throws SessionException, PersisterException {

        super( parent.getComponentId() + "_" + parent.nextChildId(),
               parent.getInboundRouter(),
               (SoupBinSocketConfig) parent.getConfig(),
               (MultiSessionThreadedDispatcher) parent.getOutboundDispatcher(),
               (MultiSessionThreadedReceiver) parent.getReciever(),
               ((SoupBinEncoder) (parent._encoder)).newInstance(),
               ((SoupBinDecoder) (parent._decoder)).newInstance(),
               ((FixDecoder) (parent._fullDecoder)).newInstance(),
               INITIAL_READ_BYTES,
               parent.getSendQueue().newInstance() );

        setLoggedNotInSession( false );

        setLogStats( parent._logStats );
        setLogEvents( parent._logEvents );
        setLogPojos( parent._logPojos );

        _socketChannel = socketChannel.newInstance( _inNativeByteBuffer, _outNativeByteBuffer );

        init();

        setConnectAttempts( 0 );

        if ( _socketChannel != null ) {
            _log.info( "SoupBinSocketSession " + getComponentId() + " connected " );

            setSessionState( SessionState.Connected );

        } else {
            _log.info( "SoupBinSocketSession " + getComponentId() + " socket is null, stopping " );

            stop();
        }

        _isChildSession = true;

        _allowMultipleChildren = false;
    }

    @Override protected final SoupBinSessionController createSessionContoller() {
        return new SoupBinSessionController( this, _config );
    }

    @Override protected final int setOutSeqNum( final Event msg ) {
        final int nextOut = _controller.getAndIncNextOutSeqNum();
        msg.setMsgSeqNum( nextOut );
        return nextOut;
    }

    @Override public int getLastSeqNumProcessed() {
        return _controller.getNextExpectedInSeqNo() - 1;
    }

    @Override public final boolean isSessionMessage( Event msg ) {
        switch( msg.getReusableType().getSubId() ) {
        case EventIds.ID_HEARTBEAT:
        case EventIds.ID_TESTREQUEST:
        case EventIds.ID_RESENDREQUEST:
        case EventIds.ID_SESSIONREJECT:
        case EventIds.ID_SEQUENCERESET:
        case EventIds.ID_LOGOUT:
        case EventIds.ID_LOGON:
            return true;
        }
        return false;
    }

    @Override public boolean isChildSession() {
        return _isChildSession;
    }

    @Override protected final void logInEvent( ZString event ) {
        if ( _logEvents ) {
            _log.infoLarge( event );
        }
    }

    @Override protected final void logOutEvent( ZString event ) {
        if ( _logEvents ) {
            _log.infoLarge( event );
        }
    }

    @Override protected LiteSocket serverNIOConnect() {

        if ( !_isChildSession ) {
            if ( !_allowMultipleChildren ) {
                return super.serverNIOConnect();
            }

            _log.info( "serverNIOConnect() parent session " + getComponentId() + " will spawn new session per connection" );

            while( !isStopping() && !isPaused() && _socketConfig.isOpenUTC( ClockFactory.get().currentTimeMillis() ) ) {
                LiteServerSocket serverSocketChannel = null;
                LiteSocket       socketChannel       = null;
                try {
                    serverSocketChannel = SocketFactory.instance().createServerSocket( _socketConfig, _inNativeByteBuffer, _outNativeByteBuffer );
                    serverSocketChannel.configureBlocking( true );
                    bindServerInterface( serverSocketChannel );
                    setSessionState( SessionState.Listening );
                    socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking( false );
                    while( !socketChannel.finishConnect() ) {
                        ThreadUtilsFactory.get().sleep( 200 );     // Spin until connection is established
                    }
                    setupChannel( socketChannel );
                    setSocketOptions( socketChannel );

                    spawn( socketChannel );

                } catch( Exception e ) {
                    _log.error( SessionConstants.ERR_OPEN_SOCK, getComponentId() + " Failed to create server NIO socket: " + e.getMessage() + ", host=" + _socketConfig.getHostname() + ", port=" + _socketConfig.getPort(), e );
                } finally {
                    cleanUp( serverSocketChannel );
                }
            }

        } else {
            _log.info( "serverNIOConnect() Child session " + getComponentId() + " cannot use reconnect, must respawn from parent, issuing stop on child session" );
            stop();
        }

        return null;
    }

    private synchronized int nextChildId() {
        return ++_nextChildId;
    }

    private void spawn( final LiteSocket socketChannel ) throws PersisterException, SessionException {
        new SoupBinSocketSession( this, socketChannel );
    }
}
