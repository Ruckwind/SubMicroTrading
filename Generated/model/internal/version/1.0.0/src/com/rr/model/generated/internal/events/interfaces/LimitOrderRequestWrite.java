package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.TimeInForce;
import com.rr.model.generated.internal.type.Side;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface LimitOrderRequestWrite extends BaseTransientLimitOrderWrite, LimitOrderRequest {

   // Getters and Setters
    void setPrice( double val );

    void setOrderQty( double val );

    void setTimeInForce( TimeInForce val );

    void setOrderReceived( long val );

    void setOrderSent( long val );

}
