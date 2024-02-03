package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.ETIEurexDataStream;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface ETIRetransmitOrderEvents extends BaseETIRequestWrite, Event {

   // Getters and Setters
    int getSubscriptionScope();

    short getPartitionID();

    ETIEurexDataStream getRefApplID();

    ViewString getApplBegMsgID();

    ViewString getApplEndMsgID();

    @Override void dump( ReusableString out );

}
