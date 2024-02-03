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

public interface CancelRequestWrite extends BaseOrderRequestWrite, CancelRequest {

   // Getters and Setters
    void setAccount( byte[] buf, int offset, int len );
    ReusableString getAccountForUpdate();

    @Override void setClOrdId( byte[] buf, int offset, int len );
    @Override ReusableString getClOrdIdForUpdate();

    @Override void setOrigClOrdId( byte[] buf, int offset, int len );
    @Override ReusableString getOrigClOrdIdForUpdate();

    void setOrderId( byte[] buf, int offset, int len );
    ReusableString getOrderIdForUpdate();

}
