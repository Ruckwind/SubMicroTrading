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

public interface LogoutWrite extends SessionHeaderWrite, Logout {

   // Getters and Setters
    void setSenderCompId( byte[] buf, int offset, int len );
    ReusableString getSenderCompIdForUpdate();

    void setSenderSubId( byte[] buf, int offset, int len );
    ReusableString getSenderSubIdForUpdate();

    void setTargetCompId( byte[] buf, int offset, int len );
    ReusableString getTargetCompIdForUpdate();

    void setTargetSubId( byte[] buf, int offset, int len );
    ReusableString getTargetSubIdForUpdate();

    void setOnBehalfOfId( byte[] buf, int offset, int len );
    ReusableString getOnBehalfOfIdForUpdate();

    void setText( byte[] buf, int offset, int len );
    ReusableString getTextForUpdate();

    void setLastMsgSeqNumProcessed( int val );

    void setNextExpectedMsgSeqNum( int val );

}
