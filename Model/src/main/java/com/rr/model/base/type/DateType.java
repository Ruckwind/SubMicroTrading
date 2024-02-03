/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base.type;

import com.rr.model.base.PrimitiveType;

public class DateType extends PrimitiveType {

    @Override
    protected String getTypeStr() {
        return "int";
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
