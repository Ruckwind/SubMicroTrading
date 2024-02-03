/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

// category of reusable types, not an enum so it can be extended

public interface ReusableCategory {

    @Override String toString(); // implementation expected to be enum

    int getBaseId();

    int getSize();
}
