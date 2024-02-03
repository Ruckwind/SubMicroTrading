/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.session.*;
import com.rr.core.thread.RunState;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;

import java.nio.ByteBuffer;

public class DummySession implements RecoverableSession {

    private final     ThreadLocal<AllEventRecycler> _outboundRecycler = ThreadLocal.withInitial( () -> new com.rr.model.generated.internal.events.factory.AllEventRecycler() );
    private           String                        _id;
    private transient RunState                      _runState         = RunState.Unknown;

    public DummySession( String id ) { _id = id; }

    @Override public boolean canHandle()                             { return true; }

    @Override
    public void handle( Event event ) {
        handleNow( event );
    }

    @Override public String getComponentId()                         { return _id; }

    @Override public int getIntId() {
        return 0;
    }    @Override public void setChainSession( Session sess )            { /* nothing */ }

    @Override public ZString getConnectionId()                                                                                { return null; }    @Override public Session getChainSession()                       { return null; }

    @Override public void setConnectionId( final ZString connId )                                                             { /* nothing */ }

    @Override public void attachReceiver( Receiver receiver )                                                                 { /* nothing */ }

    @Override public void stop()                                                                                              { /* nothing */ }

    @Override public void recover( DMARecoveryController ctl )                                                                { /* nothing */ }

    @Override public void connect()                                                                                           { /* nothing */ }

    @Override public void disconnect( boolean tryReconnect )                                                                  { /* nothing */ }

    @Override public boolean isRejectOnDisconnect()                                                                           { return false; }

    @Override public void registerConnectionListener( ConnectionListener listener )                                           { /* nothing */ }

    @Override public boolean isConnected()                                                                                    { return false; }

    @Override public boolean isStopping()                                                                                     { return false; }

    @Override public void internalConnect()                                                                                   { /* nothing */ }

    @Override public SessionState getSessionState()                                                                           { return null; }

    @Override public boolean isLoggedIn()                            { return true; }

    @Override public void processNextInbound()                                                                                { /* nothing */ }

    /**
     * handle event ... as this dummy session has no dispatch queue then many threads could invoke this method at same time
     * given recycler in not threadsafe it must be protected ... hence use of threadlocal recycler
     *
     * @param msg
     */
    @Override public void handleNow( Event msg ) {
        _outboundRecycler.get().recycle( msg );
    }

    @Override public void handleForSync( Event msg )                                                                          { /* nothing */ }

    @Override public RunState getRunState()                          { return _runState; }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    @Override public void init( SMTStartContext ctx, CreationPhase creationPhase )                                            { /* nothing */ }

    @Override public void prepare()                                                                                           { /* nothing */ }

    @Override public boolean hasOutstandingWork()                                                                             { return false; }

    @Override public void init()                                     { /* nothing */ }

    @Override public void startWork()                                                                                         { /* nothing */ }    @Override public void setRejectOnDisconnect( boolean reject )                                                             { /* nothing */ }

    @Override public void stopWork()                                                                                          { /* nothing */ }    @Override public boolean getRejectOnDisconnect()                                                                          { return false; }

    @Override public void threadedInit()                             { /* nothing */ }    @Override public void processIncoming()                                                                                   { /* nothing */ }

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
