package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Modification Indicator
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum MMTModificationIndicator implements SingleByteLookup {

    TradeModification( TypeIds.MMTMODIFICATIONINDICATOR_TRADEMODIFICATION, "A" ),
    TradeCancel( TypeIds.MMTMODIFICATIONINDICATOR_TRADECANCEL, "C" ),
    NotSpecified( TypeIds.MMTMODIFICATIONINDICATOR_NOTSPECIFIED, "-" ),
    Unknown( TypeIds.MMTMODIFICATIONINDICATOR_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    MMTModificationIndicator( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 45;
    private static final MMTModificationIndicator[] _entries = new MMTModificationIndicator[23];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( MMTModificationIndicator en : MMTModificationIndicator.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static MMTModificationIndicator getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTModificationIndicator" );
        }
        MMTModificationIndicator eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTModificationIndicator" );
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
