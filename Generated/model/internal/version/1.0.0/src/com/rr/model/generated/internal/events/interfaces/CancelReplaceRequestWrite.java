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

public interface CancelReplaceRequestWrite extends OrderRequestWrite, CancelReplaceRequest {

   // Getters and Setters
    @Override void setClOrdId( byte[] buf, int offset, int len );
    @Override ReusableString getClOrdIdForUpdate();

    @Override void setOrigClOrdId( byte[] buf, int offset, int len );
    @Override ReusableString getOrigClOrdIdForUpdate();

    @Override void setExDest( byte[] buf, int offset, int len );
    @Override ReusableString getExDestForUpdate();

    @Override void setSecurityExchange( ExchangeCode val );

    void setOrderId( byte[] buf, int offset, int len );
    ReusableString getOrderIdForUpdate();

}
