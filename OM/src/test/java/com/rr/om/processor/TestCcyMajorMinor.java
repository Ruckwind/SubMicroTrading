/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

import com.rr.core.lang.Constants;
import com.rr.core.model.ClientProfile;
import com.rr.core.model.Currency;
import com.rr.model.generated.internal.events.impl.ClientNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderSingleImpl;
import com.rr.model.generated.internal.events.interfaces.NewOrderAck;
import com.rr.om.model.instrument.InstrumentWrite;
import com.rr.om.order.Order;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TestCcyMajorMinor extends BaseProcessorTestCase {

    @SuppressWarnings( "boxing" )
    @Test
    public void testMajorToMinor() {

        double                   majorPx = 25.12;
        ClientNewOrderSingleImpl nos     = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, majorPx, _upMsgHandler );
        nos.setCurrency( Currency.GBP );
        ((InstrumentWrite) nos.getInstrument()).setCurrency( Currency.GBp );
        _proc.handle( nos );
        MarketNewOrderSingleImpl mnos = (MarketNewOrderSingleImpl) getMessage( _downQ, MarketNewOrderSingleImpl.class );

        // MARKET IS IN POUNDS
        assertSame( Currency.GBp, mnos.getCurrency() );
        assertEquals( (majorPx * 100.0), mnos.getPrice(), Constants.TICK_WEIGHT );

        Order order = _proc.getOrder( nos.getClOrdId() );

        NewOrderAck mack = FixTestUtils.getMarketACK( _decoder, "TST0000001", 100, 25.12, _mktOrderId, _execId );
        _proc.handle( mack );
        checkQueueSize( 1, 0, 0 );
        ClientNewOrderAckImpl cack = (ClientNewOrderAckImpl) getMessage( _upQ, ClientNewOrderAckImpl.class );

        assertSame( Currency.GBP, cack.getCurrency() );
        assertEquals( majorPx, cack.getPrice(), Constants.WEIGHT );

        ClientProfile client = order.getClientProfile();

        double totVal = nos.getPrice() * nos.getOrderQty() * nos.getCurrency().toUSDFactor();

        assertEquals( totVal, client.getTotalOrderValueUSD(), Constants.WEIGHT );
    }

    @SuppressWarnings( "boxing" )
    @Test
    public void testMinorToMajor() {

        double                   minorPx = 2512.0;
        ClientNewOrderSingleImpl nos     = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, minorPx, _upMsgHandler );
        nos.setCurrency( Currency.GBp );
        ((InstrumentWrite) nos.getInstrument()).setCurrency( Currency.GBP );
        _proc.handle( nos );
        MarketNewOrderSingleImpl mnos = (MarketNewOrderSingleImpl) getMessage( _downQ, MarketNewOrderSingleImpl.class );

        // MARKET IS IN POUNDS
        assertSame( Currency.GBP, mnos.getCurrency() );
        assertEquals( (minorPx / 100.0), mnos.getPrice(), Constants.WEIGHT );

        Order order = _proc.getOrder( nos.getClOrdId() );

        NewOrderAck mack = FixTestUtils.getMarketACK( _decoder, "TST0000001", 100, 25.12, _mktOrderId, _execId );
        _proc.handle( mack );
        checkQueueSize( 1, 0, 0 );
        ClientNewOrderAckImpl cack = (ClientNewOrderAckImpl) getMessage( _upQ, ClientNewOrderAckImpl.class );

        assertSame( Currency.GBp, cack.getCurrency() );
        assertEquals( minorPx, cack.getPrice(), Constants.WEIGHT );

        ClientProfile client = order.getClientProfile();

        double totVal = nos.getPrice() * nos.getOrderQty() * nos.getCurrency().toUSDFactor();

        assertEquals( totVal, client.getTotalOrderValueUSD(), Constants.WEIGHT );
    }

}
