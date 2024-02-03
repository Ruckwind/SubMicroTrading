/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.entry;

import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;

public final class LongFieldDictEntry implements DictEntry {

    private final long _init;
    private       long _val;

    public LongFieldDictEntry( String initVal ) {
        this( FieldUtils.parseLong( initVal ) );
    }

    public LongFieldDictEntry( long init ) {
        _init = init;
    }

    @Override
    public boolean hasValue() {
        return _val != Constants.UNSET_LONG;
    }

    @Override
    public void log( ReusableString dest ) {
        dest.append( _val );
    }

    @Override
    public void reset() {
        _val = _init;
    }

    public long getInit() {
        return _init;
    }

    public long getVal() {
        return _val;
    }

    public void setVal( long val ) {
        _val = val;
    }
}
