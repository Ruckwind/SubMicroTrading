package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.TradingSessionID;
import com.rr.model.generated.internal.type.TradingSessionSubID;
import com.rr.model.generated.internal.type.TradSesStatus;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface TradingSessionStatus extends SessionHeaderWrite, Event {

   // Getters and Setters
    int getMarketSegmentID();

    TradingSessionID getTradingSessionID();

    TradingSessionSubID getTradingSessionSubID();

    TradSesStatus getTradSesStatus();

    long getTransactTime();

    @Override void dump( ReusableString out );

}
