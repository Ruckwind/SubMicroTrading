/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.model.EventHandler;
import com.rr.core.model.ModelVersion;
import com.rr.om.model.event.EventBuilder;
import com.rr.om.processor.states.*;
import com.rr.om.registry.TradeRegistry;
import com.rr.om.validate.EventValidator;

public class SyncEventProcessorImpl extends EventProcessorImpl {

    public SyncEventProcessorImpl( ModelVersion version,
                                   int expectedOrders,
                                   EventValidator validator,
                                   EventBuilder builder,
                                   EventDispatcher dispatcher,
                                   EventHandler hub,
                                   TradeRegistry tradeRegistry ) {

        super( version, expectedOrders, validator, builder, dispatcher, hub, tradeRegistry );
    }

    @Override
    protected OrderState createOpenState() {
        return new SyncOpenState( this );
    }

    @Override
    protected OrderState createPendingCancelReplaceState() {
        return new SyncPendingCancelReplaceState( this );
    }

    @Override
    protected OrderState createPendingCancelState() {
        return new SyncPendingCancelState( this );
    }

    @Override
    protected OrderState createPendingNewState() {
        return new SyncPendingNewState( this );
    }

    @Override
    protected OrderState createTerminalState() {
        return new SyncTerminalState( this );
    }
}
