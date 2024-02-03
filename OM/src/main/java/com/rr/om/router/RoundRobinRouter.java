/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.router;

import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.model.EventHandler;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.model.generated.internal.events.interfaces.BaseOrderRequest;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;

public final class RoundRobinRouter implements OrderRouter {

    private final EventHandler[] _sessions;
    private final String         _id;
    private       int            _nextIdx = 0;

    public RoundRobinRouter( EventHandler[] downStream ) {
        this( null, downStream );
    }

    public RoundRobinRouter( String id, EventHandler[] downStream ) {
        _sessions = downStream;
        _id       = id;
    }

    @Override public EventHandler[] getAllRoutes() {
        return _sessions;
    }

    @Override public EventHandler getRoute( final BaseOrderRequest nos, final EventHandler replyHandler ) {

        if ( !(nos instanceof NewOrderSingle) ) throw new SMTRuntimeException( "RoundRobinRouter is stateless and doesnt support cancel or amend" );

        final EventHandler s = _sessions[ _nextIdx ];

        if ( ++_nextIdx >= _sessions.length ) _nextIdx = 0;

        return s;
    }

    @Override public void purgeHandler( final EventHandler deadHandler )  { /* nothing */ }

    @Override public void purgeRoute( final ZString clOrdId, Logger log ) { /* nothing */ }

    @Override public String getComponentId() {
        return _id;
    }
}
