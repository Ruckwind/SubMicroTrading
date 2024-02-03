package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.RefPriceType;
import com.rr.model.generated.internal.type.ExecInst;
import com.rr.model.generated.internal.type.HandlInst;
import com.rr.model.generated.internal.type.OrderCapacity;
import com.rr.model.generated.internal.type.OrdType;
import com.rr.model.generated.internal.type.TimeInForce;
import com.rr.model.generated.internal.type.BookingType;
import com.rr.model.generated.internal.type.TargetStrategy;
import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.OrdDestType;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;
import com.rr.model.internal.type.*;
import com.rr.model.generated.internal.core.ModelReusableTypes;
import com.rr.model.generated.internal.core.SizeType;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.interfaces.*;

@SuppressWarnings( { "unused", "override"  })

public final class CancelReplaceRequestImpl implements OrderRequest, CancelReplaceRequestWrite, Copyable<CancelReplaceRequest>, ClientCancelReplaceRequestWrite, Reusable<CancelReplaceRequestImpl> {

   // Attrs

    private transient          CancelReplaceRequestImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString _clOrdId = new ReusableString( SizeType.CLORDID_LENGTH.getSize() );
    private final ReusableString _origClOrdId = new ReusableString( SizeType.CLORDID_LENGTH.getSize() );
    private final ReusableString  _exDest = new ReusableString( SizeType.EXDESTINATION_LENGTH.getSize() );
    private final ReusableString _orderId = new ReusableString( SizeType.ORDERID_LENGTH.getSize() );
    private final ReusableString  _account = new ReusableString( SizeType.ACCOUNT_LENGTH.getSize() );
    private final ReusableString  _text = new ReusableString( SizeType.TEXT_LENGTH.getSize() );
    private double _price = Constants.UNSET_DOUBLE;
    private double _orderQty = Constants.UNSET_DOUBLE;
    private int _tickOffset = Constants.UNSET_INT;
    private final ReusableString  _StratParams = new ReusableString( SizeType.STRATPARAM_LENGTH.getSize() );
    @TimestampMS private long _effectiveTime = Constants.UNSET_LONG;
    @TimestampMS private long _expireTime = Constants.UNSET_LONG;
    private long _orderReceived = Constants.UNSET_LONG;
    private long _orderSent = Constants.UNSET_LONG;
    private final ReusableString  _securityId = new ReusableString( SizeType.SECURITYID_LENGTH.getSize() );
    private final ReusableString  _symbol = new ReusableString( SizeType.SYMBOL_LENGTH.getSize() );
    private int _maturityMonthYear = Constants.UNSET_INT;
    @TimestampMS private long _transactTime = Constants.UNSET_LONG;
    private double _curPos = Constants.UNSET_DOUBLE;
    private double _curRefPx = Constants.UNSET_DOUBLE;
    private final ReusableString  _parentClOrdId = new ReusableString( SizeType.CLORDID_LENGTH.getSize() );
    private final ReusableString  _stratId = new ReusableString( SizeType.STRAT_DEF_ID_LEN.getSize() );
    private final ReusableString  _origStratId = new ReusableString( SizeType.STRAT_DEF_ID_LEN.getSize() );
    private final ReusableString _senderCompId = new ReusableString( SizeType.COMPID_LENGTH.getSize() );
    private final ReusableString  _onBehalfOfId = new ReusableString( SizeType.COMPID_LENGTH.getSize() );
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private ExchangeCode _securityExchange;
    private RefPriceType _refPriceType;
    private ExecInst _execInst;
    private HandlInst _handlInst;
    private OrderCapacity _orderCapacity;
    private OrdType _ordType;
    private SecurityType _securityType;
    private TimeInForce _timeInForce;
    private BookingType _bookingType;
    private TargetStrategy _targetStrategy;
    private Instrument _instrument;
    private ClientProfile _client;
    private Currency _currency;
    private SecurityIDSource _securityIDSource;
    private Side _side;
    private OrdDestType _targetDest;
    private PartyID _broker;
    private PartyID _clearer;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getClOrdId() { return _clOrdId; }

    @Override public final void setClOrdId( byte[] buf, int offset, int len ) { _clOrdId.setValue( buf, offset, len ); }
    @Override public final ReusableString getClOrdIdForUpdate() { return _clOrdId; }

