/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.recovery;

import com.rr.core.model.Event;
import com.rr.core.recovery.dma.DMARecoverySessionContext;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.model.internal.type.ExecType;
import org.junit.Test;

import static org.junit.Assert.*;

public class HFTRecoveryTradeTest extends BaseHFTRecoveryTst {

    private final int    qty1 = 100;
    private final double px1  = 25.25;
    private final double fillPx1 = 25.15, fillPx2 = 25.175, fillPx3 = 24.9;
    private final double avePx1  = fillPx1;
    private PKeys pkNOS1   = new PKeys();
    private PKeys pkTrade1 = new PKeys();
    private PKeys pkTrade2 = new PKeys();    private final int fillQty1 = 10, fillQty2 = 15, fillQty3 = (qty1 - fillQty1 - fillQty2);
    private PKeys pkTrade3 = new PKeys();

    @Test
    public void testFullFill() {

        setupNOSandACK( "C0000001A", fillQty1, px1, pkNOS1 );
        setupTrade( fillQty1, fillPx1, cumQty1, avePx1, "EX_TR_1", pkNOS1, pkTrade1, OrdStatus.Filled );

        _orderMap.clear();

        // persister's are now populated, play persisted events into reconciler

        _ctl.start();

        DMARecoverySessionContext clientIn    = _ctl.startedInbound( _client1 );
        DMARecoverySessionContext clientOut   = _ctl.startedOutbound( _client1 );
        DMARecoverySessionContext exchangeIn  = _ctl.startedInbound( _exchange1 );
        DMARecoverySessionContext exchangeOut = _ctl.startedOutbound( _exchange1 );

        replay( clientIn, pkNOS1._cInKey );
        replay( exchangeOut, pkNOS1._mOutKey );
        replay( exchangeIn, pkNOS1._mInKey );
        replay( clientOut, pkNOS1._cOutKey );

        replay( exchangeIn, pkTrade1._mInKey );
        replay( clientOut, pkTrade1._cOutKey );

        _ctl.reconcile();

        assertEquals( 1, _orders.size() );

        Event upChain   = _ctl.getUpstreamChain();
        Event downChain = _ctl.getDownChain();

        assertNull( upChain );
        assertNull( downChain );
    }    private final int    cumQty1 = fillQty1;

    @Test
    public void testFullFillOutOfOrder() {

        setupNOSandACK( "C0000001A", fillQty1, px1, pkNOS1 );
        setupTrade( fillQty1, fillPx1, cumQty1, avePx1, "EX_TR_1", pkNOS1, pkTrade1, OrdStatus.PartiallyFilled );

        _orderMap.clear();

        // persister's are now populated, play persisted events into reconciler

        _ctl.start();

        DMARecoverySessionContext clientIn    = _ctl.startedInbound( _client1 );
        DMARecoverySessionContext clientOut   = _ctl.startedOutbound( _client1 );
        DMARecoverySessionContext exchangeIn  = _ctl.startedInbound( _exchange1 );
        DMARecoverySessionContext exchangeOut = _ctl.startedOutbound( _exchange1 );

        replay( exchangeIn, pkNOS1._mInKey );
        replay( exchangeIn, pkTrade1._mInKey );

        replay( exchangeOut, pkNOS1._mOutKey );

        replay( clientOut, pkNOS1._cOutKey );
        replay( clientOut, pkTrade1._cOutKey );

        replay( clientIn, pkNOS1._cInKey );

        _ctl.reconcile();

        assertEquals( 1, _orders.size() );

        Event upChain   = _ctl.getUpstreamChain();
        Event downChain = _ctl.getDownChain();

        assertNull( upChain );
        assertNull( downChain );
    }

