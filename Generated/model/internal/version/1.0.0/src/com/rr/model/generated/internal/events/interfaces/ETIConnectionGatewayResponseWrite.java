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

public interface ETIConnectionGatewayResponseWrite extends BaseETIResponseWrite, ETIConnectionGatewayResponse {

   // Getters and Setters
    void setRequestTime( long val );

    @Override void setMsgSeqNum( int val );

    void setGatewayID( int val );

    void setGatewaySubID( int val );

    void setSecGatewayID( int val );

    void setSecGatewaySubID( int val );

    void setSessionMode( ETISessionMode val );

    void setTradSesMode( ETIEnv val );

}
