/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;

public final class LongFieldValWrapper implements FieldValWrapper {

    private long _val;

    @Override
    public boolean hasValue() {
        return _val != Constants.UNSET_LONG;
    }

    @Override
    public void reset() {
        _val = Constants.UNSET_LONG;
    }

    @Override
    public void log( ReusableString dest ) {
        dest.append( _val );
    }

    public long getVal() {
        return _val;
    }

    public void setVal( long val ) {
        _val = val;
    }
}
