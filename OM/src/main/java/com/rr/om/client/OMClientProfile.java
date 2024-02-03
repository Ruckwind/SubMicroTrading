/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.client;

import com.rr.core.model.ClientProfile;
import com.rr.model.generated.internal.events.interfaces.Alert;
import com.rr.model.generated.internal.events.interfaces.CancelReplaceRequest;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.om.model.event.OrderEventHandlers;
import com.rr.om.order.Order;
import com.rr.om.processor.states.StateException;

public interface OMClientProfile extends ClientProfile, OrderEventHandlers {

    Alert handleAmendGetAlerts( final Order order, final CancelReplaceRequest msg ) throws StateException;

    Alert handleNOSGetAlerts( final Order order, final NewOrderSingle msg ) throws StateException;

    boolean isSendClientLateFills();

    /**
     * @param sendClientLateFills
     * @return previous value
     */
    boolean setSendClientLateFills( boolean sendClientLateFills );
}
