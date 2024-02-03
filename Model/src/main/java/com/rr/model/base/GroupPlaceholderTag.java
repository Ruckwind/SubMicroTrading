/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

public final class GroupPlaceholderTag extends FixTag {

    private final FixTagSet _repeatingGroup;
    private final String    _id;
    private final String    _modelAttr;
    private final int       _counterTag;
    private final boolean   _isMandatory;

    public GroupPlaceholderTag( FixTagSet repeatingGroup, String id, int counterTag, boolean isMandatory, String modelAttr ) {
        super( counterTag, isMandatory );

        _repeatingGroup = repeatingGroup;
        _id             = id;
        _isMandatory    = isMandatory;
        _counterTag     = counterTag;
        _modelAttr      = modelAttr;
    }

    @Override
    public Tag cloneTag() {
        return new GroupPlaceholderTag( _repeatingGroup, _id, _counterTag, _isMandatory, _modelAttr );
    }

    @Override
    public void dump( StringBuilder b ) {
        b.append( " repeatingGrp " ).append( _id ).append( ", counterTag=" ).append( _counterTag ).append( ", modelAttr=" ).append( _modelAttr ).append( "[ " );
        _repeatingGroup.dump( b );
        b.append( " ] " );
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        dump( s );
        return s.toString();
    }

    public int getCounterTag() {
        return _counterTag;
    }

    public String getId() {
        return _id;
    }

    public String getModelAttr() {
        return _modelAttr;
    }

    public FixTagSet getRepeatingGroup() {
        return _repeatingGroup;
    }
}
