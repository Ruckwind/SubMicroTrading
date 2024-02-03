/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.meta;

public class MetaGroupEntry extends MetaTemplate {

    private boolean _optional;

    public MetaGroupEntry( String name, int id, boolean optionalSeq ) {
        super( name, id, null );
        _optional = optionalSeq;
    }

    @Override
    public boolean isOptional() {
        return _optional;
    }

    @Override
    public String toString() {
        return "\n       GROUP : isOptional=" + isOptional() + "  :  {\n" + super.toString() + "\n}\n";
    }
}

