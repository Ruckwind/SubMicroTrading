/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.codec.binary.fastfix.msgdict.TemplateFieldReader;

public abstract class FieldReader implements TemplateFieldReader {

    private final boolean _isOptional;
    private final String  _name;
    private final int     _id;

    public FieldReader( String name, int id, boolean isOptional ) {
        _isOptional = isOptional;
        _name       = name;
        _id         = id;
    }

    @Override
    public int getId() {
        return _id;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public boolean isOptional() {
        return _isOptional;
    }

    protected final void throwMissingPreviousException() {
        throw new RuntimeDecodingException( "Missing previous value for field " + getName() );
    }

    protected final void throwMissingValueException() {
        throw new RuntimeDecodingException( "Missing mandatory value for field " + getName() );
    }
}
