/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.validate;

import com.rr.core.lang.ViewString;
import com.rr.model.generated.internal.events.interfaces.CancelReplaceRequest;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.model.generated.internal.type.CxlRejReason;
import com.rr.model.generated.internal.type.OrdRejReason;
import com.rr.om.order.Order;

/**
 * Event validator .. note dont throw exception so dont need pay cost of extra try catch handlers
 * caller will generally need to generate reject so returning false makes more sense in this case
 * <p>
 * EventValidator should NOT validate exchange specifics like OrdType, TIF
 *
 * @author Richard Rose
 */
public interface EventValidator {

    /**
     * @return appropriate reject reason from last validation of NOS
     */
    OrdRejReason getOrdRejectReason();

    /**
     * @return error message from last validation, suitable for encoding in text field
     */
    ViewString getRejectReason();

    /**
     * @return appropriate reject reason from last validation of CancelReplaceRequest
     */
    CxlRejReason getReplaceRejectReason();

    /**
     * validate the AMEND event
     *
     * @return true if validated ok
     */
    boolean validate( CancelReplaceRequest newReq, Order order );

    /**
     * validate the NOS event
     *
     * @param msg
     * @return true if validated ok
     */
    boolean validate( NewOrderSingle msg, Order order );
}
