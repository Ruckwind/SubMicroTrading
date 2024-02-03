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

public interface UTPTradingSessionStatus extends BaseUTPWrite, Event {

   // Getters and Setters
    long getMktPhaseChgTime();

    ViewString getInstClassId();

    ViewString getInstClassStatus();

    boolean getOrderEntryAllowed();

    ViewString getTradingSessionId();

    @Override void dump( ReusableString out );

}
