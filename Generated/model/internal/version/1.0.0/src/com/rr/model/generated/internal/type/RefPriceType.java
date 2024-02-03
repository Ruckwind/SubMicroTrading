package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Price Reference Type
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum RefPriceType implements SingleByteLookup {

    LastPrice( TypeIds.REFPRICETYPE_LASTPRICE, "1" ),
    OpenPrice( TypeIds.REFPRICETYPE_OPENPRICE, "2" ),
    ArrivalPrice( TypeIds.REFPRICETYPE_ARRIVALPRICE, "3" ),
    VWAPDay( TypeIds.REFPRICETYPE_VWAPDAY, "4" ),
    VWAPLastTenMin( TypeIds.REFPRICETYPE_VWAPLASTTENMIN, "5" ),
    Auto( TypeIds.REFPRICETYPE_AUTO, "6" ),
    LastMidPrice( TypeIds.REFPRICETYPE_LASTMIDPRICE, "7" ),
    LastBestBid( TypeIds.REFPRICETYPE_LASTBESTBID, "8" ),
    LastBestAsk( TypeIds.REFPRICETYPE_LASTBESTASK, "9" ),
    Unknown( TypeIds.REFPRICETYPE_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    RefPriceType( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 49;
    private static final RefPriceType[] _entries = new RefPriceType[15];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( RefPriceType en : RefPriceType.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static RefPriceType getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for RefPriceType" );
        }
        RefPriceType eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for RefPriceType" );
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
