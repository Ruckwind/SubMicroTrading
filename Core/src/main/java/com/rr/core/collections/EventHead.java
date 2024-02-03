/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;
import com.rr.core.model.BaseEvent;

public final class EventHead extends BaseEvent {

    @Override public void dump( ReusableString out ) { /* nothing */ }

    @Override
    public ReusableType getReusableType() {
        return CollectionTypes.EventHead;
    }
}

