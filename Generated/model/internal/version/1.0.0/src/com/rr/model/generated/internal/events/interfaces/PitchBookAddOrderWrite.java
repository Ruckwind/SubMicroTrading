package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.PitchOrderTypeIndicator;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface PitchBookAddOrderWrite extends BaseCboePitchWrite, PitchBookAddOrder {

   // Getters and Setters
    void setOrderId( long val );

    void setSide( Side val );

    void setOrderQty( int val );

    void setSecurityId( byte[] buf, int offset, int len );
    ReusableString getSecurityIdForUpdate();

    void setSecurityIdSrc( SecurityIDSource val );

    void setSecurityExchange( ExchangeCode val );

    void setPrice( double val );

    void setTypeIndic( PitchOrderTypeIndicator val );

}
