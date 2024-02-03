/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.ClientProfile;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.NewOrderAck;
import com.rr.model.generated.internal.type.CxlRejReason;
import com.rr.model.generated.internal.type.CxlRejResponseTo;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.om.order.Order;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestVagueCancelReject extends BaseProcessorTestCase {

    @SuppressWarnings( "boxing" )
    @Test
    public void testCancelRejectOrdStateExpired() {

        ReusableString       msgBuf     = new ReusableString();
        Standard44DecoderOMS decoder    = FixTestUtils.getOMSDecoder44();
        ReusableString       clOrdId    = new ReusableString();
        ReusableString       mktClOrdId = new ReusableString();
        ReusableString       orderId    = new ReusableString();

        // SEND NOS
        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, 25.12, _upMsgHandler );
        _proc.handle( nos );

        Order order = _proc.getOrder( nos.getClOrdId() );

        clearQueues();

        ZString mktOrderId = new ViewString( "ORD000001" );
        orderId.setValue( mktOrderId );

        // SYNTH MARKET ACK
        NewOrderAck mack = FixTestUtils.getMarketACK( _decoder, "TST0000001", 100, 25.12, mktOrderId, new ViewString( "EXE50001" ) );

        mktClOrdId.copy( mack.getClOrdId() );

        _proc.handle( mack );
        checkQueueSize( 1, 0, 0 );

        // MACK now recycled

        // GET THE CLIENT ACK -------------------------------------------------------------------------------
        @SuppressWarnings( "unused" )
        ClientNewOrderAckImpl cack = (ClientNewOrderAckImpl) getMessage( _upQ, ClientNewOrderAckImpl.class );

        assertEquals( nos.getClOrdId(), order.getClientClOrdIdChain() );
        assertSame( _proc.getStateOpen(), order.getState() );

        clearQueues();

        // SEND CANCEL --------------------------------------------------------------------------------------
        clOrdId.setValue( "TST000001B" );

        ClientCancelRequestImpl ccan = FixTestUtils.getClientCancelRequest( msgBuf, decoder, clOrdId, nos.getClOrdId(), _upMsgHandler );

        _proc.handle( ccan );
        checkQueueSize( 0, 1, 0 );

        // GET THE MARKEY CANCEL REQ -------------------------------------------------------------------------
        MarketCancelRequestImpl mcan = (MarketCancelRequestImpl) getMessage( _downQ, MarketCancelRequestImpl.class );

        assertSame( _proc.getStatePendingCancel(), order.getState() );

        clearQueues();

        // SYNTH MARKET CANCEL REJECTED -----------------------------------------------------------------------------
        MarketVagueOrderRejectImpl mcxl = FixTestUtils.getMarketVagueReject( mcan.getClOrdId(), CAN_REJECT, true );
        _proc.handle( mcxl );

        // MCXL now recycled, also CCAN recycled

        checkQueueSize( 1, 0, 0 );

        // GET THE CLIENT CANCEL REJECT -------------------------------------------------------------------------
        ClientCancelRejectImpl ccxl = (ClientCancelRejectImpl) getMessage( _upQ, ClientCancelRejectImpl.class );

        checkClientCancelReject( nos, ccxl, clOrdId, nos.getClOrdId(), mktOrderId, CAN_REJECT,
                                 CxlRejReason.Other, CxlRejResponseTo.CancelRequest, OrdStatus.Stopped );

        //  check version and order state
        assertSame( _proc.getStateCompleted(), order.getState() );
        assertSame( order.getLastAckedVerion(), order.getPendingVersion() );
        assertEquals( clOrdId, order.getClientClOrdIdChain() );
        assertEquals( mcan.getClOrdId(), order.getClientClOrdIdChain() ); // as exchange is not using its own ids
        assertEquals( OrdStatus.Stopped, order.getLastAckedVerion().getOrdStatus() );

        ClientProfile client = order.getClientProfile();
        assertNotNull( client );

        // check client limits are zero ascumqty was zero
        assertSame( order.getClientProfile(), nos.getClient() );
        assertEquals( 0, client.getTotalOrderQty(), Constants.TICK_WEIGHT );
        assertEquals( 0.0, client.getTotalOrderValueUSD(), Constants.WEIGHT );
    }

    @SuppressWarnings( "boxing" )
    @Test
    public void testCancelRejectOrdStateNew() {

        ReusableString clOrdId    = new ReusableString();
        ReusableString mktClOrdId = new ReusableString();
        ReusableString orderId    = new ReusableString();

        // SEND NOS
        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, 25.12, _upMsgHandler );
        _proc.handle( nos );

        Order order = _proc.getOrder( nos.getClOrdId() );

        clearQueues();

        ZString mktOrderId = new ViewString( "ORD000001" );
        orderId.setValue( mktOrderId );

        // SYNTH MARKET ACK
        NewOrderAck mack = FixTestUtils.getMarketACK( _decoder, "TST0000001", 100, 25.12, mktOrderId, new ViewString( "EXE50001" ) );

        mktClOrdId.copy( mack.getClOrdId() );

        _proc.handle( mack );
        checkQueueSize( 1, 0, 0 );

        // MACK now recycled

        // GET THE CLIENT ACK -------------------------------------------------------------------------------
        @SuppressWarnings( "unused" )
        ClientNewOrderAckImpl cack = (ClientNewOrderAckImpl) getMessage( _upQ, ClientNewOrderAckImpl.class );

        assertEquals( nos.getClOrdId(), order.getClientClOrdIdChain() );
        assertSame( _proc.getStateOpen(), order.getState() );

        clearQueues();

        // SEND CANCEL --------------------------------------------------------------------------------------
        clOrdId.setValue( "TST000001B" );

        ClientCancelRequestImpl ccan = FixTestUtils.getClientCancelRequest( _msgBuf, _decoder, clOrdId, nos.getClOrdId(), _upMsgHandler );

        _proc.handle( ccan );
        checkQueueSize( 0, 1, 0 );

        // GET THE MARKEY CANCEL REQ -------------------------------------------------------------------------
        MarketCancelRequestImpl mcan = (MarketCancelRequestImpl) getMessage( _downQ, MarketCancelRequestImpl.class );

        assertSame( _proc.getStatePendingCancel(), order.getState() );

        clearQueues();

        // SYNTH MARKET CANCEL REJECTED -----------------------------------------------------------------------------
        MarketVagueOrderRejectImpl mcxl = FixTestUtils.getMarketVagueReject( mcan.getClOrdId(), CAN_REJECT, false );
        _proc.handle( mcxl );

        // MCXL now recycled, also CCAN recycled

        checkQueueSize( 1, 0, 0 );

        // GET THE CLIENT CANCEL REJECT -------------------------------------------------------------------------
        ClientCancelRejectImpl ccxl = (ClientCancelRejectImpl) getMessage( _upQ, ClientCancelRejectImpl.class );

        checkClientCancelReject( nos, ccxl, clOrdId, nos.getClOrdId(), mktOrderId, CAN_REJECT,
                                 CxlRejReason.Other, CxlRejResponseTo.CancelRequest, OrdStatus.New );

        //  check version and order state
        assertSame( _proc.getStateOpen(), order.getState() );
        assertSame( order.getLastAckedVerion(), order.getPendingVersion() );
        assertEquals( clOrdId, order.getClientClOrdIdChain() );
        assertEquals( mcan.getClOrdId(), order.getClientClOrdIdChain() ); // as exchange is not using its own ids
        assertEquals( OrdStatus.New, order.getLastAckedVerion().getOrdStatus() );

        ClientProfile client = order.getClientProfile();
        assertNotNull( client );

        // check client limits are zero ascumqty was zero
        assertSame( order.getClientProfile(), nos.getClient() );
        assertEquals( 100, client.getTotalOrderQty(), Constants.TICK_WEIGHT );
        assertEquals( 2512.0, client.getTotalOrderValueUSD(), Constants.WEIGHT );

    }
}
