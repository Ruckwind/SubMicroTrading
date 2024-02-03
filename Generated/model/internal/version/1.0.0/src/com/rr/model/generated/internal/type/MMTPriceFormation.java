package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Contribution to Price Formation or the Price Discovery Process
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum MMTPriceFormation implements SingleByteLookup {

    StandardTrade( TypeIds.MMTPRICEFORMATION_STANDARDTRADE, "P" ),
    NonPriceFormingTrade( TypeIds.MMTPRICEFORMATION_NONPRICEFORMINGTRADE, "T" ),
    NonContributing( TypeIds.MMTPRICEFORMATION_NONCONTRIBUTING, "J" ),
    PriceNotAvailPending( TypeIds.MMTPRICEFORMATION_PRICENOTAVAILPENDING, "N" ),
    Unknown( TypeIds.MMTPRICEFORMATION_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    MMTPriceFormation( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 63;
    private static final MMTPriceFormation[] _entries = new MMTPriceFormation[22];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( MMTPriceFormation en : MMTPriceFormation.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static MMTPriceFormation getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTPriceFormation" );
        }
        MMTPriceFormation eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTPriceFormation" );
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
