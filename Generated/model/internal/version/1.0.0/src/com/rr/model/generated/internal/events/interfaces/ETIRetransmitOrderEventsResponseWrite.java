package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface ETIRetransmitOrderEventsResponseWrite extends BaseETIResponseWrite, ETIRetransmitOrderEventsResponse {

   // Getters and Setters
    void setRequestTime( long val );

    void setApplTotalMessageCount( short val );

    void setApplEndMsgID( byte[] buf, int offset, int len );
    ReusableString getApplEndMsgIDForUpdate();

    void setRefApplLastMsgID( byte[] buf, int offset, int len );
    ReusableString getRefApplLastMsgIDForUpdate();

}
