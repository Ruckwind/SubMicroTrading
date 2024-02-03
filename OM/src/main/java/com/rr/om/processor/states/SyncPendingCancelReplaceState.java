/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor.states;

import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.om.order.Order;
import com.rr.om.processor.EventProcessor;

public class SyncPendingCancelReplaceState extends PendingCancelReplaceState {

    public SyncPendingCancelReplaceState( EventProcessor proc ) {
        super( proc );
    }

    @Override
    public void handleTradeNew( Order order, TradeNew msg ) throws StateException {
        synchronized( order ) {
            super.handleTradeNew( order, msg );
        }
    }

    @Override
    public void handleCancelReject( Order order, CancelReject mktReject ) throws StateException {
        synchronized( order ) {
            super.handleCancelReject( order, mktReject );
        }
    }

    @Override
    public void handleCancelled( Order order, Cancelled cancelled ) throws StateException {
        synchronized( order ) {
            super.handleCancelled( order, cancelled );
        }
    }

    @Override
    public void handleReplaced( Order order, Replaced replaced ) throws StateException {
        synchronized( order ) {
            super.handleReplaced( order, replaced );
        }
    }

    @Override
    public void handleTradeCorrect( Order order, TradeCorrect msg ) throws StateException {
        synchronized( order ) {
            super.handleTradeCorrect( order, msg );
        }
    }

    @Override
    public void handleTradeCancel( Order order, TradeCancel msg ) throws StateException {
        synchronized( order ) {
            super.handleTradeCancel( order, msg );
        }
    }

    @Override
    public boolean isPending() { return true; }
}
