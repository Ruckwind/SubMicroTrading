/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup.sim;

import com.rr.core.codec.BaseReject;
import com.rr.core.collections.EventQueue;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.*;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.factory.AllEventRecycler;
import com.rr.model.generated.internal.events.interfaces.CancelReplaceRequest;
import com.rr.model.generated.internal.events.interfaces.CancelRequest;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.model.generated.internal.type.CxlRejResponseTo;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.om.model.event.EventBuilder;
import com.rr.om.model.event.EventBuilderImpl;
import com.rr.om.warmup.ExchangeSim;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings( "NonAtomicOperationOnVolatileField" ) public final class WarmupExchangeSimAdapter implements EventHandler {

    static final ZString TAG_MSG_TYPE     = new ViewString( "35" );
    static final ZString TAG_CLORDID      = new ViewString( "11" );
    static final ZString TAG_ORIG_CLORDID = new ViewString( "41" );
    private static final Logger    _log            = ConsoleFactory.console( WarmupExchangeSimAdapter.class, Level.info );
    private static final ErrorCode ERR_HANDLE_FAIL = new ErrorCode( "WES100", "Exception processing event " );
    private static final ZString   FAILED_DECODE   = new ViewString( "(WES) Try to process failed decode event : " );
    private final String           _name          = "WarmExSimAd";
    private final AllEventRecycler _eventRecycler = new AllEventRecycler();

    private final EventQueue      _queue;
    private final EventDispatcher _simDispatcher;
    private final EventBuilder    _eventBuilder = new EventBuilderImpl(); // @ NOTE only use under handleNow thread SHARED ExchangeSim

    private final ExchangeSim    _exchangeSim;
    private final ReusableString _errMsg = new ReusableString( 100 );

    private volatile AtomicInteger _sent = new AtomicInteger();
    private volatile int           _requests;

    public WarmupExchangeSimAdapter( EventDispatcher dispatcher, int expectedOrders, EventQueue queue ) {
        _queue         = queue;
        _simDispatcher = dispatcher;

        _simDispatcher.setHandler( this );
        _simDispatcher.start();

        _eventBuilder.initPools();

        _exchangeSim = new ExchangeSim( expectedOrders, _eventBuilder );
    }

    @Override
    public String getComponentId() {
        return _name;
    }

    @Override
    public void handle( final Event msg ) {

        if ( msg.getEventHandler() == null ) {
            final ReusableString errMsg = TLC.instance().getString();
            errMsg.setValue( "Error msg has no source handler : " );
            msg.dump( errMsg );
            _log.info( errMsg );
            TLC.instance().recycle( errMsg );
        }
        _queue.add( msg );
    }

    @Override
    public void handleNow( final Event msg ) {

        Event        reply = null;
        EventHandler sess  = null;

        try {
            sess = msg.getEventHandler();

            switch( msg.getReusableType().getSubId() ) {
            case EventIds.ID_NEWORDERSINGLE:
                reply = handleNewOrderSingle( (NewOrderSingle) msg );
                // NOS used by reply msg - dont recycle
                break;
            case EventIds.ID_CANCELREPLACEREQUEST:
                reply = handleCancelReplaceRequest( (CancelReplaceRequest) msg );
                // amend used by reply msg - dont recyle
                break;
            case EventIds.ID_CANCELREQUEST:
                reply = handleCancelRequest( (CancelRequest) msg );
                _eventRecycler.recycle( msg );
                break;
            default:
                if ( msg instanceof BaseReject<?> ) {
                    reply = processDecodeFailure( (BaseReject<?>) msg );
                }
            }

        } catch( Exception e ) {

            _log.error( ERR_HANDLE_FAIL, e.getMessage(), e );
        }

        handleReply( msg, reply, sess );
    }

    @Override public boolean canHandle() { return true; }

    @Override public void threadedInit() {
        /* nothing */
    }

    public int getRequests() {
        return _requests;
    }

    public int getSent() {
        return _sent.get();
    }

    public void reset() {
        _sent.set( 0 );
    }

    public void setLogOrderInTS( boolean logOrderInTS ) {
        _exchangeSim.setLogOrderInTS( logOrderInTS );
    }

    public void stop() {
        _simDispatcher.setStopping();
    }

    private Event handleCancelReplaceRequest( final CancelReplaceRequest msg ) {
        ++_requests;
        return _exchangeSim.applyCancelReplaceRequest( msg );
    }

    private Event handleCancelRequest( final CancelRequest msg ) {
        ++_requests;
        return _exchangeSim.applyCancelRequest( msg );
    }

    private Event handleNewOrderSingle( final NewOrderSingle msg ) {
        ++_requests;
        return _exchangeSim.applyNewOrderSingle( msg );
    }

    private void handleReply( final Event msg, Event reply, final EventHandler sess ) {
        if ( reply != null ) {

            while( reply != null ) {
                final Event tmp = reply.getNextQueueEntry();

                reply.detachQueue();
                reply.setEventHandler( sess );
                _sent.incrementAndGet();

                if ( sess == null ) {
                    _log.warn( "WarmupExchangeSimAdapter: message has no messageHandler (should be src session)  type=" + msg.getClass().getSimpleName() );
                } else {
                    sess.handle( reply );
                }

                reply = tmp;
            }
        }
    }

    private Event processDecodeFailure( BaseReject<?> msg ) {

        _errMsg.copy( FAILED_DECODE );
        msg.dump( _errMsg );
        _log.warn( _errMsg );

        if ( msg.getNumFields() == 0 ) { // probably just a disconnect
            return null;
        }

        ZString type    = msg.getFixField( TAG_MSG_TYPE );
        byte[]  bType   = type.getBytes();
        byte    msgType = bType[ 0 ];

        Event reject;

        if ( msgType == 'D' ) {
            ViewString clOrdId = msg.getFixField( TAG_CLORDID );
            OrdStatus  status  = OrdStatus.Rejected;

            reject = _eventBuilder.getNOSReject( clOrdId, status, msg.getMessage(), msg );

        } else if ( msgType == 'F' ) {
            ViewString clOrdId     = msg.getFixField( TAG_CLORDID );
            ViewString origClOrdId = msg.getFixField( TAG_ORIG_CLORDID );
            OrdStatus  status      = OrdStatus.Rejected;

            reject = _eventBuilder.getCancelReject( clOrdId, origClOrdId, msg.getMessage(), CxlRejResponseTo.CancelRequest, status );

        } else if ( msgType == 'G' ) {
            ViewString clOrdId     = msg.getFixField( TAG_CLORDID );
            ViewString origClOrdId = msg.getFixField( TAG_ORIG_CLORDID );
            OrdStatus  status      = OrdStatus.Rejected;

            reject = _eventBuilder.getCancelReject( clOrdId, origClOrdId, msg.getMessage(), CxlRejResponseTo.CancelReplace, status );

        } else {
            reject = _eventBuilder.createSessionReject( msg.getMessage(), msg );
        }

        reject.setEventHandler( msg.getEventHandler() );

        return reject;
    }
}
