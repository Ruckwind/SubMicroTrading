package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.SecurityTradingStatus;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;
import com.rr.model.internal.type.SubEvent;

@SuppressWarnings( { "unused", "override"  })

public interface SecMassStatGrp extends SubEvent {

   // Getters and Setters
    ViewString getSecurityId();

    SecurityIDSource getSecurityIDSource();

    SecurityTradingStatus getSecurityTradingStatus();

    boolean getSecurityStatus();

    @Override void dump( ReusableString out );

    void setSecurityId( byte[] buf, int offset, int len );
    ReusableString getSecurityIdForUpdate();

    void setSecurityIDSource( SecurityIDSource val );

    void setSecurityTradingStatus( SecurityTradingStatus val );

    void setSecurityStatus( boolean val );

}
