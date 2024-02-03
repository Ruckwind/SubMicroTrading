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

public interface BookAddOrderWrite extends BaseITCHWrite, BookAddOrder {

   // Getters and Setters
    void setNanosecond( int val );

    void setOrderId( long val );

    void setSide( Side val );

    void setOrderQty( int val );

    void setBook( Book val );

    void setPrice( double val );

}
