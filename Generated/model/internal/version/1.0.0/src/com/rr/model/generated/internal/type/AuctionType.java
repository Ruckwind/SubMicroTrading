package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Auction Type
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum AuctionType implements SingleByteLookup {

    Opening( TypeIds.AUCTIONTYPE_OPENING, "O" ),
    Closing( TypeIds.AUCTIONTYPE_CLOSING, "C" ),
    Halt( TypeIds.AUCTIONTYPE_HALT, "H" ),
    Volatility( TypeIds.AUCTIONTYPE_VOLATILITY, "V" ),
    Periodic( TypeIds.AUCTIONTYPE_PERIODIC, "P" ),
    Unknown( TypeIds.AUCTIONTYPE_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    AuctionType( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 63;
    private static final AuctionType[] _entries = new AuctionType[24];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( AuctionType en : AuctionType.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static AuctionType getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for AuctionType" );
        }
        AuctionType eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for AuctionType" );
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
