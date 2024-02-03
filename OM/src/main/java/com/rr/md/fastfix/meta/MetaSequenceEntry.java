/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.meta;

public class MetaSequenceEntry extends MetaTemplate {

    private MetaFieldEntry _lengthField = null;
    private boolean        _optional;

    public MetaSequenceEntry( String name, int id, boolean optionalSeq ) {
        super( name, id, null );
        _optional = optionalSeq;
    }

    @Override
    public boolean isOptional() {
        return _optional;

//        return _lengthField.isOptional();
    }

    @Override
    public String toString() {
        return "\n       SEQUENCE : isOptional=" + isOptional() + ", LENGTH_FLD=" + getLengthField().toString() + "  :  {\n" + super.toString() + "\n}\n";
    }

    public MetaFieldEntry getLengthField() {
        return _lengthField;
    }

    public void setLengthField( MetaFieldEntry lengthField ) {
        _lengthField = lengthField;
    }
}

