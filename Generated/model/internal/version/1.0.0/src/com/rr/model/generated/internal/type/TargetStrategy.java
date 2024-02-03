package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Target Execution Strategy
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum TargetStrategy implements SingleByteLookup {

    VWAP( TypeIds.TARGETSTRATEGY_VWAP, "1" ),
    WithVolume( TypeIds.TARGETSTRATEGY_WITHVOLUME, "2" ),
    ImplementionShortFall( TypeIds.TARGETSTRATEGY_IMPLEMENTIONSHORTFALL, "3" ),
    TWAP( TypeIds.TARGETSTRATEGY_TWAP, "4" ),
    PEG( TypeIds.TARGETSTRATEGY_PEG, "5" ),
    Close( TypeIds.TARGETSTRATEGY_CLOSE, "6" ),
    OpenAuction( TypeIds.TARGETSTRATEGY_OPENAUCTION, "7" ),
    Unknown( TypeIds.TARGETSTRATEGY_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    TargetStrategy( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 49;
    private static final TargetStrategy[] _entries = new TargetStrategy[15];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( TargetStrategy en : TargetStrategy.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static TargetStrategy getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for TargetStrategy" );
        }
        TargetStrategy eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for TargetStrategy" );
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
