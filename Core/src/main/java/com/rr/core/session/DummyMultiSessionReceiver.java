/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

public class DummyMultiSessionReceiver implements MultiSessionReceiver {

    private final String _id;

    public DummyMultiSessionReceiver() {
        this( null );
    }

    public DummyMultiSessionReceiver( String id ) {
        _id = id;
    }

    @Override
    public void addSession( NonBlockingSession nonBlockingSession ) {
        // nothing
    }

    @Override
    public int getNumSessions() {
        return 0;
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override
    public void start() {
        // nothing
    }

    @Override
    public void setStopping( boolean stopping ) {
        // nothing
    }

    @Override
    public boolean isStarted() {
        return true;
    }
}
