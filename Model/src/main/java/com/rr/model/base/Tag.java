/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public interface Tag extends Comparable<Tag> {

    Tag cloneTag();

    void dump( StringBuilder b );

    Integer getTag();
}
