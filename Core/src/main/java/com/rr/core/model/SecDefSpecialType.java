/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.codec.RuntimeDecodingException;

/**
 * some sec defs are formed from special data and require unique processing in places
 * <p>
 * key one at present is CMEFuture
 */
public enum SecDefSpecialType implements SingleByteLookup {

    Standard( "S" ),
    CMEFuture( "C" ),
    Unknown( "?" );

    private static SecDefSpecialType[] _entries = new SecDefSpecialType[ 256 ];

    static {
        for ( int i = 0; i < _entries.length; i++ ) {
            _entries[ i ] = Unknown;
        }

        for ( SecDefSpecialType en : SecDefSpecialType.values() ) {
            _entries[ en.getVal() ] = en;
        }
    }

    private byte _val;

    public static int getMaxOccurs()   { return 1; }

    public static int getMaxValueLen() { return 1; }

    public static SecDefSpecialType getVal( byte val ) {
        SecDefSpecialType eval;
        eval = _entries[ val ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char) val + " for ProductType" );
        return eval;
    }

    SecDefSpecialType( String val ) {
        _val = val.getBytes()[ 0 ];
    }

    @Override public int getID() { return ordinal(); }

    @Override public final byte getVal() {
        return _val;
    }

    public String id() { return name(); }
}
