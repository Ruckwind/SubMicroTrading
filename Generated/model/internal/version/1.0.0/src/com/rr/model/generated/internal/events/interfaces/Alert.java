package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.Side;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface Alert extends CommonExchangeHeader, Event {

   // Getters and Setters
    ViewString getClOrdId();

    ViewString getSecurityId();

    ViewString getSymbol();

    Currency getCurrency();

    SecurityIDSource getSecurityIDSource();

    ViewString getText();

    double getOrderQty();

    double getPrice();

    Side getSide();

    @Override void dump( ReusableString out );

}
