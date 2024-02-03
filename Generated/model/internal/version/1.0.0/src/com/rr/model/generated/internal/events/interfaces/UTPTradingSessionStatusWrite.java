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

public interface UTPTradingSessionStatusWrite extends BaseUTPWrite, UTPTradingSessionStatus {

   // Getters and Setters
    void setMktPhaseChgTime( long val );

    void setInstClassId( byte[] buf, int offset, int len );
    ReusableString getInstClassIdForUpdate();

    void setInstClassStatus( byte[] buf, int offset, int len );
    ReusableString getInstClassStatusForUpdate();

    void setOrderEntryAllowed( boolean val );

    void setTradingSessionId( byte[] buf, int offset, int len );
    ReusableString getTradingSessionIdForUpdate();

}
