package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;
import com.rr.model.internal.type.SubEvent;

@SuppressWarnings( { "unused", "override"  })

public interface SecurityAltID extends SubEvent {

   // Getters and Setters
    ViewString getSecurityAltID();

    SecurityIDSource getSecurityAltIDSource();

    @Override void dump( ReusableString out );

    void setSecurityAltID( byte[] buf, int offset, int len );
    ReusableString getSecurityAltIDForUpdate();

    void setSecurityAltIDSource( SecurityIDSource val );

}
