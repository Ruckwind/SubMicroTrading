package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.StatsPriceType;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface PitchPriceStatisticWrite extends BaseCboePitchWrite, PitchPriceStatistic {

   // Getters and Setters
    void setSecurityId( byte[] buf, int offset, int len );
    ReusableString getSecurityIdForUpdate();

    void setSecurityIdSrc( SecurityIDSource val );

    void setSecurityExchange( ExchangeCode val );

    void setPrice( double val );

    void setStatType( StatsPriceType val );

}
