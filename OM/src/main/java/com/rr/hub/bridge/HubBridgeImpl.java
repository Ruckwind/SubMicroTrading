/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.hub.bridge;

import com.rr.core.model.Event;

public class HubBridgeImpl implements HubBridge {

    @SuppressWarnings( "unused" )
    private final int    _expOrders;
    private final String _name = "HubBridge";

    public HubBridgeImpl( int expOrders ) {
        _expOrders = expOrders;
    }

    @Override public boolean canHandle() { return true; }

    @Override
    public void handle( Event msg ) {
        handleNow( msg );
    }

    @Override
    public void handleNow( Event msg ) {
        // @TODO batch together and insert into DB
    }

    @Override
    public String getComponentId() {
        return _name;
    }

    @Override public void threadedInit() { /* nothing */ }
}
