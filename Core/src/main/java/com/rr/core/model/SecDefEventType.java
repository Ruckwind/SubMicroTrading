package com.rr.core.model;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

/**
 * code to represent type of event
 */

import com.rr.core.codec.RuntimeDecodingException;

@SuppressWarnings( { "unused", "override" } )

public enum SecDefEventType implements SingleByteLookup {

    Put( "1" ),
    Call( "2" ),
    Tender( "3" ),
    SinkingFundCall( "4" ),
    Activation( "5" ),
    FirstNoticeDate( "6" ),
    LastTradeableDate( "7" ),
    Unknown( "?" );

    private static final int               _indexOffset = 49;
    private static final SecDefEventType[] _entries     = new SecDefEventType[ 15 ];

    static {
        for ( int i = 0; i < _entries.length; i++ ) {
            _entries[ i ] = Unknown;
        }

        for ( SecDefEventType en : SecDefEventType.values() ) {
            if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    private final byte _val;

    public static int getMaxOccurs()   { return 1; }

    public static int getMaxValueLen() { return 1; }

    public static SecDefEventType getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char) val + " for SecDefEventType" );
        }
        SecDefEventType eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char) val + " for SecDefEventType" );
        return eval;
    }

    SecDefEventType( String val ) {
        _val = val.getBytes()[ 0 ];
    }

    @Override public int getID() { return ordinal(); }

    @Override public final byte getVal() {
        return _val;
    }

    public String id()           { return name(); }
}
