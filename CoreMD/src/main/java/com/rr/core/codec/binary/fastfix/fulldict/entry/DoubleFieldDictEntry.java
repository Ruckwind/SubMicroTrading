/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.entry;

import com.rr.core.codec.binary.fastfix.common.FastFixDecimal;
import com.rr.core.codec.binary.fastfix.common.FieldUtils;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.utils.Utils;

public final class DoubleFieldDictEntry implements DictEntry {

    private final FastFixDecimal _prevDecimal = new FastFixDecimal();

    private final double _init;
    private       double _val;

    public DoubleFieldDictEntry() {
        this( Constants.UNSET_DOUBLE );
    }

    public DoubleFieldDictEntry( String init ) {
        this( FieldUtils.parseDouble( init ) );
    }

    public DoubleFieldDictEntry( double init ) {
        _init = init;
    }

    @Override
    public boolean hasValue() {
        return Utils.hasVal( _val );
    }

    @Override
    public void reset() {
        _val = _init;
        _prevDecimal.set( _init );
    }

    @Override
    public void log( ReusableString dest ) {
        dest.append( _val );
    }

    public double getInit() {
        return _init;
    }

    public FastFixDecimal getPrevDecimal() {
        return _prevDecimal;
    }

    public double getVal() {
        return _val;
    }

    public void setVal( double val ) {
        _val = val;
    }

    public double setVal( final int exponent, final long mantissa ) {
        return _val = _prevDecimal.set( exponent, mantissa );
    }
}
