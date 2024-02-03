package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.RefPriceType;
import com.rr.model.generated.internal.type.OrderCapacity;
import com.rr.model.generated.internal.type.OrdDestType;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface MarketCancelReplaceRequestUpdate extends OrderRequest, CancelReplaceRequest {

   // Getters and Setters
    void setClOrdId( byte[] buf, int offset, int len );
    ReusableString getClOrdIdForUpdate();

    void setOrigClOrdId( byte[] buf, int offset, int len );
    ReusableString getOrigClOrdIdForUpdate();

    void setSecurityExchange( ExchangeCode val );

    void setOrderId( byte[] buf, int offset, int len );
    ReusableString getOrderIdForUpdate();

    void setPrice( double val );

    void setOrderQty( double val );

    void setRefPriceType( RefPriceType val );

    void setTickOffset( int val );

    void setOrderCapacity( OrderCapacity val );

    void setStratParams( byte[] buf, int offset, int len );
    ReusableString getStratParamsForUpdate();

    @Override void setOrderSent( long val );

    void setMaturityMonthYear( int val );

    void setCurrency( Currency val );

    void setCurPos( double val );

    void setCurRefPx( double val );

    void setTargetDest( OrdDestType val );

    void setParentClOrdId( byte[] buf, int offset, int len );
    ReusableString getParentClOrdIdForUpdate();

    void setStratId( byte[] buf, int offset, int len );
    ReusableString getStratIdForUpdate();

    void setOrigStratId( byte[] buf, int offset, int len );
    ReusableString getOrigStratIdForUpdate();

    @Override void setMsgSeqNum( int val );

    void setPossDupFlag( boolean val );

    void setEventTimestamp( long val );


    void setSrcEvent( OrderRequest request );
    OrderRequest getSrcEvent();

}
