/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.model.generated.internal.events.impl.CancelRequestImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderAckImpl;
import com.rr.model.generated.internal.events.interfaces.CancelRequest;
import com.rr.model.generated.internal.events.interfaces.NewOrderAck;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.om.model.event.EventBuilderImpl;
import com.rr.om.order.Order;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestNewOrderAckUnknownOrder extends BaseProcessorTestCase {

    @Test
    public void testNOS() {

        ZString mktOrderId = new ViewString( "ORD000001" );
        ZString execId     = new ViewString( "EXE50001" );

        NewOrderAck mack = FixTestUtils.getMarketACK( _decoder, "TST0000001", 100, 25.12, mktOrderId, execId );
        _proc.handle( mack );

        Order order = _proc.getOrder( mack.getClOrdId() );

        assertNull( order );

        checkQueueSize( 0, 1, 1 );

        MarketNewOrderAckImpl hubAck = (MarketNewOrderAckImpl) getMessage( _hubQ, MarketNewOrderAckImpl.class );
        CancelRequestImpl     mfc    = (CancelRequestImpl) getMessage( _downQ, CancelRequestImpl.class );

        assertSame( mack, hubAck );
        assertEquals( OrdStatus.UnseenOrder, hubAck.getOrdStatus() );

        checkMarketForceCancel( mack, mfc );
    }

    @Test
    public void testNOSNoCancelDownstream() {

        ZString mktOrderId = new ViewString( "ORD000002" );
        ZString execId     = new ViewString( "EXE50002" );

        NewOrderAck mack = FixTestUtils.getMarketACK( _decoder, "TST0000002", 100, 25.12, mktOrderId, execId );

        _procCfg.setForceCancelUnknownExexId( false );
        _proc.handle( mack );

        Order order = _proc.getOrder( mack.getClOrdId() );

        assertNull( order );

        checkQueueSize( 0, 0, 1 );

        MarketNewOrderAckImpl hubAck = (MarketNewOrderAckImpl) getMessage( _hubQ, MarketNewOrderAckImpl.class );

        assertSame( mack, hubAck );
        assertEquals( OrdStatus.UnseenOrder, hubAck.getOrdStatus() );
    }

    private void checkMarketForceCancel( NewOrderAck mack, CancelRequest fcan ) {

        assertEquals( mack.getClOrdId(), fcan.getOrigClOrdId() );
        assertEquals( mack.getSide(), fcan.getSide() );
        assertEquals( mack.getOrderId(), fcan.getOrderId() );

        ReusableString cl = new ReusableString( EventBuilderImpl.FORCE_CANCEL_PREFIX );
        cl.append( mack.getClOrdId() );

        assertEquals( cl, fcan.getClOrdId() );
    }
}
