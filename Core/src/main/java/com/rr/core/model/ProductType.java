/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.codec.RuntimeDecodingException;

public enum ProductType implements SingleByteLookup {

    GenericFuture( "G" ),
    Future( "F" ),
    Option( "O" ),
    Equity( "E" ),
    Other( "U" ),
    FX( "X" ),
    Strat( "S" ),
    Index( "I" ),
    Basket( "B" ),
    Unknown( "?" );

    private static ProductType[] _entries = new ProductType[ 256 ];

    static {
        for ( int i = 0; i < _entries.length; i++ ) {
            _entries[ i ] = Unknown;
        }

        for ( ProductType en : ProductType.values() ) {
            _entries[ en.getVal() ] = en;
        }
    }

    private byte _val;

    public static int getMaxOccurs()   { return 1; }

    public static int getMaxValueLen() { return 1; }

    public static ProductType getVal( byte val ) {
        ProductType eval;
        eval = _entries[ val ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char) val + " for ProductType" );
        return eval;
    }

    ProductType( String val ) {
        _val = val.getBytes()[ 0 ];
    }

    @Override public int getID() { return ordinal(); }

    @Override public final byte getVal() {
        return _val;
    }
}
