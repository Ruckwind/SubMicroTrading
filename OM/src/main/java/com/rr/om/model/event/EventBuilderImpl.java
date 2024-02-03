/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.model.event;

import com.rr.core.codec.BaseReject;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.*;
import com.rr.core.pool.SuperpoolManager;
import com.rr.model.generated.internal.events.factory.*;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.events.interfaces.*;
import com.rr.model.generated.internal.type.*;
import com.rr.model.internal.type.ExecType;
import com.rr.om.client.OMEnricher;
import com.rr.om.model.fix.FixTags;
import com.rr.om.order.Order;
import com.rr.om.order.OrderVersion;
import com.rr.om.registry.TradeWrapper;

/**
 * responsible for managing event pools and the population of events
 * this includes copying fields from previous event version
 *
 * @author Richard Rose
 */
public class EventBuilderImpl implements EventBuilder {

    public static final ZString NONE                    = new ViewString( "NON" );
    public static final ZString ACK_EXECID_PREFIX       = new ViewString( "ACK" );
    public static final ZString REJ_EXECID_PREFIX       = new ViewString( "REJ" );
    public static final ZString FORCE_CANCEL_PREFIX     = new ViewString( "FCL" );
    public static final ZString CANCELLED_EXECID_PREFIX = new ViewString( "CAN" );
    public static final ZString REPLACED_EXECID_PREFIX  = new ViewString( "REP" );
    public static final ZString SYNTH_REJ_PREFIX        = new ViewString( "SRJ" );
    private static final Logger _log = LoggerFactory.create( EventBuilderImpl.class );
    private static final ZString TAG_SYMBOL      = new ViewString( "" + FixTags.Symbol );
    private static final ZString TAG_SIDE        = new ViewString( "" + FixTags.Side );
    private static final ZString TAG_SECURITY_ID = new ViewString( "" + FixTags.SecurityID );
    private static final ZString TAG_IDSource    = new ViewString( "" + FixTags.IDSource );

    private static final ZString MISSING_CLORDID = new ViewString( "Missing clOrdId, unable to generate exec report reject" );
    private static final ZString NOT_ENOUGH_INFO = new ViewString( "Missing side or symbol, unable to generate exec report reject" );

    private ClientCancelRejectFactory         _cancelRejectFactory;
    private ClientRejectedFactory             _rejectedFactory;
    private MarketNewOrderSingleFactory       _marketNOSFactory;
    private ClientNewOrderAckFactory          _clientNewOrderAckFactory;
    private MarketCancelRequestFactory        _marketCancelRequestFactory;
    private MarketCancelReplaceRequestFactory _marketCancelReplaceRequestFactory;
    private ClientCancelledFactory            _clientCancelledFactory;
    private ClientExpiredFactory              _clientExpiredFactory;
    private ClientReplacedFactory             _clientReplacedFactory;
    private ClientTradeNewFactory             _clientTradeNewFactory;
    private ClientTradeCancelFactory          _clientTradeCancelFactory;
    private ClientTradeCorrectFactory         _clientTradeCorrectFactory;

    private RejectedFactory      _decodeNOSRejectedFactory;
    private CancelRequestFactory _recoveryForceCancelFactory;
    private CancelRejectFactory  _recoveryCancelRejectFactory;

    private SessionRejectFactory _sessionRejectedFactory;

    private int _idx = 1;

    public static void makeForceCancelClOrdId( final ReusableString newClOrdId, ViewString origClOrdId ) {
        newClOrdId.setValue( FORCE_CANCEL_PREFIX );
        newClOrdId.append( origClOrdId );
    }

    public EventBuilderImpl() {
        // nada
    }

