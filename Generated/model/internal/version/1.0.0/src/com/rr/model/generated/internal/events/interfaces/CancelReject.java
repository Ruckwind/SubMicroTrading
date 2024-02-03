package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.CxlRejReason;
import com.rr.model.generated.internal.type.CxlRejResponseTo;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface CancelReject extends CommonExchangeHeader, Event {

   // Getters and Setters
    ViewString getOrderId();

    ViewString getClOrdId();

    ViewString getOrigClOrdId();

    CxlRejReason getCxlRejReason();

    CxlRejResponseTo getCxlRejResponseTo();

    OrdStatus getOrdStatus();

    ViewString getText();

    ViewString getSecurityId();

    ViewString getSymbol();

    SecurityIDSource getSecurityIDSource();

    @Override void dump( ReusableString out );

}
