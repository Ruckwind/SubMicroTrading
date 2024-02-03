package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Negotiated Trade or Pre-Trade Transparency Waiver
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum MMTNegotiatedTradeOrPreTradeWaiver implements SingleByteLookup {

    NegotiatedTradeInLiquidFinancialInstruments( TypeIds.MMTNEGOTIATEDTRADEORPRETRADEWAIVER_NEGOTIATEDTRADEINLIQUIDFINANCIALINSTRUMENTS, "1" ),
    NegotiatedTradeInIlliquidFinancialInstruments( TypeIds.MMTNEGOTIATEDTRADEORPRETRADEWAIVER_NEGOTIATEDTRADEINILLIQUIDFINANCIALINSTRUMENTS, "2" ),
    NegotiatedTradeSubjectToConditionsOtherThanCurrentMarketPrice( TypeIds.MMTNEGOTIATEDTRADEORPRETRADEWAIVER_NEGOTIATEDTRADESUBJECTTOCONDITIONSOTHERTHANCURRENTMARKETPRICE, "3" ),
    NegotiatedTradeWhereNoneOfAboveApply( TypeIds.MMTNEGOTIATEDTRADEORPRETRADEWAIVER_NEGOTIATEDTRADEWHERENONEOFABOVEAPPLY, "N" ),
    PreTradeTransparencyWaiverIlliquidInstrumentOnSI( TypeIds.MMTNEGOTIATEDTRADEORPRETRADEWAIVER_PRETRADETRANSPARENCYWAIVERILLIQUIDINSTRUMENTONSI, "4" ),
    PreTradeTransparencyWaiverAboveStandardMarketSizeOnSI( TypeIds.MMTNEGOTIATEDTRADEORPRETRADEWAIVER_PRETRADETRANSPARENCYWAIVERABOVESTANDARDMARKETSIZEONSI, "5" ),
    PreTradeTransparencyWaiversIlliquidAndAboveStandardMarketSizeOSI( TypeIds.MMTNEGOTIATEDTRADEORPRETRADEWAIVER_PRETRADETRANSPARENCYWAIVERSILLIQUIDANDABOVESTANDARDMARKETSIZEOSI, "6" ),
    NotSpecified( TypeIds.MMTNEGOTIATEDTRADEORPRETRADEWAIVER_NOTSPECIFIED, "-" ),
    Unknown( TypeIds.MMTNEGOTIATEDTRADEORPRETRADEWAIVER_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    MMTNegotiatedTradeOrPreTradeWaiver( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 45;
    private static final MMTNegotiatedTradeOrPreTradeWaiver[] _entries = new MMTNegotiatedTradeOrPreTradeWaiver[34];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( MMTNegotiatedTradeOrPreTradeWaiver en : MMTNegotiatedTradeOrPreTradeWaiver.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static MMTNegotiatedTradeOrPreTradeWaiver getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTNegotiatedTradeOrPreTradeWaiver" );
        }
        MMTNegotiatedTradeOrPreTradeWaiver eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTNegotiatedTradeOrPreTradeWaiver" );
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
