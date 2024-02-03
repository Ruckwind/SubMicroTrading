/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.utils.Utils;

public final class DoubleFieldValWrapper implements FieldValWrapper {

    private double _val;

    @Override
    public boolean hasValue() {
        return Utils.hasVal( _val );
    }

    @Override
    public void log( ReusableString dest ) {
        dest.append( _val );
    }

    @Override
    public void reset() {
        _val = Constants.UNSET_DOUBLE;
    }

    public double getVal() {
        return _val;
    }

    public void setVal( double val ) {
        _val = val;
    }
}
