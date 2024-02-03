/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.router;

import com.rr.core.component.SMTComponent;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.model.EventHandler;
import com.rr.model.generated.internal.events.interfaces.BaseOrderRequest;

/**
 * router tailored to the processor
 */
public interface OrderRouter extends SMTComponent {

    EventHandler[] getAllRoutes();

    /**
     * return route for the NOS
     * <p>
     * cancel and amends use sticky routing
     *
     * @param req - a NOS / Amend / Cancel
     * @return
     */
    EventHandler getRoute( BaseOrderRequest req, EventHandler src );

    default void purgeHandler( EventHandler deadHandler ) { }

    void purgeRoute( ZString clOrdId, Logger log );

    ;
}
