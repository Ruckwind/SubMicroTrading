package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.RunStatus;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface AppRun extends SessionHeaderWrite, Event {

   // Getters and Setters
    ViewString getUserName();

    long getLiveStartTimestamp();

    long getLiveEndTimestamp();

    RunStatus getStatus();

    double getUnrealisedTotalPnL();

    double getRealisedTotalPnL();

    int getNumTrades();

    long getId();

    int getNumStrategies();

    @Override void dump( ReusableString out );

}
