package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.RefPriceType;
import com.rr.model.generated.internal.type.ExecInst;
import com.rr.model.generated.internal.type.HandlInst;
import com.rr.model.generated.internal.type.OrderCapacity;
import com.rr.model.generated.internal.type.OrdType;
import com.rr.model.generated.internal.type.TimeInForce;
import com.rr.model.generated.internal.type.BookingType;
import com.rr.model.generated.internal.type.TargetStrategy;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface OrderRequest extends BaseOrderRequest, Event {

   // Getters and Setters
    ViewString getAccount();

    ViewString getText();

    double getPrice();

    double getOrderQty();

    RefPriceType getRefPriceType();

    /**
     *optional offset for limit price generation
     */
    int getTickOffset();

    ExecInst getExecInst();

    HandlInst getHandlInst();

    OrderCapacity getOrderCapacity();

    OrdType getOrdType();

    SecurityType getSecurityType();

    TimeInForce getTimeInForce();

    BookingType getBookingType();

    TargetStrategy getTargetStrategy();

    ViewString getStratParams();

    long getEffectiveTime();

    long getExpireTime();

    long getOrderReceived();

    void setOrderSent( long val );
    long getOrderSent();

    @Override void dump( ReusableString out );

}
