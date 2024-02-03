package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface CancelReplaceRequest extends OrderRequest, Event {

   // Getters and Setters
    @Override ViewString getClOrdId();

    @Override ViewString getOrigClOrdId();

    @Override ViewString getExDest();

    @Override ExchangeCode getSecurityExchange();

    ViewString getOrderId();

    @Override void dump( ReusableString out );

}