    @Override
    public void initPools() {
        SuperpoolManager sp = SuperpoolManager.instance();

        _cancelRejectFactory               = sp.getFactory( ClientCancelRejectFactory.class, ClientCancelRejectImpl.class );
        _rejectedFactory                   = sp.getFactory( ClientRejectedFactory.class, ClientRejectedImpl.class );
        _marketNOSFactory                  = sp.getFactory( MarketNewOrderSingleFactory.class, MarketNewOrderSingleImpl.class );
        _clientNewOrderAckFactory          = sp.getFactory( ClientNewOrderAckFactory.class, ClientNewOrderAckImpl.class );
        _marketCancelRequestFactory        = sp.getFactory( MarketCancelRequestFactory.class, MarketCancelRequestImpl.class );
        _marketCancelReplaceRequestFactory = sp.getFactory( MarketCancelReplaceRequestFactory.class, MarketCancelReplaceRequestImpl.class );
        _clientCancelledFactory            = sp.getFactory( ClientCancelledFactory.class, ClientCancelledImpl.class );
        _clientExpiredFactory              = sp.getFactory( ClientExpiredFactory.class, ClientExpiredImpl.class );
        _clientReplacedFactory             = sp.getFactory( ClientReplacedFactory.class, ClientReplacedImpl.class );
        _clientTradeNewFactory             = sp.getFactory( ClientTradeNewFactory.class, ClientTradeNewImpl.class );
        _clientTradeCancelFactory          = sp.getFactory( ClientTradeCancelFactory.class, ClientTradeCancelImpl.class );
        _clientTradeCorrectFactory         = sp.getFactory( ClientTradeCorrectFactory.class, ClientTradeCorrectImpl.class );
        _sessionRejectedFactory            = sp.getFactory( SessionRejectFactory.class, SessionRejectImpl.class );
        _recoveryForceCancelFactory        = sp.getFactory( CancelRequestFactory.class, CancelRequestImpl.class );
        _decodeNOSRejectedFactory          = sp.getFactory( RejectedFactory.class, RejectedImpl.class );
        _recoveryCancelRejectFactory       = sp.getFactory( CancelRejectFactory.class, CancelRejectImpl.class );
    }

    @Override
    public Rejected synthNOSRejected( NewOrderSingle nos, ZString rejectReason, OrdRejReason reason, OrdStatus status ) {
        final ClientRejectedImpl reject = _rejectedFactory.get();

        reject.setSrcEvent( nos );
        reject.getOrderIdForUpdate().setValue( NONE );
        reject.setOrdRejReason( reason );
        reject.getTextForUpdate().setValue( rejectReason );
        reject.setOrdStatus( status );
        reject.setExecType( ExecType.Rejected );

        reject.setCumQty( 0 );
        reject.setAvgPx( 0.0 );
        reject.setLeavesQty( 0 );

        reject.getExecIdForUpdate().copy( SYNTH_REJ_PREFIX ).append( nos.getClOrdId() ).append( '_' ).append( ++_idx );

        return reject;
    }

    /**
     * get a CancelReject from pool and populate
     * <p>
     * used to reject a F or G fix message
     *
     * @param clOrdId
     * @param origClOrdId
     * @param orderId
     * @param rejectReason
     * @param reason
     * @param status
     * @return
     */
    @Override
    public CancelReject getCancelReject( ZString clOrdId,
                                         ZString origClOrdId,
                                         ZString orderId,
                                         ZString rejectReason,
                                         CxlRejReason reason,
                                         CxlRejResponseTo msgTypeRejected,
                                         OrdStatus status ) {

        final CancelRejectImpl reject = _recoveryCancelRejectFactory.get();

        reject.getClOrdIdForUpdate().setValue( clOrdId );
        reject.getOrigClOrdIdForUpdate().setValue( origClOrdId );
        reject.getOrderIdForUpdate().setValue( (orderId == null) ? NONE : orderId );
        reject.getTextForUpdate().setValue( rejectReason );

        reject.setCxlRejResponseTo( msgTypeRejected );
        reject.setCxlRejReason( reason );
        reject.setOrdStatus( status );

        return reject;
    }

