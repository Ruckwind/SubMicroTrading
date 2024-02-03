package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 ETI Session Mode
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum ETISessionMode implements SingleByteLookup {

    HF( TypeIds.ETISESSIONMODE_HF, (byte)1 ),
    LF( TypeIds.ETISESSIONMODE_LF, (byte)2 ),
    Unknown( TypeIds.ETISESSIONMODE_UNKNOWN, (byte)-1 );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    ETISessionMode( int id, byte val ) {
        _val = val;
        _id = id;
    }
    private static final int _indexOffset = 0;
    private static final ETISessionMode[] _entries = new ETISessionMode[3];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( ETISessionMode en : ETISessionMode.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static ETISessionMode getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + val + " for ETISessionMode" );
        }
        ETISessionMode eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + val + " for ETISessionMode" );
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
