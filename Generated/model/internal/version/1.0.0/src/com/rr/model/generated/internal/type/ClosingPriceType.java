package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Type of closing price
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum ClosingPriceType implements SingleByteLookup {

    OfficialClose( TypeIds.CLOSINGPRICETYPE_OFFICIALCLOSE, "1" ),
    OfficialIndicative( TypeIds.CLOSINGPRICETYPE_OFFICIALINDICATIVE, "2" ),
    OfficialCarryOver( TypeIds.CLOSINGPRICETYPE_OFFICIALCARRYOVER, "3" ),
    LastPrice( TypeIds.CLOSINGPRICETYPE_LASTPRICE, "4" ),
    LastEligiblePrice( TypeIds.CLOSINGPRICETYPE_LASTELIGIBLEPRICE, "5" ),
    Manual( TypeIds.CLOSINGPRICETYPE_MANUAL, "6" ),
    Unknown( TypeIds.CLOSINGPRICETYPE_UNKNOWN, "0" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    ClosingPriceType( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 48;
    private static final ClosingPriceType[] _entries = new ClosingPriceType[7];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( ClosingPriceType en : ClosingPriceType.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static ClosingPriceType getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for ClosingPriceType" );
        }
        ClosingPriceType eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for ClosingPriceType" );
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
