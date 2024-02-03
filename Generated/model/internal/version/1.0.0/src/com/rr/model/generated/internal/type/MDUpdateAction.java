package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Market Data Update Entry Type
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum MDUpdateAction implements SingleByteLookup {

    New( TypeIds.MDUPDATEACTION_NEW, "0" ),
    Change( TypeIds.MDUPDATEACTION_CHANGE, "1" ),
    Delete( TypeIds.MDUPDATEACTION_DELETE, "2" ),
    DeleteThru( TypeIds.MDUPDATEACTION_DELETETHRU, "3" ),
    DeleteFrom( TypeIds.MDUPDATEACTION_DELETEFROM, "4" ),
    Overlay( TypeIds.MDUPDATEACTION_OVERLAY, "5" ),
    Unknown( TypeIds.MDUPDATEACTION_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    MDUpdateAction( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 48;
    private static final MDUpdateAction[] _entries = new MDUpdateAction[16];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( MDUpdateAction en : MDUpdateAction.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static MDUpdateAction getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MDUpdateAction" );
        }
        MDUpdateAction eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MDUpdateAction" );
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
