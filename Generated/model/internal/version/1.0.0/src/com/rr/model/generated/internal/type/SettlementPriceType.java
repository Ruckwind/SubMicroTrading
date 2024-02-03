package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Type of settlement price
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum SettlementPriceType implements SingleByteLookup {

    Final( TypeIds.SETTLEMENTPRICETYPE_FINAL, "1" ),
    Theoretical( TypeIds.SETTLEMENTPRICETYPE_THEORETICAL, "2" ),
    Manual( TypeIds.SETTLEMENTPRICETYPE_MANUAL, "3" ),
    Unknown( TypeIds.SETTLEMENTPRICETYPE_UNKNOWN, "0" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    SettlementPriceType( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 48;
    private static final SettlementPriceType[] _entries = new SettlementPriceType[4];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( SettlementPriceType en : SettlementPriceType.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static SettlementPriceType getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for SettlementPriceType" );
        }
        SettlementPriceType eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for SettlementPriceType" );
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
