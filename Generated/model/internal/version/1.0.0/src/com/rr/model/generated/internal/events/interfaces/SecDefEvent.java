package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;
import com.rr.model.internal.type.SubEvent;

@SuppressWarnings( { "unused", "override"  })

public interface SecDefEvent extends SubEvent, com.rr.core.model.InstrumentEvent {

   // Getters and Setters
    SecDefEventType getEventType();

    long getEventDate();

    long getEventTime();

    @Override void dump( ReusableString out );

    void setEventType( SecDefEventType val );

    void setEventDate( long val );

    void setEventTime( long val );

}