    @Override public final ViewString getOrigClOrdId() { return _origClOrdId; }

    @Override public final void setOrigClOrdId( byte[] buf, int offset, int len ) { _origClOrdId.setValue( buf, offset, len ); }
    @Override public final ReusableString getOrigClOrdIdForUpdate() { return _origClOrdId; }

    @Override public final ViewString getExDest() { return _exDest; }

    @Override public final void setExDest( byte[] buf, int offset, int len ) { _exDest.setValue( buf, offset, len ); }
    @Override public final ReusableString getExDestForUpdate() { return _exDest; }

    @Override public final ExchangeCode getSecurityExchange() { return _securityExchange; }
    @Override public final void setSecurityExchange( ExchangeCode val ) { _securityExchange = val; }

    @Override public final ViewString getOrderId() { return _orderId; }

    @Override public final void setOrderId( byte[] buf, int offset, int len ) { _orderId.setValue( buf, offset, len ); }
    @Override public final ReusableString getOrderIdForUpdate() { return _orderId; }

    @Override public final ViewString getAccount() { return _account; }

    @Override public final void setAccount( byte[] buf, int offset, int len ) { _account.setValue( buf, offset, len ); }
    @Override public final ReusableString getAccountForUpdate() { return _account; }

    @Override public final ViewString getText() { return _text; }

    @Override public final void setText( byte[] buf, int offset, int len ) { _text.setValue( buf, offset, len ); }
    @Override public final ReusableString getTextForUpdate() { return _text; }

    @Override public final double getPrice() { return _price; }
    @Override public final void setPrice( double val ) { _price = val; }

    @Override public final double getOrderQty() { return _orderQty; }
    @Override public final void setOrderQty( double val ) { _orderQty = val; }

    @Override public final RefPriceType getRefPriceType() { return _refPriceType; }
    @Override public final void setRefPriceType( RefPriceType val ) { _refPriceType = val; }

    @Override public final int getTickOffset() { return _tickOffset; }
    @Override public final void setTickOffset( int val ) { _tickOffset = val; }

    @Override public final ExecInst getExecInst() { return _execInst; }
    @Override public final void setExecInst( ExecInst val ) { _execInst = val; }

    @Override public final HandlInst getHandlInst() { return _handlInst; }
    @Override public final void setHandlInst( HandlInst val ) { _handlInst = val; }

    @Override public final OrderCapacity getOrderCapacity() { return _orderCapacity; }
    @Override public final void setOrderCapacity( OrderCapacity val ) { _orderCapacity = val; }

    @Override public final OrdType getOrdType() { return _ordType; }
    @Override public final void setOrdType( OrdType val ) { _ordType = val; }

    @Override public final SecurityType getSecurityType() { return _securityType; }
    @Override public final void setSecurityType( SecurityType val ) { _securityType = val; }

    @Override public final TimeInForce getTimeInForce() { return _timeInForce; }
    @Override public final void setTimeInForce( TimeInForce val ) { _timeInForce = val; }

    @Override public final BookingType getBookingType() { return _bookingType; }
    @Override public final void setBookingType( BookingType val ) { _bookingType = val; }

    @Override public final TargetStrategy getTargetStrategy() { return _targetStrategy; }
    @Override public final void setTargetStrategy( TargetStrategy val ) { _targetStrategy = val; }

    @Override public final ViewString getStratParams() { return _StratParams; }

    @Override public final void setStratParams( byte[] buf, int offset, int len ) { _StratParams.setValue( buf, offset, len ); }
    @Override public final ReusableString getStratParamsForUpdate() { return _StratParams; }

    @Override public final long getEffectiveTime() { return _effectiveTime; }
    @Override public final void setEffectiveTime( long val ) { _effectiveTime = val; }

    @Override public final long getExpireTime() { return _expireTime; }
    @Override public final void setExpireTime( long val ) { _expireTime = val; }

    @Override public final long getOrderReceived() { return _orderReceived; }
    @Override public final void setOrderReceived( long val ) { _orderReceived = val; }

    @Override public final long getOrderSent() { return _orderSent; }
    @Override public final void setOrderSent( long val ) { _orderSent = val; }

    @Override public final Instrument getInstrument() { return _instrument; }
    @Override public final void setInstrument( Instrument val ) { _instrument = val; }

