package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.RunStatus;
import com.rr.model.generated.internal.events.interfaces.StratInstrument;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface StrategyRun extends SessionHeaderWrite, Event {

   // Getters and Setters
    ViewString getUserName();

    long getLiveStartTimestamp();

    ViewString getIdOfExportComponent();

    RunStatus getStatus();

    ViewString getAlgoId();

    ViewString getStratTimeZone();

    long getBtStartTimestamp();

    long getBtEndTimestamp();

    double getUnrealisedTotalPnL();

    double getRealisedTotalPnL();

    int getNumTrades();

    ViewString getStrategyDefinition();

    long getId();

    int getNoInstEntries();

    StratInstrument getInstruments();

    @Override void dump( ReusableString out );

}
