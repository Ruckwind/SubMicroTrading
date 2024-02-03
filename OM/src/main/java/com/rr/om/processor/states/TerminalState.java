/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor.states;

import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.om.order.Order;
import com.rr.om.order.OrderVersion;
import com.rr.om.processor.EventProcessor;

public class TerminalState extends BaseOrderState {

    public TerminalState( EventProcessor proc ) {
        super( proc );
    }

    @Override
    public void handleCancelled( Order order, Cancelled cancelled ) throws StateException {
        ignoreEvent( order, cancelled );
    }

    @Override
    public void handleDoneForDay( Order order, DoneForDay msg ) throws StateException {
        ignoreEvent( order, msg );
    }

    @Override
    public void handleExpired( Order order, Expired msg ) throws StateException {
        ignoreEvent( order, msg );
    }

    @Override
    public void handleNewOrderAck( Order order, NewOrderAck msg ) throws StateException {
        ignoreEvent( order, msg );
    }

    @Override
    public void handleRejected( Order order, Rejected msg ) throws StateException {
        ignoreEvent( order, msg );
    }

    @Override
    public void handleReplaced( Order order, Replaced msg ) throws StateException {
        ignoreEvent( order, msg );
    }

    /**
     * Restated represents an ExecutionRpt sent by the sellside communicating a change in the order or a restatement of the
     * order's parameters without an electronic request from the customer. ExecRestatementReason <378> must be set.
     * This is used for GT orders and corporate actions
     */
    @Override
    public void handleRestated( Order order, Restated msg ) throws StateException {
        ignoreEvent( order, msg );
    }

    @Override
    public void handleStopped( Order order, Stopped msg ) throws StateException {
        ignoreEvent( order, msg );
    }

    @Override
    public void handleSuspended( Order order, Suspended msg ) throws StateException {
        ignoreEvent( order, msg );
    }

    @Override
    public void handleTradeCancel( Order order, TradeCancel msg ) throws StateException {

        final OrderVersion lastAcc = order.getLastAckedVerion();

        // only reopen trade that was fully filled and that a client can handle
        if ( lastAcc.getOrdStatus() == OrdStatus.Filled && order.getClientProfile().isSendClientLateFills() == true ) {
            lastAcc.setOrdStatus( (lastAcc.getCumQty() > 0) ? OrdStatus.PartiallyFilled : OrdStatus.New );
        }

        super.handleTradeCancel( order, msg );

        if ( lastAcc.getOrdStatus() == OrdStatus.Filled && order.getClientProfile().isSendClientLateFills() == true ) {
            order.setState( _proc.getStateOpen() );
        }
    }

    @Override
    public void handleTradeCorrect( Order order, TradeCorrect msg ) throws StateException {
        final OrderVersion lastAcc = order.getLastAckedVerion();

        // only reopen trade that was fully filled and that a client can handle
        if ( lastAcc.getOrdStatus() == OrdStatus.Filled && order.getClientProfile().isSendClientLateFills() == true ) {
            lastAcc.setOrdStatus( (lastAcc.getCumQty() > 0) ? OrdStatus.PartiallyFilled : OrdStatus.New );
        }

        super.handleTradeCorrect( order, msg );

        if ( lastAcc.getOrdStatus() == OrdStatus.Filled && order.getClientProfile().isSendClientLateFills() == true ) {
            order.setState( _proc.getStateOpen() );
        }
    }

    @Override
    public boolean isPending() { return false; }
}
