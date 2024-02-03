/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base.type;

import com.rr.model.base.PrimitiveType;

public class CharType extends PrimitiveType {

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    protected String getTypeStr() {
        return "byte";
    }

    @Override
    protected void setArrayLength( int arraySize ) {
        throw new RuntimeException( "Char type is not allowed to be type array .. use viewstring  or reusablestring instead " + this );
    }
}
