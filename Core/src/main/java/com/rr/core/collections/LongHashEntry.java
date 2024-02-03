/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.collections;

import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.Reusable;
import com.rr.core.lang.ReusableType;

public final class LongHashEntry<T> implements Reusable<LongHashEntry<T>> {

    long             _key;
    T                _value;
    LongHashEntry<T> _next;

    @Override
    public LongHashEntry<T> getNext() {
        return _next;
    }

    @Override
    public void setNext( LongHashEntry<T> nxt ) {
        _next = nxt;
    }

    @Override
    public ReusableType getReusableType() {
        return CoreReusableType.LongMapHashEntry;
    }

    @Override
    public void reset() {
        _key   = 0;
        _value = null;
        _next  = null;
    }

    void set( long key, LongHashEntry<T> next, T value ) {
        _key   = key;
        _next  = next;
        _value = value;
    }
}
