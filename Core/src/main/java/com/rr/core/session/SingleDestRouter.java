/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;

public class SingleDestRouter extends AbstractEventRouter {

    private final EventHandler _delegate;

    public SingleDestRouter( EventHandler delegate ) {
        this( "SingleDestRouter", delegate );
    }

    public SingleDestRouter( String id, EventHandler delegate ) {
        super( id );

        _delegate = delegate;
    }

    @Override
    public boolean canHandle() {
        return _delegate.canHandle();
    }

    @Override
    public final void handle( final Event msg ) {
        _delegate.handle( msg );
    }

    @Override
    public void handleNow( Event msg ) {
        _delegate.handle( msg );
    }

    @Override
    public void threadedInit() {
        // nothing
    }
}
