package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.EncryptMethod;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface Logon extends SessionHeaderWrite, Event {

   // Getters and Setters
    ViewString getSenderCompId();

    ViewString getSenderSubId();

    ViewString getTargetCompId();

    ViewString getTargetSubId();

    ViewString getOnBehalfOfId();

    EncryptMethod getEncryptMethod();

    int getHeartBtInt();

    int getRawDataLen();

    ViewString getRawData();

    boolean getResetSeqNumFlag();

    int getNextExpectedMsgSeqNum();

    @Override void dump( ReusableString out );

}
