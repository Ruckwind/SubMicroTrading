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

public final class MarketNewOrderSingleImpl implements OrderRequest, MarketNewOrderSingleUpdate, Reusable<MarketNewOrderSingleImpl> {

   // Attrs

    private transient          MarketNewOrderSingleImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private final ReusableString  _clOrdId = new ReusableString( SizeType.CLORDID_LENGTH.getSize() );
    private double _price = Constants.UNSET_DOUBLE;
    private double _orderQty = Constants.UNSET_DOUBLE;
    private int _tickOffset = Constants.UNSET_INT;
    private final ReusableString  _StratParams = new ReusableString( SizeType.STRATPARAM_LENGTH.getSize() );
    private long _orderSent = Constants.UNSET_LONG;
    private int _maturityMonthYear = Constants.UNSET_INT;
    private double _curPos = Constants.UNSET_DOUBLE;
    private double _curRefPx = Constants.UNSET_DOUBLE;
    private final ReusableString  _parentClOrdId = new ReusableString( SizeType.CLORDID_LENGTH.getSize() );
    private final ReusableString  _stratId = new ReusableString( SizeType.STRAT_DEF_ID_LEN.getSize() );
    private final ReusableString  _origStratId = new ReusableString( SizeType.STRAT_DEF_ID_LEN.getSize() );
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private RefPriceType _refPriceType;
    private OrderCapacity _orderCapacity;
    private Currency _currency;
    private OrdDestType _targetDest;
    private ExchangeCode _securityExchange;

    private OrderRequest  _srcEvent;
    private int           _flags          = 0;

   // Getters and Setters
    @Override public final ViewString getClOrdId() { return _clOrdId; }

    @Override public final void setClOrdId( byte[] buf, int offset, int len ) { _clOrdId.setValue( buf, offset, len ); }
    @Override public final ReusableString getClOrdIdForUpdate() { return _clOrdId; }

    @Override public final ViewString getAccount() { return _srcEvent.getAccount(); }


    @Override public final ViewString getText() { return _srcEvent.getText(); }


    @Override public final double getPrice() { return _price; }
    @Override public final void setPrice( double val ) { _price = val; }

    @Override public final double getOrderQty() { return _orderQty; }
    @Override public final void setOrderQty( double val ) { _orderQty = val; }

    @Override public final RefPriceType getRefPriceType() { return _refPriceType; }
    @Override public final void setRefPriceType( RefPriceType val ) { _refPriceType = val; }

    @Override public final int getTickOffset() { return _tickOffset; }
    @Override public final void setTickOffset( int val ) { _tickOffset = val; }

    @Override public final ExecInst getExecInst() { return _srcEvent.getExecInst(); }

    @Override public final HandlInst getHandlInst() { return _srcEvent.getHandlInst(); }

    @Override public final OrderCapacity getOrderCapacity() { return _orderCapacity; }
    @Override public final void setOrderCapacity( OrderCapacity val ) { _orderCapacity = val; }

    @Override public final OrdType getOrdType() { return _srcEvent.getOrdType(); }

    @Override public final SecurityType getSecurityType() { return _srcEvent.getSecurityType(); }

    @Override public final TimeInForce getTimeInForce() { return _srcEvent.getTimeInForce(); }

    @Override public final BookingType getBookingType() { return _srcEvent.getBookingType(); }

    @Override public final TargetStrategy getTargetStrategy() { return _srcEvent.getTargetStrategy(); }

    @Override public final ViewString getStratParams() { return _StratParams; }

    @Override public final void setStratParams( byte[] buf, int offset, int len ) { _StratParams.setValue( buf, offset, len ); }
    @Override public final ReusableString getStratParamsForUpdate() { return _StratParams; }

    @Override public final long getEffectiveTime() { return _srcEvent.getEffectiveTime(); }

    @Override public final long getExpireTime() { return _srcEvent.getExpireTime(); }

    @Override public final long getOrderReceived() { return _srcEvent.getOrderReceived(); }

    @Override public final long getOrderSent() { return _srcEvent.getOrderSent(); }
    @Override public final void setOrderSent( long val ) { _srcEvent.setOrderSent( val ); }

    @Override public final Instrument getInstrument() { return _srcEvent.getInstrument(); }

    @Override public final ClientProfile getClient() { return _srcEvent.getClient(); }

    @Override public final ViewString getOrigClOrdId() { return _srcEvent.getOrigClOrdId(); }


    @Override public final ViewString getSecurityId() { return _srcEvent.getSecurityId(); }


    @Override public final ViewString getSymbol() { return _srcEvent.getSymbol(); }


    @Override public final int getMaturityMonthYear() { return _maturityMonthYear; }
    @Override public final void setMaturityMonthYear( int val ) { _maturityMonthYear = val; }

    @Override public final Currency getCurrency() { return _currency; }
    @Override public final void setCurrency( Currency val ) { _currency = val; }

