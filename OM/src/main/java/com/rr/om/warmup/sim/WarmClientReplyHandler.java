/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup.sim;

import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.session.SessionStatusEvent;
import com.rr.core.utils.Utils;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.impl.HeartbeatImpl;
import com.rr.model.generated.internal.events.interfaces.CancelReject;
import com.rr.model.generated.internal.events.interfaces.CommonExecRpt;
import com.rr.model.generated.internal.events.interfaces.NewOrderAck;
import com.rr.model.generated.internal.events.interfaces.Rejected;
import com.rr.om.dummy.warmup.ClientStatsManager;

import java.util.concurrent.atomic.AtomicInteger;

public class WarmClientReplyHandler implements EventHandler {

    private static final Logger _log = LoggerFactory.create( WarmClientReplyHandler.class );

    private final String _name = "WarmClientReply";

    private AllEventRecycler   _eventRecycler    = new AllEventRecycler();
    private ClientStatsManager _statsMgr;
    private AtomicInteger      _received         = new AtomicInteger( 0 );
    private AtomicInteger      _statBasedReplies = new AtomicInteger( 0 );

    public WarmClientReplyHandler( ClientStatsManager statsMgr ) {
        _statsMgr = statsMgr;
    }

    @Override
    public String getComponentId() {
        return _name;
    }

    @Override
    public void handle( Event msg ) {
        handleNow( msg );
    }

    @Override
    public void handleNow( Event msg ) {
        boolean statsAdded = false;

        switch( msg.getReusableType().getSubId() ) {
        case EventIds.ID_NEWORDERACK:
            NewOrderAck ack = (NewOrderAck) msg;
            if ( _statsMgr.replyRecieved( ack.getClOrdId(), ack.getAckReceived() ) ) statsAdded = true;
            else {
                _log.info( "WarmClientReplyHandler: Stats cant find entry for exec " +
                           msg.getReusableType().toString() + ", clOrdId=" + ack.getClOrdId() + ", received=" + _received );
            }
            break;
        case EventIds.ID_REPLACED:
        case EventIds.ID_CANCELLED:
            CommonExecRpt exec = (CommonExecRpt) msg;
            if ( _statsMgr.replyRecieved( exec.getClOrdId(), Utils.nanoTime() ) ) statsAdded = true;
            else {
                _log.info( "WarmClientReplyHandler: Stats cant find entry for exec " +
                           msg.getReusableType().toString() + ", clOrdId=" + exec.getClOrdId() + ", received=" + _received );
            }
            break;
        case EventIds.ID_REJECTED:
            Rejected rej = (Rejected) msg;
            if ( _statsMgr.replyRecieved( rej.getClOrdId(), Utils.nanoTime() ) ) statsAdded = true;
            else {
                // in non blocking sessions, if downstream session is disconnected the order will be rejected and the
                // sent callback is NOT invoked so the order will not be registered in the statsMgr

                _log.info( "WarmClientReplyHandler: Stats cant find entry for exec " +
                           msg.getReusableType().toString() + ", clOrdId=" + rej.getClOrdId() + " : " + rej.getText() + ", received=" + _received );
            }
            break;
        case EventIds.ID_CANCELREJECT:
            CancelReject crej = (CancelReject) msg;
            if ( _statsMgr.replyRecieved( crej.getClOrdId(), Utils.nanoTime() ) ) statsAdded = true;
            else {
                _log.info( "WarmClientReplyHandler: Stats cant find entry for exec " +
                           msg.getReusableType().toString() + ", clOrdId=" + crej.getClOrdId() + ", received=" + _received );
            }
            break;
        case EventIds.ID_TRADENEW:
        case EventIds.ID_HEARTBEAT:
            break;
        default:
            if ( msg.getClass() != SessionStatusEvent.class ) {
                _log.info( "WarmClientReplyHandler: Unexpected reply " + msg.getReusableType().toString() + ", received=" + _received );
            }
            break;
        }

        if ( statsAdded ) {
            if ( msg.getClass() != SessionStatusEvent.class && msg.getClass() != HeartbeatImpl.class ) {
                _statBasedReplies.incrementAndGet();
                _received.incrementAndGet();
            }
        }

        _eventRecycler.recycle( msg );
    }

    @Override public boolean canHandle() { return true; }

    @Override public void threadedInit() { /* nothing */ }

    public synchronized int getReceived() {
        return _received.get();
    }

    public synchronized int getStatsBasedReplies() {
        return _statBasedReplies.get();
    }

    public synchronized void reset() {
        _statBasedReplies.set( 0 );
        _received.set( 0 );
    }
}

