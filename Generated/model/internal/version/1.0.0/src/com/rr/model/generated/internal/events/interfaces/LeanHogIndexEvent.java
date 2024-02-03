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

public interface LeanHogIndexEvent extends CommonHeaderWrite, Event, com.rr.core.model.InstRefDataEvent<LeanHogIndexEvent> {

   // Getters and Setters
    DataSrc getDataSrc();

    @Override Instrument getInstrument();

    ViewString getSubject();

    /**
     *optional sequence number which scopes update to instrument
     */
    long getDataSeqNum();

    /**
     *the date the index is being published for
     */
    int getIndexDate();

    double getNegotHeadCount();

    double getNegotAverageNetPrice();

    double getNegotAverageCarcWt();

    double getSpmfHeadCount();

    double getSpmfAverageNetPrice();

    double getSpmfAverageCarcWt();

    double getNegotSpmfHeadCount();

    double getNegotSpmfAverageNetPrice();

    double getNegotSpmfAverageCarcWt();

    double getDailyWeightedPrice();

    double getIndexValue();

    @Override void dump( ReusableString out );

}
