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
import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.OrdDestType;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface OrderRequestWrite extends BaseOrderRequestWrite, OrderRequest {

   // Getters and Setters
    void setAccount( byte[] buf, int offset, int len );
    ReusableString getAccountForUpdate();

    void setText( byte[] buf, int offset, int len );
    ReusableString getTextForUpdate();

    void setPrice( double val );

    void setOrderQty( double val );

    void setRefPriceType( RefPriceType val );

    void setTickOffset( int val );

    void setExecInst( ExecInst val );

    void setHandlInst( HandlInst val );

    void setOrderCapacity( OrderCapacity val );

    void setOrdType( OrdType val );

    void setSecurityType( SecurityType val );

    void setTimeInForce( TimeInForce val );

    void setBookingType( BookingType val );

    void setTargetStrategy( TargetStrategy val );

    void setStratParams( byte[] buf, int offset, int len );
    ReusableString getStratParamsForUpdate();

    void setEffectiveTime( long val );

    void setExpireTime( long val );

    void setOrderReceived( long val );

    void setOrderSent( long val );

}
