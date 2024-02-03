package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.ETIEnv;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface ETISessionLogonResponseWrite extends BaseETIResponseWrite, ETISessionLogonResponse {

   // Getters and Setters
    void setRequestTime( long val );

    void setThrottleTimeIntervalMS( long val );

    void setThrottleNoMsgs( int val );

    void setThrottleDisconnectLimit( int val );

    void setHeartBtIntMS( int val );

    void setSessionInstanceID( int val );

    void setTradSesMode( ETIEnv val );

    void setDefaultCstmApplVerID( byte[] buf, int offset, int len );
    ReusableString getDefaultCstmApplVerIDForUpdate();

}
