/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common;

import com.rr.core.lang.ReusableString;

public final class StringFieldValWrapper implements FieldValWrapper {

    // DONT MAKE NON-FINAL 
    private final ReusableString _val = new ReusableString();

    @Override
    public boolean hasValue() {
        return _val != null && _val.length() > 0;
    }

    @Override
    public void log( ReusableString dest ) {
        dest.append( _val );
    }

    @Override
    public void reset() {
        _val.reset();
    }

    public ReusableString getVal() {
        return _val;
    }

    public void setVal( ReusableString val ) {
        _val.copy( val );
    }
}
