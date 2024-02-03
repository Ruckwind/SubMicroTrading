package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.Side;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface ForceCancel extends CommonHeader, Event {

   // Getters and Setters
    ViewString getClOrdId();

    ViewString getOrigClOrdId();

    ViewString getOrderId();

    Side getSide();

    @Override int getMsgSeqNum();

    @Override boolean getPossDupFlag();

    @Override void dump( ReusableString out );

}
