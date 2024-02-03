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

public interface UTPLogonWrite extends BaseUTPWrite, UTPLogon {

   // Getters and Setters
    void setLastMsgSeqNum( int val );

    void setUserName( byte[] buf, int offset, int len );
    ReusableString getUserNameForUpdate();

}
