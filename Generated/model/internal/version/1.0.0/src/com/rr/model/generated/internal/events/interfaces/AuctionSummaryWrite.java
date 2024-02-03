package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.AuctionType;
import com.rr.model.generated.internal.type.PriceCollarTolerance;
import com.rr.model.generated.internal.type.AuctionCollarIncludesPrimaryQuotes;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface AuctionSummaryWrite extends BaseCboePitchWrite, AuctionSummary {

   // Getters and Setters
    void setSecurityId( byte[] buf, int offset, int len );
    ReusableString getSecurityIdForUpdate();

    void setSecurityIdSrc( SecurityIDSource val );

    void setSecurityExchange( ExchangeCode val );

    void setAuctionType( AuctionType val );

    void setPrice( double val );

    void setQty( int val );

    void setPriceCollarTol( PriceCollarTolerance val );

    void setIncPrimary( AuctionCollarIncludesPrimaryQuotes val );

}
