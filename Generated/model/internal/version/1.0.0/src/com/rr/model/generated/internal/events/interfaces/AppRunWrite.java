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

public interface AppRunWrite extends SessionHeaderWrite, AppRun {

   // Getters and Setters
    void setUserName( byte[] buf, int offset, int len );
    ReusableString getUserNameForUpdate();

    void setLiveStartTimestamp( long val );

    void setLiveEndTimestamp( long val );

    void setStatus( RunStatus val );

    void setUnrealisedTotalPnL( double val );

    void setRealisedTotalPnL( double val );

    void setNumTrades( int val );

    void setId( long val );

    void setNumStrategies( int val );

}
