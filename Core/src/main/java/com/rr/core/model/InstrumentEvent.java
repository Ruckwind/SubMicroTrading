/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ReusableString;

public interface InstrumentEvent {

    void dump( ReusableString out );

    long getEventDate();

    long getEventTime();

    // Getters and Setters
    SecDefEventType getEventType();
}
