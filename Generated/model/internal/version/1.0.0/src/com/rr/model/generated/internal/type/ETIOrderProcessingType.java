package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 ETI Order Processing Type
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum ETIOrderProcessingType implements SingleByteLookup {

    Automated( TypeIds.ETIORDERPROCESSINGTYPE_AUTOMATED, "A" ),
    Manual( TypeIds.ETIORDERPROCESSINGTYPE_MANUAL, "M" ),
    Both( TypeIds.ETIORDERPROCESSINGTYPE_BOTH, "B" ),
    None( TypeIds.ETIORDERPROCESSINGTYPE_NONE, "N" ),
    Unknown( TypeIds.ETIORDERPROCESSINGTYPE_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    ETIOrderProcessingType( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 63;
    private static final ETIOrderProcessingType[] _entries = new ETIOrderProcessingType[16];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( ETIOrderProcessingType en : ETIOrderProcessingType.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static ETIOrderProcessingType getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for ETIOrderProcessingType" );
        }
        ETIOrderProcessingType eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for ETIOrderProcessingType" );
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
