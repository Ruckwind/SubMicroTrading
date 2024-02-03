/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public class FixTag implements Tag {

    private final Integer _tag;
    private final boolean _isMandatory;

    public FixTag( int tag, boolean isMandatory ) {
        this( new Integer( tag ), isMandatory );
    }

    public FixTag( Integer tag, boolean isMandatory ) {
        _tag         = tag;
        _isMandatory = isMandatory;
    }

    public FixTag( Integer tag ) {
        this( tag, false );
    }

    public FixTag( int tag ) {
        this( tag, false );
    }

    @Override
    public Tag cloneTag() {
        return new FixTag( _tag, _isMandatory );
    }

    @Override
    public void dump( StringBuilder b ) {
        b.append( _tag ).append( ", isMand=" ).append( _isMandatory );
    }

    @Override
    public Integer getTag() {
        return _tag;
    }

    @Override public int compareTo( final Tag o ) {
        return _tag.compareTo( o.getTag() );
    }

    @Override
    public int hashCode() {
        return _tag.hashCode();
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( !(obj instanceof Tag) )
            return false;
        Tag other = (Tag) obj;
        return _tag.intValue() == other.getTag().intValue();
    }

    @Override
    public String toString() {
        return _tag + ", isMand=" + _isMandatory;
    }

    public boolean isMandatory() {
        return _isMandatory;
    }
}
