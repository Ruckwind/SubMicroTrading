/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

public interface DeepCopy<T> {

    /**
     * deep copy all primitive members ... deep copy all nonprimitive fields
     */

    void deepCopyFrom( T src );
}
