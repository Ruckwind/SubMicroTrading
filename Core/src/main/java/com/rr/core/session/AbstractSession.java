/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.codec.Decoder;
import com.rr.core.codec.Encoder;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.codec.RuntimeEncodingException;
import com.rr.core.component.CompRunState;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.*;
import com.rr.core.logger.ExceptionTrace;
import com.rr.core.logger.Level;
import com.rr.core.model.Event;
import com.rr.core.model.MsgFlag;
import com.rr.core.persister.DummyPersister;
import com.rr.core.persister.PersistentReplayListener;
import com.rr.core.persister.Persister;
import com.rr.core.persister.PersisterException;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.recovery.dma.DMARecoverySessionContext;
import com.rr.core.recycler.EventRecycler;
import com.rr.core.utils.*;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class AbstractSession extends CommonAbstractSession implements RecoverableSession {

    private static final ZString REJ_DISCONNECTED = new ViewString( "Rejected as not connected" );
    private static final ZString DROP_MSG         = new ViewString( "Session dropping session message as not connected : " );
    private static final ZString ENCODE_ERR       = new ViewString( "Encoding error : " );
    private static final ZString SMT_SEND_ERR     = new ViewString( "SMT send error : " );
    private static final int THREAD_READ_THROTTLE_MS = 1;
    private static int _nextIntId = 0;
    protected final EventRouter     _inboundRouter;
    protected final ZString         _logInHdr;
    protected final ZString         _logOutHdr;
    protected final EventDispatcher _outboundDispatcher;
    protected final Encoder         _encoder;
    protected final Decoder         _decoder;
    protected final Decoder         _fullDecoder;
    protected final boolean _markConfirmationEnabled;
    protected final ReusableString _logInErrMsg  = new ReusableString( 100 );
    protected final ReusableString _logOutErrMsg = new ReusableString( 100 );
    protected final ReusableString _logOutMsg    = new ReusableString( 100 );
    private final   ReusableString  _logInbound  = new ReusableString();
    private final   ReusableString  _logOutbound = new ReusableString();
    private final ThroughPutMeter _inThruMeter;
    private final SessionConfig _config;
    private final EventRecycler _inboundRecycler;
    private final EventRecycler _outboundRecycler;
    
    protected       boolean         _logStats    = true;
    protected     boolean       _logEvents             = true;
    protected     boolean       _logPojos              = false;
    protected Session   _chainSession = null;
    protected Persister _inPersister  = new DummyPersister();
    protected Persister _outPersister = new DummyPersister();
    protected         Throttler _throttler = new NullThrottler();
    private int  _readSpinDelay    = THREAD_READ_THROTTLE_MS;
    private int  _readSpinThrottle = 0; // default is OFF
    private long _cnt;
    private       boolean         _rejectIfNotConnected = true;
    private       boolean       _dropCopyInboundEvents = true;                  // send copy of inbound events to chain session ... chain session must NOT recycle
    private       SessionState  _state                 = SessionState.Disconnected;
    private DMARecoverySessionContext _recoverySessionContextIn;
    private DMARecoverySessionContext _recoverySessionContextOut;
    private volatile boolean _outRecovered = false;
    private volatile boolean _inRecovered  = false;
    private long _lastSent = 0;
    private long _reads    = 0;
    private long _writes   = 0;

    public static synchronized int nextIntId() {
        return ++_nextIntId;
    }

    public static int getLogHdrLen( String name, boolean isInbound ) {
        if ( isInbound ) return (" IN [" + name + "]: ").length();

        return (" OUT [" + name + "]: ").length();
    }

    public static int getDataOffset( String name, boolean isInbound ) {
        return AbstractSession.getLogHdrLen( name, isInbound );
    }

    public AbstractSession( String name,
                            EventRouter inboundRouter,
                            SessionConfig config,
                            EventDispatcher dispatcher,
                            Encoder encoder,
                            Decoder decoder,
                            Decoder fullDecoder ) {
        super( name );

        _config        = config;
        _inboundRouter = inboundRouter;

        _outboundDispatcher = dispatcher;
        _encoder            = encoder;
        _decoder            = decoder;
        _fullDecoder        = fullDecoder;

        try {
            _inboundRecycler  = ReflectUtils.create( config.getRecycler() );
            _outboundRecycler = ReflectUtils.create( config.getRecycler() );
        } catch( Exception e ) {
            throw new SMTRuntimeException( "Must configure valid recycler with session " + name );
        }

        _inThruMeter = new ThroughPutMeter( name + "InMeter", config.getStatsBlockSize() );

        _logInHdr  = new ViewString( "  IN [" + name + "]: " );
        _logOutHdr = new ViewString( " OUT [" + name + "]: " );

        _logInbound.copy( _logInHdr );
        _logOutbound.copy( _logOutHdr );

        _markConfirmationEnabled = config.isMarkConfirmationEnabled();
        _dropCopyInboundEvents   = config.isDropCopyInboundEvents();

        _readSpinThrottle = config.getReadSpinThrottle();
        _readSpinDelay    = config.getReadSpinDelayMS();

        if ( config.getMaxMsgsPerSecond() > 0 ) {
            setThrottle( config.getMaxMsgsPerSecond(), 0, 1000 );
        }

        ShutdownManager.instance().register( "Stop" + getComponentId(), () -> {

            try {
                ReusableString msg = new ReusableString();
                finalLog( msg );
                _log.info( msg );
                stop();
                disconnect( false );
            } catch( Throwable t ) {
                _log.error( SHUT_ERR, "Unexpected exception in session shutdown: " + t.getMessage(), t );
            }

        }, ShutdownManager.Priority.Medium );
    }

    @Override public void init() throws PersisterException {
        setCompRunState( CompRunState.Initialised );
    }

    @Override public final void handle( final Event msg ) {
        _outboundDispatcher.dispatch( msg );
    }    
    
    @Override public String info() {
        String chain = (_chainSession == null) ? "N/A" : _chainSession.getComponentId();

        return (this.getClass().getSimpleName() + " id=" + getComponentId() + "  " + _config.info() + ", " + _outboundDispatcher.info() +
                ", logEvents=" + _logEvents + ", logStats=" + _logStats + ", chainSession=" + chain + ", reads=" + _reads + ", writes=" + _writes);
    }

    @Override public SessionConfig getConfig() { return _config; }

    @Override public void init( SMTStartContext ctx, CreationPhase creationPhase ) {
        try {
            init();
        } catch( PersisterException e ) {
            throw new SMTRuntimeException( getComponentId() + " failed to initialise : " + e.getMessage(), e );
        }
    }

    @Override public void prepare() { /* nothing */ }
    
    
    @Override public void stop() {
        if ( isStopping() )
            return;

        setCompRunState( CompRunState.Stopping );

        notifyAll();

        _inThruMeter.finish();
    }    
    
    @Override public void setLogEvents( boolean on ) {
        _logEvents = on;
    }

    @Override public void recover( final DMARecoveryController ctl ) {

        _log.info( getComponentId() + " recover, kick off REC threads" );
        setInboundRecoveryFinished( false );
        setOutboundRecoveryFinished( false );

        Thread inRec = new Thread( () -> recoverInbound( ctl ), "REC_IN_" + getComponentId() );

        Thread outRec = new Thread( () -> recoverOutBound( ctl ), "REC_OUT_" + getComponentId() );

        inRec.start();
        outRec.start();
    }    @Override public boolean isLogEvents() {
        return _logEvents;
    }

    @Override public synchronized void disconnect( boolean tryReconnect ) {

        if ( getSessionState() != SessionState.Disconnected ) {

            setSessionState( SessionState.Disconnected );

            try {
                disconnectCleanup();
            } catch( Exception e ) {
                _log.warn( getComponentId() + " AbstractSession exception on disconnectCleanup : " + e.getMessage() );
                ExceptionTrace.getStackTrace( _logOutMsg, e.getCause() );
                _log.info( _logOutMsg );
            }

            if ( !tryReconnect && !getCompRunState().isPaused() ) {
                setPaused( true );
            }

            notifyAll();
        }
    }

    @Override public boolean isRejectOnDisconnect() {
        return _rejectIfNotConnected;
    }

    @Override public final boolean isConnected() {
        return getSessionState() == SessionState.Connected;
    }

    @Override public final SessionState getSessionState() {
        return _state;
    }

    @Override public boolean isLoggedIn()                       { return isConnected(); }

    /**
     * all messages must pass through handleNow (or handleDisconnectedNow) which applies configured throttler
     */
    @Override public final void handleNow( Event msg ) {

        if ( msg != null ) {

            if ( isConnected() ) {                      // IMPORTANT DONT USE loggedIn here, as in logger in process need send msgs !
                try {
                    _throttler.checkThrottle( msg );

                    sendNow( msg );

                    postSend( msg );

                    ++_writes;

                } catch( IOException e ) {
                    handleOutboundError( e, msg );

                    sendChain( msg, true );

                } catch( SMTRuntimeException e ) {

                    handleSendSMTException( e, msg );

                } catch( Exception e ) {
                    // not a socket error dont drop socket
                    logOutboundError( e, msg );
                }

            } else {

                sendChain( msg, false );

                if ( discardOnDisconnect( msg ) == false ) {

                    if ( rejectMessageUpstream( msg, REJ_DISCONNECTED ) ) {
                        // message recycled by successful reject processing
                    } else {
                        // unable to reject message so dispatch to queue

                        if ( _outboundDispatcher.canQueue() ) {
                            _outboundDispatcher.dispatch( msg );
                        } else {
                            _logOutMsg.copy( DROP_MSG );
                            msg.dump( _logOutMsg );
                            _log.info( _logOutMsg );

                            outboundRecycle( msg );
                        }
                    }
                } else {
                    if ( msg.getReusableType() != CoreReusableType.NullEvent ) {
                        _logOutMsg.copy( DROP_MSG );
                        msg.dump( _logOutMsg );
                        _log.info( _logOutMsg );

                        outboundRecycle( msg );
                    }
                }
            }
        }
    }

    @Override public void setRejectOnDisconnect( boolean reject ) {
        _rejectIfNotConnected = reject;
    }

    @Override public boolean getRejectOnDisconnect() {
        return _rejectIfNotConnected;
    }

    @Override public void processIncoming() {

        try {
            while( ! isStopping() ) { // dont use the sync metod isStopping, will hit sync barrier when end msg
                processNextInbound();

                readThrottle();
            }

        } catch( SessionException e ) {

            logInboundError( e );
            disconnect( !e.isForcedLogout() ); // pause session on forced logout

        } catch( DisconnectedException e ) {

            if ( ! isStopping() && _state != SessionState.Disconnected ) logDisconnected( e );

            disconnect( true );

        } catch( IOException e ) {

            if ( ! isStopping() && _state != SessionState.Disconnected ) logInboundError( e );

            disconnect( true );

        } catch( RuntimeDecodingException e ) {
            logInboundDecodingError( e );
        } catch( Exception e ) {
            // not a socket error dont drop socket
            logInboundError( e );
        }
    }

    @Override public void setLogStats( boolean logStats ) { _logStats = logStats; }

    @Override public void startWork() {
        setCompRunState( CompRunState.Started );
        connect();
    }

    @Override public void stopWork() { stop(); }

    @Override public String toString() {
        ReusableString msg = new ReusableString();
        finalLog( msg );
        return msg.toString();
    }

    public EventRouter getInboundRouter() { return _inboundRouter; }

    public boolean isLogPojos()                      { return _logPojos; }

    public void setLogPojos( boolean logPojos )      { _logPojos = logPojos; }    @Override public final void inboundRecycle( Event msg )  { _inboundRecycler.recycle( msg ); }

    public final void logDisconnected( Exception e ) {
        _logInErrMsg.copy( getComponentId() ).append( ' ' ).append( e.getMessage() );
        _log.warn( _logInErrMsg );
    }    @Override public final void outboundRecycle( Event msg ) { _outboundRecycler.recycle( msg ); }

    public void logInboundDecodingError( RuntimeDecodingException e ) {
        _logInErrMsg.copy( getComponentId() ).append( ' ' ).append( e.getMessage() );
        _logInErrMsg.append( ' ' ).append( e.getFixMsg() );
        _log.error( ERR_IN_MSG, _logInErrMsg );
    }    @Override public final boolean rejectMessageUpstream( Event msg, ZString errMsg ) {

        // dont reject if posDup is true OR message is tagged as reconciliation

        if ( _rejectIfNotConnected &&
             msg.isFlagSet( MsgFlag.PossDupFlag ) == false &&
             msg.isFlagSet( MsgFlag.Reconciliation ) == false ) {

            Event synthMessage = _encoder.unableToSend( msg, errMsg );

            if ( synthMessage != null ) {

                _inboundRouter.handle( synthMessage );

                // DONT RECYCLE MESSAGE IS ATTACHED TO Reject

                return true;
            }
        }

        return false;
    }

    /*
     * PRIVATE METHODS
     */
    public void logInboundError( Exception e ) {
        _logInErrMsg.copy( getComponentId() ).append( ' ' ).append( e.getMessage() );
        _log.error( ERR_IN_MSG, _logInErrMsg, e );
    }

    public void logOutboundEncodingError( RuntimeEncodingException e ) {
        _logOutErrMsg.copy( getComponentId() ).append( ' ' ).append( e.getMessage() ).append( ":: " );
        _log.error( ERR_OUT_MSG, _logOutErrMsg, e );
    }

    @Override public void setPaused( boolean paused ) {
        if ( paused ) { // trying to pause

            if ( setCompRunState( CompRunState.PassivePause ) ) {
                _log.info( "Session " + id() + " now paused so disconnect if connected" );

                if ( isConnected() ) {
                    aboutToForceDisconnect();

                    disconnect( false );
                }
            }

        } else { // trying to unpause

            if ( setCompRunState( CompRunState.Active ) ) {
                if ( ! isConnected() ) {
                    _log.info( "Session " + id() + " now unpaused : connect will occur async" );

                    getConnectionListenerSet().forEach( ( l) -> l.disconnected( this ) ); // not connected and need to reconnect, disconnected handler will try async reconnect
                }
            }
        }

        synchronized( this ) {
            this.notifyAll();
        }
    }

    protected void aboutToForceDisconnect() {
        // placeholder
    }

    protected void connected()              { /* for specialisation */ }

    protected abstract void disconnectCleanup();

    protected void disconnected()           { /* for specialisation */ }

    protected void finalLog( ReusableString msg ) {
        msg.append( "Session #" + getIntId() + " " + getComponentId() + ", reads=" + _reads + ", writes=" + _writes );
    }

    protected final EventDispatcher getOutboundDispatcher()  { return _outboundDispatcher; }

    protected long getReads()                                   { return _reads; }

    protected final EventRouter getRouter() { return _inboundRouter; }    @Override public final void dispatchInbound( Event msg ) {
        Event tmp;
        while( msg != null ) {
            tmp = msg.getNextQueueEntry();
            if ( tmp != null ) {
                msg.detachQueue();
            }
            msg.setEventHandler( this );
            if ( _dropCopyInboundEvents ) {
                sendChain( msg, false );
            }
            _inboundRouter.handle( msg );
            msg = tmp;
        }
        incrementInboundCount();
    }

    protected Level getSessionChangeStateLogLevel() {
        if ( AppState.isTerminal() ) return Level.info;

        return Level.vhigh;
    }

    protected void handleOutboundError( IOException e, Event msg ) {
        logOutboundError( e, msg );
        disconnect( true );
    }

    protected final void handleSendSMTException( SMTRuntimeException e, Event msg ) {
        if ( e instanceof RuntimeEncodingException ) {
            _logOutMsg.copy( ENCODE_ERR ).append( e.getMessage() );
        } else {
            _logOutMsg.copy( SMT_SEND_ERR ).append( e.getMessage() );
        }

        logOutboundError( e, msg );

        if ( rejectMessageUpstream( msg, _logOutMsg ) ) {
            // message recycled by successful reject processing
        } else {
            // unable to reject message so logger error

            outboundRecycle( msg );
        }
    }    @Override public final void setChainSession( Session sess ) { _chainSession = sess; }

    protected final void incrementInboundCount() {
        if ( ++_reads == 1 ) {
            _inThruMeter.reset();
        }
        _inThruMeter.hit();
    }    @Override public Session getChainSession()                  { return _chainSession; }

    protected final void lastSent( long time ) { _lastSent = time; }

    /*
     * PROTECTED METHODS
     */

    protected abstract void logInEvent( ZString event );

    // logger inbound event, useful for Binary protocols
    protected final void logInEventPojo( Event msg ) {
        if ( _logPojos ) {
            _logInbound.setLength( _logInHdr.length() );
            msg.dump( _logInbound );
            _log.infoLarge( _logInbound );
        }
    }

    protected abstract void logOutEvent( ZString event );

    // logger outbound event, useful for Binary protocols
    protected final void logOutEventPojo( Event msg ) {
        if ( _logPojos ) {
            _logOutbound.setLength( _logOutHdr.length() );
            msg.dump( _logOutbound );
            _log.infoLarge( _logOutbound );
        }
    }

    protected final void logOutboundError( Exception e, Event msg ) {
        _logOutErrMsg.copy( getComponentId() ).append( ' ' ).append( e.getMessage() ).append( ":: " );
        msg.dump( _logOutErrMsg );

        _log.error( ERR_OUT_MSG, _logOutErrMsg, e );
    }

    protected abstract void persistIntegrityCheck( boolean inbound, long key, Event msg );

    protected void postSend( Event msg ) { sendChain( msg, true ); }

    @SuppressWarnings( "synthetic-access" )
    protected void recoverInbound( final DMARecoveryController ctl ) {

        _log.info( getComponentId() + " recoverInbound started" );

        try {
            _inPersister.replay( new PersistentReplayListener() {

                @Override
                public void started() {
                    _recoverySessionContextIn = ctl.startedInbound( AbstractSession.this );
                    _recoverySessionContextIn.setPersister( _inPersister );
                }

                @Override
                public void completed() {
                    ctl.completedInbound( _recoverySessionContextIn );
                    setInboundRecoveryFinished( true );
                }

                @Override
                public void failed() {
                    ctl.failedInbound( _recoverySessionContextIn );
                    setInboundRecoveryFinished( true );
                }

                @Override
                public void message( Persister p, long key, byte[] buf, int offset, int len, short flags ) {
                    boolean inBound = true;
                    Event   msg     = recoveryDecode( buf, offset, len, inBound );
                    if ( msg != null ) {
                        persistIntegrityCheck( true, key, msg );
                        ctl.processInbound( _recoverySessionContextIn, key, msg, flags );
                    }
                }

                @Override
                public void message( Persister p, long key, byte[] buf, int offset, int len, byte[] opt, int optOffset, int optLen, short flags ) {
                    boolean inBound = true;
                    Event   msg     = recoveryDecodeWithContext( buf, offset, len, opt, optOffset, optLen, inBound );
                    if ( msg != null ) {
                        persistIntegrityCheck( true, key, msg );
                        ctl.processInbound( _recoverySessionContextIn, key, msg, flags );
                    }
                }
            } );
        } catch( PersisterException e ) {
            _log.error( RECOVER_ERR_IN, getComponentId(), e );

            ctl.failedInbound( _recoverySessionContextIn );
        }

        _log.info( getComponentId() + " recoverInbound completed " );
    }

    @SuppressWarnings( "synthetic-access" )
    protected void recoverOutBound( final DMARecoveryController ctl ) {

        _log.info( getComponentId() + " recoverOutbound started" );

        try {
            _outPersister.replay( new PersistentReplayListener() {

                @Override
                public void started() {
                    _recoverySessionContextOut = ctl.startedOutbound( AbstractSession.this );
                    _recoverySessionContextOut.setPersister( _outPersister );
                }

                @Override
                public void completed() {
                    ctl.completedOutbound( _recoverySessionContextOut );
                    setOutboundRecoveryFinished( true );
                }

                @Override
                public void failed() {
                    ctl.failedOutbound( _recoverySessionContextOut );
                    setOutboundRecoveryFinished( true );
                }

                @Override
                public void message( Persister p, long key, byte[] buf, int offset, int len, short flags ) {
                    boolean inBound = false;
                    Event   msg     = recoveryDecode( buf, offset, len, inBound );
                    persistIntegrityCheck( false, key, msg );
                    ctl.processOutbound( _recoverySessionContextOut, key, msg, flags );
                }

                @Override
                public void message( Persister p, long key, byte[] buf, int offset, int len, byte[] opt, int optOffset, int optLen, short flags ) {
                    boolean inBound = false;
                    Event   msg     = recoveryDecodeWithContext( buf, offset, len, opt, optOffset, optLen, inBound );
                    persistIntegrityCheck( false, key, msg );
                    ctl.processOutbound( _recoverySessionContextOut, key, msg, flags );
                }
            } );
        } catch( PersisterException e ) {
            _log.error( RECOVER_ERR_OUT, getComponentId(), e );

            ctl.failedOutbound( _recoverySessionContextOut );
        }

        _log.info( getComponentId() + " recoverOutbound completed" );
    }

    protected abstract Event recoveryDecode( byte[] buf, int offset, int len, boolean inBound );

    protected abstract Event recoveryDecodeWithContext( byte[] buf, int offset, int len, byte[] opt, int optOffset, int optLen, boolean inBound );

    protected abstract void sendChain( Event msg, boolean canRecycle );

    /**
     * ensure message is encoded and send (if possible), message may be recyled/sent to chain session after this
     *
     * @param msg
     * @throws IOException
     */
    protected abstract void sendNow( Event msg ) throws IOException;

    protected void setInboundRecoveryFinished( boolean finished )  { _inRecovered = finished; }

    protected void setOutboundRecoveryFinished( boolean finished ) { _outRecovered = finished; }    /**
     * BLOCKING call waiting for recovery to finish replay
     */
    @Override public void waitForRecoveryToComplete() {
        while( _outRecovered == false || _inRecovered == false ) {

            ThreadUtilsFactory.get().sleep( 500 );
        }
    }

    protected synchronized final SessionState setSessionState( SessionState newState ) {
        if ( newState != _state ) {

            _log.log( getSessionChangeStateLogLevel(), id() + " Session change from " + _state.toString() + " to " + newState.toString() + " : " + info() );

            _state = newState;

            if ( newState == SessionState.Connected ) {
                _outboundDispatcher.handleStatusChange( this, true );
                connected();
                dispatchInbound( new SessionStatusEvent().set( getIntId(), SessionStatus.CONNECTED, getConnectionId() ) );
            } else if ( newState == SessionState.Disconnected ) {
                _outboundDispatcher.handleStatusChange( this, false );
                dispatchInbound( new SessionStatusEvent().set( getIntId(), SessionStatus.DISCONNECTED, getConnectionId() ) );
                disconnected();
            }

            for ( ConnectionListener listener : getConnectionListenerSet() ) {
                if ( newState == SessionState.Connected ) {
                    listener.connected( this );
                } else if ( newState == SessionState.Disconnected ) {
                    listener.disconnected( this );
                }
            }
        }

        return _state;
    }    @Override public long getLastSent() { return _lastSent; }

    private void readThrottle() {
        if ( _readSpinThrottle > 0 && ++_cnt % _readSpinThrottle == 0 ) {
            ThreadUtilsFactory.get().sleep( _readSpinDelay );
        }
    }


    @Override public boolean isPaused()              { return getCompRunState().isPaused(); }

    @Override public SessionDirection getDirection() { return _config.getDirection(); }

    /**
     * set throttler for sending of messages, messages exceeding this rate will be rejected
     *
     * @param throttleNoMsgs         - restrict new messages to this many messages per period (throttler may allow cancels and reject NOS/REP)
     * @param disconnectLimit        - total message limit in period (all messages rejected)
     * @param throttleTimeIntervalMS - throttle period in ms
     */
    @Override public void setThrottle( int throttleNoMsgs, int disconnectLimit, long throttleTimeIntervalMS ) {
        Class<? extends Throttler> throttlerClass = _config.getThrottlerClass();

        _log.info( "Installing throttler for " + getComponentId() + " msgsPerPeriod=" + throttleNoMsgs + ", periodMS=" + throttleTimeIntervalMS );

        Throttler t = ReflectUtils.create( throttlerClass );

        t.setThrottleNoMsgs( throttleNoMsgs );
        t.setThrottleTimeIntervalMS( throttleTimeIntervalMS );
        t.setDisconnectLimit( disconnectLimit );

        _throttler = t;
    }

    @Override public Event recoverEvent( boolean isInbound, long persistKey, ReusableString tmpBigBuf, ByteBuffer tmpCtxBuf ) {
        Event msg = null;

        Persister p = (isInbound) ? _inPersister : _outPersister;

        final byte[] buf = tmpBigBuf.getBytes();

        try {
            int len = p.read( persistKey, buf, 0, tmpCtxBuf );

            if ( len > 0 ) {
                int ctxLen = tmpCtxBuf.position();
                if ( ctxLen > 0 ) {
                    byte[] optBuf = tmpCtxBuf.array();
                    msg = recoveryDecodeWithContext( buf, 0, len, optBuf, 0, ctxLen, isInbound );
                } else {
                    msg = recoveryDecode( buf, 0, len, isInbound );
                }
            }
        } catch( PersisterException e ) {
            _log.warn( "RecoverEvent " + getComponentId() + " : " + e.getMessage() );
        }

        return msg;
    }
}
