package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.UTPRejCode;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface UTPLogonRejectWrite extends BaseUTPWrite, UTPLogonReject {

   // Getters and Setters
    void setLastMsgSeqNumRcvd( int val );

    void setLastMsgSeqNumSent( int val );

    void setRejectCode( UTPRejCode val );

    void setRejectText( byte[] buf, int offset, int len );
    ReusableString getRejectTextForUpdate();

}
