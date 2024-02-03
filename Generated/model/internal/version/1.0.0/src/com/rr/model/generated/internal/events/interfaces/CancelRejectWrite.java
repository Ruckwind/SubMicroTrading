package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.CxlRejReason;
import com.rr.model.generated.internal.type.CxlRejResponseTo;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface CancelRejectWrite extends CommonExchangeHeaderWrite, CancelReject {

   // Getters and Setters
    void setOrderId( byte[] buf, int offset, int len );
    ReusableString getOrderIdForUpdate();

    void setClOrdId( byte[] buf, int offset, int len );
    ReusableString getClOrdIdForUpdate();

    void setOrigClOrdId( byte[] buf, int offset, int len );
    ReusableString getOrigClOrdIdForUpdate();

    void setCxlRejReason( CxlRejReason val );

    void setCxlRejResponseTo( CxlRejResponseTo val );

    void setOrdStatus( OrdStatus val );

    void setText( byte[] buf, int offset, int len );
    ReusableString getTextForUpdate();

    void setSecurityId( byte[] buf, int offset, int len );
    ReusableString getSecurityIdForUpdate();

    void setSymbol( byte[] buf, int offset, int len );
    ReusableString getSymbolForUpdate();

    void setSecurityIDSource( SecurityIDSource val );

}