    @Override
    public Rejected createClientRejected( Order order, Rejected msg ) {
        final ClientRejectedImpl crej = _rejectedFactory.get();

        final OrderVersion ver = order.getPendingVersion();
        final OrderRequest src = (OrderRequest) ver.getBaseOrderRequest();

        crej.setSrcEvent( src );
        crej.setAvgPx( 0.0 );
        crej.setCumQty( 0 );
        crej.setLeavesQty( ver.getLeavesQty() );
        crej.setOrdStatus( ver.getOrdStatus() );

        crej.setEventHandler( src.getEventHandler() );
        crej.getOrderIdForUpdate().setValue( ver.getMarketOrderId() );
        crej.setMktCapacity( ver.getMarketCapacity() );
        crej.getOrigClOrdIdForUpdate().setValue( src.getOrigClOrdId() );

        crej.setExecType( ExecType.Rejected );

        if ( msg != null ) {
            crej.getTextForUpdate().copy( msg.getText() );

            final Instrument inst = src.getInstrument();

            ((ExchangeInstrument) inst).getExchange().makeExecIdUnique( crej.getExecIdForUpdate(), msg.getExecId(), inst );
        } else {
            final ReusableString synthAckExecId = crej.getExecIdForUpdate();
            synthAckExecId.setValue( REJ_EXECID_PREFIX );
            synthAckExecId.append( src.getClOrdId() ).append( '_' ).append( ++_idx );
        }

        return crej;
    }

    @Override
    public CancelReject createClientCancelReject( Order order, CancelReject mktReject ) {

        final ClientCancelRejectImpl reject = _cancelRejectFactory.get();

        final OrderVersion     cancelReqVer = order.getPendingVersion();
        final BaseOrderRequest cancelReqSrc = cancelReqVer.getBaseOrderRequest();

        final OrderVersion ver = order.getLastAckedVerion();
        final OrderRequest src = (OrderRequest) ver.getBaseOrderRequest();

        // only fields from cancel request required is the clOrdId
        reject.getClOrdIdForUpdate().setValue( cancelReqSrc.getClOrdId() );

        // take the mktOrderId from the cancel req ver
        reject.getOrderIdForUpdate().setValue( ver.getMarketOrderId() );

        reject.setOrdStatus( ver.getOrdStatus() );

        //  DONT use the cancel req as the base ... its missing alot of info
        // @NOTE taking the origClOrdId from the src event relies on correct state management
        reject.setSrcEvent( src );
        reject.getOrigClOrdIdForUpdate().setValue( src.getClOrdId() );
        reject.setEventHandler( src.getEventHandler() );

        if ( mktReject != null ) {
            reject.getTextForUpdate().setValue( mktReject.getText() );

            reject.setCxlRejReason( mktReject.getCxlRejReason() );
            reject.setCxlRejResponseTo( CxlRejResponseTo.CancelRequest );
        }

        return reject;
    }

    /**
     * all manipulation relating to generation of market order should happen here
     */
    @Override
    public NewOrderSingle createMarketNewOrderSingle( Order order, NewOrderSingle clientNos ) {

        final MarketNewOrderSingleUpdate mnos = _marketNOSFactory.get();

        mnos.setSrcEvent( clientNos );
        mnos.setOrderQty( clientNos.getOrderQty() );
        mnos.getParentClOrdIdForUpdate().setValue( clientNos.getClOrdId() );

        double price = order.getPendingVersion().getMarketPrice();

        final Instrument inst            = clientNos.getInstrument();
        final Currency   tradingCurrency = inst.getCurrency();

        mnos.setPrice( price );
        mnos.setCurrency( tradingCurrency );

        final Exchange   exchange = ((ExchangeInstrument) inst).getExchange();
        final OMEnricher enricher = (OMEnricher) exchange.getEnricher();
        exchange.generateMarketClOrdId( mnos.getClOrdIdForUpdate(), clientNos.getClOrdId() );
        enricher.enrich( order, mnos );

        return mnos;
    }

    @Override
    public NewOrderAck createClientNewOrderAck( Order order, NewOrderAck msg ) {

        final ClientNewOrderAckImpl cack = _clientNewOrderAckFactory.get();

        final OrderVersion ver = order.getPendingVersion();
        final OrderRequest src = (OrderRequest) ver.getBaseOrderRequest();

        cack.setSrcEvent( src );
        cack.setAvgPx( 0.0 );
        cack.setCumQty( 0 );
        cack.setLeavesQty( ver.getLeavesQty() );
        cack.setOrdStatus( ver.getOrdStatus() );

        cack.setEventHandler( src.getEventHandler() );
        cack.getOrderIdForUpdate().setValue( ver.getMarketOrderId() );
        cack.setMktCapacity( ver.getMarketCapacity() );

        cack.setExecType( ExecType.New );

        if ( msg != null ) {
            cack.setAckReceived( msg.getAckReceived() );
            final ExchangeInstrument inst = (ExchangeInstrument) src.getInstrument();
            inst.getExchange().makeExecIdUnique( cack.getExecIdForUpdate(), msg.getExecId(), inst );
        } else {
            final ReusableString synthAckExecId = cack.getExecIdForUpdate();
            synthAckExecId.setValue( ACK_EXECID_PREFIX );
            synthAckExecId.append( src.getClOrdId() );
        }

        return cack;
    }

