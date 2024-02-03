/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

public interface ShallowCopy<T> {

    /**
     * shallow copy all primitive members ... EXCLUDING nonprimitive fields
     */

    void shallowCopyFrom( T src );

    /**
     * shallow merge all primitive members ... EXCLUDING nonprimitive fields
     */

    void shallowMergeFrom( T src );
}
