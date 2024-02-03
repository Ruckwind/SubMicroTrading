package com.rr.model.generated.internal.type;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/


/**
 Trading Mode
*/

import com.rr.core.utils.*;
import com.rr.model.internal.type.*;
import com.rr.core.model.*;
import com.rr.core.codec.RuntimeDecodingException;
import com.rr.model.generated.internal.type.TypeIds;

@SuppressWarnings( { "unused", "override"  })

public enum MMTTradingMode implements SingleByteLookup {

    UndefinedAuction( TypeIds.MMTTRADINGMODE_UNDEFINEDAUCTION, "1" ),
    ContinuousTrading( TypeIds.MMTTRADINGMODE_CONTINUOUSTRADING, "2" ),
    AtMarketCloseTrading( TypeIds.MMTTRADINGMODE_ATMARKETCLOSETRADING, "3" ),
    OutOfMainSession( TypeIds.MMTTRADINGMODE_OUTOFMAINSESSION, "4" ),
    TradeReportingOnExchange( TypeIds.MMTTRADINGMODE_TRADEREPORTINGONEXCHANGE, "5" ),
    TradeReportingOffExchange( TypeIds.MMTTRADINGMODE_TRADEREPORTINGOFFEXCHANGE, "6" ),
    TradeReportingSystematicInternalizer( TypeIds.MMTTRADINGMODE_TRADEREPORTINGSYSTEMATICINTERNALIZER, "7" ),
    ScheduledOpeningAuction( TypeIds.MMTTRADINGMODE_SCHEDULEDOPENINGAUCTION, "O" ),
    ScheduledClosingAuction( TypeIds.MMTTRADINGMODE_SCHEDULEDCLOSINGAUCTION, "K" ),
    ScheduledIntradayAuction( TypeIds.MMTTRADINGMODE_SCHEDULEDINTRADAYAUCTION, "I" ),
    UnscheduledAuction( TypeIds.MMTTRADINGMODE_UNSCHEDULEDAUCTION, "U" ),
    Unknown( TypeIds.MMTTRADINGMODE_UNKNOWN, "?" );

    public static int getMaxOccurs() { return 1; }

    public static int getMaxValueLen() { return 1; }

    private final byte _val;
    private final int _id;

    MMTTradingMode( int id, String val ) {
        _val = val.getBytes()[0];
        _id = id;
    }
    private static final int _indexOffset = 49;
    private static final MMTTradingMode[] _entries = new MMTTradingMode[37];

    static {
        for ( int i=0 ; i < _entries.length ; i++ ) {
             _entries[i] = Unknown; }

        for ( MMTTradingMode en : MMTTradingMode.values() ) {
             if ( en == Unknown ) continue;
            _entries[ en.getVal() - _indexOffset ] = en;
        }
    }

    public static MMTTradingMode getVal( byte val ) {
        final int arrIdx = val - _indexOffset;
        if ( arrIdx < 0 || arrIdx >= _entries.length ) {
            throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTTradingMode" );
        }
        MMTTradingMode eval;
        eval = _entries[ arrIdx ];
        if ( eval == Unknown ) throw new RuntimeDecodingException( "Unsupported value of " + (char)val + " for MMTTradingMode" );
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
