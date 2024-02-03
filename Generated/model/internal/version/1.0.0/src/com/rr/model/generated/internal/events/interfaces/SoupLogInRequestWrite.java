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

public interface SoupLogInRequestWrite extends BaseSoupWrite, SoupLogInRequest {

   // Getters and Setters
    void setUserName( byte[] buf, int offset, int len );
    ReusableString getUserNameForUpdate();

    void setPassword( byte[] buf, int offset, int len );
    ReusableString getPasswordForUpdate();

    void setRequestedSession( byte[] buf, int offset, int len );
    ReusableString getRequestedSessionForUpdate();

    void setRequestedSeqNum( long val );

}
