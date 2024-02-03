package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.ETISessionMode;
import com.rr.model.generated.internal.type.ETIEnv;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface ETIConnectionGatewayResponse extends BaseETIResponseWrite, Event {

   // Getters and Setters
    long getRequestTime();

    @Override int getMsgSeqNum();

    int getGatewayID();

    int getGatewaySubID();

    int getSecGatewayID();

    int getSecGatewaySubID();

    ETISessionMode getSessionMode();

    ETIEnv getTradSesMode();

    @Override void dump( ReusableString out );

}
