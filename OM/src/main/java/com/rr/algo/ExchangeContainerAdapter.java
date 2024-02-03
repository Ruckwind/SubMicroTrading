/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.algo;

import com.rr.core.model.Event;
import com.rr.core.session.AbstractEventRouter;

/**
 * adapts exchange responses to algo Container
 */
public final class ExchangeContainerAdapter extends AbstractEventRouter {

    private SimpleAlgo _container;

    public ExchangeContainerAdapter( String id, SimpleAlgo container ) {
        super( id );
        _container = container;
    }

    @Override
    public boolean canHandle() {
        return true;
    }

    @Override
    public void handle( Event msg ) {
        handleNow( msg );
    }

    @Override
    public void handleNow( Event msg ) {
        _container.handleMarketEvent( msg );
    }

    @Override public void threadedInit() { /* nothing */ }

    public void setContainer( SimpleAlgo container ) {
        _container = container;
    }
}