    @Test
    public void testMissingClientPartial() {

        setupNOSandACK( "C0000001A", qty1, px1, pkNOS1 );
        setupTrade( fillQty1, fillPx1, cumQty1, avePx1, "EX_TR_1", pkNOS1, pkTrade1, OrdStatus.PartiallyFilled );

        _orderMap.clear();

        // persister's are now populated, play persisted events into reconciler

        _ctl.start();

        DMARecoverySessionContext clientIn    = _ctl.startedInbound( _client1 );
        DMARecoverySessionContext clientOut   = _ctl.startedOutbound( _client1 );
        DMARecoverySessionContext exchangeIn  = _ctl.startedInbound( _exchange1 );
        DMARecoverySessionContext exchangeOut = _ctl.startedOutbound( _exchange1 );

        replay( clientIn, pkNOS1._cInKey );
        replay( exchangeOut, pkNOS1._mOutKey );
        replay( exchangeIn, pkNOS1._mInKey );
        replay( clientOut, pkNOS1._cOutKey );

        replay( exchangeIn, pkTrade1._mInKey );

        // missing  client send of Trade

        _ctl.reconcile();

        assertEquals( 1, _orders.size() );

        Event upChain   = _ctl.getUpstreamChain();
        Event downChain = _ctl.getDownChain();

        assertNotNull( upChain );
        assertNotNull( downChain );

        OrderRequest clientReq = (OrderRequest) clientIn.regenerate( pkNOS1._cInKey );
        NewOrderAck  clientRep = (NewOrderAck) clientOut.regenerate( pkNOS1._cOutKey );

        TradeNew clientTradeNew = (TradeNew) upChain;
        checkTrade( clientTradeNew, clientReq, clientRep, OrdStatus.PartiallyFilled, ExecType.Trade, fillQty1, cumQty1, fillPx1, avePx1 );

        Cancelled clientUnsolCancelled = (Cancelled) upChain.getNextQueueEntry();
        checkExec( clientUnsolCancelled, clientReq, clientRep, OrdStatus.Canceled, ExecType.Canceled, cumQty1, avePx1 );

        CancelRequest mktCanReq = (CancelRequest) downChain;
        NewOrderAck   mktAck    = (NewOrderAck) exchangeIn.regenerate( pkNOS1._mInKey );
        checkCancel( mktAck, mktCanReq );
    }    private final int    cumQty2 = fillQty1 + fillQty2;

    @Test
    public void testPartialFill() {

        setupNOSandACK( "C0000001A", qty1, px1, pkNOS1 );
        setupTrade( fillQty1, fillPx1, cumQty1, avePx1, "EX_TR_1", pkNOS1, pkTrade1, OrdStatus.PartiallyFilled );

        _orderMap.clear();

        // persister's are now populated, play persisted events into reconciler

        _ctl.start();

        DMARecoverySessionContext clientIn    = _ctl.startedInbound( _client1 );
        DMARecoverySessionContext clientOut   = _ctl.startedOutbound( _client1 );
        DMARecoverySessionContext exchangeIn  = _ctl.startedInbound( _exchange1 );
        DMARecoverySessionContext exchangeOut = _ctl.startedOutbound( _exchange1 );

        replay( clientIn, pkNOS1._cInKey );
        replay( exchangeOut, pkNOS1._mOutKey );
        replay( exchangeIn, pkNOS1._mInKey );
        replay( clientOut, pkNOS1._cOutKey );

        replay( exchangeIn, pkTrade1._mInKey );
        replay( clientOut, pkTrade1._cOutKey );

        _ctl.reconcile();

        assertEquals( 1, _orders.size() );

        Event upChain   = _ctl.getUpstreamChain();
        Event downChain = _ctl.getDownChain();

        assertNotNull( upChain );
        assertNotNull( downChain );

        Cancelled    clientUnsolCancelled = (Cancelled) upChain;
        OrderRequest clientReq            = (OrderRequest) clientIn.regenerate( pkNOS1._cInKey );
        NewOrderAck  clientRep            = (NewOrderAck) clientOut.regenerate( pkNOS1._cOutKey );
        checkExec( clientUnsolCancelled, clientReq, clientRep, OrdStatus.Canceled, ExecType.Canceled, cumQty1, avePx1 );

        CancelRequest mktCanReq = (CancelRequest) downChain;
        NewOrderAck   mktAck    = (NewOrderAck) exchangeIn.regenerate( pkNOS1._mInKey );
        checkCancel( mktAck, mktCanReq );
    }    private final double avePx2  = (fillQty1 * fillPx1 + fillQty2 * fillPx2) / (fillQty1 + fillQty2);

