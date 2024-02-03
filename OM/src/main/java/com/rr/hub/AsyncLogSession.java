/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.hub;

import com.rr.core.collections.BlockingSyncQueue;
import com.rr.core.collections.ConcLinkedEventQueueSingle;
import com.rr.core.collections.EventQueue;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.dispatch.ThreadedDispatcher;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.persister.PersisterException;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.session.*;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * a session which only logs events and then recycles them
 *
 * @author Richard Rose
 */
public class AsyncLogSession extends CommonAbstractSession {

    private static final Logger _log = LoggerFactory.create( AsyncLogSession.class );
    private final EventQueue     _queue;
    private final AtomicBoolean  _isStopping = new AtomicBoolean( false );
    private final ReusableString _logMsg     = new ReusableString( 1024 );
    private final EventDispatcher _dispatcher;
    private AllEventRecycler _recycler;

    public AsyncLogSession( String id, boolean slowMode ) {
        super( id );

        _queue = (slowMode) ? new BlockingSyncQueue() : new ConcLinkedEventQueueSingle();

        _dispatcher = new ThreadedDispatcher( "HubAsyncLogDispatcher", _queue );
        _dispatcher.setHandler( this );
    }

    @Override public boolean canHandle()                                                                                      { return !_isStopping.get(); }

    @Override public void handle( Event event ) {
        _queue.add( event );
    }

    @Override public boolean hasOutstandingWork()                                                                             { return false; }

    @Override public void init() throws PersisterException                                                                    { /* nothing */ }

    @Override public void attachReceiver( Receiver receiver )                                                                 { /* nothing */ }

    @Override public void stop() {
        if ( !_isStopping.compareAndSet( false, true ) ) {
            _dispatcher.stopWork();
        }
    }

    @Override public void recover( DMARecoveryController ctl )                                                                { /* nothing */ }

    @Override public void connect()                                                                                           { /* nothing */ }

    @Override public void disconnect( boolean tryReconnect )                                                                  { /* nothing */ }

    @Override public boolean isRejectOnDisconnect()                                                                           { return false; }

    @Override public void registerConnectionListener( ConnectionListener listener )                                           { /* nothing */ }

    @Override public boolean isConnected()                                                                                    { return false; }

    @Override public boolean isStopping()                                                                                     { return false; }

    @Override public void internalConnect()                                                                                   { /* nothing */ }

    @Override public SessionState getSessionState()                                                                           { return null; }

    @Override public boolean isLoggedIn()                                                                                     { return true; }

    @Override public void processNextInbound()                                                                                { /* nothing */ }

    @Override public void handleNow( Event msg ) {
        _logMsg.copy( "AsyncLogSession DUMMY DROPCOPY : " );
        msg.dump( _logMsg );
        _log.info( _logMsg );
        _recycler.recycle( msg );
    }

    @Override public void handleForSync( Event msg )                                                                          { /* nothing */ }

    @Override public void init( SMTStartContext ctx, CreationPhase creationPhase )                                            { /* nothing */ }

    @Override public void prepare() { /* nothing */ }

    @Override public void startWork() {
        _dispatcher.startWork();
    }

    @Override public void stopWork()                                                                                          { stop(); }

    @Override public void threadedInit() {
        _recycler = TLC.instance().getInstanceOf( AllEventRecycler.class );
    }    @Override public void setRejectOnDisconnect( boolean reject )                                                             { /* nothing */ }

    @Override public boolean getRejectOnDisconnect()                                                                          { return false; }

    @Override public void processIncoming()                                                                                   { /* nothing */ }

    @Override public void setLogStats( boolean logStats )                                                                     { /* nothing */ }

    @Override public void setLogEvents( boolean on )                                                                          { /* nothing */ }

    @Override public boolean isLogEvents()                                                                                    { return false; }

    @Override public boolean rejectMessageUpstream( Event msg, ZString errMsg )                                               { return false; }

    @Override public boolean discardOnDisconnect( Event msg )                                                                 { return false; }

    @Override public void setChainSession( Session sess )                                                                     { /* nothing */ }

    @Override public Session getChainSession()                                                                                { return null; }

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
