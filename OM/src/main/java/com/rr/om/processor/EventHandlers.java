/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

import com.rr.core.model.EventHandler;

public interface EventHandlers extends OMOrderHandler, QuoteHandler, EventHandler {

    // tag
}
