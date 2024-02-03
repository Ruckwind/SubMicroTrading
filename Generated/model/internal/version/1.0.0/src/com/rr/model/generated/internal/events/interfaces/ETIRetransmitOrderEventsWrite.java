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

public interface ETIRetransmitOrderEventsWrite extends BaseETIRequestWrite, ETIRetransmitOrderEvents {

   // Getters and Setters
    void setSubscriptionScope( int val );

    void setPartitionID( short val );

    void setRefApplID( ETIEurexDataStream val );

    void setApplBegMsgID( byte[] buf, int offset, int len );
    ReusableString getApplBegMsgIDForUpdate();

    void setApplEndMsgID( byte[] buf, int offset, int len );
    ReusableString getApplEndMsgIDForUpdate();

}
