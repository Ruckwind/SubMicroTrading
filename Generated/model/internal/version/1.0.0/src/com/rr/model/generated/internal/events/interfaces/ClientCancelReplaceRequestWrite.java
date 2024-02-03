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

public interface ClientCancelReplaceRequestWrite extends OrderRequest, CancelReplaceRequest {

   // Getters and Setters
    void setClOrdId( byte[] buf, int offset, int len );
    ReusableString getClOrdIdForUpdate();

    void setOrigClOrdId( byte[] buf, int offset, int len );
    ReusableString getOrigClOrdIdForUpdate();

    AssignableString getExDestForUpdate();

    void setSecurityExchange( ExchangeCode val );

    void setOrderId( byte[] buf, int offset, int len );
    ReusableString getOrderIdForUpdate();

    AssignableString getAccountForUpdate();

    AssignableString getTextForUpdate();

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

    AssignableString getStratParamsForUpdate();

    void setEffectiveTime( long val );

    void setExpireTime( long val );

    void setOrderReceived( long val );

    @Override void setOrderSent( long val );

    void setInstrument( Instrument val );

    void setClient( ClientProfile val );

    AssignableString getSecurityIdForUpdate();

    AssignableString getSymbolForUpdate();

    void setMaturityMonthYear( int val );

    void setCurrency( Currency val );

    void setSecurityIDSource( SecurityIDSource val );

    void setTransactTime( long val );

    void setSide( Side val );

    void setCurPos( double val );

    void setCurRefPx( double val );

    void setTargetDest( OrdDestType val );

    void setBroker( PartyID val );

    void setClearer( PartyID val );

    AssignableString getParentClOrdIdForUpdate();

    AssignableString getStratIdForUpdate();

    AssignableString getOrigStratIdForUpdate();

    void setSenderCompId( byte[] buf, int offset, int len );
    ReusableString getSenderCompIdForUpdate();

    AssignableString getOnBehalfOfIdForUpdate();

    @Override void setMsgSeqNum( int val );

    void setPossDupFlag( boolean val );

    void setEventTimestamp( long val );

}
