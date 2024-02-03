/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.model.Event;

public class DummyRouter extends AbstractEventRouter {

    public DummyRouter( final String id ) {
        super( id );
    }

    @Override public void handle( Event msg ) {
        // NOTHING
    }

    @Override public void handleNow( Event msg ) {
        // NOTHING
    }

    @Override public boolean canHandle() {
        return false;
    }

    @Override public void threadedInit() {
        // NOTHING
    }
}
