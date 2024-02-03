/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base.type;

import com.rr.model.base.PrimitiveType;

public class ViewStringType extends PrimitiveType {

    private String _size = "1";

    @Override
    protected String getTypeStr() {
        return "ViewString";
    }

    @Override
    public String getTypeDeclaration() {
        String typeStr = getTypeStr();

        typeStr = typeStr + "( _buf )";

        return typeStr;
    }

    @Override
    public String getSize() {
        return _size;
    }

    @Override
    public void setSize( String sizeStr ) {
        _size = sizeStr;
    }

    @Override
    public String getTypeDefinition() {
        String typeStr = getTypeStr();

        return "final " + typeStr;
    }

    @Override
    public boolean isValid() {
        return true;
    }

}
