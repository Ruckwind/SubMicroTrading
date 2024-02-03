package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.SettlementPriceType;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface SettlementPriceEvent extends CommonHeaderWrite, Event, com.rr.core.model.InstRefDataEvent<SettlementPriceEvent> {

   // Getters and Setters
    DataSrc getDataSrc();

    Instrument getInstrument();

    ViewString getSubject();

    /**
     *optional sequence number which scopes update to instrument
     */
    long getDataSeqNum();

    double getSettlementPrice();

    SettlementPriceType getSettlementPriceType();

    long getSettleDateTime();

    @Override void dump( ReusableString out );

}
