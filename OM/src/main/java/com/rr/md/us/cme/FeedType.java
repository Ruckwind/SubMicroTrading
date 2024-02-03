/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme;

import com.rr.core.utils.SMTRuntimeException;

public enum FeedType {

    HistoricalReplay( 'H' ),
    Incremental( 'I' ),
    InstrumentReplay( 'N' ),
    Snapshot( 'S' );

    private byte _code;

    public static FeedType lookup( byte code ) {
        switch( code ) {
        case 'H':
            return HistoricalReplay;
        case 'I':
            return Incremental;
        case 'N':
            return InstrumentReplay;
        case 'S':
            return Snapshot;
        // force
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        default:
            throw new SMTRuntimeException( "Invalid FeedType code of " + code );
        }
    }

    FeedType( char code ) {
        _code = (byte) code;
    }

    public byte getCode() {
        return _code;
    }
}


