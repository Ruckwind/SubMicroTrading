/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.recovery;

import com.rr.core.model.Event;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.recovery.dma.DMARecoverySessionContext;
import com.rr.core.session.RecoverableSession;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;

public class DummyRecoveryController implements DMARecoveryController {

    private final AllEventRecycler _inboundRecycler  = new com.rr.model.generated.internal.events.factory.AllEventRecycler();
    private final AllEventRecycler _outboundRecycler = new com.rr.model.generated.internal.events.factory.AllEventRecycler();
    private final String           _id;

    public DummyRecoveryController() {
        this( "DummyRecoveryController" );
    }

    public DummyRecoveryController( String id ) {
        _id = id;
    }

    @Override
    public void commit() {
        // nothing
    }

    @Override
    public void completedInbound( DMARecoverySessionContext ctx ) {
        // nothing
    }

    @Override
    public void completedOutbound( DMARecoverySessionContext ctx ) {
        // nothing
    }

    @Override
    public void failedInbound( DMARecoverySessionContext ctx ) {
        // nothing
    }

    @Override
    public void failedOutbound( DMARecoverySessionContext ctx ) {
        // nothing
    }

    @Override
    public void processInbound( DMARecoverySessionContext ctx, long persistKey, Event msg, short persistFlags ) {
        synchronized( _inboundRecycler ) {
            _inboundRecycler.recycle( msg );
        }
    }

    @Override
    public void processOutbound( DMARecoverySessionContext ctx, long persistKey, Event msg, short persistFlags ) {
        synchronized( _outboundRecycler ) {
            _outboundRecycler.recycle( msg );
        }
    }

    @Override
    public void reconcile() {
        // nothing
    }

    @Override
    public void start() {
        // nothing
    }

    @Override
    public DMARecoverySessionContext startedInbound( RecoverableSession sess ) {
        return new RecoverySessionContextImpl( sess, true );
    }

    @Override
    public DMARecoverySessionContext startedOutbound( RecoverableSession sess ) {
        return new RecoverySessionContextImpl( sess, false );
    }

    @Override
    public String getComponentId() {
        return _id;
    }
}
