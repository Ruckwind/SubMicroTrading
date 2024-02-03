/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor.states;

import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.om.order.Order;
import com.rr.om.processor.EventProcessor;

public class SyncPendingNewState extends PendingNewState {

    public SyncPendingNewState( EventProcessor proc ) {
        super( proc );
    }

    @Override
    public boolean isPending() { return true; }

    @Override
    public void handleNewOrderSingle( Order order, NewOrderSingle cnos ) throws StateException {
        synchronized( order ) {
            super.handleNewOrderSingle( order, cnos );
        }
    }

    @Override
    public void handleRejected( Order order, Rejected msg ) throws StateException {
        synchronized( order ) {
            super.handleRejected( order, msg );
        }
    }

    @Override
    public void handleNewOrderAck( Order order, NewOrderAck msg ) throws StateException {
        synchronized( order ) {
            super.handleNewOrderAck( order, msg );
        }
    }

    @Override
    public void handleTradeNew( Order order, TradeNew msg ) throws StateException {
        synchronized( order ) {
            super.handleTradeNew( order, msg );
        }
    }

    @Override
    public void handleCancelled( Order order, Cancelled cancelled ) throws StateException {
        synchronized( order ) {
            super.handleCancelled( order, cancelled );
        }
    }
}
