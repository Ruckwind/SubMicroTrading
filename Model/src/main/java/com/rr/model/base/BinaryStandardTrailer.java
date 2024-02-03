/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public class BinaryStandardTrailer extends BaseBinaryTagSet {

    public BinaryStandardTrailer() {
        super( "StandardTrailer", null );
    }

    public BinaryStandardTrailer( BinaryStandardTrailer trailer ) {
        super( trailer.getName(), trailer );
    }
}
