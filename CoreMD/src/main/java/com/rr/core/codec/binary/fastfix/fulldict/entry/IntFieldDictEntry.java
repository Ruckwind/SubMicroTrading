/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.entry;

import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;

public final class IntFieldDictEntry implements DictEntry {

    private final int _init;
    private       int _val;

    public IntFieldDictEntry( String initVal ) {
        this( FieldUtils.parseInt( initVal ) );
    }

    public IntFieldDictEntry( int init ) {
        _init = init;
    }

    @Override
    public boolean hasValue() {
        return _val != Constants.UNSET_INT;
    }

    @Override
    public void reset() {
        _val = _init;
    }

    @Override
    public void log( ReusableString dest ) {
        dest.append( _val );
    }

    public int getInit() {
        return _init;
    }

    public int getVal() {
        return _val;
    }

    public void setVal( int val ) {
        _val = val;
    }
}
