/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.router;

import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.model.EventHandler;
import com.rr.model.generated.internal.events.interfaces.BaseOrderRequest;

public final class SingleDestRouter implements OrderRouter {

    private final EventHandler   _dest;
    private final EventHandler[] _all;
    private final String         _id;

    public SingleDestRouter( final EventHandler dest ) {
        this( null, dest );
    }

    public SingleDestRouter( String id, final EventHandler dest ) {
        super();
        _dest = dest;
        _all  = new EventHandler[] { dest };
        _id   = id;
    }

    @Override public final EventHandler[] getAllRoutes() {
        return _all;
    }

    @Override public final EventHandler getRoute( final BaseOrderRequest nos, final EventHandler replyHandler ) {
        return _dest;
    }

    @Override public void purgeHandler( final EventHandler deadHandler )  { /* nothing */ }

    @Override public void purgeRoute( final ZString clOrdId, Logger log ) { /* nothing */ }

    @Override public String getComponentId() {
        return _id;
    }
}
