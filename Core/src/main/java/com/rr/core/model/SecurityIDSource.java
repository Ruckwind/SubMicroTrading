/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

/**
 * Identifies class or source of the SecurityID (48) value. Required if SecurityID (48) is specified
 * <p>
 * InternalString is a generated String identifier which is unique but not immutable, if symbol changes the id will change
 */

import com.rr.core.codec.RuntimeDecodingException;

public enum SecurityIDSource implements SingleByteLookup {

    DEAD_1( "1" ),
    DEAD_2( "2" ),
    ISIN( "4" ),
    RIC( "5" ), // DEPRECATED
    FIGI( "f" ),
    ExchangeSymbol( "8" ),                              // eg for CHIX is VODl
    BloombergCode( "V" ),          // eg VOD EB Equity, BloomBerg Ticker would be VOD which for equities is used as Symbol (tag 55)
    PrimaryBloombergCode( "v" ),   // eg VOD LN Equity, BloomBerg Ticker would be VOD which for equities is used as Symbol (tag 55)
    BloombergTicker( "A" ),                             // eg VOD
    UniqueInstId( "M", true ),
    ExchangeLongId( "P" ),               // a long id for the security assigned by exchange
    SecurityDesc( "Q" ),
    Symbol( "S" ),                       // tag 55
    PrimaryMarketSymbol( "s" ),          // symbol or ticker of the instrument in its primary market
    InternalString( "Y", true ),

    StrategyId( "Z", true ),

    Unknown( "?" );

    private static final SecurityIDSource[] _entries = new SecurityIDSource[ 256 ];

    static {
        for ( int i = 0; i < _entries.length; i++ ) {
            _entries[ i ] = Unknown;
        }

        for ( SecurityIDSource en : SecurityIDSource.values() ) {
            _entries[ en.getVal() ] = en;
        }

        _entries[ (byte) 'M' ] = ExchangeSymbol; // map MarketPlaceAssignedIdentifier  to ExchangeSymbol
    }

    private final boolean _universallyUnique;
    private final byte    _val;

    public static int getMaxOccurs()   { return 1; }

    public static int getMaxValueLen() { return 1; }

    public static SecurityIDSource getVal( byte val ) {
        SecurityIDSource eval;
        eval = _entries[ val ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char) val + " for SecurityIDSource" );
        return eval;
    }

    SecurityIDSource( String val ) {
        this( val, false );
    }

    SecurityIDSource( String val, boolean isUniversallyUnique ) {
        _universallyUnique = isUniversallyUnique;
        _val               = val.getBytes()[ 0 ];
    }

    @Override public int getID() { return ordinal(); }

    @Override public final byte getVal() {
        return _val;
    }

    public String id()                   { return name(); }

    public boolean isUniversallyUnique() { return _universallyUnique; }
}