    @Override
    public CancelRequest createForceMarketCancel( ViewString origClOrdId, Side side, ViewString orderId, ViewString srcLinkId ) {

        final CancelRequestImpl cancel = _recoveryForceCancelFactory.get();

        cancel.getOrigClOrdIdForUpdate().setValue( origClOrdId );
        final ReusableString clOrdId = cancel.getClOrdIdForUpdate();

        makeForceCancelClOrdId( clOrdId, origClOrdId );

        cancel.setSide( side );

        cancel.getOrderIdForUpdate().setValue( orderId );
        cancel.getParentClOrdIdForUpdate().setValue( srcLinkId );

        return cancel;
    }

    @Override
    public CancelRequest createMarketCancelRequest( Order order, CancelRequest clientCancelRequest ) {
        final MarketCancelRequestUpdate mcan = _marketCancelRequestFactory.get();

        // dont use the cancel req as a base as it doesnt have all the fields on it
        final OrderVersion ver = order.getLastAckedVerion();
        final OrderRequest src = (OrderRequest) ver.getBaseOrderRequest();

        mcan.setSrcEvent( src );
        mcan.getOrigClOrdIdForUpdate().setValue( ver.getMarketClOrdId() );
        mcan.getOrderIdForUpdate().setValue( ver.getMarketOrderId() );
        mcan.getParentClOrdIdForUpdate().setValue( clientCancelRequest.getClOrdId() );

        final Exchange exchange = order.getExchange();
        exchange.generateMarketClOrdId( mcan.getClOrdIdForUpdate(), clientCancelRequest.getClOrdId() );

        return mcan;
    }

    @Override
    public Cancelled createClientCanceled( Order order, Cancelled cancelled ) {
        final ClientCancelledUpdate ccan = _clientCancelledFactory.get();

        final OrderVersion     cancelReqVer = order.getPendingVersion();
        final BaseOrderRequest cancelReqSrc = cancelReqVer.getBaseOrderRequest();

        final OrderVersion ver = order.getLastAckedVerion();
        final OrderRequest src = (OrderRequest) ver.getBaseOrderRequest();

        // only fields from cancel request required is the clOrdId
        ccan.getClOrdIdForUpdate().setValue( cancelReqSrc.getClOrdId() );

        // take the mktOrderId from the cancel req ver
        ccan.getOrderIdForUpdate().setValue( ver.getMarketOrderId() );

        final double avgPx = EventUtils.convertForMajorMinor( src, ver.getAvgPx() );

        //  DONT use the cancel req as the base ... its missing alot of info
        ccan.getOrigClOrdIdForUpdate().setValue( src.getClOrdId() );
        ccan.setSrcEvent( src );
        ccan.setAvgPx( avgPx );
        ccan.setCumQty( ver.getCumQty() );
        ccan.setLeavesQty( ver.getLeavesQty() );
        ccan.setOrdStatus( ver.getOrdStatus() );

        ccan.setEventHandler( src.getEventHandler() );
        ccan.setMktCapacity( ver.getMarketCapacity() );

        ccan.setExecType( ExecType.Canceled );

        if ( cancelled != null ) {
            final Instrument inst = src.getInstrument();
            ((ExchangeInstrument) inst).getExchange().makeExecIdUnique( ccan.getExecIdForUpdate(), cancelled.getExecId(), inst );
        } else {
            final ReusableString synthAckExecId = ccan.getExecIdForUpdate();
            synthAckExecId.setValue( CANCELLED_EXECID_PREFIX );
            synthAckExecId.append( src.getClOrdId() ).append( '_' ).append( ++_idx );
        }

        return ccan;
    }

