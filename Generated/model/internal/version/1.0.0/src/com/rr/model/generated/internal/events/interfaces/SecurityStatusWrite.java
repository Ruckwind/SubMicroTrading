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

public interface SecurityStatusWrite extends BaseMDResponseWrite, SecurityStatus {

   // Getters and Setters
    void setSecurityIDSource( SecurityIDSource val );

    void setSecurityID( byte[] buf, int offset, int len );
    ReusableString getSecurityIDForUpdate();

    void setTradeDate( int val );

    void setHighPx( double val );

    void setLowPx( double val );

    void setSecurityTradingStatus( SecurityTradingStatus val );

    void setHaltReason( int val );

    void setSecurityTradingEvent( int val );

    void setSymbol( byte[] buf, int offset, int len );
    ReusableString getSymbolForUpdate();

    void setSecurityExchange( ExchangeCode val );

}
