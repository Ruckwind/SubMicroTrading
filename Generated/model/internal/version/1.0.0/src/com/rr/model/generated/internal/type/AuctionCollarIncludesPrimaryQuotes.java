package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Indicates whether the BBO used to collar this update includes the Primary Market quotes
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum AuctionCollarIncludesPrimaryQuotes implements SingleByteLookup {

    Includes( TypeIds.AUCTIONCOLLARINCLUDESPRIMARYQUOTES_INCLUDES, "P" ),
    Excludes( TypeIds.AUCTIONCOLLARINCLUDESPRIMARYQUOTES_EXCLUDES, "N" ),
    NotSpecified( TypeIds.AUCTIONCOLLARINCLUDESPRIMARYQUOTES_NOTSPECIFIED, "-" ),
    Unknown( TypeIds.AUCTIONCOLLARINCLUDESPRIMARYQUOTES_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    AuctionCollarIncludesPrimaryQuotes( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 45;
    private static final AuctionCollarIncludesPrimaryQuotes[] _entries = new AuctionCollarIncludesPrimaryQuotes[36];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( AuctionCollarIncludesPrimaryQuotes en : AuctionCollarIncludesPrimaryQuotes.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static AuctionCollarIncludesPrimaryQuotes getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for AuctionCollarIncludesPrimaryQuotes" );
        }
        AuctionCollarIncludesPrimaryQuotes eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for AuctionCollarIncludesPrimaryQuotes" );
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
