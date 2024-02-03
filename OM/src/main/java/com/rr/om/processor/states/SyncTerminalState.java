/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor.states;

import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.om.order.Order;
import com.rr.om.processor.EventProcessor;

public class SyncTerminalState extends TerminalState {

    public SyncTerminalState( EventProcessor proc ) {
        super( proc );
    }

    @Override
    public void handleCancelled( Order order, Cancelled cancelled ) throws StateException {
        synchronized( order ) {
            super.handleCancelled( order, cancelled );
        }
    }

    @Override
    public void handleDoneForDay( Order order, DoneForDay msg ) throws StateException {
        synchronized( order ) {
            super.handleDoneForDay( order, msg );
        }
    }

    @Override
    public void handleExpired( Order order, Expired msg ) throws StateException {
        synchronized( order ) {
            super.handleExpired( order, msg );
        }
    }

    @Override
    public void handleNewOrderAck( Order order, NewOrderAck msg ) throws StateException {
        synchronized( order ) {
            super.handleNewOrderAck( order, msg );
        }
    }

    @Override
    public void handleRejected( Order order, Rejected msg ) throws StateException {
        synchronized( order ) {
            super.handleRejected( order, msg );
        }
    }

    @Override
    public void handleReplaced( Order order, Replaced msg ) throws StateException {
        synchronized( order ) {
            super.handleReplaced( order, msg );
        }
    }

    /**
     * Restated represents an ExecutionRpt sent by the sellside communicating a change in the order or a restatement of the
     * order's parameters without an electronic request from the customer. ExecRestatementReason <378> must be set.
     * This is used for GT orders and corporate actions
     */
    @Override
    public void handleRestated( Order order, Restated msg ) throws StateException {
        synchronized( order ) {
            super.handleRestated( order, msg );
        }
    }

    @Override
    public void handleStopped( Order order, Stopped msg ) throws StateException {
        synchronized( order ) {
            super.handleStopped( order, msg );
        }
    }

    @Override
    public void handleSuspended( Order order, Suspended msg ) throws StateException {
        synchronized( order ) {
            super.handleSuspended( order, msg );
        }
    }

    @Override
    public void handleTradeCancel( Order order, TradeCancel msg ) throws StateException {
        synchronized( order ) {
            super.handleTradeCancel( order, msg );
        }
    }

    @Override
    public void handleTradeCorrect( Order order, TradeCorrect msg ) throws StateException {
        synchronized( order ) {
            super.handleTradeCorrect( order, msg );
        }
    }

    @Override
    public boolean isPending() { return false; }
}
