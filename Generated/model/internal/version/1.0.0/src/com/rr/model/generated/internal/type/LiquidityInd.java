package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Indicator to identify whether this fill was a result of a liquidity provider providing or liquidity taker taking the liquidity. Applicable
            only for OrdStatus (39) of Partial or Filled
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum LiquidityInd implements SingleByteLookup {

    NeitherAddOrRemove( TypeIds.LIQUIDITYIND_NEITHERADDORREMOVE, "0" ),
    AddedLiquidity( TypeIds.LIQUIDITYIND_ADDEDLIQUIDITY, "1" ),
    RemovedLiquidity( TypeIds.LIQUIDITYIND_REMOVEDLIQUIDITY, "2" ),
    LiquidityRoutedOut( TypeIds.LIQUIDITYIND_LIQUIDITYROUTEDOUT, "3" ),
    Auction( TypeIds.LIQUIDITYIND_AUCTION, "4" ),
    Conditional( TypeIds.LIQUIDITYIND_CONDITIONAL, "8" ),
    BlockIndicationExec( TypeIds.LIQUIDITYIND_BLOCKINDICATIONEXEC, "9" ),
    Unknown( TypeIds.LIQUIDITYIND_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    LiquidityInd( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 48;
    private static final LiquidityInd[] _entries = new LiquidityInd[16];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( LiquidityInd en : LiquidityInd.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static LiquidityInd getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for LiquidityInd" );
        }
        LiquidityInd eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for LiquidityInd" );
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
