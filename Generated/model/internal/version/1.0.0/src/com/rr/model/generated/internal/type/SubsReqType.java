package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Market Data Subscription Type
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum SubsReqType implements SingleByteLookup {

    Subscribe( TypeIds.SUBSREQTYPE_SUBSCRIBE, "1" ),
    Unsubscribe( TypeIds.SUBSREQTYPE_UNSUBSCRIBE, "2" ),
    Unknown( TypeIds.SUBSREQTYPE_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    SubsReqType( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 49;
    private static final SubsReqType[] _entries = new SubsReqType[15];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( SubsReqType en : SubsReqType.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static SubsReqType getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for SubsReqType" );
        }
        SubsReqType eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for SubsReqType" );
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
