/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.dispatch;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.dummy.warmup.DummyEventHandler;
import com.rr.core.lang.CoreReusableType;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.thread.RunState;

public class DirectDispatcher implements EventDispatcher {

    private final              String       _id;
    private                    EventHandler _handler  = new DummyEventHandler();
    private transient volatile RunState     _runState = RunState.Created;

    public DirectDispatcher() {
        this( null );
    }

    public DirectDispatcher( String id ) {
        _id = id;
    }

    @Override public boolean canQueue() {
        return false;               // CANT ENQUEUE
    }

    @Override public void dispatch( Event msg ) {
        if ( msg.getReusableType() != CoreReusableType.NullEvent ) {
            synchronized( _handler ) {
                _handler.handleNow( msg );
            }
        }
    }

    @Override public void dispatchForSync( Event msg ) {
        if ( msg.getReusableType() != CoreReusableType.NullEvent ) {
            synchronized( _handler ) {
                _handler.handleNow( msg );
            }
        }
    }

    @Override public void handleStatusChange( EventHandler handler, boolean isOk ) {
        // nothing
    }

    @Override public String info() {
        return "DirectDispatcher";
    }

    @Override public void setHandler( EventHandler handler ) {
        _handler = handler;
    }

    @Override public void setStopping() {
        setRunState( RunState.Stopping );
    }

    @Override public synchronized void start() {
        if ( _runState != RunState.Active && _runState != RunState.Stopping ) {
            _handler.threadedInit();
            setRunState( RunState.Active );
        }
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState() {
        return _runState;
    }

    @Override public void init( SMTStartContext ctx, CreationPhase creationPhase ) { /* nothing */ }

    @Override public void prepare()                                                { /* nothing */ }

    @Override public RunState setRunState( final RunState newState ) {
        return _runState = newState;
    }

    @Override public void startWork() {
        start();
    }

    @Override public void stopWork() {
        setStopping();
    }
}
