/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;

public class NullEvent extends BaseEvent {

    public NullEvent() {
        this( null );
    }

    public NullEvent( EventHandler src ) {
        super();
        setEventHandler( src );
    }

    @Override public void dump( ReusableString out ) { /* nothing */ }

    @Override public ReusableType getReusableType() {
        return CoreReusableType.NullEvent;
    }
}
