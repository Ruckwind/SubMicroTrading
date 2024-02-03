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

public interface ETISessionLogonResponse extends BaseETIResponseWrite, Event {

   // Getters and Setters
    long getRequestTime();

    long getThrottleTimeIntervalMS();

    int getThrottleNoMsgs();

    int getThrottleDisconnectLimit();

    int getHeartBtIntMS();

    int getSessionInstanceID();

    ETIEnv getTradSesMode();

    ViewString getDefaultCstmApplVerID();

    @Override void dump( ReusableString out );

}
