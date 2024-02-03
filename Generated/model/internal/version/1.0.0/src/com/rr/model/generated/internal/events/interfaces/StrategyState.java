package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.RunStatus;
import com.rr.model.generated.internal.events.interfaces.StratInstrumentState;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface StrategyState extends SessionHeaderWrite, Event {

   // Getters and Setters
    ViewString getUserName();

    long getLiveStartTimestamp();

    ViewString getIdOfExportComponent();

    RunStatus getStatus();

    long getStratTimestamp();

    double getUnrealisedTotalPnL();

    double getRealisedTotalPnL();

    long getId();

    boolean getIsDeltaMode();

    /**
     *number of StategyState messages in current snapshot
     */
    int getStratStateMsgsInGrp();

    /**
     *index of this StategyState message in current snapshot
     */
    int getCurStratStateMsgInGrp();

    int getNoInstEntries();

    StratInstrumentState getInstState();

    @Override void dump( ReusableString out );

}
