package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.OrdDestType;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface BaseOrderRequest extends CommonClientHeader, Event {

   // Getters and Setters
    Instrument getInstrument();

    ClientProfile getClient();

    ViewString getClOrdId();

    ViewString getOrigClOrdId();

    ViewString getSecurityId();

    ViewString getSymbol();

    /**
     *format YYYYMMDD
     */
    int getMaturityMonthYear();

    Currency getCurrency();

    SecurityIDSource getSecurityIDSource();

    long getTransactTime();

    Side getSide();

    double getCurPos();

    double getCurRefPx();

    OrdDestType getTargetDest();

    ViewString getExDest();

    ExchangeCode getSecurityExchange();

    PartyID getBroker();

    PartyID getClearer();

    ViewString getParentClOrdId();

    ViewString getStratId();

    ViewString getOrigStratId();

    @Override void dump( ReusableString out );

}
