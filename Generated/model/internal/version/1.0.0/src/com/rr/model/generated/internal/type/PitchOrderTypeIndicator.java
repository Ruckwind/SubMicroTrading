package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Off book quote frp, Systematic Internaliser reported on TRF feed
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum PitchOrderTypeIndicator implements SingleByteLookup {

    SystematicInternaliser( TypeIds.PITCHORDERTYPEINDICATOR_SYSTEMATICINTERNALISER, "S" ),
    Other( TypeIds.PITCHORDERTYPEINDICATOR_OTHER, "Z" ),
    Unknown( TypeIds.PITCHORDERTYPEINDICATOR_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    PitchOrderTypeIndicator( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 63;
    private static final PitchOrderTypeIndicator[] _entries = new PitchOrderTypeIndicator[28];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( PitchOrderTypeIndicator en : PitchOrderTypeIndicator.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static PitchOrderTypeIndicator getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for PitchOrderTypeIndicator" );
        }
        PitchOrderTypeIndicator eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for PitchOrderTypeIndicator" );
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
