/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base.type;

import com.rr.model.base.PrimitiveType;
import com.rr.model.generator.ModelConstants;

public class ReusableStringType extends PrimitiveType {

    private String _size = "1";

    @Override
    public String getSize() {
        return _size;
    }

    @Override
    public void setSize( String sizeStr ) {
        _size = sizeStr;
    }

    @Override
    public String getTypeDeclaration() {
        String typeStr = getTypeStr();

        typeStr = typeStr + "( " + ModelConstants.SIZE_TYPE_FILE_NAME + "." + _size + ".getSize() )";

        return typeStr;
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

    @Override
    protected String getTypeStr() {
        return "ReusableString";
    }

}
