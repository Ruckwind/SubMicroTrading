/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public class BinaryStandardHeader extends BaseBinaryTagSet {

    public BinaryStandardHeader() {
        super( "StandardHeader", null );
    }

    public BinaryStandardHeader( BinaryStandardHeader header ) {
        super( header.getName(), header );
    }
}
