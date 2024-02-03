package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Statistics Price Type
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum StatsPriceType implements SingleByteLookup {

    Closing( TypeIds.STATSPRICETYPE_CLOSING, "C" ),
    High( TypeIds.STATSPRICETYPE_HIGH, "H" ),
    Low( TypeIds.STATSPRICETYPE_LOW, "L" ),
    Opening( TypeIds.STATSPRICETYPE_OPENING, "O" ),
    PreviousClosing( TypeIds.STATSPRICETYPE_PREVIOUSCLOSING, "P" ),
    Unknown( TypeIds.STATSPRICETYPE_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    StatsPriceType( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 63;
    private static final StatsPriceType[] _entries = new StatsPriceType[18];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( StatsPriceType en : StatsPriceType.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static StatsPriceType getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for StatsPriceType" );
        }
        StatsPriceType eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for StatsPriceType" );
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
