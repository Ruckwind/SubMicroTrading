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

public interface InstrumentSimDataWrite extends CommonHeaderWrite, InstrumentSimData, com.rr.core.model.InstRefData {

   // Getters and Setters
    void setInstrument( Instrument val );

    void setDataSeqNum( long val );

    void setIdSource( SecurityIDSource val );

    void setContract( byte[] buf, int offset, int len );
    ReusableString getContractForUpdate();

    void setSecurityExchange( ExchangeCode val );

    void setBidSpreadEstimate( double val );

    void setLimitStratImproveEst( double val );

}