    @Override
    public TradeNew createClientTradeNew( Order order, TradeNew msg, TradeWrapper tradeWrapper ) {
        final ClientTradeNewImpl cfill = _clientTradeNewFactory.get();

        final OrderVersion ver = order.getLastAckedVerion();
        final OrderRequest src = (OrderRequest) ver.getBaseOrderRequest();

        cfill.setSrcEvent( src );
        cfill.setOrdStatus( order.getPendingVersion().getOrdStatus() );

        cfill.setEventHandler( src.getEventHandler() );
        cfill.getOrderIdForUpdate().setValue( ver.getMarketOrderId() );
        cfill.setMktCapacity( ver.getMarketCapacity() );
        cfill.setMultiLegReportingType( msg.getMultiLegReportingType() );
        cfill.getSecurityDescForUpdate().setValue( msg.getSecurityDesc() );

        final double avgPx = EventUtils.convertForMajorMinor( src, ver.getAvgPx() );

        cfill.setAvgPx( avgPx );
        cfill.setCumQty( ver.getCumQty() );
        cfill.setLeavesQty( ver.getLeavesQty() );

        final double lastPx = EventUtils.convertForMajorMinor( src, msg.getLastPx() );

        cfill.setLastPx( lastPx );
        cfill.setLastQty( msg.getLastQty() );
        cfill.getLastMktForUpdate().setValue( msg.getLastMkt() );
        cfill.setLiquidityInd( msg.getLiquidityInd() );

        cfill.setExecType( ExecType.Trade );
        final Instrument inst = src.getInstrument();
        final Exchange   ex   = ((ExchangeInstrument) inst).getExchange();

        if ( ex.isGeneratedExecIDRequired() ) {

            ex.makeExecIdUnique( cfill.getExecIdForUpdate(), msg.getExecId(), inst );

            if ( tradeWrapper != null ) tradeWrapper.setClientExecId( cfill.getExecId() );

        } else { // client execId side same as exchangeSide

            cfill.getExecIdForUpdate().setValue( msg.getExecId() );
        }

        return cfill;
    }

    @Override
    public CancelReplaceRequest createMarketCancelReplaceRequest( Order order, CancelReplaceRequest clientReplaceRequest ) {
        final MarketCancelReplaceRequestUpdate mrep = _marketCancelReplaceRequestFactory.get();
        final OrderVersion                     ver  = order.getLastAckedVerion();

        mrep.setSrcEvent( clientReplaceRequest );
        mrep.getOrigClOrdIdForUpdate().setValue( ver.getMarketClOrdId() );
        mrep.getOrderIdForUpdate().setValue( ver.getMarketOrderId() );

        final Exchange exchange = order.getExchange();
        exchange.generateMarketClOrdId( mrep.getClOrdIdForUpdate(), clientReplaceRequest.getClOrdId() );

        mrep.setOrderQty( clientReplaceRequest.getOrderQty() );

        double price = order.getPendingVersion().getMarketPrice();

        final Instrument inst            = clientReplaceRequest.getInstrument();
        final Currency   tradingCurrency = inst.getCurrency();

        mrep.setPrice( price );
        mrep.setCurrency( tradingCurrency );

        final OMEnricher enricher = (OMEnricher) exchange.getEnricher();
        enricher.enrich( order, mrep );

        return mrep;
    }

    @Override
    public Replaced createClientReplaced( Order order, Replaced replaced ) {
        final ClientReplacedUpdate ccan = _clientReplacedFactory.get();

        final OrderVersion ver = order.getPendingVersion();
        final OrderRequest src = (OrderRequest) ver.getBaseOrderRequest();

        // take the mktOrderId from the cancel req ver
        ccan.getOrderIdForUpdate().setValue( ver.getMarketOrderId() );

        final double avgPx = EventUtils.convertForMajorMinor( src, ver.getAvgPx() );

        ccan.getOrigClOrdIdForUpdate().setValue( src.getOrigClOrdId() );
        ccan.setSrcEvent( src );
        ccan.setAvgPx( avgPx );
        ccan.setCumQty( ver.getCumQty() );
        ccan.setLeavesQty( ver.getLeavesQty() );
        ccan.setOrdStatus( ver.getOrdStatus() );

        ccan.setEventHandler( src.getEventHandler() );
        ccan.setMktCapacity( ver.getMarketCapacity() );

        ccan.setExecType( ExecType.Replaced );

        if ( replaced != null ) {
            final Instrument inst = src.getInstrument();
            ((ExchangeInstrument) inst).getExchange().makeExecIdUnique( ccan.getExecIdForUpdate(), replaced.getExecId(), inst );
        } else {
            final ReusableString synthAckExecId = ccan.getExecIdForUpdate();
            synthAckExecId.setValue( REPLACED_EXECID_PREFIX );
            synthAckExecId.append( src.getClOrdId() ).append( '_' ).append( ++_idx );
        }

        return ccan;
    }

