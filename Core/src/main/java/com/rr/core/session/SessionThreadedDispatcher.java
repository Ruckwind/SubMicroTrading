/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.collections.BlockingSyncQueue;
import com.rr.core.collections.EventQueue;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.Constants;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.thread.RunState;
import com.rr.core.utils.ThreadPriority;

/**
 * a pausable dispatcher customised for session awareness
 */
public final class SessionThreadedDispatcher implements EventDispatcher {

    private final    EventQueue                 _queue;
    private final    EventQueue                 _syncQueue;
    private final    InternalThreadedDispatcher _threadDispatcher;
    private final    String                     _id;
    private volatile RunState                   _runState = RunState.Created;

    public SessionThreadedDispatcher( String id, EventQueue queue, ThreadPriority threadPriority ) {
        this( id, queue, threadPriority, Constants.LOW_PRI_LOOP_WAIT_MS );
    }

    public SessionThreadedDispatcher( String id, EventQueue queue, ThreadPriority threadPriority, int maxSleep ) {
        super();
        _queue     = queue;
        _syncQueue = new BlockingSyncQueue();
        _id        = id;

        String dispatchName = "DISPATCH_" + id;
        _threadDispatcher = new InternalThreadedDispatcher( dispatchName, threadPriority, queue, _syncQueue, maxSleep );
    }

    @Override
    public boolean canQueue() {
        return true;
    }

    @Override
    public void dispatch( Event msg ) {
        _queue.add( msg );
    }

    @Override
    public void dispatchForSync( Event msg ) {
        _syncQueue.add( msg );
    }

    @Override
    public void handleStatusChange( EventHandler handler, boolean connected ) {
        _threadDispatcher.connected( connected );
    }

    @Override
    public String info() {
        return "SessionThreadedDispatcher( " + _threadDispatcher.getName() + ", queue=" + _queue.getClass().getSimpleName() + ")";
    }

    @Override
    public void setHandler( EventHandler handler ) {

        RecoverableSession sess = (RecoverableSession) handler;

        _threadDispatcher.setSession( sess );
    }

    @Override
    public void setStopping() {
        _threadDispatcher.setFinished();
        setRunState( RunState.Stopping );
    }

    @Override
    public synchronized void start() {
        if ( _runState == RunState.Created ) {
            _threadDispatcher.setDaemon( true );
            _threadDispatcher.start();
            setRunState( RunState.Active );
        }
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState() {
        return _runState;
    }

    @Override public RunState setRunState( final RunState newState ) {
        RunState old = _runState;

        if ( old != newState ) {
            _runState = newState;
        }

        return old;
    }

    @Override
    public void init( SMTStartContext ctx, CreationPhase creationPhase ) {
        // nothing
    }

    @Override
    public void prepare() {
        // nothing
    }

    @Override
    public void startWork() {
        start();
    }

    @Override
    public void stopWork() {
        setStopping();
    }
}