    @Test
    public void testPartialFillOutOfOrder() {

        setupNOSandACK( "C0000001A", qty1, px1, pkNOS1 );
        setupTrade( fillQty1, fillPx1, cumQty1, avePx1, "EX_TR_1", pkNOS1, pkTrade1, OrdStatus.PartiallyFilled );

        _orderMap.clear();

        // persister's are now populated, play persisted events into reconciler

        _ctl.start();

        DMARecoverySessionContext clientIn    = _ctl.startedInbound( _client1 );
        DMARecoverySessionContext clientOut   = _ctl.startedOutbound( _client1 );
        DMARecoverySessionContext exchangeIn  = _ctl.startedInbound( _exchange1 );
        DMARecoverySessionContext exchangeOut = _ctl.startedOutbound( _exchange1 );

        replay( exchangeIn, pkNOS1._mInKey );
        replay( exchangeIn, pkTrade1._mInKey );

        replay( exchangeOut, pkNOS1._mOutKey );

        replay( clientIn, pkNOS1._cInKey );

        replay( clientOut, pkNOS1._cOutKey );
        replay( clientOut, pkTrade1._cOutKey );

        _ctl.reconcile();

        assertEquals( 1, _orders.size() );

        Event upChain   = _ctl.getUpstreamChain();
        Event downChain = _ctl.getDownChain();

        assertNotNull( upChain );
        assertNotNull( downChain );

        Cancelled    clientUnsolCancelled = (Cancelled) upChain;
        OrderRequest clientReq            = (OrderRequest) clientIn.regenerate( pkNOS1._cInKey );
        NewOrderAck  clientRep            = (NewOrderAck) clientOut.regenerate( pkNOS1._cOutKey );
        checkExec( clientUnsolCancelled, clientReq, clientRep, OrdStatus.Canceled, ExecType.Canceled, cumQty1, avePx1 );

        CancelRequest mktCanReq = (CancelRequest) downChain;
        NewOrderAck   mktAck    = (NewOrderAck) exchangeIn.regenerate( pkNOS1._mInKey );
        checkCancel( mktAck, mktCanReq );
    }    private final int    cumQty3 = fillQty1 + fillQty2 + fillQty3;

    @Test
    public void testThreeFillsFull() {

        setupNOSandACK( "C0000001A", qty1, px1, pkNOS1 );
        setupTrade( fillQty1, fillPx1, cumQty1, avePx1, "EX_TR_1", pkNOS1, pkTrade1, OrdStatus.PartiallyFilled );
        setupTrade( fillQty2, fillPx2, cumQty2, avePx2, "EX_TR_2", pkNOS1, pkTrade2, OrdStatus.PartiallyFilled );
        setupTrade( fillQty3, fillPx3, cumQty3, avePx3, "EX_TR_3", pkNOS1, pkTrade3, OrdStatus.Filled );

        _orderMap.clear();

        // persister's are now populated, play persisted events into reconciler

        _ctl.start();

        DMARecoverySessionContext clientIn    = _ctl.startedInbound( _client1 );
        DMARecoverySessionContext clientOut   = _ctl.startedOutbound( _client1 );
        DMARecoverySessionContext exchangeIn  = _ctl.startedInbound( _exchange1 );
        DMARecoverySessionContext exchangeOut = _ctl.startedOutbound( _exchange1 );

        replay( clientIn, pkNOS1._cInKey );
        replay( exchangeOut, pkNOS1._mOutKey );
        replay( exchangeIn, pkNOS1._mInKey );
        replay( clientOut, pkNOS1._cOutKey );

        replay( exchangeIn, pkTrade1._mInKey );
        replay( clientOut, pkTrade1._cOutKey );

        replay( exchangeIn, pkTrade2._mInKey );
        replay( clientOut, pkTrade2._cOutKey );

        replay( exchangeIn, pkTrade3._mInKey );
        replay( clientOut, pkTrade3._cOutKey );

        _ctl.reconcile();

        assertEquals( 1, _orders.size() );

        Event upChain   = _ctl.getUpstreamChain();
        Event downChain = _ctl.getDownChain();

        assertNull( upChain );
        assertNull( downChain );
    }    private final double avePx3  = (fillQty1 * fillPx1 + fillQty2 * fillPx2 + fillQty3 * fillPx3) / (fillQty1 + fillQty2 + fillQty3);

