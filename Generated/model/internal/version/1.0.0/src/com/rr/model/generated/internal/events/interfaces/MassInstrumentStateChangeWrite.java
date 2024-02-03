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

public interface MassInstrumentStateChangeWrite extends SessionHeaderWrite, MassInstrumentStateChange {

   // Getters and Setters
    void setMarketSegmentID( int val );

    void setInstrumentScopeProductComplex( int val );

    void setSecurityMassTradingStatus( int val );

    void setTransactTime( long val );

    void setNumRelatedSym( int val );

    void setInstState( SecMassStatGrp val );

}
