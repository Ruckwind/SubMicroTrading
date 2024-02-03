package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.OrdDestType;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface ClientCancelRequestWrite extends BaseOrderRequest, CancelRequest {

   // Getters and Setters
    AssignableString getAccountForUpdate();

    void setClOrdId( byte[] buf, int offset, int len );
    ReusableString getClOrdIdForUpdate();

    void setOrigClOrdId( byte[] buf, int offset, int len );
    ReusableString getOrigClOrdIdForUpdate();

    void setOrderId( byte[] buf, int offset, int len );
    ReusableString getOrderIdForUpdate();

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

    AssignableString getExDestForUpdate();

    void setSecurityExchange( ExchangeCode val );

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
