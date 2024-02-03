/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;

public final class IntFieldValWrapper implements FieldValWrapper {

    private int _val;

    @Override
    public boolean hasValue() {
        return _val != Constants.UNSET_INT;
    }

    @Override
    public void log( ReusableString dest ) {
        dest.append( _val );
    }

    @Override
    public void reset() {
        _val = Constants.UNSET_INT;
    }

    public int getVal() {
        return _val;
    }

    public void setVal( int val ) {
        _val = val;
    }
}
