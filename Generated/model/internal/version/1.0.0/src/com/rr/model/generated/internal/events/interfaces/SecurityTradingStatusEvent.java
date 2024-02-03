package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.SecurityTradingStatus;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface SecurityTradingStatusEvent extends CommonHeaderWrite, Event, com.rr.core.model.InstRefDataEvent<SecurityTradingStatusEvent> {

   // Getters and Setters
    DataSrc getDataSrc();

    @Override Instrument getInstrument();

    ViewString getSubject();

    /**
     *optional sequence number which scopes update to instrument
     */
    long getDataSeqNum();

    SecurityTradingStatus getTradingStatus();

    @Override void dump( ReusableString out );

}
