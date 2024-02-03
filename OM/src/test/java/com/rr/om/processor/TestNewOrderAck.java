/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.ClientProfile;
import com.rr.core.model.ExchangeInstrument;
import com.rr.core.model.Instrument;
import com.rr.model.generated.internal.events.impl.ClientNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.interfaces.NewOrderAck;
import com.rr.model.generated.internal.type.OrderCapacity;
import com.rr.om.dummy.warmup.DummyExchange;
import com.rr.om.order.Order;
import com.rr.om.order.OrderVersion;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestNewOrderAck extends BaseProcessorTestCase {

    @SuppressWarnings( "boxing" )
    @Test
    public void testAckWithNonUniqueExecId() {

        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, 25.12, _upMsgHandler );
        _proc.handle( nos );

        Order order = _proc.getOrder( nos.getClOrdId() );

        clearQueues();

        ZString mktOrderId = new ViewString( "ORD000001" );
        ZString execId     = new ViewString( "EXE50001" );

        NewOrderAck mack = FixTestUtils.getMarketACK( _decoder, "TST0000001", 100, 25.12, mktOrderId, execId );

        Instrument    inst      = nos.getInstrument();
        DummyExchange exch      = (DummyExchange) ((ExchangeInstrument) inst).getExchange();
        boolean       wasUnique = false;
        try {
            wasUnique = exch.setExecIdUnqiue( false );

            _proc.handle( mack );
        } finally {
            exch.setExecIdUnqiue( wasUnique );
        }

        assertSame( _proc.getStateOpen(), order.getState() );
        assertSame( order.getLastAckedVerion(), order.getPendingVersion() );

        OrderVersion ver = order.getLastAckedVerion();

        assertNotNull( ver );

        assertSame( nos, ver.getBaseOrderRequest() );
        assertEquals( nos.getOrderQty(), ver.getLeavesQty(), Constants.TICK_WEIGHT );
        assertEquals( 0.0, ver.getAvgPx(), Constants.WEIGHT );
        assertEquals( 0, ver.getCumQty(), Constants.TICK_WEIGHT );
        assertEquals( OrderCapacity.Principal, ver.getMarketCapacity() );

        ClientProfile client = order.getClientProfile();

        assertNotNull( client );

        double totVal = nos.getPrice() * nos.getOrderQty() * nos.getCurrency().toUSDFactor();

        assertSame( order.getClientProfile(), nos.getClient() );
        assertEquals( nos.getOrderQty(), client.getTotalOrderQty(), Constants.TICK_WEIGHT );
        assertEquals( totVal, client.getTotalOrderValueUSD(), Constants.WEIGHT );

        checkQueueSize( 1, 0, 0 );

        ClientNewOrderAckImpl cack = (ClientNewOrderAckImpl) getMessage( _upQ, ClientNewOrderAckImpl.class );

        ZString generatedExecId = new ViewString( exch.getExchangeCode().getMIC() + new String( inst.getCurrency().getVal() ) + "EXE50001" );

        checkClientAck( nos, cack, mktOrderId, generatedExecId );
    }

    @SuppressWarnings( "boxing" )
    @Test
    public void testAckWithUniqueExecId() {

        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, 25.12, _upMsgHandler );
        _proc.handle( nos );

        Order order = _proc.getOrder( nos.getClOrdId() );

        clearQueues();

        ZString mktOrderId = new ViewString( "ORD000001" );
        ZString execId     = new ViewString( "EXE50001" );

        NewOrderAck mack = FixTestUtils.getMarketACK( _decoder, "TST0000001", 100, 25.12, mktOrderId, execId );
        _proc.handle( mack );

        assertSame( _proc.getStateOpen(), order.getState() );
        assertSame( order.getLastAckedVerion(), order.getPendingVersion() );

        OrderVersion ver = order.getLastAckedVerion();

        assertNotNull( ver );

        assertSame( nos, ver.getBaseOrderRequest() );
        assertEquals( nos.getOrderQty(), ver.getLeavesQty(), Constants.TICK_WEIGHT );
        assertEquals( 0.0, ver.getAvgPx(), Constants.WEIGHT );
        assertEquals( 0, ver.getCumQty(), Constants.TICK_WEIGHT );
        assertEquals( OrderCapacity.Principal, ver.getMarketCapacity() );

        ClientProfile client = order.getClientProfile();

        assertNotNull( client );

        double totVal = nos.getPrice() * nos.getOrderQty() * nos.getCurrency().toUSDFactor();

        assertSame( order.getClientProfile(), nos.getClient() );
        assertEquals( nos.getOrderQty(), client.getTotalOrderQty(), Constants.TICK_WEIGHT );
        assertEquals( totVal, client.getTotalOrderValueUSD(), Constants.WEIGHT );

        checkQueueSize( 1, 0, 0 );

        ClientNewOrderAckImpl cack = (ClientNewOrderAckImpl) getMessage( _upQ, ClientNewOrderAckImpl.class );

        checkClientAck( nos, cack, mktOrderId, execId );
    }
}
