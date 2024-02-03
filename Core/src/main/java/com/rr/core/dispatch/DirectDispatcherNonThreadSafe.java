/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.dispatch;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.thread.RunState;

public class DirectDispatcherNonThreadSafe implements EventDispatcher {

    private final              String       _id;
    private                    EventHandler _handler;
    private transient volatile RunState     _runState = RunState.Created;

    public DirectDispatcherNonThreadSafe() {
        this( null );
    }

    public DirectDispatcherNonThreadSafe( String id ) {
        _id = id;
    }

    @Override
    public boolean canQueue() {
        return false;               // CANT ENQUEUE
    }

    @Override
    public void dispatch( Event msg ) {
        _handler.handleNow( msg );
    }

    @Override
    public void dispatchForSync( Event msg ) {
        _handler.handleNow( msg );
    }

    @Override
    public void handleStatusChange( EventHandler handler, boolean isOk ) {
        // nothing
    }

    @Override
    public String info() {
        return "DirectDispatcherNonThreadSafe";
    }

    @Override
    public void setHandler( EventHandler handler ) {
        _handler = handler;
    }

    @Override
    public void setStopping() {
        setRunState( RunState.Stopping );
    }

    @Override
    public synchronized void start() {
        if ( _runState != RunState.Active && _runState != RunState.Stopping ) {
            _handler.threadedInit();
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
        return _runState = newState;
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
