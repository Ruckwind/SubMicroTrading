/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.meta;

import com.rr.core.codec.binary.fastfix.common.FieldDataType;

public class MetaBaseEntry {

    private final String        _name;
    private final int           _id;
    private final FieldDataType _type;
    private final boolean       _isOptional;

    public MetaBaseEntry( String name, int id, boolean optional, FieldDataType t ) {
        _name       = name;
        _id         = id;
        _type       = t;
        _isOptional = optional;
    }

    @Override
    public String toString() {
        return "name=" + getName() + ", id=" + getId() + ", isOptional=" + isOptional() + ", type=" + getType();

    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public FieldDataType getType() {
        return _type;
    }

    public boolean isOptional() {
        return _isOptional;
    }

    public boolean requiresPresenceBit() {
        return false;
    }
}
