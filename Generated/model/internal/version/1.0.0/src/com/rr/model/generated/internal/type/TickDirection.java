package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Security Trading Status
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum TickDirection implements SingleByteLookup {

    PlusTick( TypeIds.TICKDIRECTION_PLUSTICK, "0" ),
    ZeroPlusTick( TypeIds.TICKDIRECTION_ZEROPLUSTICK, "1" ),
    MinusTick( TypeIds.TICKDIRECTION_MINUSTICK, "2" ),
    ZeroMinusTick( TypeIds.TICKDIRECTION_ZEROMINUSTICK, "3" ),
    Unknown( TypeIds.TICKDIRECTION_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    TickDirection( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 48;
    private static final TickDirection[] _entries = new TickDirection[16];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( TickDirection en : TickDirection.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static TickDirection getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for TickDirection" );
        }
        TickDirection eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for TickDirection" );
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
