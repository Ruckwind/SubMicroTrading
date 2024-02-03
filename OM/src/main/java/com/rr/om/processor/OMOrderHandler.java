/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.om.processor.states.StateException;

public interface OMOrderHandler {

    void handleCancelReject( final CancelReject msg ) throws StateException;

    void handleCancelReplaceRequest( final CancelReplaceRequest msg ) throws StateException;

    void handleCancelRequest( final CancelRequest msg ) throws StateException;

    void handleCancelled( final Cancelled msg ) throws StateException;

    void handleDoneForDay( final DoneForDay msg ) throws StateException;

    void handleExpired( final Expired msg ) throws StateException;

    void handleNewOrderAck( final NewOrderAck msg ) throws StateException;

    void handleNewOrderSingle( final NewOrderSingle msg ) throws StateException;

    void handleOrderStatus( final OrderStatus msg ) throws StateException;

    void handleRejected( final Rejected msg ) throws StateException;

    void handleReplaced( final Replaced msg ) throws StateException;

    void handleRestated( final Restated msg ) throws StateException;

    void handleStopped( final Stopped msg ) throws StateException;

    void handleSuspended( final Suspended msg ) throws StateException;

    void handleTradeCancel( final TradeCancel msg ) throws StateException;

    void handleTradeCorrect( final TradeCorrect msg ) throws StateException;

    void handleTradeNew( final TradeNew msg ) throws StateException;

    void handleVagueReject( final VagueOrderReject msg ) throws StateException;
}
