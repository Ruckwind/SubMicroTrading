package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.OrdRejReason;
import com.rr.model.generated.internal.type.TradingStatus;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface Rejected extends CommonExecRpt, Event {

   // Getters and Setters
    ViewString getOrigClOrdId();

    OrdRejReason getOrdRejReason();

    TradingStatus getTradingStatus();

    @Override void dump( ReusableString out );

}
