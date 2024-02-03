/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.processor;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.ClientProfile;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.om.client.OMClientProfile;
import com.rr.om.order.Order;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestTradeCorrect extends BaseProcessorTestCase {

    @Test
    public void testCorrectDown() {

        // send NOS to mkt
        ClientNewOrderSingleImpl cnos = FixTestUtils.getClientNOS( _msgBuf, _decoder, _clOrdId, 100, 25.12, _upMsgHandler );
        _proc.handle( cnos );
        clearQueues();

        // synth mkt ack
        MarketNewOrderAckImpl mack = FixTestUtils.getMarketACK( _msgBuf, _decoder, _mktClOrdId, 100, 25.12, _mktOrderId, _execId );
        _mktClOrdId.copy( mack.getClOrdId() );
        _proc.handle( mack );
        clearQueues();

        // MACK is now recycled !

        // order now open send partial fill and get client fill
        int    lastQty = 10;
        double lastPx  = 24.0;

        int    corrQty = 5;
        double corrPx  = 22.0;

        TradeNew mfill = FixTestUtils.getMarketTradeNew( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, lastQty, lastPx, _fillExecId );
        _proc.handle( mfill );
        checkQueueSize( 1, 0, 0 );
        getMessage( _upQ, ClientTradeNewImpl.class );

        _execId.setValue( "Correct001" );
        TradeCorrect mcan = FixTestUtils.getMarketTradeCorrect( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, corrQty, corrPx,
                                                                _execId, _fillExecId, OrdStatus.PartiallyFilled );
        _proc.handle( mcan );
        checkQueueSize( 1, 0, 0 );
        ClientTradeCorrectImpl ccan = (ClientTradeCorrectImpl) getMessage( _upQ, ClientTradeCorrectImpl.class );

        Order order = _proc.getOrder( cnos.getClOrdId() );

        assertSame( _proc.getStateOpen(), order.getState() );
        assertSame( order.getLastAckedVerion(), order.getPendingVersion() );

        checkTradeCorrect( order, cnos, ccan, _mktOrderId, _execId, corrQty, corrPx, corrQty, corrPx * corrQty, OrdStatus.PartiallyFilled );
    }

    @Test
    public void testCorrectPartialFillOnCorrectledSentClient() {

        // send NOS to mkt
        ClientNewOrderSingleImpl cnos = FixTestUtils.getClientNOS( _msgBuf, _decoder, _clOrdId, 100, 25.12, _upMsgHandler );
        _proc.handle( cnos );
        clearQueues();

        Order order = _proc.getOrder( cnos.getClOrdId() );

        // synth mkt ack
        MarketNewOrderAckImpl mack = FixTestUtils.getMarketACK( _msgBuf, _decoder, _mktClOrdId, 100, 25.12, _mktOrderId, _execId );
        _proc.handle( mack );
        clearQueues();

        // send partial fill
        int    lastQty = 10;
        double lastPx  = 24.0;

        TradeNew mfill = FixTestUtils.getMarketTradeNew( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, lastQty, lastPx, _fillExecId );
        _proc.handle( mfill );
        checkQueueSize( 1, 0, 0 );
        getMessage( _upQ, ClientTradeNewImpl.class );

        // SEND Correct --------------------------------------------------------------------------------------
        ClientCancelRequestImpl ccan = FixTestUtils.getClientCancelRequest( _msgBuf, _decoder, _cancelClOrdId, cnos.getClOrdId(), _upMsgHandler );
        _proc.handle( ccan );
        clearQueues();

        // SYNTH MARKET CorrectLED -----------------------------------------------------------------------------
        Cancelled mcxl = FixTestUtils.getCancelled( _msgBuf, _decoder, _cancelClOrdId, cnos.getOrigClOrdId(), 100, 25.12, _mktOrderId, _cancelExecId );
        _proc.handle( mcxl );
        clearQueues();

        assertSame( _proc.getStateCompleted(), order.getState() );

        // order is in terminal state

        assertEquals( true, order.getClientProfile().isSendClientLateFills() );

        int    corrQty = 5;
        double corrPx  = 22.0;

        _execId.setValue( "Correct001" );
        TradeCorrect mcan = FixTestUtils.getMarketTradeCorrect( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, corrQty, corrPx,
                                                                _execId, _fillExecId, OrdStatus.Canceled );
        _proc.handle( mcan );
        checkQueueSize( 1, 0, 0 );
        ClientTradeCorrectImpl ctcan = (ClientTradeCorrectImpl) getMessage( _upQ, ClientTradeCorrectImpl.class );

        checkTradeCorrect( order, cnos, ctcan, _mktOrderId, _execId, corrQty, corrPx, corrQty, corrPx * corrQty, OrdStatus.Canceled );
    }

    @Test
    public void testCorrectPartialOnOpenState() {

        // send NOS to mkt
        ClientNewOrderSingleImpl cnos = FixTestUtils.getClientNOS( _msgBuf, _decoder, _clOrdId, 100, 25.12, _upMsgHandler );
        _proc.handle( cnos );
        clearQueues();

        Order order = _proc.getOrder( cnos.getClOrdId() );

        // synth mkt ack
        MarketNewOrderAckImpl mack = FixTestUtils.getMarketACK( _msgBuf, _decoder, _mktClOrdId, 100, 25.12, _mktOrderId, _execId );
        _mktClOrdId.copy( mack.getClOrdId() );
        _proc.handle( mack );
        clearQueues();

        // MACK is now recycled !

        // order now open send partial fill and get client fill
        int    lastQty = 10;
        double lastPx  = 24.0;

        int    corrQty = 15;
        double corrPx  = 22.0;

        TradeNew mfill = FixTestUtils.getMarketTradeNew( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, lastQty, lastPx, _fillExecId );
        _proc.handle( mfill );
        checkQueueSize( 1, 0, 0 );
        getMessage( _upQ, ClientTradeNewImpl.class );

        _execId.setValue( "Correct001" );
        TradeCorrect mcan = FixTestUtils.getMarketTradeCorrect( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, corrQty, corrPx,
                                                                _execId, _fillExecId, OrdStatus.PartiallyFilled );
        _proc.handle( mcan );
        checkQueueSize( 1, 0, 0 );
        ClientTradeCorrectImpl ccan = (ClientTradeCorrectImpl) getMessage( _upQ, ClientTradeCorrectImpl.class );

        assertSame( _proc.getStateOpen(), order.getState() );
        assertSame( order.getLastAckedVerion(), order.getPendingVersion() );

        checkTradeCorrect( order, cnos, ccan, _mktOrderId, _execId, corrQty, corrPx, corrQty, corrPx * corrQty, OrdStatus.PartiallyFilled );
    }

    @SuppressWarnings( "boxing" )
    @Test
    public void testFillInPendingAmend() {

        int    lastQty;
        double lastPx;
        int    cumQty    = 0;
        double totTraded = 0.0;

        // SEND NOS
        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, 25.0, _upMsgHandler );
        _proc.handle( nos );

        Order order = _proc.getOrder( nos.getClOrdId() );

        clearQueues();

        ZString mktOrderId = new ViewString( "ORD000001" );
        _orderId.setValue( mktOrderId );

        // SYNTH MARKET ACK
        NewOrderAck mack = FixTestUtils.getMarketACK( _decoder, "TST0000001", 100, 25.0, mktOrderId, new ViewString( "EXE50001" ) );

        _mktOrigClOrdId.copy( mack.getClOrdId() );

        _proc.handle( mack );
        checkQueueSize( 1, 0, 0 );

        // MACK now recycled

        // GET THE CLIENT ACK -------------------------------------------------------------------------------
        @SuppressWarnings( "unused" )
        ClientNewOrderAckImpl cack = (ClientNewOrderAckImpl) getMessage( _upQ, ClientNewOrderAckImpl.class );

        assertEquals( nos.getClOrdId(), order.getClientClOrdIdChain() );
        assertSame( _proc.getStateOpen(), order.getState() );

        clearQueues();

        // FILL
        // was 100 * 25 = 2500, fillVal = 120, totVal = 2495
        lastPx  = 24.0;
        lastQty = 5;
        cumQty += lastQty;
        totTraded += (lastQty * lastPx);
        sendFILL( _proc, _msgBuf, _decoder, OrdStatus.PartiallyFilled, order, lastQty, lastPx, cumQty, totTraded, _proc.getStateOpen() );

        // SEND CLIENT REPLACE ------------------------------------------------------------------------------
        _clOrdId.setValue( "TST000001B" );

        ClientCancelReplaceRequestImpl ccrr = FixTestUtils.getClientCancelReplaceRequest( _msgBuf, _decoder, _clOrdId, nos.getClOrdId(),
                                                                                          90, 23.0, _upMsgHandler );

        _proc.handle( ccrr );
        checkQueueSize( 0, 1, 0 );

        // GET THE MARKEY REPLACE REQ -------------------------------------------------------------------------
        MarketCancelReplaceRequestImpl mcrr = (MarketCancelReplaceRequestImpl) getMessage( _downQ, MarketCancelReplaceRequestImpl.class );

        assertSame( _proc.getStatePendingReplace(), order.getState() );

        checkMarketReplace( ccrr, mcrr, _mktOrigClOrdId, _orderId );

        clearQueues();

        // SYNTH TRADE Correct IN PENDING REPLACE -----------------------------------------------------------------------------

        int    corrQty = 5;
        double corrPx  = 22.0;

        // this simulates exchange sending PartialFil BUST to New - ie replace not yet processed at exchange
        TradeCorrect mcan = FixTestUtils.getMarketTradeCorrect( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, nos, corrQty, corrPx,
                                                                _execId, _fillExecId, OrdStatus.PartiallyFilled );
        _proc.handle( mcan );
        checkQueueSize( 1, 0, 0 );
        ClientTradeCorrectImpl ctcan = (ClientTradeCorrectImpl) getMessage( _upQ, ClientTradeCorrectImpl.class );

        // cant rely on ord status from exchange need manage ... here state is PendingReplace
        checkTradeCorrect( order, nos, ctcan, _mktOrderId, _execId, corrQty, corrPx, corrQty, corrPx * corrQty, OrdStatus.PendingReplace );

        // SYNTH MARKET REPLACED -----------------------------------------------------------------------------
        _execId.setValue( "EXE50002" );

        Replaced mrep = FixTestUtils.getMarketReplaced( _msgBuf, _decoder, mcrr.getClOrdId(), mcrr.getOrigClOrdId(), 90, 23.0, _orderId, _execId );
        _proc.handle( mrep );

        // MCXL now recycled, also CCAN recycled

        checkQueueSize( 1, 0, 0 );

        // GET THE MARKEY Correct REQ -------------------------------------------------------------------------
        ClientReplacedImpl crep = (ClientReplacedImpl) getMessage( _upQ, ClientReplacedImpl.class );

        checkClientReplaced( ccrr, crep, mktOrderId, _execId, corrQty, corrPx * corrQty );

        //  check version and order state
        assertSame( _proc.getStateOpen(), order.getState() );
        assertSame( order.getLastAckedVerion(), order.getPendingVersion() );
        assertEquals( _clOrdId, order.getClientClOrdIdChain() );
        assertEquals( mcrr.getClOrdId(), order.getClientClOrdIdChain() ); // as exchange is not using its own ids
        assertEquals( OrdStatus.PartiallyFilled, order.getLastAckedVerion().getOrdStatus() ); // fix 44 doesnt use ord status replaced

        ClientProfile client = order.getClientProfile();
        assertNotNull( client );

        // check client limits are zero as cumqty was zero
        assertSame( order.getClientProfile(), nos.getClient() );
        assertEquals( 90, client.getTotalOrderQty(), Constants.TICK_WEIGHT );
        assertEquals( 23.0 * (90 - corrQty) + corrPx * corrQty, client.getTotalOrderValueUSD(), Constants.WEIGHT );
    }

    @Test
    public void testFullFillBustToOpen() {

        // send NOS to mkt
        ClientNewOrderSingleImpl cnos = FixTestUtils.getClientNOS( _msgBuf, _decoder, _clOrdId, 100, 25.12, _upMsgHandler );
        _proc.handle( cnos );
        clearQueues();

        Order order = _proc.getOrder( cnos.getClOrdId() );

        // synth mkt ack
        MarketNewOrderAckImpl mack = FixTestUtils.getMarketACK( _msgBuf, _decoder, _mktClOrdId, 100, 25.12, _mktOrderId, _execId );
        _proc.handle( mack );
        clearQueues();

        // send partial fill
        int    lastQty = 100;
        double lastPx  = 24.0;

        TradeNew mfill = FixTestUtils.getMarketTradeNew( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, lastQty, lastPx, _fillExecId );
        _proc.handle( mfill );
        checkQueueSize( 1, 0, 0 );
        getMessage( _upQ, ClientTradeNewImpl.class );

        assertSame( _proc.getStateCompleted(), order.getState() );

        // order is in terminal state 

        assertEquals( true, order.getClientProfile().isSendClientLateFills() );

        int    corrQty = 0;
        double corrPx  = 0.0;

        _execId.setValue( "Correct001" );
        TradeCorrect mcan = FixTestUtils.getMarketTradeCorrect( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, corrQty, corrPx,
                                                                _execId, _fillExecId, OrdStatus.New );
        _proc.handle( mcan );
        checkQueueSize( 1, 0, 0 );
        ClientTradeCorrectImpl ctcan = (ClientTradeCorrectImpl) getMessage( _upQ, ClientTradeCorrectImpl.class );

        checkTradeCorrect( order, cnos, ctcan, _mktOrderId, _execId, corrQty, corrPx, corrQty, corrPx * corrQty, OrdStatus.New );
    }

    @Test
    public void testFullFillBustToPartiallyFilled() {

        // send NOS to mkt
        ClientNewOrderSingleImpl cnos = FixTestUtils.getClientNOS( _msgBuf, _decoder, _clOrdId, 100, 25.12, _upMsgHandler );
        _proc.handle( cnos );
        clearQueues();

        Order order = _proc.getOrder( cnos.getClOrdId() );

        // synth mkt ack
        MarketNewOrderAckImpl mack = FixTestUtils.getMarketACK( _msgBuf, _decoder, _mktClOrdId, 100, 25.12, _mktOrderId, _execId );
        _proc.handle( mack );
        clearQueues();

        int    lastQty;
        double lastPx;
        int    cumQty    = 0;
        double totTraded = 0.0;

        TradeNew t2;

        // send partial fill

        lastPx  = 24.0;
        lastQty = 5;
        cumQty += lastQty;
        totTraded += (lastQty * lastPx);
        sendFILL( _proc, _msgBuf, _decoder, OrdStatus.PartiallyFilled, order, lastQty, lastPx, cumQty, totTraded, _proc.getStateOpen() );
        ReusableString fillExec1 = new ReusableString( _fillExecId );

        lastPx  = 24.0;
        lastQty = 95;
        cumQty += lastQty;
        totTraded += (lastQty * lastPx);
        t2      = sendFILL( _proc, _msgBuf, _decoder, OrdStatus.Filled, order, lastQty, lastPx, cumQty, totTraded, _proc.getStateCompleted() );
        ReusableString fillExec2 = new ReusableString( _fillExecId );

        assertSame( _proc.getStateCompleted(), order.getState() );

        // order is in terminal state 

        assertEquals( true, order.getClientProfile().isSendClientLateFills() );

        // bust t1 to partiallyFilled -> note px and qty NOT supplied only the ref
        int    corrQty1 = 5;
        double corrPx1  = 22.0;

        _execId.setValue( "Correct001" );
        TradeCorrect mcan = FixTestUtils.getMarketTradeCorrect( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, corrQty1, corrPx1,
                                                                _execId, fillExec1, OrdStatus.PartiallyFilled );
        _proc.handle( mcan );
        checkQueueSize( 1, 0, 0 );
        ClientTradeCorrectImpl ctcan = (ClientTradeCorrectImpl) getMessage( _upQ, ClientTradeCorrectImpl.class );

        checkTradeCorrect( order, cnos, ctcan, _mktOrderId, _execId, corrQty1, corrPx1,
                           t2.getLastQty() + corrQty1,
                           (t2.getLastPx() * t2.getLastQty()) + (corrQty1 * corrPx1),
                           OrdStatus.PartiallyFilled );

        // bust t2 top new

        int    corrQty2 = 15;
        double corrPx2  = 27.0;

        _execId.setValue( "Correct002" );
        TradeCorrect mcan2 = FixTestUtils.getMarketTradeCorrect( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, corrQty2, corrPx2,
                                                                 _execId, fillExec2, OrdStatus.PartiallyFilled );
        _proc.handle( mcan2 );
        checkQueueSize( 1, 0, 0 );
        ClientTradeCorrectImpl ctcan2 = (ClientTradeCorrectImpl) getMessage( _upQ, ClientTradeCorrectImpl.class );

        checkTradeCorrect( order, cnos, ctcan2, _mktOrderId, _execId, corrQty2, corrPx2,
                           corrQty1 + corrQty2,
                           (corrQty1 * corrPx1) + (corrQty2 * corrPx2),
                           OrdStatus.PartiallyFilled );
    }

    @Test
    public void testPartialFillOnTerminalNotSentClient() {

        // send NOS to mkt
        ClientNewOrderSingleImpl cnos               = FixTestUtils.getClientNOS( _msgBuf, _decoder, _clOrdId, 100, 25.12, _upMsgHandler );
        boolean                  wasClientLateFills = ((OMClientProfile) cnos.getClient()).setSendClientLateFills( false );

        try {
            _proc.handle( cnos );
            clearQueues();

            Order order = _proc.getOrder( cnos.getClOrdId() );

            // synth mkt ack
            MarketNewOrderAckImpl mack = FixTestUtils.getMarketACK( _msgBuf, _decoder, _mktClOrdId, 100, 25.12, _mktOrderId, _execId );
            _proc.handle( mack );
            clearQueues();

            // send partial late fill
            int    lastQty = 10;
            double lastPx  = 24.0;

            TradeNew mfill = FixTestUtils.getMarketTradeNew( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, lastQty, lastPx, _fillExecId );
            _proc.handle( mfill );
            checkQueueSize( 1, 0, 0 );
            getMessage( _upQ, ClientTradeNewImpl.class );

            // SEND Correct --------------------------------------------------------------------------------------
            ClientCancelRequestImpl ccan = FixTestUtils.getClientCancelRequest( _msgBuf, _decoder, _cancelClOrdId, cnos.getClOrdId(), _upMsgHandler );
            _proc.handle( ccan );
            clearQueues();

            // SYNTH MARKET CorrectLED -----------------------------------------------------------------------------
            Cancelled mcxl = FixTestUtils.getCancelled( _msgBuf, _decoder, _cancelClOrdId, cnos.getOrigClOrdId(), 100, 25.12, _mktOrderId, _cancelExecId );
            _proc.handle( mcxl );
            clearQueues();

            assertSame( _proc.getStateCompleted(), order.getState() );

            // order is in terminal state BUST GOES TO HUB

            assertEquals( false, order.getClientProfile().isSendClientLateFills() );

            int    corrQty1 = 5;
            double corrPx1  = 22.0;

            _execId.setValue( "Correct001" );
            TradeCorrect mcan = FixTestUtils.getMarketTradeCorrect( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, corrQty1,
                                                                    corrPx1, _execId, _fillExecId, OrdStatus.Canceled );
            _proc.handle( mcan );
            checkQueueSize( 0, 0, 1 );
            ClientTradeCorrectImpl ctcan = (ClientTradeCorrectImpl) getMessage( _hubQ, ClientTradeCorrectImpl.class );

            checkTradeCorrect( order, cnos, ctcan, _mktOrderId, _execId, corrQty1, corrPx1, corrQty1, corrQty1 * corrPx1, OrdStatus.Canceled );

            assertSame( _proc.getStateCompleted(), order.getState() );
            assertSame( order.getLastAckedVerion(), order.getPendingVersion() );

        } finally {
            ((OMClientProfile) cnos.getClient()).setSendClientLateFills( wasClientLateFills );
        }
    }

    @Test
    public void testTradeCorrectDupOnOpenState() {

        // send NOS to mkt
        ClientNewOrderSingleImpl cnos = FixTestUtils.getClientNOS( _msgBuf, _decoder, _clOrdId, 100, 25.12, _upMsgHandler );
        _proc.handle( cnos );
        clearQueues();

        // synth mkt ack
        MarketNewOrderAckImpl mack = FixTestUtils.getMarketACK( _msgBuf, _decoder, _mktClOrdId, 100, 25.12, _mktOrderId, _execId );
        _mktClOrdId.copy( mack.getClOrdId() );
        _proc.handle( mack );
        clearQueues();

        // MACK is now recycled !

        // order now open send partial fill and get client fill
        int    lastQty = 10;
        double lastPx  = 24.0;

        TradeNew mfill = FixTestUtils.getMarketTradeNew( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, lastQty, lastPx, _fillExecId );
        _proc.handle( mfill );
        checkQueueSize( 1, 0, 0 );
        getMessage( _upQ, ClientTradeNewImpl.class );

        int    corrQty = 5;
        double corrPx  = 22.0;

        // SEND TRADE CORRECT
        _execId.setValue( "Correct001" );
        TradeCorrect mcan = FixTestUtils.getMarketTradeCorrect( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, corrQty, corrPx,
                                                                _execId, _fillExecId, OrdStatus.PartiallyFilled );
        _proc.handle( mcan );
        checkQueueSize( 1, 0, 0 );
        ClientTradeCorrectImpl ccan = (ClientTradeCorrectImpl) getMessage( _upQ, ClientTradeCorrectImpl.class );

        Order order = _proc.getOrder( cnos.getClOrdId() );
        checkTradeCorrect( order, cnos, ccan, _mktOrderId, _execId, corrQty, corrPx, corrQty, corrPx * corrQty, OrdStatus.PartiallyFilled );

        // SEND DUP CORRECT
        _execId.setValue( "Correct001" );
        TradeCorrect mcan2 = FixTestUtils.getMarketTradeCorrect( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, corrQty, corrPx,
                                                                 _execId, _fillExecId, OrdStatus.PartiallyFilled );
        _proc.handle( mcan2 );
        checkQueueSize( 0, 0, 0 ); // dup ignored

        assertSame( _proc.getStateOpen(), order.getState() );
        assertSame( order.getLastAckedVerion(), order.getPendingVersion() );
    }

    @Test
    public void testTradeCorrectOnPendingCancel() {

        // send NOS to mkt
        ClientNewOrderSingleImpl cnos = FixTestUtils.getClientNOS( _msgBuf, _decoder, _clOrdId, 100, 25.12, _upMsgHandler );
        _proc.handle( cnos );
        clearQueues();

        Order order = _proc.getOrder( cnos.getClOrdId() );

        // synth mkt ack
        MarketNewOrderAckImpl mack = FixTestUtils.getMarketACK( _msgBuf, _decoder, _mktClOrdId, 100, 25.12, _mktOrderId, _execId );
        _proc.handle( mack );
        clearQueues();

        // send partial fill
        int    lastQty = 10;
        double lastPx  = 24.0;

        TradeNew mfill = FixTestUtils.getMarketTradeNew( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, lastQty, lastPx, _fillExecId );
        _proc.handle( mfill );
        checkQueueSize( 1, 0, 0 );
        getMessage( _upQ, ClientTradeNewImpl.class );

        // SEND Correct --------------------------------------------------------------------------------------
        ClientCancelRequestImpl ccan = FixTestUtils.getClientCancelRequest( _msgBuf, _decoder, _cancelClOrdId, cnos.getClOrdId(), _upMsgHandler );
        _proc.handle( ccan );
        clearQueues();

        assertSame( _proc.getStatePendingCancel(), order.getState() );

        // order is PENDING Correct

        assertEquals( true, order.getClientProfile().isSendClientLateFills() );

        assertSame( _proc.getStatePendingCancel(), order.getState() );
        assertNotSame( order.getLastAckedVerion(), order.getPendingVersion() );

        int    corrQty = 15;
        double corrPx  = 21.0;
        // this simulates exchange sending PartialFil BUST to New - ie Correct not yet processed at exchange
        TradeCorrect mcan = FixTestUtils.getMarketTradeCorrect( _msgBuf, _decoder, _mktOrderId, _mktClOrdId, cnos, corrQty, corrPx,
                                                                _execId, _fillExecId, OrdStatus.New ); // try and trick with New
        _proc.handle( mcan );
        checkQueueSize( 1, 0, 0 );
        ClientTradeCorrectImpl ctcan = (ClientTradeCorrectImpl) getMessage( _upQ, ClientTradeCorrectImpl.class );

        // cant rely on ord status from exchange need manage ... here state is PendingCancel
        checkTradeCorrect( order, cnos, ctcan, _mktOrderId, _execId, corrQty, corrPx, corrQty, corrQty * corrPx, OrdStatus.PendingCancel );
    }
}
