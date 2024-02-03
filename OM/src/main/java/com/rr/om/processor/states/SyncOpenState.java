/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor.states;

import com.rr.model.generated.internal.events.interfaces.CancelReplaceRequest;
import com.rr.model.generated.internal.events.interfaces.CancelRequest;
import com.rr.model.generated.internal.events.interfaces.Cancelled;
import com.rr.model.generated.internal.events.interfaces.TradeNew;
import com.rr.om.order.Order;
import com.rr.om.processor.EventProcessor;

public class SyncOpenState extends OpenState {

    public SyncOpenState( EventProcessor proc ) {
        super( proc );
    }

    @Override
    public void handleTradeNew( Order order, TradeNew msg ) throws StateException {
        synchronized( order ) {
            super.handleTradeNew( order, msg );
        }
    }

    @Override
    public void handleCancelReplaceRequest( Order order, CancelReplaceRequest replaceRequest ) throws StateException {
        synchronized( order ) {
            super.handleCancelReplaceRequest( order, replaceRequest );
        }
    }

    @Override
    public void handleCancelRequest( Order order, CancelRequest cancelRequest ) throws StateException {
        synchronized( order ) {
            super.handleCancelRequest( order, cancelRequest );
        }
    }

    @Override
    public void handleCancelled( Order order, Cancelled cancelled ) throws StateException {
        synchronized( order ) {
            super.handleCancelled( order, cancelled );
        }
    }

    @Override
    public boolean isPending() { return false; }
}
