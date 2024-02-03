/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.recovery;

import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.recovery.dma.DMARecoveryController;
import com.rr.core.recovery.dma.DMARecoverySessionContext;
import com.rr.core.session.RecoverableSession;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * A standard OM recovery controller
 * <p>
 * Each session has two event streams one for inbound events, one for outbound events
 * Within a stream each event is guarenteed to be in time order (ie order received / sent)
 * <p>
 * Each session replays its inbound and outbound streams from persistence into the controller concurrently
 * Each session is replayed into the controller concurrently
 * <p>
 * Standard OM controller behaviour
 * <p>
 * 1) open orders remain open, ie client orders not auto cancelled
 * 2) orders received but not sent to exchange will be cancel rejected back to client
 * 3) fills from exchange not sent to client will be sent to client
 * 4) events received but not sent to hub are sent to hub
 * 5) events sent but not sent to hub are sent to hub
 * 6) processors order map and trade registry should look as if the processes hadnt been restarted
 * 7) and pending orders force cancelled (downstream and upstream - up requires reject then cancelled)
 * this is to avoid edge case conditions
 */

@SuppressWarnings( "MismatchedQueryAndUpdateOfCollection" ) public class PassiveDMARecoveryController implements DMARecoveryController {

    private static final Logger _log = LoggerFactory.create( PassiveDMARecoveryController.class );

    private static final ErrorCode ERR_INBOUND_REPLY  = new ErrorCode( "REC100", "FAILED replay of inbound messages" );
    private static final ErrorCode ERR_OUTBOUND_REPLY = new ErrorCode( "REC110", "FAILED replay of outbound messages" );

    private final AllEventRecycler _inboundRecycler  = new com.rr.model.generated.internal.events.factory.AllEventRecycler();
    private final AllEventRecycler _outboundRecycler = new com.rr.model.generated.internal.events.factory.AllEventRecycler();

    private final ConcurrentHashMap<RecoverableSession, DMARecoverySessionContext> _inSessCtx  = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<RecoverableSession, DMARecoverySessionContext> _outSessCtx = new ConcurrentHashMap<>();

    private final String _id;

    public PassiveDMARecoveryController() {
        this( "PassiveDMARecoveryController" );
    }

    public PassiveDMARecoveryController( String id ) {
        _id = id;
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override
    public void start() {
        _log.info( "Starting recovery process" );
    }

    @Override
    public DMARecoverySessionContext startedInbound( RecoverableSession sess ) {
        RecoverySessionContextImpl ctx = new RecoverySessionContextImpl( sess, true );
        _inSessCtx.put( sess, ctx );
        return ctx;
    }

    @Override
    public void completedInbound( DMARecoverySessionContext ctx ) {
        _log.info( "Completed replay of inbound messages from " + ctx.getSession().getComponentId() );
    }

    @Override
    public void failedInbound( DMARecoverySessionContext ctx ) {
        _log.error( ERR_INBOUND_REPLY, "from " + ctx.getSession().getComponentId() );
    }

    @Override
    public void processInbound( DMARecoverySessionContext ctx, long persistKey, Event msg, short persistFlags ) {
        synchronized( _inboundRecycler ) {
            while( msg != null ) {
                _inboundRecycler.recycle( msg );
                msg = msg.getNextQueueEntry();
            }
        }
    }

    @Override
    public DMARecoverySessionContext startedOutbound( RecoverableSession sess ) {
        RecoverySessionContextImpl ctx = new RecoverySessionContextImpl( sess, true );
        _outSessCtx.put( sess, ctx );
        return ctx;
    }

    @Override
    public void completedOutbound( DMARecoverySessionContext ctx ) {
        _log.info( "Completed replay of outbound messages from " + ctx.getSession().getComponentId() );
    }

    @Override
    public void failedOutbound( DMARecoverySessionContext ctx ) {
        _log.error( ERR_OUTBOUND_REPLY, "from " + ctx.getSession().getComponentId() );
    }

    @Override
    public void processOutbound( DMARecoverySessionContext ctx, long persistKey, Event msg, short persistFlags ) {
        synchronized( _outboundRecycler ) {
            while( msg != null ) {
                _outboundRecycler.recycle( msg );
                msg = msg.getNextQueueEntry();
            }
        }
    }

    @Override
    public void reconcile() {
        throw new RuntimeException( "NOT YET IMPLEMENTED" );
    }

    @Override
    public void commit() {
        throw new RuntimeException( "NOT YET IMPLEMENTED" );
    }
}
