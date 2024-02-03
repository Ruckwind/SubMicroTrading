/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;

public class PassThruRouter extends AbstractEventRouter {

    private final EventHandler _delegate;

    public PassThruRouter( String id, EventHandler delegate ) {
        super( id );
        _delegate = delegate;
    }

    @Override
    public boolean canHandle() {
        return _delegate.canHandle();
    }

    @Override
    public void handle( Event msg ) {
        _delegate.handle( msg );
    }

    @Override
    public void handleNow( Event msg ) {
        _delegate.handle( msg ); // dont allow bypass of delegates potential threading
    }

    @Override
    public void threadedInit() {
        // nothing
    }
}
