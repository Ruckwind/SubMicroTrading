/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.meta;

import com.rr.core.codec.binary.fastfix.common.FieldDataType;
import com.rr.core.codec.binary.fastfix.common.FieldOperator;

public class DecimalMetaFieldEntry extends MetaBaseEntry {

    private final FieldOperator  _operator = FieldOperator.NOOP;
    private       MetaFieldEntry _exp;
    private       MetaFieldEntry _mant;

    public DecimalMetaFieldEntry( String name, int id, boolean optional ) {
        super( name, id, optional, FieldDataType.decimal );
    }

    @Override
    public String toString() {
        return super.toString() + " DECIMAL " + ", EXP=[" + getExp().toString() + "], MANT=[" + getMant().toString() + "]";
    }

    @Override
    public boolean isOptional() {
        return _exp.isOptional() || _mant.isOptional();
    }

    public MetaFieldEntry getExp() {
        return _exp;
    }

    public void setExp( MetaFieldEntry exp ) {
        _exp = exp;
    }

    public MetaFieldEntry getMant() {
        return _mant;
    }

    public void setMant( MetaFieldEntry mant ) {
        _mant = mant;
    }

    public FieldOperator getOperator() {
        return _operator;
    }
}

