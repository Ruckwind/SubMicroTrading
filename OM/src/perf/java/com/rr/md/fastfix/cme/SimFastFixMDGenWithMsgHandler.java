/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.cme;

import com.rr.core.model.Event;
import com.rr.om.warmup.sim.SimCMEFastFixSender;

import java.util.List;

/**
 * a specialisation of SimCMEFastFixSender but doesnt send generated messages over socket
 */
public final class SimFastFixMDGenWithMsgHandler extends SimCMEFastFixSender {

    public interface Listener {

        void onEvent( Event msg, int templateId );
    }
    private final Listener _listener;

    public SimFastFixMDGenWithMsgHandler( List<byte[]> templateRequests, boolean nanoTiming, Listener l ) {
        super( templateRequests, nanoTiming, null );

        _listener = l;
    }

    @Override
    protected void doSend( int templateId, Event msg, long now ) {
        _listener.onEvent( msg, templateId );
    }
}
