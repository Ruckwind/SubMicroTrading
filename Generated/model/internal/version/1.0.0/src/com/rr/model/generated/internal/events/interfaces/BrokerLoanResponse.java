package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.LoanRateType;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface BrokerLoanResponse extends CommonHeaderWrite, Event, com.rr.core.model.CoreBrokerLoanResponse {

   // Getters and Setters
    DataSrc getDataSrc();

    Instrument getInstrument();

    /**
     *optional sequence number which scopes update to instrument
     */
    long getDataSeqNum();

    ViewString getSubject();

    SecurityIDSource getIdSource();

    ViewString getSecurityId();

    ViewString getReference();

    boolean getIsDisabled();

    ExchangeCode getSecurityExchange();

    double getApproveQty();

    double getAmount();

    LoanRateType getType();

    PartyID getBroker();

    @Override void dump( ReusableString out );

}