    @Override
    public CancelReject createClientCancelReplaceReject( Order order, CancelReject mktReject ) {

        final ClientCancelRejectImpl reject = _cancelRejectFactory.get();

        final OrderVersion acc = order.getLastAckedVerion();
        final OrderVersion ver = order.getPendingVersion();
        final OrderRequest src = (OrderRequest) ver.getBaseOrderRequest();

        reject.getOrderIdForUpdate().setValue( acc.getMarketOrderId() );

        reject.getClOrdIdForUpdate().setValue( src.getClOrdId() );
        reject.getOrigClOrdIdForUpdate().setValue( src.getOrigClOrdId() );

        reject.setOrdStatus( acc.getOrdStatus() );

        reject.setSrcEvent( src );
        reject.setEventHandler( src.getEventHandler() );

        if ( mktReject != null ) {
            reject.getTextForUpdate().setValue( mktReject.getText() );

            reject.setCxlRejReason( mktReject.getCxlRejReason() );
            reject.setCxlRejResponseTo( CxlRejResponseTo.CancelReplace );
        }

        return reject;
    }

    @Override
    public TradeCancel createClientTradeCancel( Order order, TradeCancel msg, TradeWrapper origTrade, TradeWrapper cancelWrapper ) {
        final ClientTradeCancelImpl cancel = _clientTradeCancelFactory.get();

        final OrderVersion ver = order.getLastAckedVerion();
        final OrderRequest src = (OrderRequest) ver.getBaseOrderRequest();

        cancel.setSrcEvent( src );
        cancel.setOrdStatus( order.getPendingVersion().getOrdStatus() );

        cancel.setEventHandler( src.getEventHandler() );
        cancel.getOrderIdForUpdate().setValue( ver.getMarketOrderId() );
        cancel.setMktCapacity( ver.getMarketCapacity() );
        cancel.setMultiLegReportingType( msg.getMultiLegReportingType() );
        cancel.getSecurityDescForUpdate().setValue( msg.getSecurityDesc() );

        final double avgPx = EventUtils.convertForMajorMinor( src, ver.getAvgPx() );

        cancel.setAvgPx( avgPx );
        cancel.setCumQty( ver.getCumQty() );
        cancel.setLeavesQty( ver.getLeavesQty() );

        final double lastPx = EventUtils.convertForMajorMinor( src, msg.getLastPx() );

        cancel.setLastPx( lastPx );
        cancel.setLastQty( msg.getLastQty() );
        cancel.getLastMktForUpdate().setValue( msg.getLastMkt() );
        cancel.setLiquidityInd( msg.getLiquidityInd() );

        cancel.setExecType( ExecType.TradeCancel );
        final Instrument inst = src.getInstrument();
        final Exchange   ex   = ((ExchangeInstrument) inst).getExchange();

        if ( ex.isGeneratedExecIDRequired() && origTrade != null ) {

            ex.makeExecIdUnique( cancel.getExecIdForUpdate(), msg.getExecId(), inst );

            cancelWrapper.setClientExecId( cancel.getExecId() );

            // get the generated execId for the trade being busted
            cancel.getExecRefIDForUpdate().setValue( origTrade.getClientExecId() );

        } else { // client execId side same as exchangeSide

            cancel.getExecIdForUpdate().setValue( msg.getExecId() );
            cancel.getExecRefIDForUpdate().setValue( msg.getExecRefID() );
        }

        return cancel;
    }

