/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.algo.t1;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.session.*;
import com.rr.core.thread.RunState;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DummyExchangeSession implements RecoverableSession {

    private final     List<Event> _events       = new ArrayList<>();
    private           Session     _chainSession = null;
    private transient RunState    _runState     = RunState.Unknown;

    public DummyExchangeSession() {
    }

    @Override public String getComponentId() { return null; }

    @Override public int getIntId() {
        return 0;
    }

    @Override public ZString getConnectionId()                    { return null; }

    @Override public void setConnectionId( final ZString connId ) { /* nothimng */ }

    @Override public void attachReceiver( Receiver receiver )                                                                 { /* nothing */ }

    @Override public void stop()                                                                                              { /* nothing */ }    @Override public void setChainSession( Session sess ) {
        if ( sess.getClass() != DummyExchangeSession.class ) {
            throw new RuntimeException( "UNSAFE can only use DummyExchangeSession as chain session due to potential recycling issues" );
        }
        _chainSession = sess;
    }

    @Override public void recover( DMARecoveryController ctl )                                                                { /* nothing */ }

    @Override public void connect()                                                                                           { /* nothing */ }

    @Override public void disconnect( boolean tryReconnect )                                                                  { /* nothing */ }    @Override public Session getChainSession() {
        return _chainSession;
    }

    @Override public boolean isRejectOnDisconnect()                                                                           { return false; }

    @Override public void registerConnectionListener( ConnectionListener listener )                                           { /* nothing */ }

    @Override public boolean isConnected()                                                                                    { return false; }

    @Override public boolean isStopping()                                                                                     { return false; }

    @Override public void internalConnect()                                                                                   { /* nothing */ }

    @Override public SessionState getSessionState()                                                                           { return null; }

    @Override public boolean isLoggedIn()    { return true; }

    @Override public void processNextInbound()                                                                                { /* nothing */ }

    @Override public void handleNow( Event msg ) {

        _events.add( msg );

        if ( _chainSession != null ) {
            _chainSession.handle( msg );
        }
    }

    @Override public void handleForSync( Event msg )                                                                          { /* nothing */ }

    @Override public RunState getRunState()                          { return _runState; }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    @Override public void init( SMTStartContext ctx, CreationPhase creationPhase )                                            { /* nothing */ }

    @Override public void prepare()                                                                                           { /* nothing */ }

    @Override
    public void handle( Event event ) {
        handleNow( event );
    }

    @Override public boolean canHandle()     { return true; }

    @Override public boolean hasOutstandingWork()                                                                             { return false; }

    @Override public void init()             { /* nothing */ }

    @Override public void startWork()                                                                                         { /* nothing */ }

    @Override public void stopWork()                                                                                          { /* nothing */ }    @Override public void setRejectOnDisconnect( boolean reject )                                                             { /* nothing */ }

    @Override public void threadedInit()     { /* nothing */ }    @Override public boolean getRejectOnDisconnect()                                                                          { return false; }

    public List<Event> getEvents() {
        return _events;
    }    @Override public void processIncoming()                                                                                   { /* nothing */ }

    @Override public void setLogStats( boolean logStats )                                                                     { /* nothing */ }

    @Override public void setLogEvents( boolean on )                                                                          { /* nothing */ }

    @Override public boolean isLogEvents()                                                                                    { return false; }

    @Override public boolean rejectMessageUpstream( Event msg, ZString errMsg )                                               { return false; }

    @Override public boolean discardOnDisconnect( Event msg )                                                                 { return false; }

    @Override public void inboundRecycle( Event msg )                                                                         { /* nothing */ }

    @Override public void outboundRecycle( Event msg )                                                                        { /* nothing */ }

    @Override public void dispatchInbound( Event msg )                                                                        { /* nothing */ }

    @Override public void waitForRecoveryToComplete()                                                                         { /* nothing */ }

    @Override public long getLastSent()                                                                                       { return 0; }

    @Override public String info()                                                                                            { return null; }

    @Override public void setPaused( boolean paused )                                                                         { /* nothing */ }

    @Override public void persistLastInboundMesssage()                                                                        { /* nothing */ }

    @Override public SessionConfig getConfig()                                                                                { return null; }

    @Override public boolean isPaused()                                                                                       { return false; }

    @Override public SessionDirection getDirection()                                                                          { return null; }

    @Override public void setThrottle( int throttleNoMsgs, int disconnectLimit, long throttleTimeIntervalMS )                 {/* nothing */ }

    @Override public Event recoverEvent( boolean isInbound, long persistKey, ReusableString tmpBigBuf, ByteBuffer tmpCtxBuf ) { return null; }










}
