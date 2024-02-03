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

public interface BaseOrderRequestWrite extends CommonClientHeaderWrite, BaseOrderRequest {

   // Getters and Setters
    void setInstrument( Instrument val );

    void setClient( ClientProfile val );

    void setClOrdId( byte[] buf, int offset, int len );
    ReusableString getClOrdIdForUpdate();

    void setOrigClOrdId( byte[] buf, int offset, int len );
    ReusableString getOrigClOrdIdForUpdate();

    void setSecurityId( byte[] buf, int offset, int len );
    ReusableString getSecurityIdForUpdate();

    void setSymbol( byte[] buf, int offset, int len );
    ReusableString getSymbolForUpdate();

    void setMaturityMonthYear( int val );

    void setCurrency( Currency val );

    void setSecurityIDSource( SecurityIDSource val );

    void setTransactTime( long val );

    void setSide( Side val );

    void setCurPos( double val );

    void setCurRefPx( double val );

    void setTargetDest( OrdDestType val );

    void setExDest( byte[] buf, int offset, int len );
    ReusableString getExDestForUpdate();

    void setSecurityExchange( ExchangeCode val );

    void setBroker( PartyID val );

    void setClearer( PartyID val );

    void setParentClOrdId( byte[] buf, int offset, int len );
    ReusableString getParentClOrdIdForUpdate();

    void setStratId( byte[] buf, int offset, int len );
    ReusableString getStratIdForUpdate();

    void setOrigStratId( byte[] buf, int offset, int len );
    ReusableString getOrigStratIdForUpdate();

}
