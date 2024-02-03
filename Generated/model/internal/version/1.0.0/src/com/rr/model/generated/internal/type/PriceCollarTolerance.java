package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Indicates whether the price on this update is outside the exchange price collar
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum PriceCollarTolerance implements SingleByteLookup {

    Outside( TypeIds.PRICECOLLARTOLERANCE_OUTSIDE, "O" ),
    Inside( TypeIds.PRICECOLLARTOLERANCE_INSIDE, "I" ),
    NotSpecified( TypeIds.PRICECOLLARTOLERANCE_NOTSPECIFIED, "-" ),
    Unknown( TypeIds.PRICECOLLARTOLERANCE_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    PriceCollarTolerance( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 45;
    private static final PriceCollarTolerance[] _entries = new PriceCollarTolerance[35];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( PriceCollarTolerance en : PriceCollarTolerance.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static PriceCollarTolerance getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for PriceCollarTolerance" );
        }
        PriceCollarTolerance eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for PriceCollarTolerance" );
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
