/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

import com.rr.core.lang.Constants;
import com.rr.core.model.ClientProfile;
import com.rr.core.model.Currency;
import com.rr.model.generated.internal.events.impl.ClientNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.ClientTradeNewImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderSingleImpl;
import com.rr.model.generated.internal.events.interfaces.NewOrderAck;
import com.rr.model.generated.internal.events.interfaces.TradeNew;
import com.rr.om.model.instrument.InstrumentWrite;
import com.rr.om.order.Order;
import com.rr.om.order.OrderVersion;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TestCcyMajorMinorFills extends BaseProcessorTestCase {

    @Test
    public void testMajorToMinor() {

        double                   majorPx = 25.12;
        ClientNewOrderSingleImpl cnos    = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, majorPx, _upMsgHandler );
        cnos.setCurrency( Currency.GBP );
        ((InstrumentWrite) cnos.getInstrument()).setCurrency( Currency.GBp );
        _proc.handle( cnos );
        MarketNewOrderSingleImpl mnos = (MarketNewOrderSingleImpl) getMessage( _downQ, MarketNewOrderSingleImpl.class );

        // MARKET IS IN POUNDS
        assertSame( Currency.GBp, mnos.getCurrency() );
        assertEquals( (majorPx * 100.0), mnos.getPrice(), Constants.TICK_WEIGHT );

        Order order = _proc.getOrder( cnos.getClOrdId() );

        NewOrderAck mack = FixTestUtils.getMarketACK( _decoder, "TST0000001", 100, 25.12, _mktOrderId, _execId );
        _proc.handle( mack );
        checkQueueSize( 1, 0, 0 );
        ClientNewOrderAckImpl cack = (ClientNewOrderAckImpl) getMessage( _upQ, ClientNewOrderAckImpl.class );

        assertSame( Currency.GBP, cack.getCurrency() );
        assertEquals( majorPx, cack.getPrice(), Constants.TICK_WEIGHT );

        ClientProfile client = order.getClientProfile();

        // order now open send partial fill and get client fill
        int    lastQty = 10;
        double lastPx  = 2400.0;

        double totVal = (cnos.getPrice() * cnos.getOrderQty() - (25.12 - 24) * 10) * cnos.getCurrency().toUSDFactor();

        TradeNew mfill = FixTestUtils.getMarketTradeNew( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, lastQty, lastPx, _fillExecId );
        _proc.handle( mfill );
        checkQueueSize( 1, 0, 0 );
        ClientTradeNewImpl cfill = (ClientTradeNewImpl) getMessage( _upQ, ClientTradeNewImpl.class );

        assertEquals( totVal, client.getTotalOrderValueUSD(), Constants.TICK_WEIGHT );
        OrderVersion lastAcc = order.getLastAckedVerion();

        Assert.assertEquals( 2400.0, lastAcc.getAvgPx(), Constants.TICK_WEIGHT );
        Assert.assertEquals( 24.0, cfill.getAvgPx(), Constants.TICK_WEIGHT );
        Assert.assertEquals( 24.0, cfill.getLastPx(), Constants.TICK_WEIGHT );
        Assert.assertSame( cnos.getCurrency(), cfill.getCurrency() );
    }

    @SuppressWarnings( "boxing" )
    @Test
    public void testMinorToMajor() {

        double                   minorPx = 2512.0;
        ClientNewOrderSingleImpl cnos    = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, minorPx, _upMsgHandler );
        cnos.setCurrency( Currency.GBp );
        ((InstrumentWrite) cnos.getInstrument()).setCurrency( Currency.GBP );
        _proc.handle( cnos );
        MarketNewOrderSingleImpl mnos = (MarketNewOrderSingleImpl) getMessage( _downQ, MarketNewOrderSingleImpl.class );

        // MARKET IS IN POUNDS
        assertSame( Currency.GBP, mnos.getCurrency() );
        assertEquals( (minorPx / 100.0), mnos.getPrice(), Constants.TICK_WEIGHT );

        Order order = _proc.getOrder( cnos.getClOrdId() );

        NewOrderAck mack = FixTestUtils.getMarketACK( _decoder, "TST0000001", 100, 25.12, _mktOrderId, _execId );
        _proc.handle( mack );
        checkQueueSize( 1, 0, 0 );
        ClientNewOrderAckImpl cack = (ClientNewOrderAckImpl) getMessage( _upQ, ClientNewOrderAckImpl.class );

        assertSame( Currency.GBp, cack.getCurrency() );
        assertEquals( minorPx, cack.getPrice(), Constants.TICK_WEIGHT );

        ClientProfile client = order.getClientProfile();

        double totVal = cnos.getPrice() * cnos.getOrderQty() * cnos.getCurrency().toUSDFactor();

        assertEquals( totVal, client.getTotalOrderValueUSD(), Constants.WEIGHT );

        // order now open send partial fill and get client fill
        int    lastQty = 10;
        double lastPx  = 24.0;

        TradeNew mfill = FixTestUtils.getMarketTradeNew( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, lastQty, lastPx, _fillExecId );
        _proc.handle( mfill );
        checkQueueSize( 1, 0, 0 );
        ClientTradeNewImpl cfill = (ClientTradeNewImpl) getMessage( _upQ, ClientTradeNewImpl.class );

        OrderVersion lastAcc = order.getLastAckedVerion();

        Assert.assertEquals( 24.0, lastAcc.getAvgPx(), Constants.WEIGHT );
        Assert.assertEquals( 2400.0, cfill.getAvgPx(), Constants.TICK_WEIGHT );
        Assert.assertEquals( 2400.0, cfill.getLastPx(), Constants.TICK_WEIGHT );
        Assert.assertSame( cnos.getCurrency(), cfill.getCurrency() );
    }
}
