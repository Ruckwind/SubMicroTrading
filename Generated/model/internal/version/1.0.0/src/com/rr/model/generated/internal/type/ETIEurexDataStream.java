package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 ETI Eurex Data Stream
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum ETIEurexDataStream implements SingleByteLookup {

    Trade( TypeIds.ETIEUREXDATASTREAM_TRADE, (byte)1 ),
    News( TypeIds.ETIEUREXDATASTREAM_NEWS, (byte)2 ),
    ServiceAvailability( TypeIds.ETIEUREXDATASTREAM_SERVICEAVAILABILITY, (byte)3 ),
    SessionData( TypeIds.ETIEUREXDATASTREAM_SESSIONDATA, (byte)4 ),
    ListenerData( TypeIds.ETIEUREXDATASTREAM_LISTENERDATA, (byte)5 ),
    RiskControl( TypeIds.ETIEUREXDATASTREAM_RISKCONTROL, (byte)6 ),
    Unknown( TypeIds.ETIEUREXDATASTREAM_UNKNOWN, (byte)-1 );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    ETIEurexDataStream( int id, byte val ) {
        _val = val;
        _id = id;
    }
    private static final int _indexOffset = 0;
    private static final ETIEurexDataStream[] _entries = new ETIEurexDataStream[7];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( ETIEurexDataStream en : ETIEurexDataStream.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static ETIEurexDataStream getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + val + " for ETIEurexDataStream" );
        }
        ETIEurexDataStream eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + val + " for ETIEurexDataStream" );
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
