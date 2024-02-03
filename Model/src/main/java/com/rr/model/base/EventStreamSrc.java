/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

/**
 * client events will generate seperate exchange side events
 * exchange events will generate seperate client side events
 * client /exchange events will have a recovery event which will contain all fields
 * <p>
 * source of both generates a single event which can be used either side .... eg session messages
 */
public enum EventStreamSrc {
    client, exchange, both, recovery
}
