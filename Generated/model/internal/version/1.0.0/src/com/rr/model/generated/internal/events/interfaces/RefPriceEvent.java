package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface RefPriceEvent extends CommonHeaderWrite, Event, com.rr.core.model.InstRefDataEvent<RefPriceEvent> {

   // Getters and Setters
    DataSrc getDataSrc();

    Instrument getInstrument();

    /**
     *optional sequence number which scopes update to instrument
     */
    long getDataSeqNum();

    ViewString getSubject();

    ViewString getCode();

    /**
     *value for code
     */
    double getVal();

    @Override void dump( ReusableString out );

}
