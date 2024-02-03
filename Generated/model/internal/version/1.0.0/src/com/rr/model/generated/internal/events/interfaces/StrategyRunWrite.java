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

public interface StrategyRunWrite extends SessionHeaderWrite, StrategyRun {

   // Getters and Setters
    void setUserName( byte[] buf, int offset, int len );
    ReusableString getUserNameForUpdate();

    void setLiveStartTimestamp( long val );

    void setIdOfExportComponent( byte[] buf, int offset, int len );
    ReusableString getIdOfExportComponentForUpdate();

    void setStatus( RunStatus val );

    void setAlgoId( byte[] buf, int offset, int len );
    ReusableString getAlgoIdForUpdate();

    void setStratTimeZone( byte[] buf, int offset, int len );
    ReusableString getStratTimeZoneForUpdate();

    void setBtStartTimestamp( long val );

    void setBtEndTimestamp( long val );

    void setUnrealisedTotalPnL( double val );

    void setRealisedTotalPnL( double val );

    void setNumTrades( int val );

    void setStrategyDefinition( byte[] buf, int offset, int len );
    ReusableString getStrategyDefinitionForUpdate();

    void setId( long val );

    void setNoInstEntries( int val );

    void setInstruments( StratInstrument val );

}
