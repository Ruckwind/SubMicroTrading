package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.interfaces.SecMassStatGrp;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface MassInstrumentStateChange extends SessionHeaderWrite, Event {

   // Getters and Setters
    int getMarketSegmentID();

    int getInstrumentScopeProductComplex();

    int getSecurityMassTradingStatus();

    long getTransactTime();

    int getNumRelatedSym();

    SecMassStatGrp getInstState();

    @Override void dump( ReusableString out );

}
