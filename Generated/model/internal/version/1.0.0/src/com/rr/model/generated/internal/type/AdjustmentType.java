package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Type of adjustment to make
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum AdjustmentType implements SingleByteLookup {

    AmountPerShare( TypeIds.ADJUSTMENTTYPE_AMOUNTPERSHARE, "1" ),
    Factor( TypeIds.ADJUSTMENTTYPE_FACTOR, "2" ),
    Percentage( TypeIds.ADJUSTMENTTYPE_PERCENTAGE, "3" ),
    NotApplicable( TypeIds.ADJUSTMENTTYPE_NOTAPPLICABLE, "0" ),
    Unknown( TypeIds.ADJUSTMENTTYPE_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    AdjustmentType( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 48;
    private static final AdjustmentType[] _entries = new AdjustmentType[16];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( AdjustmentType en : AdjustmentType.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static AdjustmentType getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for AdjustmentType" );
        }
        AdjustmentType eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for AdjustmentType" );
        return eval;
    }

    @Override
    public final byte getVal() {
        return _val;
    }

    public final int getID() {
        return _id;
    }

}