    @Override
    public TradeCorrect createClientTradeCorrect( Order order, TradeCorrect msg, TradeWrapper origTrade, TradeWrapper correctWrapper ) {
        final ClientTradeCorrectImpl correct = _clientTradeCorrectFactory.get();

        final OrderVersion ver = order.getLastAckedVerion();
        final OrderRequest src = (OrderRequest) ver.getBaseOrderRequest();

        correct.setSrcEvent( src );
        correct.setOrdStatus( order.getPendingVersion().getOrdStatus() );

        correct.setEventHandler( src.getEventHandler() );
        correct.getOrderIdForUpdate().setValue( ver.getMarketOrderId() );
        correct.setMktCapacity( ver.getMarketCapacity() );
        correct.setMultiLegReportingType( msg.getMultiLegReportingType() );
        correct.getSecurityDescForUpdate().setValue( msg.getSecurityDesc() );

        final double avgPx = EventUtils.convertForMajorMinor( src, ver.getAvgPx() );

        correct.setAvgPx( avgPx );
        correct.setCumQty( ver.getCumQty() );
        correct.setLeavesQty( ver.getLeavesQty() );

        final double lastPx = EventUtils.convertForMajorMinor( src, msg.getLastPx() );

        correct.setLastPx( lastPx );
        correct.setLastQty( msg.getLastQty() );
        correct.getLastMktForUpdate().setValue( msg.getLastMkt() );
        correct.setLiquidityInd( msg.getLiquidityInd() );

        correct.setExecType( ExecType.TradeCorrect );
        final Instrument inst = src.getInstrument();
        final Exchange   ex   = ((ExchangeInstrument) inst).getExchange();

        if ( ex.isGeneratedExecIDRequired() && origTrade != null ) {

            ex.makeExecIdUnique( correct.getExecIdForUpdate(), msg.getExecId(), inst );

            correctWrapper.setClientExecId( correct.getExecId() );

            // get the generated execId for the trade being busted
            correct.getExecRefIDForUpdate().setValue( origTrade.getClientExecId() );

        } else { // client execId side same as exchangeSide

            correct.getExecIdForUpdate().setValue( msg.getExecId() );
            correct.getExecRefIDForUpdate().setValue( msg.getExecRefID() );
        }

        return correct;
    }

    @Override
    public SessionReject createSessionReject( String rejectReason, BaseReject<?> msg ) {

        final SessionRejectImpl reject = _sessionRejectedFactory.get();

        reject.setRefSeqNum( msg.getMsgSeqNum() );
        reject.getTextForUpdate().setValue( rejectReason );

        return reject;
    }

    @Override
    public CancelReject getCancelReject( ZString clOrdId,
                                         ZString origClOrdId,
                                         String rejectReason,
                                         CxlRejResponseTo msgTypeRejected,
                                         OrdStatus status ) {

        final CancelRejectImpl reject = _recoveryCancelRejectFactory.get();

        reject.getClOrdIdForUpdate().setValue( clOrdId );
        reject.getOrigClOrdIdForUpdate().setValue( origClOrdId );
        reject.getOrderIdForUpdate().setValue( NONE );
        reject.getTextForUpdate().setValue( rejectReason );

        reject.setCxlRejResponseTo( msgTypeRejected );
        reject.setCxlRejReason( CxlRejReason.Other );
        reject.setOrdStatus( status );

        return reject;
    }

