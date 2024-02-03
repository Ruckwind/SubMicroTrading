package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Trading Session Sub ID
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum TradingSessionSubID implements SingleByteLookup {

    Unknown( TypeIds.TRADINGSESSIONSUBID_UNKNOWN, "0" ),
    PreTrading( TypeIds.TRADINGSESSIONSUBID_PRETRADING, "1" ),
    OpeningAuction( TypeIds.TRADINGSESSIONSUBID_OPENINGAUCTION, "2" ),
    ContinuousTrading( TypeIds.TRADINGSESSIONSUBID_CONTINUOUSTRADING, "3" ),
    ClosingAuction( TypeIds.TRADINGSESSIONSUBID_CLOSINGAUCTION, "4" ),
    PostTrading( TypeIds.TRADINGSESSIONSUBID_POSTTRADING, "5" ),
    IntradayAuction( TypeIds.TRADINGSESSIONSUBID_INTRADAYAUCTION, "6" ),
    Quiescent( TypeIds.TRADINGSESSIONSUBID_QUIESCENT, "7" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    TradingSessionSubID( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 48;
    private static final TradingSessionSubID[] _entries = new TradingSessionSubID[8];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( TradingSessionSubID en : TradingSessionSubID.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static TradingSessionSubID getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for TradingSessionSubID" );
        }
        TradingSessionSubID eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for TradingSessionSubID" );
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
