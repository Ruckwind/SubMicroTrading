package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Market Mechanism
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum MMTMarketMechanism implements SingleByteLookup {

    CentralLimitOrderBook( TypeIds.MMTMARKETMECHANISM_CENTRALLIMITORDERBOOK, "1" ),
    QuoteDrivenMarket( TypeIds.MMTMARKETMECHANISM_QUOTEDRIVENMARKET, "2" ),
    DarkOrderBook( TypeIds.MMTMARKETMECHANISM_DARKORDERBOOK, "3" ),
    OffBook( TypeIds.MMTMARKETMECHANISM_OFFBOOK, "4" ),
    PeriodicAuction( TypeIds.MMTMARKETMECHANISM_PERIODICAUCTION, "5" ),
    RequestForQuotes( TypeIds.MMTMARKETMECHANISM_REQUESTFORQUOTES, "6" ),
    AnyOtherIncludingHybrid( TypeIds.MMTMARKETMECHANISM_ANYOTHERINCLUDINGHYBRID, "7" ),
    Unknown( TypeIds.MMTMARKETMECHANISM_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    MMTMarketMechanism( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 49;
    private static final MMTMarketMechanism[] _entries = new MMTMarketMechanism[15];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( MMTMarketMechanism en : MMTMarketMechanism.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static MMTMarketMechanism getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTMarketMechanism" );
        }
        MMTMarketMechanism eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTMarketMechanism" );
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
