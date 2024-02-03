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

public interface StrategyStateWrite extends SessionHeaderWrite, StrategyState {

   // Getters and Setters
    void setUserName( byte[] buf, int offset, int len );
    ReusableString getUserNameForUpdate();

    void setLiveStartTimestamp( long val );

    void setIdOfExportComponent( byte[] buf, int offset, int len );
    ReusableString getIdOfExportComponentForUpdate();

    void setStatus( RunStatus val );

    void setStratTimestamp( long val );

    void setUnrealisedTotalPnL( double val );

    void setRealisedTotalPnL( double val );

    void setId( long val );

    void setIsDeltaMode( boolean val );

    void setStratStateMsgsInGrp( int val );

    void setCurStratStateMsgInGrp( int val );

    void setNoInstEntries( int val );

    void setInstState( StratInstrumentState val );

}
