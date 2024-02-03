/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base;

/**
 * represents an attribute in an Event which is of type SubEvent
 */
public class SubEventAttrType implements AttrType {

    private final String _id;

    private ClassDefinition _cd;

    public SubEventAttrType( String id ) {
        super();
        _id = id;
    }

    @Override
    public String getSize() {
        return "";
    }

    @Override
    public void setSize( String constantStr ) {
        throw new RuntimeException( "SubEventAttrType not appropriate : id=" + _id + ", sizeStr=" + constantStr );
    }

    @Override
    public String getTypeDeclaration() {
        return _id;
    }

    @Override
    public String getTypeDefinition() {
        return _id;
    }

    @Override
    public boolean isValid() {
        return _cd != null;
    }

    public ClassDefinition getCd() {
        return _cd;
    }

    public void setCd( ClassDefinition cd ) {
        _cd = cd;
    }
}
