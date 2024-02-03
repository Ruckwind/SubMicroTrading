/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.base.type;

import com.rr.model.base.PrimitiveType;

public class ShortType extends PrimitiveType {

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    protected String getTypeStr() {
        return "short";
    }
}
