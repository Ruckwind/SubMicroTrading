/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.utils.SMTRuntimeException;

public class GrabableMsgRouter extends AbstractEventRouter {

    private EventHandler _delegate;

    public GrabableMsgRouter( String id ) {
        super( id );
    }

    @Override public final void handle( final Event msg ) {
        if ( _delegate != null ) {
            _delegate.handle( msg );
        }
    }

    @Override public void handleNow( Event msg ) {
        if ( _delegate != null ) {
            _delegate.handle( msg );
        }
    }

    @Override public boolean canHandle() { return _delegate.canHandle(); }

    @Override public void threadedInit() { /* nothing */ }

    public void setDelegate( EventHandler delegate ) {

        if ( _delegate != null ) throw new SMTRuntimeException( "Unable to grab GrabableMsgRouter " + getComponentId() + " for " + delegate.getComponentId() + " as already assigned to " + _delegate.getComponentId() );

        _delegate = delegate;
    }
}
