package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.internal.type.ExecType;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface MarketAlertTradeMissingOrdersWrite extends Alert, AlertTradeMissingOrders {

   // Getters and Setters
    void setOrderId( byte[] buf, int offset, int len );
    ReusableString getOrderIdForUpdate();

    void setExecType( ExecType val );

    void setOrdStatus( OrdStatus val );

    void setLastQty( double val );

    void setLastPx( double val );

    void setLastMkt( byte[] buf, int offset, int len );
    ReusableString getLastMktForUpdate();

    void setText( byte[] buf, int offset, int len );
    ReusableString getTextForUpdate();

    @Override void setMsgSeqNum( int val );

    void setPossDupFlag( boolean val );

    void setEventTimestamp( long val );

}
