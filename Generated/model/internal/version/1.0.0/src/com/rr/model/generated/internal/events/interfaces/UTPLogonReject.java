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

public interface UTPLogonReject extends BaseUTPWrite, Event {

   // Getters and Setters
    int getLastMsgSeqNumRcvd();

    int getLastMsgSeqNumSent();

    UTPRejCode getRejectCode();

    ViewString getRejectText();

    @Override void dump( ReusableString out );

}