    @Override public final SecurityIDSource getSecurityIDSource() { return _srcEvent.getSecurityIDSource(); }

    @Override public final long getTransactTime() { return _srcEvent.getTransactTime(); }

    @Override public final Side getSide() { return _srcEvent.getSide(); }

    @Override public final double getCurPos() { return _curPos; }
    @Override public final void setCurPos( double val ) { _curPos = val; }

    @Override public final double getCurRefPx() { return _curRefPx; }
    @Override public final void setCurRefPx( double val ) { _curRefPx = val; }

    @Override public final OrdDestType getTargetDest() { return _targetDest; }
    @Override public final void setTargetDest( OrdDestType val ) { _targetDest = val; }

    @Override public final ViewString getExDest() { return _srcEvent.getExDest(); }


    @Override public final ExchangeCode getSecurityExchange() { return _securityExchange; }
    @Override public final void setSecurityExchange( ExchangeCode val ) { _securityExchange = val; }

    @Override public final PartyID getBroker() { return _srcEvent.getBroker(); }

    @Override public final PartyID getClearer() { return _srcEvent.getClearer(); }

    @Override public final ViewString getParentClOrdId() { return _parentClOrdId; }

    @Override public final void setParentClOrdId( byte[] buf, int offset, int len ) { _parentClOrdId.setValue( buf, offset, len ); }
    @Override public final ReusableString getParentClOrdIdForUpdate() { return _parentClOrdId; }

    @Override public final ViewString getStratId() { return _stratId; }

    @Override public final void setStratId( byte[] buf, int offset, int len ) { _stratId.setValue( buf, offset, len ); }
    @Override public final ReusableString getStratIdForUpdate() { return _stratId; }

    @Override public final ViewString getOrigStratId() { return _origStratId; }

    @Override public final void setOrigStratId( byte[] buf, int offset, int len ) { _origStratId.setValue( buf, offset, len ); }
    @Override public final ReusableString getOrigStratIdForUpdate() { return _origStratId; }

    @Override public final ViewString getSenderCompId() { return _srcEvent.getSenderCompId(); }


    @Override public final ViewString getOnBehalfOfId() { return _srcEvent.getOnBehalfOfId(); }


    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

    @Override public final void setSrcEvent( OrderRequest srcEvent ) { _srcEvent = srcEvent; }
    @Override public final OrderRequest getSrcEvent() { return _srcEvent; }


   // Reusable Contract

    @Override
    public final void reset() {
        _clOrdId.reset();
        _price = Constants.UNSET_DOUBLE;
        _orderQty = Constants.UNSET_DOUBLE;
        _refPriceType = null;
        _tickOffset = Constants.UNSET_INT;
        _orderCapacity = null;
        _StratParams.reset();
        _orderSent = Constants.UNSET_LONG;
        _maturityMonthYear = Constants.UNSET_INT;
        _currency = null;
        _curPos = Constants.UNSET_DOUBLE;
        _curRefPx = Constants.UNSET_DOUBLE;
        _targetDest = null;
        _securityExchange = null;
        _parentClOrdId.reset();
        _stratId.reset();
        _origStratId.reset();
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _srcEvent = null;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.MarketNewOrderSingle;
    }

    @Override
    public final MarketNewOrderSingleImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( MarketNewOrderSingleImpl nxt ) {
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
        out.append( "MarketNewOrderSingleImpl" ).append( ' ' );
        if ( getClOrdId().length() > 0 )             out.append( ", clOrdId=" ).append( getClOrdId() );
            out.append( ", account=" ).append( getAccount() );
            out.append( ", text=" ).append( getText() );
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
            out.append( ", origClOrdId=" ).append( getOrigClOrdId() );
            out.append( ", securityId=" ).append( getSecurityId() );
            out.append( ", symbol=" ).append( getSymbol() );
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
            out.append( ", exDest=" ).append( getExDest() );
        if ( getSecurityExchange() != null )             out.append( ", securityExchange=" );
        if ( getSecurityExchange() != null ) out.append( getSecurityExchange().id() );
        if ( getBroker() != null )             out.append( ", broker=" );
        if ( getBroker() != null ) out.append( getBroker().id() );
        if ( getClearer() != null )             out.append( ", clearer=" );
        if ( getClearer() != null ) out.append( getClearer().id() );
        if ( getParentClOrdId().length() > 0 )             out.append( ", parentClOrdId=" ).append( getParentClOrdId() );
        if ( getStratId().length() > 0 )             out.append( ", stratId=" ).append( getStratId() );
        if ( getOrigStratId().length() > 0 )             out.append( ", origStratId=" ).append( getOrigStratId() );
        if ( getSenderCompId().length() > 0 )             out.append( ", senderCompId=" ).append( getSenderCompId() );
            out.append( ", onBehalfOfId=" ).append( getOnBehalfOfId() );
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

}
