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

public interface InstrumentSimData extends CommonHeaderWrite, Event, com.rr.core.model.InstRefData {

   // Getters and Setters
    @Override Instrument getInstrument();

    /**
     *optional sequence number which scopes update to instrument
     */
    long getDataSeqNum();

    SecurityIDSource getIdSource();

    ViewString getContract();

    ExchangeCode getSecurityExchange();

    double getBidSpreadEstimate();

    double getLimitStratImproveEst();

    @Override void dump( ReusableString out );

}
