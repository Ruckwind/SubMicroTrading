package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Post-Trade Deferral or Enrichment Type
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum MMTPostTradeEnrichmentType implements SingleByteLookup {

    LimitedDetailsTrade( TypeIds.MMTPOSTTRADEENRICHMENTTYPE_LIMITEDDETAILSTRADE, "1" ),
    DailyAggregatedTrade”( TypeIds.MMTPOSTTRADEENRICHMENTTYPE_DAILYAGGREGATEDTRADE�, "2" ),
    VolumeOmissionTrade( TypeIds.MMTPOSTTRADEENRICHMENTTYPE_VOLUMEOMISSIONTRADE, "3" ),
    FourWeeksAggregationTrade( TypeIds.MMTPOSTTRADEENRICHMENTTYPE_FOURWEEKSAGGREGATIONTRADE, "4" ),
    IndefiniteAggregationTrade( TypeIds.MMTPOSTTRADEENRICHMENTTYPE_INDEFINITEAGGREGATIONTRADE, "5" ),
    EligibleForAggregatedForm( TypeIds.MMTPOSTTRADEENRICHMENTTYPE_ELIGIBLEFORAGGREGATEDFORM, "6" ),
    FullDetailsEarlierLimitedDetailsTrade( TypeIds.MMTPOSTTRADEENRICHMENTTYPE_FULLDETAILSEARLIERLIMITEDDETAILSTRADE, "7" ),
    FullDetailsEarlierDailyAggregatedTrade( TypeIds.MMTPOSTTRADEENRICHMENTTYPE_FULLDETAILSEARLIERDAILYAGGREGATEDTRADE, "8" ),
    FullDetailsEarlierVolumeOmissionTrade( TypeIds.MMTPOSTTRADEENRICHMENTTYPE_FULLDETAILSEARLIERVOLUMEOMISSIONTRADE, "9" ),
    FullDetailsFourWeeksAggregationTrade( TypeIds.MMTPOSTTRADEENRICHMENTTYPE_FULLDETAILSFOURWEEKSAGGREGATIONTRADE, "V" ),
    FullDetailsEarlierSubsequentEnrichment( TypeIds.MMTPOSTTRADEENRICHMENTTYPE_FULLDETAILSEARLIERSUBSEQUENTENRICHMENT, "W" ),
    NotApplicable( TypeIds.MMTPOSTTRADEENRICHMENTTYPE_NOTAPPLICABLE, "-" ),
    Unknown( TypeIds.MMTPOSTTRADEENRICHMENTTYPE_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    MMTPostTradeEnrichmentType( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 45;
    private static final MMTPostTradeEnrichmentType[] _entries = new MMTPostTradeEnrichmentType[43];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( MMTPostTradeEnrichmentType en : MMTPostTradeEnrichmentType.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static MMTPostTradeEnrichmentType getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTPostTradeEnrichmentType" );
        }
        MMTPostTradeEnrichmentType eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTPostTradeEnrichmentType" );
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
