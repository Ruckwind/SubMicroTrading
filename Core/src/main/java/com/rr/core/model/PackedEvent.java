/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;

import java.util.ArrayList;
import java.util.List;

public class PackedEvent extends BaseEvent {

    private List<Event> _list = new ArrayList<>();

    @Override public void dump( ReusableString out ) { /* nothing */ }

    @Override public ReusableType getReusableType() {
        return CoreReusableType.PackedEvent;
    }

    @Override public void reset() {
        super.reset();

        _list.clear();
    }

    public void addMessage( Event msg ) {
        _list.add( msg );
    }

    public List<Event> getMessageList() {
        return _list;
    }
}
