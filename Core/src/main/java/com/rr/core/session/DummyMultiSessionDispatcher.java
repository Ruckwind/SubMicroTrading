/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.thread.RunState;

public class DummyMultiSessionDispatcher implements MultiSessionDispatcher {

    private final     String   _id;
    private transient RunState _runState = RunState.Unknown;

    public DummyMultiSessionDispatcher() {
        this( null );
    }

    public DummyMultiSessionDispatcher( String id ) {
        _id = id;
    }

    @Override
    public void addSession( NonBlockingSession session ) {
        /* nothing */
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState()                          { return _runState; }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    @Override
    public void init( SMTStartContext ctx, CreationPhase creationPhase ) {
        // nothing
    }

    @Override
    public void prepare() {
        // nothing
    }

    @Override
    public void start() {
        /* nothing */
    }

    @Override
    public void setStopping() {
        /* nothing */
    }

    @Override
    public void dispatch( Event msg ) {
        /* nothing */
    }

    @Override
    public void setHandler( EventHandler handler ) {
        /* nothing */
    }

    @Override
    public void handleStatusChange( EventHandler handler, boolean isOk ) {
        /* nothing */
    }

    @Override
    public boolean canQueue() {
        return true;
    }

    @Override
    public String info() {
        return "DummyMultiSessionDispatcher";
    }

    @Override
    public void dispatchForSync( Event msg ) {
        /* nothing */
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
