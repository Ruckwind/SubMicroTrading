/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.model.event;

import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.om.order.Order;
import com.rr.om.processor.states.StateException;

public interface OrderEventHandlers {

    void handleCancelReject( final Order order, final CancelReject msg ) throws StateException;

    void handleCancelReplaceRequest( final Order order, final CancelReplaceRequest msg ) throws StateException;

    void handleCancelRequest( final Order order, final CancelRequest msg ) throws StateException;

    void handleCancelled( final Order order, final Cancelled msg ) throws StateException;

    void handleDoneForDay( final Order order, final DoneForDay msg ) throws StateException;

    void handleExpired( final Order order, final Expired msg ) throws StateException;

    void handleNewOrderAck( final Order order, final NewOrderAck msg ) throws StateException;

    void handleNewOrderSingle( final Order order, final NewOrderSingle msg ) throws StateException;

    void handleOrderStatus( final Order order, final OrderStatus msg ) throws StateException;

    void handleRejected( final Order order, final Rejected msg ) throws StateException;

    void handleReplaced( final Order order, final Replaced msg ) throws StateException;

    void handleRestated( final Order order, final Restated msg ) throws StateException;

    void handleStopped( final Order order, final Stopped msg ) throws StateException;

    void handleSuspended( final Order order, final Suspended msg ) throws StateException;

    void handleTradeCancel( final Order order, final TradeCancel msg ) throws StateException;

    void handleTradeCorrect( final Order order, final TradeCorrect msg ) throws StateException;

    void handleTradeNew( final Order order, final TradeNew msg ) throws StateException;

    void handleVagueReject( final Order order, final VagueOrderReject msg ) throws StateException;

}
