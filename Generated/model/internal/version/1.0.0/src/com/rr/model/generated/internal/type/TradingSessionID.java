package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Trading Session ID
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum TradingSessionID implements SingleByteLookup {

    Unknown( TypeIds.TRADINGSESSIONID_UNKNOWN, "0" ),
    Day( TypeIds.TRADINGSESSIONID_DAY, "1" ),
    HalfDay( TypeIds.TRADINGSESSIONID_HALFDAY, "2" ),
    Morning( TypeIds.TRADINGSESSIONID_MORNING, "3" ),
    Afternoon( TypeIds.TRADINGSESSIONID_AFTERNOON, "4" ),
    Evening( TypeIds.TRADINGSESSIONID_EVENING, "5" ),
    AfterHours( TypeIds.TRADINGSESSIONID_AFTERHOURS, "6" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    TradingSessionID( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 48;
    private static final TradingSessionID[] _entries = new TradingSessionID[7];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( TradingSessionID en : TradingSessionID.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static TradingSessionID getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for TradingSessionID" );
        }
        TradingSessionID eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for TradingSessionID" );
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
