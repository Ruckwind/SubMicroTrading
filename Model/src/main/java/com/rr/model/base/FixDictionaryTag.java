/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

import java.util.Objects;

public class FixDictionaryTag {

    private final int     _id;
    private final String  _name;
    private final FixType _fixType;
    private       String  _src;

    public FixDictionaryTag( int id, String name, FixType fixType ) {
        _id      = id;
        _name    = name;
        _fixType = fixType;
    }

    public FixDictionaryTag( FixDictionaryTag value ) {
        _id      = value._id;
        _name    = value._name;
        _fixType = value._fixType;
        _src     = value._src;
    }

    @Override public int hashCode() {

        return Objects.hash( _id );
    }

    @Override public boolean equals( final Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        final FixDictionaryTag that = (FixDictionaryTag) o;
        return _id == that._id;
    }

    public FixType getFixType() {
        return _fixType;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getSrc() {
        return _src;
    }

    public void setSrc( String src ) {
        _src = src;
    }
}
