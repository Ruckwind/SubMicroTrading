package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.internal.type.ExecType;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.OrderCapacity;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface MarketCancelledWrite extends CommonExecRpt, Cancelled {

   // Getters and Setters
    void setClOrdId( byte[] buf, int offset, int len );
    ReusableString getClOrdIdForUpdate();

    void setOrigClOrdId( byte[] buf, int offset, int len );
    ReusableString getOrigClOrdIdForUpdate();

    void setExecId( byte[] buf, int offset, int len );
    ReusableString getExecIdForUpdate();

    void setOrderId( byte[] buf, int offset, int len );
    ReusableString getOrderIdForUpdate();

    void setExecType( ExecType val );

    void setOrdStatus( OrdStatus val );

    void setTransactTime( long val );

    void setLeavesQty( double val );

    void setCumQty( double val );

    void setAvgPx( double val );

    void setSide( Side val );

    void setText( byte[] buf, int offset, int len );
    ReusableString getTextForUpdate();

    void setMktCapacity( OrderCapacity val );

    void setParentClOrdId( byte[] buf, int offset, int len );
    ReusableString getParentClOrdIdForUpdate();

    void setStratId( byte[] buf, int offset, int len );
    ReusableString getStratIdForUpdate();

    void setOrigStratId( byte[] buf, int offset, int len );
    ReusableString getOrigStratIdForUpdate();

    @Override void setMsgSeqNum( int val );

    void setPossDupFlag( boolean val );

    void setEventTimestamp( long val );

}
