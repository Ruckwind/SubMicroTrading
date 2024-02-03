/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public class FixStandardTrailer extends BaseFixTagSet {

    public FixStandardTrailer() {
        super( "StandardTrailer", null );
    }

    public FixStandardTrailer( FixStandardTrailer trailer ) {
        super( trailer.getName(), trailer );
    }
}
