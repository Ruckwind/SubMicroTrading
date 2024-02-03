package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 All keys that can be used in strategy params string
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum StratParamKeys implements SingleByteLookup {

    MaxPercentageByVolume( TypeIds.STRATPARAMKEYS_MAXPERCENTAGEBYVOLUME, "A" ),
    DisplayQty( TypeIds.STRATPARAMKEYS_DISPLAYQTY, "B" ),
    DisplaySizePercentage( TypeIds.STRATPARAMKEYS_DISPLAYSIZEPERCENTAGE, "C" ),
    DisplaySizeRandomPercentage( TypeIds.STRATPARAMKEYS_DISPLAYSIZERANDOMPERCENTAGE, "D" ),
    RandomClipDelay( TypeIds.STRATPARAMKEYS_RANDOMCLIPDELAY, "E" ),
    TriggerPrice( TypeIds.STRATPARAMKEYS_TRIGGERPRICE, "F" ),
    Urgency( TypeIds.STRATPARAMKEYS_URGENCY, "G" ),
    NearSideOnly( TypeIds.STRATPARAMKEYS_NEARSIDEONLY, "H" ),
    PegType( TypeIds.STRATPARAMKEYS_PEGTYPE, "I" ),
    OffsetType( TypeIds.STRATPARAMKEYS_OFFSETTYPE, "J" ),
    PegOffset( TypeIds.STRATPARAMKEYS_PEGOFFSET, "K" ),
    PreTradeMaxPercentByVolume( TypeIds.STRATPARAMKEYS_PRETRADEMAXPERCENTBYVOLUME, "L" ),
    ResidualToClose( TypeIds.STRATPARAMKEYS_RESIDUALTOCLOSE, "M" ),
    RefPriceType( TypeIds.STRATPARAMKEYS_REFPRICETYPE, "N" ),
    RefPriceOffsetType( TypeIds.STRATPARAMKEYS_REFPRICEOFFSETTYPE, "O" ),
    RefPriceOffset( TypeIds.STRATPARAMKEYS_REFPRICEOFFSET, "P" ),
    Unknown( TypeIds.STRATPARAMKEYS_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    StratParamKeys( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 63;
    private static final StratParamKeys[] _entries = new StratParamKeys[18];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( StratParamKeys en : StratParamKeys.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static StratParamKeys getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for StratParamKeys" );
        }
        StratParamKeys eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for StratParamKeys" );
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
