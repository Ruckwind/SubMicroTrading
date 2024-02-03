package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.ETIOrderProcessingType;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface ETISessionLogonRequestWrite extends BaseETIRequestWrite, ETISessionLogonRequest {

   // Getters and Setters
    void setHeartBtIntMS( int val );

    void setPartyIDSessionID( int val );

    void setDefaultCstmApplVerID( byte[] buf, int offset, int len );
    ReusableString getDefaultCstmApplVerIDForUpdate();

    void setPassword( byte[] buf, int offset, int len );
    ReusableString getPasswordForUpdate();

    void setApplUsageOrders( ETIOrderProcessingType val );

    void setApplUsageQuotes( ETIOrderProcessingType val );

    void setOrderRoutingIndicator( boolean val );

    void setApplicationSystemName( byte[] buf, int offset, int len );
    ReusableString getApplicationSystemNameForUpdate();

    void setApplicationSystemVer( byte[] buf, int offset, int len );
    ReusableString getApplicationSystemVerForUpdate();

    void setApplicationSystemVendor( byte[] buf, int offset, int len );
    ReusableString getApplicationSystemVendorForUpdate();

}
