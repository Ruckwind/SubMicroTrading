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

public interface PitchBookAddOrder extends BaseCboePitchWrite, Event {

   // Getters and Setters
    long getOrderId();

    Side getSide();

    int getOrderQty();

    ViewString getSecurityId();

    SecurityIDSource getSecurityIdSrc();

    ExchangeCode getSecurityExchange();

    double getPrice();

    PitchOrderTypeIndicator getTypeIndic();

    @Override void dump( ReusableString out );

}
