/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.mds.common;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ViewString;
import com.rr.core.model.Event;
import com.rr.core.model.ExchangeInstrument;
import com.rr.core.model.InstrumentLocator;
import com.rr.core.model.SecurityIDSource;
import com.rr.mds.common.events.TradingRangeUpdate;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.dummy.warmup.TradingRangeImpl;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestTradingRangeDecode extends BaseTestCase {

    @SuppressWarnings( "boxing" )
    @Ignore
    @Test
    public void testDecode() {

        InstrumentLocator ip = new DummyInstrumentLocator();

        TradingRangeUpdate u1 = new TradingRangeUpdate();
        TradingRangeUpdate u2 = new TradingRangeUpdate();

        double lowerU1      = 12.125009;
        double upperU1      = 14.990001;
        long   lowerIdU1    = 150000000001L;
        long   upperIdU1    = 150000000002L;
        int    lowerFlagsU1 = 123456789;
        int    upperFlagsU1 = 987654321;

        double lowerU2      = 22.125009;
        double upperU2      = 24.990001;
        long   lowerIdU2    = 150000000003L;
        long   upperIdU2    = 150000000004L;
        int    lowerFlagsU2 = 1;
        int    upperFlagsU2 = Integer.MAX_VALUE;

        ViewString ricU1 = new ViewString( "BT.XLON" );
        ViewString ricU2 = new ViewString( "VOD.L" );

        u1.getRicForUpdate().copy( ricU1 );
        u2.getRicForUpdate().copy( ricU2 );

        ExchangeInstrument iU1 = ip.getExchInst( ricU1, SecurityIDSource.ExchangeSymbol, null );
        ExchangeInstrument iU2 = ip.getExchInst( ricU2, SecurityIDSource.ExchangeSymbol, null );

        TradingRangeImpl trU1 = (TradingRangeImpl) iU1.getValidTradingRange();
        TradingRangeImpl trU2 = (TradingRangeImpl) iU2.getValidTradingRange();

        u1.setBands( lowerU1, upperU1, lowerIdU1, upperIdU1, lowerFlagsU1, upperFlagsU1 );
        u2.setBands( lowerU2, upperU2, lowerIdU2, upperIdU2, lowerFlagsU2, upperFlagsU2 );

        u1.setNext( u2 );

        byte[]     buf     = new byte[ Constants.MAX_BUF_LEN ];
        MDSEncoder encoder = new MDSEncoder( buf, 0 );
        MDSDecoder decoder = new MDSDecoder();
        decoder.init( ip );

        encoder.encode( u1 );

        Event m = decoder.decode( buf, 0, buf.length );
        assertNull( m );

        assertEquals( lowerU1, trU1.getLower(), Constants.WEIGHT );
        assertEquals( upperU1, trU1.getUpper(), Constants.WEIGHT );
        assertEquals( lowerIdU1, trU1.getLowerId() );
        assertEquals( upperIdU1, trU1.getUpperId() );
        assertEquals( lowerFlagsU1, trU1.getLowerFlags() );
        assertEquals( upperFlagsU1, trU1.getUpperFlags() );

        assertEquals( lowerU2, trU2.getLower(), Constants.WEIGHT );
        assertEquals( upperU2, trU2.getUpper(), Constants.WEIGHT );
        assertEquals( lowerIdU2, trU2.getLowerId() );
        assertEquals( upperIdU2, trU2.getUpperId() );
        assertEquals( lowerFlagsU2, trU2.getLowerFlags() );
        assertEquals( upperFlagsU2, trU2.getUpperFlags() );
    }
}
