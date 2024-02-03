/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.exchange;

import com.rr.core.lang.ReusableString;
import com.rr.core.model.ExchangeValidator;
import com.rr.model.generated.internal.events.interfaces.CancelReplaceRequest;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;

public interface OMExchangeValidator extends ExchangeValidator {

    /**
     * validate the NOS appending errors to the supplied err buf
     *
     * @param msg
     * @param err
     * @param now
     */
    void validate( NewOrderSingle msg, ReusableString err, long now );

    /**
     * validate the cancel replace request, appending errors to the err buf
     *
     * @param msg
     * @param err
     */
    void validate( CancelReplaceRequest msg, ReusableString err, long now );
}
