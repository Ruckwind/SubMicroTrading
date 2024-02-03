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

public interface SecurityStatus extends BaseMDResponseWrite, Event {

   // Getters and Setters
    SecurityIDSource getSecurityIDSource();

    ViewString getSecurityID();

    int getTradeDate();

    double getHighPx();

    double getLowPx();

    SecurityTradingStatus getSecurityTradingStatus();

    int getHaltReason();

    int getSecurityTradingEvent();

    ViewString getSymbol();

    ExchangeCode getSecurityExchange();

    @Override void dump( ReusableString out );

}
