package com.rr.core.model;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

/**
 * Party used in PB and Clearing
 */

import com.rr.core.codec.RuntimeDecodingException;

@SuppressWarnings( { "unused", "override" } )

public enum PartyID implements TwoByteLookup {

    MorganStanley( "MS" ),
    Barclays( "BA" ),
    UBS( "UB" ),
    Unknown( "?" );

    private static PartyID[] _entries = new PartyID[ 256 * 256 ];

    static {
        for ( int i = 0; i < _entries.length; i++ ) { _entries[ i ] = Unknown; }

        for ( PartyID en : PartyID.values() ) {
            if ( en == Unknown ) continue;
            byte[] val = en.getVal();
            int    key = val[ 0 ] << 8;
            if ( val.length == 2 ) key += val[ 1 ];
            _entries[ key ] = en;
        }
    }

    private final byte[] _val;

    public static int getMaxOccurs()   { return 1; }

    public static int getMaxValueLen() { return 2; }

    public static PartyID getVal( byte[] val, int offset, int len ) {
        int key = val[ offset++ ] << 8;
        if ( len == 2 ) key += val[ offset ];
        PartyID eval;
        eval = _entries[ key ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + ((key > 0xFF) ? ("" + (char) (key >> 8) + (char) (key & 0xFF)) : ("" + (char) (key & 0xFF))) + " for PartyID" );
        return eval;
    }

    public static PartyID getVal( byte[] val ) {
        int offset = 0;
        int key    = val[ offset++ ] << 8;
        if ( val.length == 2 ) key += val[ offset ];
        PartyID eval;
        eval = _entries[ key ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + ((key > 0xFF) ? ("" + (char) (key >> 8) + (char) (key & 0xFF)) : ("" + (char) (key & 0xFF))) + " for PartyID" );
        return eval;
    }

    PartyID( String val ) {
        _val = val.getBytes();
    }

    @Override public int getID() { return ordinal(); }

    @Override public final byte[] getVal() {
        return _val;
    }

    public String id()           { return name(); }
}
