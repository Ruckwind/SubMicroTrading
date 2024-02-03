/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public class HandCraftedCodecDefinition {

    private final String _id;

    public HandCraftedCodecDefinition( String id ) {
        _id = id;
    }

    public String getId() {
        return _id;
    }
}