    @Test
    public void testThreeFillsFullMissingClientLastFills() {

        setupNOSandACK( "C0000001A", qty1, px1, pkNOS1 );
        setupTrade( fillQty1, fillPx1, cumQty1, avePx1, "EX_TR_1", pkNOS1, pkTrade1, OrdStatus.PartiallyFilled );
        setupTrade( fillQty2, fillPx2, cumQty2, avePx2, "EX_TR_2", pkNOS1, pkTrade2, OrdStatus.PartiallyFilled );
        setupTrade( fillQty3, fillPx3, cumQty3, avePx3, "EX_TR_3", pkNOS1, pkTrade3, OrdStatus.Filled );

        _orderMap.clear();

        // persister's are now populated, play persisted events into reconciler

        _ctl.start();

        DMARecoverySessionContext clientIn    = _ctl.startedInbound( _client1 );
        DMARecoverySessionContext clientOut   = _ctl.startedOutbound( _client1 );
        DMARecoverySessionContext exchangeIn  = _ctl.startedInbound( _exchange1 );
        DMARecoverySessionContext exchangeOut = _ctl.startedOutbound( _exchange1 );

        replay( clientIn, pkNOS1._cInKey );
        replay( exchangeOut, pkNOS1._mOutKey );
        replay( exchangeIn, pkNOS1._mInKey );
        replay( clientOut, pkNOS1._cOutKey );

        replay( exchangeIn, pkTrade1._mInKey );
        replay( clientOut, pkTrade1._cOutKey );

        replay( exchangeIn, pkTrade2._mInKey );

        replay( exchangeIn, pkTrade3._mInKey );

        _ctl.reconcile();

        assertEquals( 1, _orders.size() );

        Event upChain   = _ctl.getUpstreamChain();
        Event downChain = _ctl.getDownChain();

        assertNotNull( upChain );
        assertNull( downChain );

        OrderRequest clientReq = (OrderRequest) clientIn.regenerate( pkNOS1._cInKey );
        NewOrderAck  clientRep = (NewOrderAck) clientOut.regenerate( pkNOS1._cOutKey );

        TradeNew clientTradeNew2 = (TradeNew) upChain;
        checkTrade( clientTradeNew2, clientReq, clientRep, OrdStatus.PartiallyFilled, ExecType.Trade, fillQty2, cumQty2, fillPx2, avePx2 );

        TradeNew clientTradeNew3 = (TradeNew) upChain.getNextQueueEntry();
        checkTrade( clientTradeNew3, clientReq, clientRep, OrdStatus.Filled, ExecType.Trade, fillQty3, cumQty3, fillPx3, avePx3 );

        assertNull( clientTradeNew3.getNextQueueEntry() );
    }

    @Test
    public void testTwoFillsPartial() {

        setupNOSandACK( "C0000001A", qty1, px1, pkNOS1 );
        setupTrade( fillQty1, fillPx1, cumQty1, avePx1, "EX_TR_1", pkNOS1, pkTrade1, OrdStatus.PartiallyFilled );
        setupTrade( fillQty2, fillPx2, cumQty2, avePx2, "EX_TR_2", pkNOS1, pkTrade2, OrdStatus.PartiallyFilled );

        _orderMap.clear();

        // persister's are now populated, play persisted events into reconciler

        _ctl.start();

        DMARecoverySessionContext clientIn    = _ctl.startedInbound( _client1 );
        DMARecoverySessionContext clientOut   = _ctl.startedOutbound( _client1 );
        DMARecoverySessionContext exchangeIn  = _ctl.startedInbound( _exchange1 );
        DMARecoverySessionContext exchangeOut = _ctl.startedOutbound( _exchange1 );

        replay( clientIn, pkNOS1._cInKey );
        replay( exchangeOut, pkNOS1._mOutKey );
        replay( exchangeIn, pkNOS1._mInKey );
        replay( clientOut, pkNOS1._cOutKey );

        replay( exchangeIn, pkTrade1._mInKey );
        replay( clientOut, pkTrade1._cOutKey );

        replay( exchangeIn, pkTrade2._mInKey );
        replay( clientOut, pkTrade2._cOutKey );

        _ctl.reconcile();

        assertEquals( 1, _orders.size() );

        Event upChain   = _ctl.getUpstreamChain();
        Event downChain = _ctl.getDownChain();

        assertNotNull( upChain );
        assertNotNull( downChain );

        Cancelled    clientUnsolCancelled = (Cancelled) upChain;
        OrderRequest clientReq            = (OrderRequest) clientIn.regenerate( pkNOS1._cInKey );
        NewOrderAck  clientRep            = (NewOrderAck) clientOut.regenerate( pkNOS1._cOutKey );
        checkExec( clientUnsolCancelled, clientReq, clientRep, OrdStatus.Canceled, ExecType.Canceled, cumQty2, avePx2 );

        CancelRequest mktCanReq = (CancelRequest) downChain;
        NewOrderAck   mktAck    = (NewOrderAck) exchangeIn.regenerate( pkNOS1._mInKey );
        checkCancel( mktAck, mktCanReq );
    }












}
