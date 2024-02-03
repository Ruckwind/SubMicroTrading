package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.SessionRejectReason;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface SessionRejectWrite extends SessionHeaderWrite, SessionReject {

   // Getters and Setters
    void setRefSeqNum( int val );

    void setRefTagID( int val );

    void setRefMsgType( byte[] buf, int offset, int len );
    ReusableString getRefMsgTypeForUpdate();

    void setSessionRejectReason( SessionRejectReason val );

    void setText( byte[] buf, int offset, int len );
    ReusableString getTextForUpdate();

}
