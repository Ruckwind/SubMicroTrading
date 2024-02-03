package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.Side;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface AlertWrite extends CommonExchangeHeaderWrite, Alert {

   // Getters and Setters
    void setClOrdId( byte[] buf, int offset, int len );
    ReusableString getClOrdIdForUpdate();

    void setSecurityId( byte[] buf, int offset, int len );
    ReusableString getSecurityIdForUpdate();

    void setSymbol( byte[] buf, int offset, int len );
    ReusableString getSymbolForUpdate();

    void setCurrency( Currency val );

    void setSecurityIDSource( SecurityIDSource val );

    void setText( byte[] buf, int offset, int len );
    ReusableString getTextForUpdate();

    void setOrderQty( double val );

    void setPrice( double val );

    void setSide( Side val );

}
