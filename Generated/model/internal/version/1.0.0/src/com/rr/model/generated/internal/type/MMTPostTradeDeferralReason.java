package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Publication Mode / Post-Trade Deferral Reason
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum MMTPostTradeDeferralReason implements SingleByteLookup {

    WithoutPermittedDeferral( TypeIds.MMTPOSTTRADEDEFERRALREASON_WITHOUTPERMITTEDDEFERRAL, "1" ),
    LargeInScale”( TypeIds.MMTPOSTTRADEDEFERRALREASON_LARGEINSCALE�, "2" ),
    IlliquidInstrument( TypeIds.MMTPOSTTRADEDEFERRALREASON_ILLIQUIDINSTRUMENT, "3" ),
    SizeSpecific( TypeIds.MMTPOSTTRADEDEFERRALREASON_SIZESPECIFIC, "4" ),
    IlliquidInstrumentSizeSpecific( TypeIds.MMTPOSTTRADEDEFERRALREASON_ILLIQUIDINSTRUMENTSIZESPECIFIC, "5" ),
    IlliquidInstrumentLargeInScale( TypeIds.MMTPOSTTRADEDEFERRALREASON_ILLIQUIDINSTRUMENTLARGEINSCALE, "6" ),
    ImmediatePublication( TypeIds.MMTPOSTTRADEDEFERRALREASON_IMMEDIATEPUBLICATION, "-" ),
    Unknown( TypeIds.MMTPOSTTRADEDEFERRALREASON_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    MMTPostTradeDeferralReason( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 45;
    private static final MMTPostTradeDeferralReason[] _entries = new MMTPostTradeDeferralReason[19];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( MMTPostTradeDeferralReason en : MMTPostTradeDeferralReason.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static MMTPostTradeDeferralReason getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTPostTradeDeferralReason" );
        }
        MMTPostTradeDeferralReason eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTPostTradeDeferralReason" );
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
