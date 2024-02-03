/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor.states;

import com.rr.core.lang.ZString;
import com.rr.om.model.event.OrderEventHandlers;
import com.rr.om.order.Order;

public interface OrderState extends OrderEventHandlers {

    ZString getName();

    /**
     * @return true if this state is pending (ie PendingNew/PendingCancel/PendingAmend)
     */
    boolean isPending();

    void onEnter( Order order );

    void onExit( Order order );

    // @TODO add quouting or put in QuoteState
}