    @Override
    public Event getNOSReject( ViewString clOrdId, OrdStatus status, String message, BaseReject<?> msg ) {

        if ( clOrdId == null || clOrdId.length() == 0 || msg == null ) {
            _log.warn( MISSING_CLORDID );
            return null;
        }

        ReusableString side     = msg.getFixField( TAG_SIDE );
        ReusableString sym      = msg.getFixField( TAG_SYMBOL );
        ReusableString secId    = msg.getFixField( TAG_SECURITY_ID );
        ReusableString idSource = msg.getFixField( TAG_IDSource );

        boolean haveSym = (sym != null && sym.length() > 0) || (secId != null && idSource != null && secId.length() > 0);

        if ( side == null || side.length() == 0 || !haveSym ) {
            _log.warn( NOT_ENOUGH_INFO );
            return null;
        }

        final RejectedImpl crej = _decodeNOSRejectedFactory.get();

        try { crej.setSide( Side.getVal( side.getByte( 0 ) ) ); } catch( Exception e ) { /* dont worry */ }

        if ( message != null ) crej.getTextForUpdate().setValue( message.getBytes() );

        if ( sym != null ) crej.getSymbolForUpdate().copy( sym );
        if ( secId != null ) crej.getSecurityIdForUpdate().copy( secId );
        if ( idSource != null && idSource.length() > 0 ) crej.setSecurityIDSource( SecurityIDSource.getVal( idSource.getByte( 0 ) ) );

        crej.setEventHandler( msg.getEventHandler() );

        crej.setAvgPx( 0.0 );
        crej.setCumQty( 0 );
        crej.setLeavesQty( 0 );
        crej.setOrdStatus( OrdStatus.Rejected );
        crej.getClOrdIdForUpdate().copy( clOrdId );
        crej.getOrderIdForUpdate().setValue( NONE );

        crej.setExecType( ExecType.Rejected );

        final ReusableString synthAckExecId = crej.getExecIdForUpdate();
        synthAckExecId.setValue( REJ_EXECID_PREFIX );
        synthAckExecId.append( clOrdId ).append( '_' ).append( ++_idx );

        return crej;
    }

    @Override
    public CancelReject getClientCancelReject( Order order, VagueOrderReject mktReject, boolean isAmend ) {

        final ClientCancelRejectImpl reject = _cancelRejectFactory.get();

        final OrderVersion     cancelReqVer = order.getPendingVersion();
        final BaseOrderRequest cancelReqSrc = cancelReqVer.getBaseOrderRequest();

        final OrderVersion ver = order.getLastAckedVerion();
        final OrderRequest src = (OrderRequest) ver.getBaseOrderRequest();

        // only fields from cancel request required is the clOrdId
        reject.getClOrdIdForUpdate().setValue( cancelReqSrc.getClOrdId() );

        // take the mktOrderId from the cancel req ver
        reject.getOrderIdForUpdate().setValue( ver.getMarketOrderId() );

        reject.setOrdStatus( ver.getOrdStatus() );

        //  DONT use the cancel req as the base ... its missing alot of info
        // @NOTE taking the origClOrdId from the src event relies on correct state management
        reject.setSrcEvent( src );
        reject.getOrigClOrdIdForUpdate().setValue( src.getClOrdId() );
        reject.setEventHandler( src.getEventHandler() );

        if ( mktReject != null ) {
            reject.getTextForUpdate().setValue( mktReject.getText() );

            reject.setCxlRejReason( CxlRejReason.Other );
            reject.setCxlRejResponseTo( (isAmend) ? CxlRejResponseTo.CancelReplace : CxlRejResponseTo.CancelRequest );
        }

        return reject;
    }

    @Override
    public Expired createClientExpired( Order order, Expired cancelled ) {
        final ClientExpiredUpdate ccan = _clientExpiredFactory.get();

        final OrderVersion ver = order.getLastAckedVerion();
        final OrderRequest src = (OrderRequest) ver.getBaseOrderRequest();

        // take the mktOrderId from the cancel req ver
        ccan.getOrderIdForUpdate().setValue( ver.getMarketOrderId() );

        final double avgPx = EventUtils.convertForMajorMinor( src, ver.getAvgPx() );

        ccan.setSrcEvent( src );
        ccan.setAvgPx( avgPx );
        ccan.setCumQty( ver.getCumQty() );
        ccan.setLeavesQty( ver.getLeavesQty() );
        ccan.setOrdStatus( ver.getOrdStatus() );

        ccan.setEventHandler( src.getEventHandler() );
        ccan.setMktCapacity( ver.getMarketCapacity() );

        ccan.setExecType( ExecType.Expired );

        if ( cancelled != null ) {
            final Instrument inst = src.getInstrument();
            ((ExchangeInstrument) inst).getExchange().makeExecIdUnique( ccan.getExecIdForUpdate(), cancelled.getExecId(), inst );
        } else {
            final ReusableString synthAckExecId = ccan.getExecIdForUpdate();
            synthAckExecId.setValue( CANCELLED_EXECID_PREFIX );
            synthAckExecId.append( src.getClOrdId() ).append( '_' ).append( ++_idx );
        }

        return ccan;
    }
}
