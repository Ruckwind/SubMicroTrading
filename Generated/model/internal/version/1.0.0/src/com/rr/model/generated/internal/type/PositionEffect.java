package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Indicates whether the resulting position after a trade should be an opening position or closing position. Used for omnibus accounting -
            where accounts are held on a gross basis instead of being netted together
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum PositionEffect implements SingleByteLookup {

    Open( TypeIds.POSITIONEFFECT_OPEN, "O" ),
    Close( TypeIds.POSITIONEFFECT_CLOSE, "C" ),
    Rolled( TypeIds.POSITIONEFFECT_ROLLED, "R" ),
    FIFO( TypeIds.POSITIONEFFECT_FIFO, "F" ),
    Unknown( TypeIds.POSITIONEFFECT_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    PositionEffect( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 63;
    private static final PositionEffect[] _entries = new PositionEffect[20];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( PositionEffect en : PositionEffect.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static PositionEffect getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for PositionEffect" );
        }
        PositionEffect eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for PositionEffect" );
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
