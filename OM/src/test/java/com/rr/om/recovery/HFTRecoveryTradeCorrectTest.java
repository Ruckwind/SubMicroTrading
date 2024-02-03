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

public class HFTRecoveryTradeCorrectTest extends BaseHFTRecoveryTst {

    private final int    qty1 = 100;
    private final double px1  = 25.25;
    private final int    fillQty1 = 10;
    private final double fillPx1  = 25.15;
    private final int    cumQty1 = fillQty1;
    private final double avePx1  = fillPx1;
    private PKeys pkNOS1   = new PKeys();
    private PKeys pkTrade1 = new PKeys();
    private PKeys pkTrade2 = new PKeys();

    @Test
    public void testCorrectFullFillToPartial() {

        setupNOSandACK( "C0000001A", qty1, px1, pkNOS1 );
        setupTrade( qty1, px1, qty1, px1, "EX_TR_1", pkNOS1, pkTrade1, OrdStatus.Filled );
        setupTradeCorrect( "EX_TR_1", fillQty1, fillPx1, cumQty1, avePx1, "EX_TR_2", pkNOS1, pkTrade2, OrdStatus.PartiallyFilled );

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
        checkExec( clientUnsolCancelled, clientReq, clientRep, OrdStatus.Canceled, ExecType.Canceled, cumQty1, avePx1 );

        CancelRequest mktCanReq = (CancelRequest) downChain;
        NewOrderAck   mktAck    = (NewOrderAck) exchangeIn.regenerate( pkNOS1._mInKey );
        checkCancel( mktAck, mktCanReq );
    }

    @Test
    public void testCorrectFullFillToPartialMissingClient() {

        setupNOSandACK( "C0000001A", qty1, px1, pkNOS1 );
        setupTrade( qty1, px1, qty1, px1, "EX_TR_1", pkNOS1, pkTrade1, OrdStatus.Filled );
        setupTradeCorrect( "EX_TR_1", fillQty1, fillPx1, cumQty1, avePx1, "EX_TR_2", pkNOS1, pkTrade2, OrdStatus.PartiallyFilled );

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
        // missing client correct

        _ctl.reconcile();

        assertEquals( 1, _orders.size() );

        Event upChain   = _ctl.getUpstreamChain();
        Event downChain = _ctl.getDownChain();

        assertNotNull( upChain );
        assertNotNull( downChain );

        OrderRequest clientReq = (OrderRequest) clientIn.regenerate( pkNOS1._cInKey );
        NewOrderAck  clientRep = (NewOrderAck) clientOut.regenerate( pkNOS1._cOutKey );

        _refExecId.copy( "EX_TR_1" );
        TradeCorrect clientTradeCorrect = (TradeCorrect) upChain;
        checkTrade( clientTradeCorrect, clientReq, clientRep, OrdStatus.PartiallyFilled, ExecType.TradeCorrect, fillQty1, cumQty1, fillPx1, avePx1 );
        assertEquals( _refExecId, clientTradeCorrect.getExecRefID() );

        Cancelled clientUnsolCancelled = (Cancelled) upChain.getNextQueueEntry();
        checkExec( clientUnsolCancelled, clientReq, clientRep, OrdStatus.Canceled, ExecType.Canceled, cumQty1, avePx1 );

        CancelRequest mktCanReq = (CancelRequest) downChain;
        NewOrderAck   mktAck    = (NewOrderAck) exchangeIn.regenerate( pkNOS1._mInKey );
        checkCancel( mktAck, mktCanReq );
    }

    @Test
    public void testCorrectPartialFillToFullFill() {

        setupNOSandACK( "C0000001A", qty1, px1, pkNOS1 );
        setupTrade( 9, 25.0125, 9, 25.0125, "EX_TR_1", pkNOS1, pkTrade1, OrdStatus.PartiallyFilled );
        setupTradeCorrect( "EX_TR_1", qty1, px1, qty1, px1, "EX_TR_2", pkNOS1, pkTrade2, OrdStatus.Filled );

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

        assertNull( upChain );
        assertNull( downChain );
    }

    @Test
    public void testCorrectPartialToFullFillMissingClient() {

        setupNOSandACK( "C0000001A", qty1, px1, pkNOS1 );
        setupTrade( fillQty1, fillPx1, cumQty1, avePx1, "EX_TR_1", pkNOS1, pkTrade1, OrdStatus.PartiallyFilled );
        setupTradeCorrect( "EX_TR_1", qty1, px1, qty1, px1, "EX_TR_2", pkNOS1, pkTrade2, OrdStatus.Filled );

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
        // missing client correct 

        _ctl.reconcile();

        assertEquals( 1, _orders.size() );

        Event upChain   = _ctl.getUpstreamChain();
        Event downChain = _ctl.getDownChain();

        assertNotNull( upChain );
        assertNull( downChain );

        OrderRequest clientReq = (OrderRequest) clientIn.regenerate( pkNOS1._cInKey );
        NewOrderAck  clientRep = (NewOrderAck) clientOut.regenerate( pkNOS1._cOutKey );

        _refExecId.copy( "EX_TR_1" );
        TradeCorrect clientTradeCorrect = (TradeCorrect) upChain;
        checkTrade( clientTradeCorrect, clientReq, clientRep, OrdStatus.Filled, ExecType.TradeCorrect, qty1, qty1, px1, px1 );
        assertEquals( _refExecId, clientTradeCorrect.getExecRefID() );

        assertNull( upChain.getNextQueueEntry() );
    }
}
