/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public class FixStandardHeader extends BaseFixTagSet {

    public FixStandardHeader() {
        super( "StandardHeader", null );
    }

    public FixStandardHeader( FixStandardHeader header ) {
        super( header.getName(), header );
    }
}
