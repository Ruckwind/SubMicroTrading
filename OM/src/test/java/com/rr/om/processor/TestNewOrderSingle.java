/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ViewString;
import com.rr.core.model.*;
import com.rr.core.utils.NumberUtils;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderSingleImpl;
import com.rr.model.generated.internal.type.OrderCapacity;
import com.rr.om.dummy.warmup.DummyExchange;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.core.idgen.DailyIntegerIDGenerator;
import com.rr.om.model.instrument.InstrumentWrite;
import com.rr.om.order.Order;
import com.rr.om.order.OrderVersion;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestNewOrderSingle extends BaseProcessorTestCase {

    @Test
    public void testAckWithNumericExchangeClOrdId() {

        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, 25.12, _upMsgHandler );

        Exchange        ex   = new DummyExchange( ExchangeCode.XPAR, new DailyIntegerIDGenerator( 5, 10 ), false );
        InstrumentWrite inst = new DummyInstrumentLocator().getExchInst( new ViewString( "BT.TST" ), SecurityIDSource.ExchangeSymbol, ExchangeCode.UNKNOWN );
        inst.setCurrency( Currency.EUR );
        inst.setExchange( ex );
        nos.setInstrument( inst );

        _proc.handle( nos );

        Order order = _proc.getOrder( nos.getClOrdId() );

        checkQueueSize( 0, 1, 0 );
        Event                    m    = getMessage( _downQ, MarketNewOrderSingleImpl.class );
        MarketNewOrderSingleImpl mnos = (MarketNewOrderSingleImpl) m;

        assertNotNull( order );

        OrderVersion ver = order.getLastAckedVerion();

        assertEquals( ver.getMarketClOrdId(), mnos.getClOrdId() );
        assertTrue( mnos.getClOrdId().equals( nos.getClOrdId() ) == false );

        ViewString mClOrdId = mnos.getClOrdId();

        int id = NumberUtils.parseInt( mClOrdId );

        assertTrue( id > 500001000 );
        assertTrue( id < 600000000 );

        Order orderByMktClOrdId = _proc.getOrder( mClOrdId );

        assertSame( order, orderByMktClOrdId );
    }

    @SuppressWarnings( "boxing" )
    @Test
    public void testNOS() {

        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, 25.12, _upMsgHandler );
        _proc.handle( nos );

        Order order = _proc.getOrder( nos.getClOrdId() );

        checkQueueSize( 0, 1, 0 );

        assertNotNull( order );

        assertSame( _proc.getStatePendingNew(), order.getState() );
        assertSame( order.getLastAckedVerion(), order.getPendingVersion() );

        OrderVersion ver = order.getLastAckedVerion();

        assertNotNull( ver );

        assertSame( nos, ver.getBaseOrderRequest() );
        assertEquals( nos.getOrderQty(), ver.getLeavesQty(), Constants.TICK_WEIGHT );
        assertEquals( 0.0, ver.getAvgPx(), Constants.WEIGHT );
        assertEquals( 0, ver.getCumQty(), Constants.WEIGHT );
        assertEquals( OrderCapacity.Principal, ver.getMarketCapacity() );

        ClientProfile client = order.getClientProfile();

        assertNotNull( client );

        double totVal = nos.getPrice() * nos.getOrderQty() * nos.getCurrency().toUSDFactor();

        assertSame( order.getClientProfile(), nos.getClient() );
        assertEquals( nos.getOrderQty(), client.getTotalOrderQty(), Constants.TICK_WEIGHT );
        assertEquals( totVal, client.getTotalOrderValueUSD(), Constants.WEIGHT );

        Event                    m    = getMessage( _downQ, MarketNewOrderSingleImpl.class );
        MarketNewOrderSingleImpl mnos = (MarketNewOrderSingleImpl) m;

        assertEquals( mnos.getParentClOrdId(), nos.getClOrdId() );

        checkMarketNOS( nos, mnos );
    }
}
