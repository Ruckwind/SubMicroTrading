/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup;

import com.rr.core.factories.ReusableStringFactory;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.Instrument;
import com.rr.core.pool.SuperpoolManager;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.factory.ClientCancelledFactory;
import com.rr.model.generated.internal.events.factory.ClientNewOrderAckFactory;
import com.rr.model.generated.internal.events.factory.ClientReplacedFactory;
import com.rr.model.generated.internal.events.impl.ClientCancelledImpl;
import com.rr.model.generated.internal.events.impl.ClientNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.ClientReplacedImpl;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.*;
import com.rr.model.internal.type.ExecType;
import com.rr.om.book.sim.SimpleOrderBook;
import com.rr.om.client.OMClientProfile;
import com.rr.om.model.event.EventBuilder;
import com.rr.om.order.Order;
import com.rr.om.order.OrderImpl;
import com.rr.om.order.OrderVersion;
import com.rr.om.order.collections.OrderMap;
import com.rr.om.order.collections.SegmentOrderMap;
import com.rr.om.processor.OrderFactory;
import com.rr.om.processor.OrderVersionFactory;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings( "MismatchedQueryAndUpdateOfCollection" ) public final class ExchangeSim {

    private static final Logger _log = LoggerFactory.create( ExchangeSim.class );

    private static final ZString EXECID_PREFIX = new ViewString( "90" );
    private static final ZString DUP_ORDER     = new ViewString( "Duplicate order request" );
    private static final String  DUP_ORDER_STR = DUP_ORDER.toString();
    private static final ZString UNKNOWN_ORDER = new ViewString( "Original order not found" );
    private static final ZString ORDID_BASE    = new ViewString( "999" );

    private final OrderMap                         _orderMap;
    private final Map<ZString, Order>              _mktOrderIdMap;
    private final EventBuilder _eventBuilder; // @NOTE only use in apply method to avoid threading issues
    private final ReusableString _logMsg   = new ReusableString();
    private       Map<Instrument, SimpleOrderBook> _orderBookMap = new HashMap<>( 1024 );
    private ReusableStringFactory _reusableStringFactory;
    private OrderFactory _orderFactory;
    private OrderVersionFactory _versionFactory;
    private ClientNewOrderAckFactory _clientNewOrderAckFactory;
    private ClientCancelledFactory   _clientCancelledFactory;
    private ClientReplacedFactory    _clientReplacedFactory;
    private       int            _execId   = 1000000;
    private       ReusableString _orderId  = new ReusableString();
    private       int            _ordIdIdx = 1000000;
    private       boolean        _logOrderInTS;

    public ExchangeSim( int expectedOrders, EventBuilder eventBuilder ) {
        SuperpoolManager sp = SuperpoolManager.instance();

        _eventBuilder = eventBuilder;

        _orderMap      = new SegmentOrderMap( expectedOrders, 0.75f, 1 );
        _mktOrderIdMap = new HashMap<>( expectedOrders );

        _clientNewOrderAckFactory = sp.getFactory( ClientNewOrderAckFactory.class, ClientNewOrderAckImpl.class );
        _clientCancelledFactory   = sp.getFactory( ClientCancelledFactory.class, ClientCancelledImpl.class );
        _clientReplacedFactory    = sp.getFactory( ClientReplacedFactory.class, ClientReplacedImpl.class );

        _reusableStringFactory = sp.getFactory( ReusableStringFactory.class, ReusableString.class );
        _orderFactory          = sp.getFactory( OrderFactory.class, OrderImpl.class );
        _versionFactory        = sp.getFactory( OrderVersionFactory.class, OrderVersion.class );
    }

    public Event applyCancelReplaceRequest( CancelReplaceRequest msg ) {

        Order order = _orderMap.get( msg.getClOrdId() );

        Event reply;

        if ( order == null ) {
            order = _orderMap.get( msg.getOrigClOrdId() );

            if ( order != null ) {
                final OrderVersion version = order.getLastAckedVerion();

                if ( version.getOrdStatus().getIsTerminal() ) {

                    reply = _eventBuilder.getCancelReject( msg.getClOrdId(),
                                                           msg.getOrigClOrdId(),
                                                           version.getMarketOrderId(),
                                                           DUP_ORDER,
                                                           CxlRejReason.Other,
                                                           CxlRejResponseTo.CancelReplace,
                                                           version.getOrdStatus() );
                } else {
                    final ClientReplacedImpl crpd = _clientReplacedFactory.get();

                    double origPx  = version.getMarketPrice();
                    double origQty = version.getOrderQty();

                    enrichVersion( msg, version );
                    version.setOrdStatus( OrdStatus.Replaced );

                    ((ClientCancelReplaceRequestWrite) msg).setInstrument( version.getBaseOrderRequest().getInstrument() );

                    TradeNew trades = amendBook( version.getMarketOrderId(),
                                                 msg.getInstrument(),
                                                 msg.getOrderQty(),
                                                 msg.getPrice(),
                                                 version.getCumQty(),
                                                 origQty,
                                                 origPx,
                                                 msg.getOrdType(),
                                                 msg.getSide() );

                    enrich( order, msg.getClOrdId(), trades );

                    version.setOrdStatus( calcOrdStatus( order ) );

                    crpd.attachQueue( trades );

                    crpd.setSrcEvent( msg );
                    crpd.setAvgPx( version.getAvgPx() );
                    crpd.setCumQty( version.getCumQty() );
                    crpd.setLeavesQty( version.getLeavesQty() );
                    crpd.setOrdStatus( version.getOrdStatus() );

                    crpd.setEventHandler( msg.getEventHandler() );
                    crpd.getOrderIdForUpdate().setValue( version.getMarketOrderId() );
                    crpd.setMktCapacity( msg.getOrderCapacity() );

                    crpd.setExecType( ExecType.Replaced );

                    final ReusableString synthAckExecId = crpd.getExecIdForUpdate();
                    synthAckExecId.setValue( EXECID_PREFIX );
                    synthAckExecId.append( msg.getClOrdId() ).append( ++_execId );

                    reply = crpd;

                    _orderMap.put( _reusableStringFactory.get().copy( msg.getClOrdId() ), order );
                    _mktOrderIdMap.put( _reusableStringFactory.get().copy( crpd.getOrderId() ), order );
                }
            } else {
                final OrdStatus status = OrdStatus.Rejected;

                reply = _eventBuilder.getCancelReject( msg.getClOrdId(),
                                                       msg.getOrigClOrdId(),
                                                       null,
                                                       UNKNOWN_ORDER,
                                                       CxlRejReason.UnknownOrder,
                                                       CxlRejResponseTo.CancelReplace,
                                                       status );
            }

        } else {
            final OrdStatus status = OrdStatus.Rejected;

            reply = _eventBuilder.getCancelReject( msg.getClOrdId(),
                                                   msg.getOrigClOrdId(),
                                                   order.getLastAckedVerion().getMarketOrderId(),
                                                   DUP_ORDER,
                                                   CxlRejReason.DuplicateClOrdId,
                                                   CxlRejResponseTo.CancelReplace,
                                                   status );
        }

        return reply;
    }

    public Event applyCancelRequest( final CancelRequest msg ) {
        Order order = _orderMap.get( msg.getClOrdId() );

        Event reply;

        if ( order == null ) {
            order = _orderMap.get( msg.getOrigClOrdId() );

            if ( order != null ) {
                final OrderVersion version = order.getLastAckedVerion();

                if ( version.getOrdStatus().getIsTerminal() ) {

                    reply = _eventBuilder.getCancelReject( msg.getClOrdId(),
                                                           msg.getOrigClOrdId(),
                                                           version.getMarketOrderId(),
                                                           DUP_ORDER,
                                                           CxlRejReason.Other,
                                                           CxlRejResponseTo.CancelRequest,
                                                           version.getOrdStatus() );
                } else {
                    final ClientCancelledImpl ccld = _clientCancelledFactory.get();

                    double origPx = version.getMarketPrice();

                    OrderRequest nosAmendReq = (OrderRequest) version.getBaseOrderRequest();

                    ccld.getClOrdIdForUpdate().setValue( msg.getClOrdId() );
                    ccld.getOrderIdForUpdate().setValue( version.getMarketOrderId() );
                    ccld.getOrigClOrdIdForUpdate().setValue( msg.getClOrdId() );

                    SimpleOrderBook book = getBook( nosAmendReq.getInstrument() );

                    book.remove( version.getMarketOrderId(), version.getLeavesQty(), origPx, nosAmendReq.getOrdType(), nosAmendReq.getSide() );

                    ccld.setSrcEvent( nosAmendReq );
                    ccld.setAvgPx( version.getAvgPx() );
                    ccld.setCumQty( version.getCumQty() );
                    ccld.setLeavesQty( version.getLeavesQty() );

                    ccld.setEventHandler( msg.getEventHandler() );
                    ccld.getOrderIdForUpdate().setValue( version.getMarketOrderId() );
                    ccld.setMktCapacity( nosAmendReq.getOrderCapacity() );

                    ccld.setExecType( ExecType.Canceled );

                    final ReusableString synthAckExecId = ccld.getExecIdForUpdate();
                    synthAckExecId.setValue( EXECID_PREFIX );
                    synthAckExecId.append( msg.getClOrdId() ).append( ++_execId );

                    version.setOrdStatus( OrdStatus.Canceled );
                    ccld.setOrdStatus( OrdStatus.Canceled );

                    reply = ccld;

                    _orderMap.put( _reusableStringFactory.get().copy( msg.getClOrdId() ), order );
                    _mktOrderIdMap.put( _reusableStringFactory.get().copy( ccld.getOrderId() ), order );
                }
            } else {
                final OrdStatus status = OrdStatus.Rejected;

                reply = _eventBuilder.getCancelReject( msg.getClOrdId(),
                                                       msg.getOrigClOrdId(),
                                                       null,
                                                       UNKNOWN_ORDER,
                                                       CxlRejReason.UnknownOrder,
                                                       CxlRejResponseTo.CancelRequest,
                                                       status );
            }

        } else {
            final OrdStatus    status  = OrdStatus.Rejected;
            final OrderVersion version = order.getLastAckedVerion();

            reply = _eventBuilder.getCancelReject( msg.getClOrdId(),
                                                   msg.getOrigClOrdId(),
                                                   version.getMarketOrderId(),
                                                   DUP_ORDER,
                                                   CxlRejReason.DuplicateClOrdId,
                                                   CxlRejResponseTo.CancelRequest,
                                                   status );
        }

        return reply;
    }

    public final Event applyNewOrderSingle( final NewOrderSingle src ) {

        final ViewString clOrdId = src.getClOrdId();

        if ( _orderMap.containsKey( clOrdId ) == false ) {
            return processNewOrder( src );
        }

        return makeNOSReject( clOrdId );
    }

    public Event handle( final Event msg ) {
        Event reply = null;

        switch( msg.getReusableType().getSubId() ) {
        case EventIds.ID_NEWORDERSINGLE:
            reply = handleNewOrderSingle( (NewOrderSingle) msg );
            break;
        case EventIds.ID_CANCELREPLACEREQUEST:
            reply = handleCancelReplaceRequest( (CancelReplaceRequest) msg );
            break;
        case EventIds.ID_CANCELREQUEST:
            reply = handleCancelRequest( (CancelRequest) msg );
            break;
        default:
            break;
        }

        return reply;
    }

    public boolean isLogOrderInTS() {
        return _logOrderInTS;
    }

    public void setLogOrderInTS( boolean logOrderInTS ) {
        _logOrderInTS = logOrderInTS;
    }

    private TradeNew addToBook( final NewOrderSingle src, final Order order ) {

        final ZString    mktOrdId   = order.getLastAckedVerion().getMarketOrderId();
        final Instrument instrument = src.getInstrument();
        final double     orderQty   = src.getOrderQty();
        final double     price      = src.getPrice();
        final OrdType    ordType    = src.getOrdType();
        final Side       side       = src.getSide();

        final SimpleOrderBook book = getBook( instrument );

        return book.add( mktOrdId, orderQty, price, ordType, side );
    }

    private TradeNew amendBook( final ZString marketOrderId,
                                final Instrument instrument,
                                final double newQty,
                                final double newPrice,
                                final double cumQty,
                                final double origQty,
                                final double origPrice,
                                final OrdType ordType,
                                final Side side ) {

        final SimpleOrderBook book = getBook( instrument );

        return book.amend( marketOrderId, newQty, origQty, cumQty, newPrice, origPrice, ordType, side );
    }

    private OrdStatus calcOrdStatus( final Order order ) {
        final OrderVersion v = order.getLastAckedVerion();

        if ( v.getCumQty() == 0 ) return OrdStatus.New;
        if ( v.getLeavesQty() == 0 ) return OrdStatus.Filled;

        return OrdStatus.PartiallyFilled;
    }

    private OrderImpl createOrder( final NewOrderSingle msg ) {
        final OMClientProfile clientProf = (OMClientProfile) msg.getClient();
        final OrderImpl       order      = mkOrder( msg, clientProf );

        final ViewString clOrdId = msg.getClOrdId();

        registerOrderInMap( order, clOrdId );

        if ( _logOrderInTS ) {
            _logMsg.reset();
            _logMsg.append( "NOS_clOrdId " ).append( clOrdId ).append( ' ' ).append( (msg.getOrderReceived() >> 10) );
            _log.info( _logMsg );
        }

        return order;
    }

    private void enrich( final Order order, final ZString clOrdId, final TradeNew trades ) {

        if ( trades == null ) return;

        TradeNew curTrade = trades;

        final OrderVersion ver = order.getLastAckedVerion();

        double cumQty = ver.getCumQty();
        double total  = ver.getAvgPx() * cumQty;
        double avPx   = total / cumQty;
        double ordQty = ver.getOrderQty();
        double leavesQty;

        while( curTrade != null ) {
            MarketTradeNewWrite trade = (MarketTradeNewWrite) curTrade;

            trade.getClOrdIdForUpdate().copy( clOrdId );

            double lastQty = trade.getLastQty();
            double lastPx  = trade.getLastPx();

            total += (lastPx * lastQty);
            cumQty += lastQty;
            avPx = total / cumQty;

            leavesQty = ver.getOrderQty() - cumQty;
            if ( leavesQty <= Constants.TICK_WEIGHT ) leavesQty = 0;

            trade.setAvgPx( avPx );
            trade.setCumQty( cumQty );
            trade.setLeavesQty( leavesQty );

            curTrade = (TradeNew) curTrade.getNextQueueEntry();
        }

        ver.setAvgPx( avPx );
        ver.setCumQty( cumQty );
    }

    private void enrichVersion( OrderRequest msg, OrderVersion ver ) {
        double price = msg.getPrice();

        ver.setBaseOrderRequest( msg );
        ver.setOrderQty( msg.getOrderQty() );
        ver.setMarketPrice( price );
    }

    private SimpleOrderBook getBook( final Instrument instrument ) {
        SimpleOrderBook book = _orderBookMap.get( instrument );

        if ( book == null ) {
            book = new SimpleOrderBook();

            _orderBookMap.put( instrument, book );
        }

        return book;
    }

    private Event handleCancelReplaceRequest( final CancelReplaceRequest msg ) {
        return applyCancelReplaceRequest( msg );
    }

    private Event handleCancelRequest( final CancelRequest msg ) {
        return applyCancelRequest( msg );
    }

    private Event handleNewOrderSingle( final NewOrderSingle msg ) {
        Event replies = applyNewOrderSingle( msg );

        Event tmp = replies;

        while( tmp != null ) {
            tmp = tmp.getNextQueueEntry();

            if ( tmp instanceof CommonExecRptWrite ) {
                CommonExecRptWrite c = (CommonExecRptWrite) tmp;

                c.getParentClOrdIdForUpdate().copy( msg.getParentClOrdId() );
            }
        }

        return replies;
    }

    private Event makeNOSReject( final ViewString clOrdId ) {
        return _eventBuilder.getNOSReject( clOrdId, OrdStatus.Rejected, DUP_ORDER_STR, null );
    }

    private ClientNewOrderAckImpl makeReplyNewAck( final NewOrderSingle src, Order order, final TradeNew trades, final OrderVersion version ) {
        final ClientNewOrderAckImpl cack = _clientNewOrderAckFactory.get();
        cack.attachQueue( trades );

        cack.setSrcEvent( src );
        cack.setAvgPx( version.getAvgPx() );
        cack.setCumQty( version.getCumQty() );
        cack.setLeavesQty( version.getLeavesQty() );
        cack.setOrdStatus( version.getOrdStatus() );

        cack.setEventHandler( src.getEventHandler() );
        cack.getOrderIdForUpdate().setValue( version.getMarketOrderId() );
        cack.setMktCapacity( src.getOrderCapacity() );

        cack.setExecType( ExecType.New );

        src.setOrderSent( src.getOrderReceived() );
        cack.setAckReceived( src.getOrderReceived() );

        final ReusableString synthAckExecId = cack.getExecIdForUpdate();
        synthAckExecId.setValue( EXECID_PREFIX );
        synthAckExecId.append( src.getClOrdId() ).append( ++_execId );
        return cack;
    }

    private OrderImpl mkOrder( final NewOrderSingle msg, final OMClientProfile clientProf ) {
        final OrderImpl    order = _orderFactory.get();
        final OrderVersion ver   = _versionFactory.get();

        ver.setMarketOrderId( nextOrderId() );
        enrichVersion( msg, ver );
        ver.setOrdStatus( OrdStatus.New );

        order.setLastAckedVerion( ver );
        order.setPendingVersion( ver );

        order.setClientProfile( clientProf );
        return order;
    }

    private ZString nextOrderId() {
        _orderId.copy( ORDID_BASE ).append( ++_ordIdIdx );

        return _orderId;
    }

    private ClientNewOrderAckImpl processNewOrder( final NewOrderSingle src ) {
        final OrderImpl order  = createOrder( src );
        final TradeNew  trades = addToBook( src, order );

        return registerVersionAndAck( src, order, trades );
    }

    private void registerAckOrderId( Order order, final ClientNewOrderAckImpl cack ) {
        final ReusableString copyOrderId = _reusableStringFactory.get();
        copyOrderId.copy( cack.getOrderId() );
        _mktOrderIdMap.put( copyOrderId, order );
    }

    private void registerOrderInMap( final OrderImpl order, final ViewString clOrdId ) {
        final ReusableString copyId = _reusableStringFactory.get();
        copyId.copy( clOrdId );
        _orderMap.put( copyId, order );
    }

    private OrderVersion registerOrderVersion( final NewOrderSingle src, final Order order ) {
        final OrderVersion version = order.getLastAckedVerion();
        version.setBaseOrderRequest( src );
        version.setOrdStatus( calcOrdStatus( order ) );
        return version;
    }

    private ClientNewOrderAckImpl registerVersionAndAck( final NewOrderSingle src, final OrderImpl order, final TradeNew trades ) {
        final ViewString clOrdId = src.getClOrdId();

        enrich( order, clOrdId, trades );

        final OrderVersion          version = registerOrderVersion( src, order );
        final ClientNewOrderAckImpl cack    = makeReplyNewAck( src, order, trades, version );

        registerAckOrderId( order, cack );

        return cack;
    }
}
