/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections;

import com.rr.core.lang.Reusable;
import com.rr.core.lang.ReusableType;
import com.rr.core.lang.ViewString;
import com.rr.om.order.Order;

public final class HashEntry implements Reusable<HashEntry> {

    Order      _value;
    ViewString _key;
    HashEntry  _next;
    int        _hash;

    @Override
    public HashEntry getNext() {
        return _next;
    }

    @Override
    public void setNext( HashEntry nxt ) {
        _next = nxt;
    }

    @Override
    public ReusableType getReusableType() {
        return OrderCollectionTypes.OrderMapHashEntry;
    }

    @Override
    public void reset() {
        _key   = null;
        _hash  = 0;
        _value = null;
        _next  = null;
    }

    void set( ViewString key, int hash, HashEntry next, Order value ) {
        _key   = key;
        _hash  = hash;
        _next  = next;
        _value = value;
    }

}
