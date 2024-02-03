/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

import com.rr.core.lang.Stopable;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.model.ModelVersion;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.CxlRejReason;
import com.rr.model.generated.internal.type.OrdRejReason;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.om.model.event.EventBuilder;
import com.rr.om.order.Order;
import com.rr.om.order.OrderVersion;
import com.rr.om.processor.states.OrderState;
import com.rr.om.processor.states.StateException;
import com.rr.om.registry.TradeRegistry;
import com.rr.om.router.OrderRouter;
import com.rr.om.validate.EventValidator;

public interface EventProcessor extends EventHandlers, Stopable {

    /**
     * process the message now
     *
     * @param msg
     * @NOTE only to be invoked by the processor itself OR its dispatcher
     */
    @Override void handleNow( Event msg );

    /**
     * shutdown the processor
     */
    @Override void stop();

    /**
     * initialise the event processor, creates pools and state machine
     *
     * @NOTE MUST BE CALLED ON THREAD THE PROCESSOR RUNS WITHIN
     */
    @Override void threadedInit();

    void enqueueDownStream( Event msg );

    /**
     * METHODS INTENDED FOR PROTECTED ACCESS
     */

    void enqueueUpStream( Event msg );

    /**
     * recycle methods
     */
    void freeMessage( Event msg );

    /**
     * recycle the order, its version, its version base requests and the clOrdId chains
     *
     * @param value
     * @NOTE if a map has some refs to the clOrdIds these will be void and UNSAFE
     */
    void freeOrder( Order value );

    void freeVersion( OrderVersion ver );

    EventBuilder getEventBuilder();

    ModelVersion getEventModelVersion();

    /**
     * get instance of order version from factory
     *
     * @param cancelRequest
     * @param lastAcc
     * @return
     */
    OrderVersion getNewVersion( CancelRequest cancelRequest, OrderVersion lastAcc );

    /**
     * get instance of order version from factory
     *
     * @param lastAcc
     * @return
     */
    OrderVersion getNewVersion( CancelReplaceRequest repRequest, OrderVersion lastAcc );

    OrderState getStateCompleted();

    OrderState getStateOpen();

    OrderState getStatePendingCancel();

    /**
     * State Machine Accessors
     */

    OrderState getStatePendingNew();

    OrderState getStatePendingReplace();

    /**
     * @return the trade registry
     */
    TradeRegistry getTradeRegistry();

    /**
     * @return the processors event validator
     */
    EventValidator getValidator();

    /**
     * EXPENSIVE OPERATION, ONLY FOR USE FOR PROBLEM SOLVING OR END OF DAY
     */
    void logStats();

    void routeMessageDownstream( Order order, NewOrderSingle newMsg ) throws StateException;

    void sendAlertChain( Alert alerts );

    /**
     * create a client cancel reject to send back to client
     *
     * @param msg
     * @param rejectReason
     * @param reason
     * @param status
     */
    void sendCancelReject( CancelRequest msg, ZString rejectReason, CxlRejReason reason, OrdStatus status );

    /**
     * create a client cancel replace reject to send back to client
     *
     * @param msg
     * @param rejectReason
     * @param reason
     * @param status
     * @NOTE doesnt hold refernce to msg so that can be recycled
     */
    void sendCancelReplaceReject( CancelReplaceRequest msg, ZString rejectReason, CxlRejReason reason, OrdStatus status );

    /**
     * register marketClOrdId with the order and send message downstream
     */
    void sendDownStream( BaseOrderRequest req, Order order );

    /**
     * create a reject message and enqueue for sending back to client for a NOS
     *
     * @param nos
     * @param rejectReason
     * @param reason
     * @param status
     */
    void sendReject( NewOrderSingle nos, ZString rejectReason, OrdRejReason reason, OrdStatus status );

    /**
     * messages which cant be dealt with must be sent to the HUB
     * <p>
     * messages are not enqueued within the processor
     *
     * @param msg
     */
    void sendTradeHub( TradeBase msg );

    /**
     * set the processors downstream router
     *
     * @param router
     */
    void setProcessorRouter( OrderRouter router );

    /**
     * @return number of order NOS/AMEND/CANCEL requests
     */
    int size();
}
