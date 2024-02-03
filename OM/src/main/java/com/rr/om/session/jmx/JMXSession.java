/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.jmx;

import com.rr.core.admin.AdminAgent;
import com.rr.core.codec.BaseReject;
import com.rr.core.codec.Decoder;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.persister.PersisterException;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.session.*;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.Utils;
import com.rr.om.session.SessionManager;
import com.rr.om.utils.FixUtils;
import com.rr.om.warmup.sim.WarmupUtils;

import java.nio.ByteBuffer;

/**
 * a JMXSession for use in exchange certification by allowing upstream order injection
 * <p>
 * can inject directly to specified session bypassing processor/controller
 * <p>
 * not for use with MultiSession dispatcher/receiver
 * <p>
 * will update tag 52 / tag 60, and tag 10
 *
 * @author Richard Rose
 * @WARN doesnt recycle any objects
 */
public class JMXSession extends CommonAbstractSession {

    private static final Logger _log = LoggerFactory.create( JMXSession.class );

    private final EventRouter    _inboundRouter;
    private final Decoder        _decoder;
    private final SessionConfig  _config;
    private final SessionManager _sessMgr;

    private final ZString        _logInHdr;
    private final ZString        _logOutHdr;
    private final ReusableString _logInbound  = new ReusableString();
    private final ReusableString _logOutbound = new ReusableString();

    private final ReusableString _inMsg = new ReusableString();

    public JMXSession( String name,
                       SessionConfig cfg,
                       EventRouter inboundRouter,
                       Decoder decoder,
                       SessionManager sessionManager ) {

        super( name );
        _inboundRouter = inboundRouter;
        _decoder       = decoder;
        _config        = cfg;
        _sessMgr       = sessionManager;

        _logInHdr  = new ViewString( "     IN [" + name + "]: " );
        _logOutHdr = new ViewString( "DROP OUT [" + name + "]: " );

        _logInbound.copy( _logInHdr );
        _logOutbound.copy( _logOutHdr );

        JMXSessionAdmin sma = new JMXSessionAdmin( this );
        AdminAgent.register( sma );
    }

    @Override
    public boolean canHandle() {
        return true;
    }

    @Override
    public void handle( Event msg ) {
        _logOutbound.setLength( _logOutHdr.length() );
        msg.dump( _logOutbound );
        _log.infoLarge( _logOutbound );
    }

    @Override public boolean hasOutstandingWork() {
        return false;
    }

    @Override
    public void init() throws PersisterException {
        // nothing
    }

    @Override
    public void attachReceiver( Receiver receiver ) {
        // nothing
    }

    @Override
    public void stop() {
        // nothing
    }

    @Override
    public void recover( DMARecoveryController ctl ) {
        // nothing
    }

    @Override
    public void connect() {
        // nothing
    }

    @Override
    public void disconnect( boolean tryReconnect ) {
        // nothing
    }

    @Override
    public boolean isRejectOnDisconnect() {
        return false;
    }

    @Override
    public void registerConnectionListener( ConnectionListener listener ) {
        // nothing
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isStopping() {
        return false;
    }

    @Override
    public void internalConnect() {
        // nothing
    }

    @Override
    public SessionState getSessionState() {
        return SessionState.Connected;
    }

    @Override
    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public void processNextInbound() {
        // nothing
    }

    @Override
    public void handleNow( Event msg ) {
        handle( msg );
    }

    @Override
    public void handleForSync( Event msg ) {
        handle( msg );
    }

    @Override
    public void init( SMTStartContext ctx, CreationPhase creationPhase ) {
        try {
            init();
        } catch( PersisterException e ) {
            throw new SMTRuntimeException( getComponentId() + " failed to initialise : " + e.getMessage(), e );
        }
    }

    @Override
    public void prepare() {
        // nothing
    }

    @Override
    public void startWork() {
        connect();
    }

    @Override
    public void stopWork() {
        stop();
    }

    @Override
    public void threadedInit() {
        // nothing
    }    @Override
    public void setRejectOnDisconnect( boolean reject ) {
        // nothing
    }

    /**
     * inject message, synchronized as could be multiple JMX sessions
     *
     * @param rawMessage
     * @return status message
     */
    public synchronized String injectMessage( String rawMessage, String destSessionName ) {

        rawMessage = FixUtils.chkDelim( rawMessage );

        _log.info( getComponentId() + " INJECT IN : " + rawMessage );

        _inMsg.copy( rawMessage );

        _decoder.setReceived( Utils.nanoTime() );

        Event msg;
        try {
            msg = WarmupUtils.doDecode( _decoder, _inMsg.getBytes(), 0, _inMsg.length() );
        } catch( Exception e ) {
            return "Failed to decode message : " + e.getMessage();
        }

        if ( msg instanceof BaseReject ) {
            return "Error decoding message : " + ((BaseReject<?>) msg).getMessage();
        }

        if ( msg == null ) {
            return "No decodable message";
        }

        _logInbound.setLength( _logInHdr.length() );
        msg.dump( _logInbound );
        _log.infoLarge( _logInbound );

        return inject( msg, destSessionName );
    }    @Override
    public boolean getRejectOnDisconnect() {
        return false;
    }

    private String inject( Event msg, String sessionName ) {
        if ( sessionName == null ) {
            if ( _inboundRouter == null ) {
                return "ERROR : Must specify session to route too";
            }
            dispatchInbound( msg );
            return "Message Dispatched Inbound";
        }

        RecoverableSession session = _sessMgr.getSession( sessionName );

        if ( session == null ) return "Unable to find session " + sessionName;

        try {
            session.handle( msg );

        } catch( Exception e ) {
            return "Exception " + e.getMessage();
        }

        return "Message Dispatched Inbound to " + sessionName;
    }    @Override
    public void processIncoming() {
        // nothing
    }

    @Override
    public void setLogStats( boolean logStats ) {
        // nothing
    }

    @Override
    public void setLogEvents( boolean on ) {
        // nothing
    }

    @Override
    public boolean isLogEvents() {
        return true;
    }

    @Override
    public boolean rejectMessageUpstream( Event msg, ZString errMsg ) {
        return false;
    }

    @Override
    public boolean discardOnDisconnect( Event msg ) {
        return false;
    }

    @Override
    public void setChainSession( Session sess ) {
        // nothing
    }

    @Override
    public Session getChainSession() {
        return null;
    }

    @Override
    public void inboundRecycle( Event msg ) {
        // nothing
    }

    @Override
    public void outboundRecycle( Event msg ) {
        // nothing
    }

    @Override
    public void dispatchInbound( Event msg ) {

        msg.setEventHandler( this );

        _inboundRouter.handle( msg );
    }

    @Override
    public void waitForRecoveryToComplete() {
        // nothing
    }

    @Override
    public long getLastSent() {
        return 0;
    }

    @Override
    public String info() {
        return "JMXSession-" + getComponentId();
    }

    @Override
    public void setPaused( boolean paused ) {
        // nothing
    }

    @Override
    public void persistLastInboundMesssage() {
        // nothing
    }

    @Override
    public SessionConfig getConfig() {
        return _config;
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public SessionDirection getDirection() {
        return SessionDirection.Upstream;
    }

    @Override
    public void setThrottle( int throttleNoMsgs, int disconnectLimit, long throttleTimeIntervalMS ) {
        // nothing
    }

    @Override
    public Event recoverEvent( boolean isInbound, long persistKey, ReusableString tmpBigBuf, ByteBuffer tmpCtxBuf ) {
        return null;
    }






}
