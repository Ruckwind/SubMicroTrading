/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.model.event;

import com.rr.core.codec.BaseReject;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.*;
import com.rr.om.order.Order;
import com.rr.om.registry.TradeWrapper;

/**
 * responsible for managing event pools and the population of events
 *
 * @author Richard Rose
 */
public interface EventBuilder {

    /**
     * create a cancel reject message to be back to the client, must be response to cancel request
     *
     * @param order
     * @param mktReject
     * @return
     */
    CancelReject createClientCancelReject( Order order, CancelReject mktReject );

    /**
     * create a client side cancel replace reject based on the pending version
     *
     * @param order
     * @param mktReject
     * @return
     */
    CancelReject createClientCancelReplaceReject( Order order, CancelReject mktReject );

    /**
     * create a client cancel
     *
     * @param order
     * @param cancelled market cancelled message
     * @return
     */
    Cancelled createClientCanceled( Order order, Cancelled cancelled );

    Expired createClientExpired( Order order, Expired expired );

    /**
     * create a client new order ack based on the market ack
     *
     * @param order
     * @param msg   market ack or null if synthesizing an ack
     * @return
     */
    NewOrderAck createClientNewOrderAck( Order order, NewOrderAck msg );

    /**
     * generate a rejected from exchange or from downstream unable to dispatch
     *
     * @param order
     * @param msg
     * @return
     */
    Rejected createClientRejected( Order order, Rejected msg );

    /**
     * create a client side replaced
     *
     * @param order
     * @param replaced
     * @return
     */
    Replaced createClientReplaced( Order order, Replaced replaced );

    /**
     * create a client trade cancel
     *
     * @param order
     * @param msg
     * @param cancelTradeDetails
     * @param origTradeDetails
     * @return
     */
    TradeCancel createClientTradeCancel( Order order, TradeCancel msg, TradeWrapper origTradeDetails, TradeWrapper cancelTradeDetails );

    /**
     * create a client trade correct
     *
     * @param order
     * @param msg
     * @return
     */
    TradeCorrect createClientTradeCorrect( Order order, TradeCorrect msg, TradeWrapper origTrade, TradeWrapper correctWrapper );

    /**
     * create a client fill
     *
     * @param order
     * @param msg
     * @param tradeWrapper the trade registration entry
     * @return
     */
    TradeNew createClientTradeNew( Order order, TradeNew msg, TradeWrapper tradeWrapper );

    /**
     * create a force cancel request for an UNKNOWN order to go to exchange
     *
     * @param clOrdId
     * @param side
     * @param orderId
     * @param srcLinkId - linkId for parent/upstream order
     * @return
     */
    CancelRequest createForceMarketCancel( ViewString clOrdId, Side side, ViewString orderId, ViewString srcLinkId );

    /**
     * create a market cancel replace request
     *
     * @param order
     * @param replaceRequest
     * @return
     */
    CancelReplaceRequest createMarketCancelReplaceRequest( Order order, CancelReplaceRequest replaceRequest );

    /**
     * create a market cancel request
     *
     * @param order
     * @return
     */
    CancelRequest createMarketCancelRequest( Order order, CancelRequest clientCancelRequest );

    /**
     * create a market new order single from the order
     * <p>
     * enrich market order
     * <p>
     * populate version with any overrides that need to be retained
     *
     * @param order
     * @param nos
     * @return
     */
    NewOrderSingle createMarketNewOrderSingle( Order order, NewOrderSingle nos );

    /**
     * create a session reject from a decode exception ... strictly speaking should be an execRpt 150=8, not session reject
     */
    SessionReject createSessionReject( String err, BaseReject<?> msg );

    CancelReject getCancelReject( ZString clOrdId, ZString origClOrdId, ZString orderId, ZString rejectReason, CxlRejReason reason,
                                  CxlRejResponseTo msgTypeRejected, OrdStatus status );

    /**
     * create an amend/cancel reject from a decode exception
     */
    CancelReject getCancelReject( ZString clOrdId, ZString origClOrdId, String message, CxlRejResponseTo cancelrequest, OrdStatus status );

    /**
     * create a client cancel reject for Amend or Cancel
     *
     * @param order
     * @param mktReject
     * @param isAmend
     * @return
     */
    CancelReject getClientCancelReject( Order order, VagueOrderReject mktReject, boolean isAmend );

    /**
     * create a client reject for a NOS
     *
     * @param clOrdId
     * @param status
     * @param message
     * @param msg
     * @return
     */
    Event getNOSReject( ViewString clOrdId, OrdStatus status, String message, BaseReject<?> msg );

    /**
     * to be invoked by the thread owning the  event builder instance
     */
    void initPools();

    Rejected synthNOSRejected( NewOrderSingle nos, ZString rejectReason, OrdRejReason reason, OrdStatus status );
}
