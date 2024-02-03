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

public interface MilleniumLogonWrite extends BaseMilleniumWrite, MilleniumLogon {

   // Getters and Setters
    void setLastMsgSeqNum( int val );

    void setUserName( byte[] buf, int offset, int len );
    ReusableString getUserNameForUpdate();

    void setPassword( byte[] buf, int offset, int len );
    ReusableString getPasswordForUpdate();

    void setNewPassword( byte[] buf, int offset, int len );
    ReusableString getNewPasswordForUpdate();

}
