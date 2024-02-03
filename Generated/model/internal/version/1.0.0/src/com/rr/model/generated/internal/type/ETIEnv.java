package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 ETI Environment
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum ETIEnv implements SingleByteLookup {

    Development( TypeIds.ETIENV_DEVELOPMENT, (byte)1 ),
    Simulation( TypeIds.ETIENV_SIMULATION, (byte)2 ),
    Production( TypeIds.ETIENV_PRODUCTION, (byte)3 ),
    Acceptance( TypeIds.ETIENV_ACCEPTANCE, (byte)4 ),
    Unknown( TypeIds.ETIENV_UNKNOWN, (byte)-1 );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    ETIEnv( int id, byte val ) {
        _val = val;
        _id = id;
    }
    private static final int _indexOffset = 0;
    private static final ETIEnv[] _entries = new ETIEnv[5];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( ETIEnv en : ETIEnv.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static ETIEnv getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + val + " for ETIEnv" );
        }
        ETIEnv eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + val + " for ETIEnv" );
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