    @Override public final ClientProfile getClient() { return _client; }
    @Override public final void setClient( ClientProfile val ) { _client = val; }

    @Override public final ViewString getSecurityId() { return _securityId; }

    @Override public final void setSecurityId( byte[] buf, int offset, int len ) { _securityId.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityIdForUpdate() { return _securityId; }

    @Override public final ViewString getSymbol() { return _symbol; }

    @Override public final void setSymbol( byte[] buf, int offset, int len ) { _symbol.setValue( buf, offset, len ); }
    @Override public final ReusableString getSymbolForUpdate() { return _symbol; }

    @Override public final int getMaturityMonthYear() { return _maturityMonthYear; }
    @Override public final void setMaturityMonthYear( int val ) { _maturityMonthYear = val; }

    @Override public final Currency getCurrency() { return _currency; }
    @Override public final void setCurrency( Currency val ) { _currency = val; }

    @Override public final SecurityIDSource getSecurityIDSource() { return _securityIDSource; }
    @Override public final void setSecurityIDSource( SecurityIDSource val ) { _securityIDSource = val; }

    @Override public final long getTransactTime() { return _transactTime; }
    @Override public final void setTransactTime( long val ) { _transactTime = val; }

    @Override public final Side getSide() { return _side; }
    @Override public final void setSide( Side val ) { _side = val; }

    @Override public final double getCurPos() { return _curPos; }
    @Override public final void setCurPos( double val ) { _curPos = val; }

    @Override public final double getCurRefPx() { return _curRefPx; }
    @Override public final void setCurRefPx( double val ) { _curRefPx = val; }

    @Override public final OrdDestType getTargetDest() { return _targetDest; }
    @Override public final void setTargetDest( OrdDestType val ) { _targetDest = val; }

    @Override public final PartyID getBroker() { return _broker; }
    @Override public final void setBroker( PartyID val ) { _broker = val; }

    @Override public final PartyID getClearer() { return _clearer; }
    @Override public final void setClearer( PartyID val ) { _clearer = val; }

    @Override public final ViewString getParentClOrdId() { return _parentClOrdId; }

    @Override public final void setParentClOrdId( byte[] buf, int offset, int len ) { _parentClOrdId.setValue( buf, offset, len ); }
    @Override public final ReusableString getParentClOrdIdForUpdate() { return _parentClOrdId; }

    @Override public final ViewString getStratId() { return _stratId; }

    @Override public final void setStratId( byte[] buf, int offset, int len ) { _stratId.setValue( buf, offset, len ); }
    @Override public final ReusableString getStratIdForUpdate() { return _stratId; }

    @Override public final ViewString getOrigStratId() { return _origStratId; }

    @Override public final void setOrigStratId( byte[] buf, int offset, int len ) { _origStratId.setValue( buf, offset, len ); }
    @Override public final ReusableString getOrigStratIdForUpdate() { return _origStratId; }

    @Override public final ViewString getSenderCompId() { return _senderCompId; }

    @Override public final void setSenderCompId( byte[] buf, int offset, int len ) { _senderCompId.setValue( buf, offset, len ); }
    @Override public final ReusableString getSenderCompIdForUpdate() { return _senderCompId; }

    @Override public final ViewString getOnBehalfOfId() { return _onBehalfOfId; }

    @Override public final void setOnBehalfOfId( byte[] buf, int offset, int len ) { _onBehalfOfId.setValue( buf, offset, len ); }
    @Override public final ReusableString getOnBehalfOfIdForUpdate() { return _onBehalfOfId; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _clOrdId.reset();
        _origClOrdId.reset();
        _exDest.reset();
        _securityExchange = null;
        _orderId.reset();
        _account.reset();
        _text.reset();
        _price = Constants.UNSET_DOUBLE;
        _orderQty = Constants.UNSET_DOUBLE;
        _refPriceType = null;
        _tickOffset = Constants.UNSET_INT;
        _execInst = null;
        _handlInst = null;
        _orderCapacity = null;
        _ordType = null;
        _securityType = null;
        _timeInForce = null;
        _bookingType = null;
        _targetStrategy = null;
        _StratParams.reset();
        _effectiveTime = Constants.UNSET_LONG;
        _expireTime = Constants.UNSET_LONG;
        _orderReceived = Constants.UNSET_LONG;
        _orderSent = Constants.UNSET_LONG;
        _instrument = null;
        _client = null;
        _securityId.reset();
        _symbol.reset();
        _maturityMonthYear = Constants.UNSET_INT;
        _currency = null;
        _securityIDSource = null;
        _transactTime = Constants.UNSET_LONG;
        _side = null;
        _curPos = Constants.UNSET_DOUBLE;
        _curRefPx = Constants.UNSET_DOUBLE;
        _targetDest = null;
        _broker = null;
        _clearer = null;
        _parentClOrdId.reset();
        _stratId.reset();
        _origStratId.reset();
        _senderCompId.reset();
        _onBehalfOfId.reset();
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.CancelReplaceRequest;
    }

    @Override
    public final CancelReplaceRequestImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( CancelReplaceRequestImpl nxt ) {
        _next = nxt;
    }

    @Override
    public final void detachQueue() {
        _nextMessage = null;
    }

    @Override
    public final Event getNextQueueEntry() {
        return _nextMessage;
    }

    @Override
    public final void attachQueue( Event nxt ) {
        _nextMessage = nxt;
    }

    @Override
    public final EventHandler getEventHandler() {
        return _messageHandler;
    }

    @Override
    public final void setEventHandler( EventHandler handler ) {
        _messageHandler = handler;
    }


   // Helper methods
    @Override
    public void setFlag( MsgFlag flag, boolean isOn ) {
        _flags = MsgFlag.setFlag( _flags, flag, isOn );
    }

    @Override
    public boolean isFlagSet( MsgFlag flag ) {
        return MsgFlag.isOn( _flags, flag );
    }

    @Override
    public int getFlags() {
        return _flags;
    }

    @Override
    public String toString() {
        ReusableString buf = TLC.instance().pop();
        dump( buf );
        String rs = buf.toString();
        TLC.instance().pushback( buf );
        return rs;
    }

    @Override
    public final void dump( final ReusableString out ) {
        out.append( "CancelReplaceRequestImpl" ).append( ' ' );
        if ( getClOrdId().length() > 0 )             out.append( ", clOrdId=" ).append( getClOrdId() );
        if ( getOrigClOrdId().length() > 0 )             out.append( ", origClOrdId=" ).append( getOrigClOrdId() );
        if ( getExDest().length() > 0 )             out.append( ", exDest=" ).append( getExDest() );
        if ( getSecurityExchange() != null )             out.append( ", securityExchange=" );
        if ( getSecurityExchange() != null ) out.append( getSecurityExchange().id() );
        if ( getOrderId().length() > 0 )             out.append( ", orderId=" ).append( getOrderId() );
        if ( getAccount().length() > 0 )             out.append( ", account=" ).append( getAccount() );
        if ( getText().length() > 0 )             out.append( ", text=" ).append( getText() );
        if ( Utils.hasVal( getPrice() ) ) out.append( ", price=" ).append( getPrice() );
        if ( Utils.hasVal( getOrderQty() ) ) out.append( ", orderQty=" ).append( getOrderQty() );
        if ( getRefPriceType() != null )             out.append( ", refPriceType=" ).append( getRefPriceType() );
        if ( Constants.UNSET_INT != getTickOffset() && 0 != getTickOffset() )             out.append( ", tickOffset=" ).append( getTickOffset() );
        if ( getExecInst() != null )             out.append( ", execInst=" ).append( getExecInst() );
        if ( getHandlInst() != null )             out.append( ", handlInst=" ).append( getHandlInst() );
        if ( getOrderCapacity() != null )             out.append( ", orderCapacity=" ).append( getOrderCapacity() );
        if ( getOrdType() != null )             out.append( ", ordType=" ).append( getOrdType() );
        if ( getSecurityType() != null )             out.append( ", securityType=" );
        if ( getSecurityType() != null ) out.append( getSecurityType().id() );
        if ( getTimeInForce() != null )             out.append( ", timeInForce=" ).append( getTimeInForce() );
        if ( getBookingType() != null )             out.append( ", bookingType=" ).append( getBookingType() );
        if ( getTargetStrategy() != null )             out.append( ", targetStrategy=" ).append( getTargetStrategy() );
        if ( getStratParams().length() > 0 )             out.append( ", StratParams=" ).append( getStratParams() );
        if ( Constants.UNSET_LONG != getEffectiveTime() && 0 != getEffectiveTime() ) {
            out.append( ", effectiveTime=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getEffectiveTime() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getEffectiveTime() );
            out.append( " ( " );
            out.append( getEffectiveTime() ).append( " ) " );
        }
        if ( Constants.UNSET_LONG != getExpireTime() && 0 != getExpireTime() ) {
            out.append( ", expireTime=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getExpireTime() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getExpireTime() );
            out.append( " ( " );
            out.append( getExpireTime() ).append( " ) " );
        }
        if ( Constants.UNSET_LONG != getOrderReceived() && 0 != getOrderReceived() )             out.append( ", orderReceived=" ).append( getOrderReceived() );
        if ( Constants.UNSET_LONG != getOrderSent() && 0 != getOrderSent() )             out.append( ", orderSent=" ).append( getOrderSent() );
        if ( getInstrument() != null )             out.append( ", instrument=" );
        if ( getInstrument() != null ) out.append( getInstrument().id() );
        if ( getClient() != null )             out.append( ", client=" );
        if ( getClient() != null ) out.append( getClient().id() );
        if ( getSecurityId().length() > 0 )             out.append( ", securityId=" ).append( getSecurityId() );
        if ( getSymbol().length() > 0 )             out.append( ", symbol=" ).append( getSymbol() );
        if ( Constants.UNSET_INT != getMaturityMonthYear() && 0 != getMaturityMonthYear() )             out.append( ", maturityMonthYear=" ).append( getMaturityMonthYear() );
        if ( getCurrency() != null )             out.append( ", currency=" );
        if ( getCurrency() != null ) out.append( getCurrency().id() );
        if ( getSecurityIDSource() != null )             out.append( ", securityIDSource=" );
        if ( getSecurityIDSource() != null ) out.append( getSecurityIDSource().id() );
        if ( Constants.UNSET_LONG != getTransactTime() && 0 != getTransactTime() ) {
            out.append( ", transactTime=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getTransactTime() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getTransactTime() );
            out.append( " ( " );
            out.append( getTransactTime() ).append( " ) " );
        }
        if ( getSide() != null )             out.append( ", side=" ).append( getSide() );
        if ( Utils.hasVal( getCurPos() ) ) out.append( ", curPos=" ).append( getCurPos() );
        if ( Utils.hasVal( getCurRefPx() ) ) out.append( ", curRefPx=" ).append( getCurRefPx() );
        if ( getTargetDest() != null )             out.append( ", targetDest=" ).append( getTargetDest() );
        if ( getBroker() != null )             out.append( ", broker=" );
        if ( getBroker() != null ) out.append( getBroker().id() );
        if ( getClearer() != null )             out.append( ", clearer=" );
        if ( getClearer() != null ) out.append( getClearer().id() );
        if ( getParentClOrdId().length() > 0 )             out.append( ", parentClOrdId=" ).append( getParentClOrdId() );
        if ( getStratId().length() > 0 )             out.append( ", stratId=" ).append( getStratId() );
        if ( getOrigStratId().length() > 0 )             out.append( ", origStratId=" ).append( getOrigStratId() );
        if ( getSenderCompId().length() > 0 )             out.append( ", senderCompId=" ).append( getSenderCompId() );
        if ( getOnBehalfOfId().length() > 0 )             out.append( ", onBehalfOfId=" ).append( getOnBehalfOfId() );
        if ( Constants.UNSET_INT != getMsgSeqNum() && 0 != getMsgSeqNum() )             out.append( ", msgSeqNum=" ).append( getMsgSeqNum() );
        out.append( ", possDupFlag=" ).append( getPossDupFlag() );
        if ( Constants.UNSET_LONG != getEventTimestamp() && 0 != getEventTimestamp() ) {
            out.append( ", eventTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getEventTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getEventTimestamp() );
            out.append( " ( " );
            out.append( getEventTimestamp() ).append( " ) " );
        }
    }

    @Override public final void snapTo( CancelReplaceRequest dest ) {
        ((CancelReplaceRequestImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( CancelReplaceRequest src ) {
        getClOrdIdForUpdate().copy( src.getClOrdId() );
        getOrigClOrdIdForUpdate().copy( src.getOrigClOrdId() );
        getExDestForUpdate().copy( src.getExDest() );
        setSecurityExchange( src.getSecurityExchange() );
        getOrderIdForUpdate().copy( src.getOrderId() );
        getAccountForUpdate().copy( src.getAccount() );
        getTextForUpdate().copy( src.getText() );
        setPrice( src.getPrice() );
        setOrderQty( src.getOrderQty() );
        setRefPriceType( src.getRefPriceType() );
        setTickOffset( src.getTickOffset() );
        setExecInst( src.getExecInst() );
        setHandlInst( src.getHandlInst() );
        setOrderCapacity( src.getOrderCapacity() );
        setOrdType( src.getOrdType() );
        setSecurityType( src.getSecurityType() );
        setTimeInForce( src.getTimeInForce() );
        setBookingType( src.getBookingType() );
        setTargetStrategy( src.getTargetStrategy() );
        getStratParamsForUpdate().copy( src.getStratParams() );
        setEffectiveTime( src.getEffectiveTime() );
        setExpireTime( src.getExpireTime() );
        setOrderReceived( src.getOrderReceived() );
        setOrderSent( src.getOrderSent() );
        setInstrument( src.getInstrument() );
        setClient( src.getClient() );
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        getSymbolForUpdate().copy( src.getSymbol() );
        setMaturityMonthYear( src.getMaturityMonthYear() );
        setCurrency( src.getCurrency() );
        setSecurityIDSource( src.getSecurityIDSource() );
        setTransactTime( src.getTransactTime() );
        setSide( src.getSide() );
        setCurPos( src.getCurPos() );
        setCurRefPx( src.getCurRefPx() );
        setTargetDest( src.getTargetDest() );
        setBroker( src.getBroker() );
        setClearer( src.getClearer() );
        getParentClOrdIdForUpdate().copy( src.getParentClOrdId() );
        getStratIdForUpdate().copy( src.getStratId() );
        getOrigStratIdForUpdate().copy( src.getOrigStratId() );
        getSenderCompIdForUpdate().copy( src.getSenderCompId() );
        getOnBehalfOfIdForUpdate().copy( src.getOnBehalfOfId() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( CancelReplaceRequest src ) {
        getClOrdIdForUpdate().copy( src.getClOrdId() );
        getOrigClOrdIdForUpdate().copy( src.getOrigClOrdId() );
        getExDestForUpdate().copy( src.getExDest() );
        setSecurityExchange( src.getSecurityExchange() );
        getOrderIdForUpdate().copy( src.getOrderId() );
        getAccountForUpdate().copy( src.getAccount() );
        getTextForUpdate().copy( src.getText() );
        setPrice( src.getPrice() );
        setOrderQty( src.getOrderQty() );
        setRefPriceType( src.getRefPriceType() );
        setTickOffset( src.getTickOffset() );
        setExecInst( src.getExecInst() );
        setHandlInst( src.getHandlInst() );
        setOrderCapacity( src.getOrderCapacity() );
        setOrdType( src.getOrdType() );
        setSecurityType( src.getSecurityType() );
        setTimeInForce( src.getTimeInForce() );
        setBookingType( src.getBookingType() );
        setTargetStrategy( src.getTargetStrategy() );
        getStratParamsForUpdate().copy( src.getStratParams() );
        setEffectiveTime( src.getEffectiveTime() );
        setExpireTime( src.getExpireTime() );
        setOrderReceived( src.getOrderReceived() );
        setOrderSent( src.getOrderSent() );
        setInstrument( src.getInstrument() );
        setClient( src.getClient() );
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        getSymbolForUpdate().copy( src.getSymbol() );
        setMaturityMonthYear( src.getMaturityMonthYear() );
        setCurrency( src.getCurrency() );
        setSecurityIDSource( src.getSecurityIDSource() );
        setTransactTime( src.getTransactTime() );
        setSide( src.getSide() );
        setCurPos( src.getCurPos() );
        setCurRefPx( src.getCurRefPx() );
        setTargetDest( src.getTargetDest() );
        setBroker( src.getBroker() );
        setClearer( src.getClearer() );
        getParentClOrdIdForUpdate().copy( src.getParentClOrdId() );
        getStratIdForUpdate().copy( src.getStratId() );
        getOrigStratIdForUpdate().copy( src.getOrigStratId() );
        getSenderCompIdForUpdate().copy( src.getSenderCompId() );
        getOnBehalfOfIdForUpdate().copy( src.getOnBehalfOfId() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( CancelReplaceRequest src ) {
        if ( src.getClOrdId().length() > 0 ) getClOrdIdForUpdate().copy( src.getClOrdId() );
        if ( src.getOrigClOrdId().length() > 0 ) getOrigClOrdIdForUpdate().copy( src.getOrigClOrdId() );
        if ( src.getExDest().length() > 0 ) getExDestForUpdate().copy( src.getExDest() );
        if ( getSecurityExchange() != null )  setSecurityExchange( src.getSecurityExchange() );
        if ( src.getOrderId().length() > 0 ) getOrderIdForUpdate().copy( src.getOrderId() );
        if ( src.getAccount().length() > 0 ) getAccountForUpdate().copy( src.getAccount() );
        if ( src.getText().length() > 0 ) getTextForUpdate().copy( src.getText() );
        if ( Utils.hasVal( src.getPrice() ) ) setPrice( src.getPrice() );
        if ( Utils.hasVal( src.getOrderQty() ) ) setOrderQty( src.getOrderQty() );
        setRefPriceType( src.getRefPriceType() );
        if ( Constants.UNSET_INT != src.getTickOffset() ) setTickOffset( src.getTickOffset() );
        setExecInst( src.getExecInst() );
        setHandlInst( src.getHandlInst() );
        setOrderCapacity( src.getOrderCapacity() );
        setOrdType( src.getOrdType() );
        if ( getSecurityType() != null )  setSecurityType( src.getSecurityType() );
        setTimeInForce( src.getTimeInForce() );
        setBookingType( src.getBookingType() );
        setTargetStrategy( src.getTargetStrategy() );
        if ( src.getStratParams().length() > 0 ) getStratParamsForUpdate().copy( src.getStratParams() );
        if ( Constants.UNSET_LONG != src.getEffectiveTime() ) setEffectiveTime( src.getEffectiveTime() );
        if ( Constants.UNSET_LONG != src.getExpireTime() ) setExpireTime( src.getExpireTime() );
        if ( Constants.UNSET_LONG != src.getOrderReceived() ) setOrderReceived( src.getOrderReceived() );
        if ( Constants.UNSET_LONG != src.getOrderSent() ) setOrderSent( src.getOrderSent() );
        if ( getInstrument() != null )  setInstrument( src.getInstrument() );
        if ( getClient() != null )  setClient( src.getClient() );
        if ( src.getSecurityId().length() > 0 ) getSecurityIdForUpdate().copy( src.getSecurityId() );
        if ( src.getSymbol().length() > 0 ) getSymbolForUpdate().copy( src.getSymbol() );
        if ( Constants.UNSET_INT != src.getMaturityMonthYear() ) setMaturityMonthYear( src.getMaturityMonthYear() );
        if ( getCurrency() != null )  setCurrency( src.getCurrency() );
        if ( getSecurityIDSource() != null )  setSecurityIDSource( src.getSecurityIDSource() );
        if ( Constants.UNSET_LONG != src.getTransactTime() ) setTransactTime( src.getTransactTime() );
        setSide( src.getSide() );
        if ( Utils.hasVal( src.getCurPos() ) ) setCurPos( src.getCurPos() );
        if ( Utils.hasVal( src.getCurRefPx() ) ) setCurRefPx( src.getCurRefPx() );
        setTargetDest( src.getTargetDest() );
        if ( getBroker() != null )  setBroker( src.getBroker() );
        if ( getClearer() != null )  setClearer( src.getClearer() );
        if ( src.getParentClOrdId().length() > 0 ) getParentClOrdIdForUpdate().copy( src.getParentClOrdId() );
        if ( src.getStratId().length() > 0 ) getStratIdForUpdate().copy( src.getStratId() );
        if ( src.getOrigStratId().length() > 0 ) getOrigStratIdForUpdate().copy( src.getOrigStratId() );
        if ( src.getSenderCompId().length() > 0 ) getSenderCompIdForUpdate().copy( src.getSenderCompId() );
        if ( src.getOnBehalfOfId().length() > 0 ) getOnBehalfOfIdForUpdate().copy( src.getOnBehalfOfId() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
