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

public interface AuctionSummary extends BaseCboePitchWrite, Event {

   // Getters and Setters
    ViewString getSecurityId();

    SecurityIDSource getSecurityIdSrc();

    ExchangeCode getSecurityExchange();

    AuctionType getAuctionType();

    double getPrice();

    int getQty();

    PriceCollarTolerance getPriceCollarTol();

    AuctionCollarIncludesPrimaryQuotes getIncPrimary();

    @Override void dump( ReusableString out );

}
