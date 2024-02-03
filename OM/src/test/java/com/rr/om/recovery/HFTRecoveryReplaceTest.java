/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.recovery;

import com.rr.core.model.Event;
import com.rr.core.recovery.dma.DMARecoverySessionContext;
import com.rr.model.generated.internal.events.interfaces.CancelReplaceRequest;
import com.rr.model.generated.internal.events.interfaces.CancelRequest;
import com.rr.model.generated.internal.events.interfaces.Cancelled;
import com.rr.model.generated.internal.events.interfaces.Replaced;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.model.internal.type.ExecType;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HFTRecoveryReplaceTest extends BaseHFTRecoveryTst {

    private PKeys pkNOS1 = new PKeys();
    private PKeys pkREP1 = new PKeys();

    @Test
    public void testMktAckNoLinkId() {
        /*
         * mktAckIn (NO LINK ID !!) mktReplaced mktCancelReplaceReject mktNosOut mktCanReplaceReqOut mktCanReplaceReqOut clientNOSIn clientCanReplaceReqIn
         * clientCanReplaceReqIn
         */
    }

    /**
     * HFT REPLACE BROKEN WITH REMOVAL OF srcLinkId  as its replacement parentClOrdId isnt applicable for same use
     * and the specific srcLinkId codec logic is also removed !
     */
    @Ignore
    @Test
    public void testReplacedInOrder() {

        setupReplacedOrder( "C0000001A", "C0000001B", 100, 25.25, 90, 26.75, pkNOS1, pkREP1 );

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

        replay( clientIn, pkREP1._cInKey );
        replay( exchangeOut, pkREP1._mOutKey );
        replay( exchangeIn, pkREP1._mInKey );
        replay( clientOut, pkREP1._cOutKey );

        _ctl.reconcile();

        assertEquals( 2, _orders.size() );

        Event upChain   = _ctl.getUpstreamChain();
        Event downChain = _ctl.getDownChain();

        assertNotNull( upChain );
        assertNotNull( downChain );

        Cancelled            clientUnsolCancelled = (Cancelled) upChain;
        CancelReplaceRequest clientReq            = (CancelReplaceRequest) clientIn.regenerate( pkREP1._cInKey );
        Replaced             clientRep            = (Replaced) clientOut.regenerate( pkREP1._cOutKey );
        checkExec( clientUnsolCancelled, clientReq, clientRep, OrdStatus.Canceled, ExecType.Canceled );

        CancelRequest mktCanReq = (CancelRequest) downChain;
        Replaced      mktRep    = (Replaced) exchangeIn.regenerate( pkREP1._mInKey );
        checkCancel( mktRep, mktCanReq );
    }

    /**
     * HFT REPLACE BROKEN WITH REMOVAL OF srcLinkId  as its replacement parentClOrdId isnt applicable for same use
     * and the specific srcLinkId codec logic is also removed !
     */
    @Ignore
    @Test
    public void testReplacedReplayOutOfOrder() {

        setupReplacedOrder( "C0000001A", "C0000001B", 100, 25.25, 90, 26.75, pkNOS1, pkREP1 );

        _orderMap.clear();

        // persister's are now populated, play persisted events into reconciler

        _ctl.start();
        DMARecoverySessionContext clientIn    = _ctl.startedInbound( _client1 );
        DMARecoverySessionContext clientOut   = _ctl.startedOutbound( _client1 );
        DMARecoverySessionContext exchangeIn  = _ctl.startedInbound( _exchange1 );
        DMARecoverySessionContext exchangeOut = _ctl.startedOutbound( _exchange1 );

        // only guarantee on replay is events within stream are in order
        replay( exchangeIn, pkNOS1._mInKey );
        replay( exchangeIn, pkREP1._mInKey );

        replay( clientOut, pkNOS1._cOutKey );
        replay( clientOut, pkREP1._cOutKey );

        replay( exchangeOut, pkNOS1._mOutKey );
        replay( exchangeOut, pkREP1._mOutKey );

        replay( clientIn, pkNOS1._cInKey );
        replay( clientIn, pkREP1._cInKey );

        _ctl.reconcile();

        assertEquals( 2, _orders.size() );

        Event upChain   = _ctl.getUpstreamChain();
        Event downChain = _ctl.getDownChain();

        assertNotNull( upChain );
        assertNotNull( downChain );

        Cancelled            clientUnsolCancelled = (Cancelled) upChain;
        CancelReplaceRequest clientReq            = (CancelReplaceRequest) clientIn.regenerate( pkREP1._cInKey );
        Replaced             clientRep            = (Replaced) clientOut.regenerate( pkREP1._cOutKey );
        checkExec( clientUnsolCancelled, clientReq, clientRep, OrdStatus.Canceled, ExecType.Canceled );

        CancelRequest mktCanReq = (CancelRequest) downChain;
        Replaced      mktRep    = (Replaced) exchangeIn.regenerate( pkREP1._mInKey );
        checkCancel( mktRep, mktCanReq );
    }

    // simulate a client NOS, generate the NOS then insert into indexed inbound persister
    // _clientOut.getMissedEvents - not sent to client or HUB
    // _clientOut.getMissedHubEvents - went to client but not marked as sent to HUB
}
