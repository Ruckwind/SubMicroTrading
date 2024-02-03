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

public interface ETISessionLogonRequest extends BaseETIRequestWrite, Event {

   // Getters and Setters
    int getHeartBtIntMS();

    int getPartyIDSessionID();

    ViewString getDefaultCstmApplVerID();

    ViewString getPassword();

    ETIOrderProcessingType getApplUsageOrders();

    ETIOrderProcessingType getApplUsageQuotes();

    boolean getOrderRoutingIndicator();

    ViewString getApplicationSystemName();

    ViewString getApplicationSystemVer();

    ViewString getApplicationSystemVendor();

    @Override void dump( ReusableString out );

}
