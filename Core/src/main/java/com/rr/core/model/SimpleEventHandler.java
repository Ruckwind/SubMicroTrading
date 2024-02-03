package com.rr.core.model;

/**
 * sinmple message handler to simplify EventHandler usage where threading is not requires
 */
public abstract class SimpleEventHandler implements EventHandler {

    @Override public boolean canHandle() { return true; }

    @Override public void handle( final Event msg ) {
        handleNow( msg );
    }

    @Override public abstract void handleNow( final Event msg );

    @Override public String getComponentId() { return "AnonymousHandler"; }

    @Override public void threadedInit()     { /* nothing */ }
}
