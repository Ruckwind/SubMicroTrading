package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Trading Status
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum TradingStatus implements TwoByteLookup {

    OK( TypeIds.TRADINGSTATUS_OK, "0" ),
    BlockedAsExchangeClosed( TypeIds.TRADINGSTATUS_BLOCKEDASEXCHANGECLOSED, "1" ),
    BlockedAsTradingDisabled( TypeIds.TRADINGSTATUS_BLOCKEDASTRADINGDISABLED, "2" ),
    BlockedAsStrategyNotActive( TypeIds.TRADINGSTATUS_BLOCKEDASSTRATEGYNOTACTIVE, "3" ),
    BlockedByTradeThrottler( TypeIds.TRADINGSTATUS_BLOCKEDBYTRADETHROTTLER, "4" ),
    BlockedInvalidMktData( TypeIds.TRADINGSTATUS_BLOCKEDINVALIDMKTDATA, "5" ),
    BlockedInvalidOrdType( TypeIds.TRADINGSTATUS_BLOCKEDINVALIDORDTYPE, "6" ),
    BlockedInvalidSecType( TypeIds.TRADINGSTATUS_BLOCKEDINVALIDSECTYPE, "7" ),
    BlockedMissingInstrument( TypeIds.TRADINGSTATUS_BLOCKEDMISSINGINSTRUMENT, "8" ),
    BlockedInvalidQty( TypeIds.TRADINGSTATUS_BLOCKEDINVALIDQTY, "9" ),
    BlockedAmendsNotSupported( TypeIds.TRADINGSTATUS_BLOCKEDAMENDSNOTSUPPORTED, "10" ),
    BlockedCancelsNotSupported( TypeIds.TRADINGSTATUS_BLOCKEDCANCELSNOTSUPPORTED, "11" ),
    BlockedMissingKeyFields( TypeIds.TRADINGSTATUS_BLOCKEDMISSINGKEYFIELDS, "12" ),
    BlockedAsContractRollInProgress( TypeIds.TRADINGSTATUS_BLOCKEDASCONTRACTROLLINPROGRESS, "13" ),
    BlockedDueToCorpAction( TypeIds.TRADINGSTATUS_BLOCKEDDUETOCORPACTION, "14" ),
    BlockedInstrumentNotTradeable( TypeIds.TRADINGSTATUS_BLOCKEDINSTRUMENTNOTTRADEABLE, "15" ),
    BlockedInvalidTimeInForce( TypeIds.TRADINGSTATUS_BLOCKEDINVALIDTIMEINFORCE, "16" ),
    BlockedMissingLimits( TypeIds.TRADINGSTATUS_BLOCKEDMISSINGLIMITS, "17" ),
    BlockedCancelRequested( TypeIds.TRADINGSTATUS_BLOCKEDCANCELREQUESTED, "18" ),
    BlockedOrderNotActive( TypeIds.TRADINGSTATUS_BLOCKEDORDERNOTACTIVE, "19" ),
    BlockedZeroQty( TypeIds.TRADINGSTATUS_BLOCKEDZEROQTY, "20" ),
    FailedLimitMaxGrossFactorExposure( TypeIds.TRADINGSTATUS_FAILEDLIMITMAXGROSSFACTOREXPOSURE, "21" ),
    FailedLimitMaxNetFactorExposure( TypeIds.TRADINGSTATUS_FAILEDLIMITMAXNETFACTOREXPOSURE, "22" ),
    FailedLimitMinStratOrderQty( TypeIds.TRADINGSTATUS_FAILEDLIMITMINSTRATORDERQTY, "24" ),
    FailedLimitMaxStratOrderQty( TypeIds.TRADINGSTATUS_FAILEDLIMITMAXSTRATORDERQTY, "25" ),
    FailedLimitMaxStratOrderVal( TypeIds.TRADINGSTATUS_FAILEDLIMITMAXSTRATORDERVAL, "26" ),
    FailedLimitMinSingleOrderQty( TypeIds.TRADINGSTATUS_FAILEDLIMITMINSINGLEORDERQTY, "32" ),
    FailedLimitMaxSingleOrderQty( TypeIds.TRADINGSTATUS_FAILEDLIMITMAXSINGLEORDERQTY, "33" ),
    FailedLimitMaxSingleOrderVal( TypeIds.TRADINGSTATUS_FAILEDLIMITMAXSINGLEORDERVAL, "29" ),
    FailedLimitMaxNetPositionVal( TypeIds.TRADINGSTATUS_FAILEDLIMITMAXNETPOSITIONVAL, "36" ),
    FailedLimitMaxGrossPositionVal( TypeIds.TRADINGSTATUS_FAILEDLIMITMAXGROSSPOSITIONVAL, "37" ),
    FailedLimitMaxNetPositionQty( TypeIds.TRADINGSTATUS_FAILEDLIMITMAXNETPOSITIONQTY, "34" ),
    FailedLimitMaxGrossPositionQty( TypeIds.TRADINGSTATUS_FAILEDLIMITMAXGROSSPOSITIONQTY, "35" ),
    FailedLimitMaxPortfolioEqNetPositionVal( TypeIds.TRADINGSTATUS_FAILEDLIMITMAXPORTFOLIOEQNETPOSITIONVAL, "38" ),
    FailedLimitMaxPortfolioEqDailyTurnoverVal( TypeIds.TRADINGSTATUS_FAILEDLIMITMAXPORTFOLIOEQDAILYTURNOVERVAL, "39" ),
    BlockedInvalidOrdStratType( TypeIds.TRADINGSTATUS_BLOCKEDINVALIDORDSTRATTYPE, "40" ),
    BlockedInvalidOrdStratParams( TypeIds.TRADINGSTATUS_BLOCKEDINVALIDORDSTRATPARAMS, "41" ),
    BlockedMktDataOld( TypeIds.TRADINGSTATUS_BLOCKEDMKTDATAOLD, "42" ),
    BlockedUnknownOrder( TypeIds.TRADINGSTATUS_BLOCKEDUNKNOWNORDER, "43" ),
    BlockedRestrictedSecurity( TypeIds.TRADINGSTATUS_BLOCKEDRESTRICTEDSECURITY, "44" ),
    BlockedRestrictedSecurityNoPos( TypeIds.TRADINGSTATUS_BLOCKEDRESTRICTEDSECURITYNOPOS, "45" ),
    BlockedRestrictedSecurityNoLongPos( TypeIds.TRADINGSTATUS_BLOCKEDRESTRICTEDSECURITYNOLONGPOS, "46" ),
    BlockedRestrictedSecurityNoShortPos( TypeIds.TRADINGSTATUS_BLOCKEDRESTRICTEDSECURITYNOSHORTPOS, "47" ),
    BlockedRestrictedSecurityNoIncLongPos( TypeIds.TRADINGSTATUS_BLOCKEDRESTRICTEDSECURITYNOINCLONGPOS, "48" ),
    BlockedRestrictedSecurityNoIncShortPos( TypeIds.TRADINGSTATUS_BLOCKEDRESTRICTEDSECURITYNOINCSHORTPOS, "49" ),
    BlockedBreechMaxShort( TypeIds.TRADINGSTATUS_BLOCKEDBREECHMAXSHORT, "50" ),
    BlockedCantGenLimitPx( TypeIds.TRADINGSTATUS_BLOCKEDCANTGENLIMITPX, "51" ),
    BlockedBrokerShortRestrict( TypeIds.TRADINGSTATUS_BLOCKEDBROKERSHORTRESTRICT, "52" ),
    BlockedOther( TypeIds.TRADINGSTATUS_BLOCKEDOTHER, "53" ),
    Other( TypeIds.TRADINGSTATUS_OTHER, "54" ),
    Unknown( TypeIds.TRADINGSTATUS_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 2; }

    private final byte[] _val;
    private final int _id;

    TradingStatus( int id, String val ) {
        _val = val.getBytes();
        _id = id;
    }
    private static TradingStatus[] _entries = new TradingStatus[256*256];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) 
            { _entries[i] = Unknown; }

        for ( TradingStatus en : TradingStatus.values() ) {
             if ( en == Unknown ) continue;
            byte[] val = en.getVal();
            int key = val[0] << 8;
            if ( val.length == 2 ) key += val[1];
            _entries[ key ] = en;
        }
    }

    public static TradingStatus getVal( byte[] val, int offset, int len ) {
        int key = val[offset++] << 8;
        if ( len == 2 ) key += val[offset];
        TradingStatus eval;
        eval = _entries[ key ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + ((key>0xFF) ? ("" + (char)(key>>8) + (char)(key&0xFF)) : ("" + (char)(key&0xFF))) + " for TradingStatus" );
        return eval;
    }

    public static TradingStatus getVal( byte[] val ) {
        int offset = 0;
        int key = val[offset++] << 8;
        if ( val.length == 2 ) key += val[offset];
        TradingStatus eval;
        eval = _entries[ key ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + ((key>0xFF) ? ("" + (char)(key>>8) + (char)(key&0xFF)) : ("" + (char)(key&0xFF))) + " for TradingStatus" );
        return eval;
    }

    @Override
    public final byte[] getVal() {
        return _val;
    }

    public final int getID() {
        return _id;
    }

}
