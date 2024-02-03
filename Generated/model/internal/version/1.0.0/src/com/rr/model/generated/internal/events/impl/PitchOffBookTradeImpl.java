package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.MMTMarketMechanism;
import com.rr.model.generated.internal.type.MMTTradingMode;
import com.rr.model.generated.internal.type.MMTTransactionCategory;
import com.rr.model.generated.internal.type.MMTReferencePriceIndicator;
import com.rr.model.generated.internal.type.MMTAlgorithmicTrade;
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

public final class PitchOffBookTradeImpl implements BaseCboePitch, PitchOffBookTradeWrite, Copyable<PitchOffBookTrade>, Reusable<PitchOffBookTradeImpl> {

   // Attrs

    private transient          PitchOffBookTradeImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private long _orderId = Constants.UNSET_LONG;
    private final ReusableString  _securityId = new ReusableString( SizeType.CBOE_EUTP_SYMBOL.getSize() );
    private int _lastQty = Constants.UNSET_INT;
    private double _lastPx = Constants.UNSET_DOUBLE;
    private long _execId = Constants.UNSET_LONG;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private Side _side;
    private SecurityIDSource _securityIdSrc;
    private ExchangeCode _securityExchange;
    private MMTMarketMechanism _mktMech;
    private MMTTradingMode _tradingMode;
    private MMTTransactionCategory _tranCat;
    private MMTReferencePriceIndicator _refPriceInd;
    private MMTAlgorithmicTrade _algoTrade;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final long getOrderId() { return _orderId; }
    @Override public final void setOrderId( long val ) { _orderId = val; }

    @Override public final Side getSide() { return _side; }
    @Override public final void setSide( Side val ) { _side = val; }

    @Override public final ViewString getSecurityId() { return _securityId; }

    @Override public final void setSecurityId( byte[] buf, int offset, int len ) { _securityId.setValue( buf, offset, len ); }
    @Override public final ReusableString getSecurityIdForUpdate() { return _securityId; }

    @Override public final SecurityIDSource getSecurityIdSrc() { return _securityIdSrc; }
    @Override public final void setSecurityIdSrc( SecurityIDSource val ) { _securityIdSrc = val; }

    @Override public final ExchangeCode getSecurityExchange() { return _securityExchange; }
    @Override public final void setSecurityExchange( ExchangeCode val ) { _securityExchange = val; }

    @Override public final int getLastQty() { return _lastQty; }
    @Override public final void setLastQty( int val ) { _lastQty = val; }

    @Override public final double getLastPx() { return _lastPx; }
    @Override public final void setLastPx( double val ) { _lastPx = val; }

    @Override public final long getExecId() { return _execId; }
    @Override public final void setExecId( long val ) { _execId = val; }

    @Override public final MMTMarketMechanism getMktMech() { return _mktMech; }
    @Override public final void setMktMech( MMTMarketMechanism val ) { _mktMech = val; }

    @Override public final MMTTradingMode getTradingMode() { return _tradingMode; }
    @Override public final void setTradingMode( MMTTradingMode val ) { _tradingMode = val; }

    @Override public final MMTTransactionCategory getTranCat() { return _tranCat; }
    @Override public final void setTranCat( MMTTransactionCategory val ) { _tranCat = val; }

    @Override public final MMTReferencePriceIndicator getRefPriceInd() { return _refPriceInd; }
    @Override public final void setRefPriceInd( MMTReferencePriceIndicator val ) { _refPriceInd = val; }

    @Override public final MMTAlgorithmicTrade getAlgoTrade() { return _algoTrade; }
    @Override public final void setAlgoTrade( MMTAlgorithmicTrade val ) { _algoTrade = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _orderId = Constants.UNSET_LONG;
        _side = null;
        _securityId.reset();
        _securityIdSrc = null;
        _securityExchange = null;
        _lastQty = Constants.UNSET_INT;
        _lastPx = Constants.UNSET_DOUBLE;
        _execId = Constants.UNSET_LONG;
        _mktMech = null;
        _tradingMode = null;
        _tranCat = null;
        _refPriceInd = null;
        _algoTrade = null;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.PitchOffBookTrade;
    }

    @Override
    public final PitchOffBookTradeImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( PitchOffBookTradeImpl nxt ) {
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
        out.append( "PitchOffBookTradeImpl" ).append( ' ' );
        if ( Constants.UNSET_LONG != getOrderId() && 0 != getOrderId() )             out.append( ", orderId=" ).append( getOrderId() );
        if ( getSide() != null )             out.append( ", side=" ).append( getSide() );
        if ( getSecurityId().length() > 0 )             out.append( ", securityId=" ).append( getSecurityId() );
        if ( getSecurityIdSrc() != null )             out.append( ", securityIdSrc=" );
        if ( getSecurityIdSrc() != null ) out.append( getSecurityIdSrc().id() );
        if ( getSecurityExchange() != null )             out.append( ", securityExchange=" );
        if ( getSecurityExchange() != null ) out.append( getSecurityExchange().id() );
        if ( Constants.UNSET_INT != getLastQty() && 0 != getLastQty() )             out.append( ", lastQty=" ).append( getLastQty() );
        if ( Utils.hasVal( getLastPx() ) ) out.append( ", lastPx=" ).append( getLastPx() );
        if ( Constants.UNSET_LONG != getExecId() && 0 != getExecId() )             out.append( ", execId=" ).append( getExecId() );
        if ( getMktMech() != null )             out.append( ", mktMech=" ).append( getMktMech() );
        if ( getTradingMode() != null )             out.append( ", tradingMode=" ).append( getTradingMode() );
        if ( getTranCat() != null )             out.append( ", tranCat=" ).append( getTranCat() );
        if ( getRefPriceInd() != null )             out.append( ", refPriceInd=" ).append( getRefPriceInd() );
        if ( getAlgoTrade() != null )             out.append( ", algoTrade=" ).append( getAlgoTrade() );
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

    @Override public final void snapTo( PitchOffBookTrade dest ) {
        ((PitchOffBookTradeImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( PitchOffBookTrade src ) {
        setOrderId( src.getOrderId() );
        setSide( src.getSide() );
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        setSecurityIdSrc( src.getSecurityIdSrc() );
        setSecurityExchange( src.getSecurityExchange() );
        setLastQty( src.getLastQty() );
        setLastPx( src.getLastPx() );
        setExecId( src.getExecId() );
        setMktMech( src.getMktMech() );
        setTradingMode( src.getTradingMode() );
        setTranCat( src.getTranCat() );
        setRefPriceInd( src.getRefPriceInd() );
        setAlgoTrade( src.getAlgoTrade() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( PitchOffBookTrade src ) {
        setOrderId( src.getOrderId() );
        setSide( src.getSide() );
        getSecurityIdForUpdate().copy( src.getSecurityId() );
        setSecurityIdSrc( src.getSecurityIdSrc() );
        setSecurityExchange( src.getSecurityExchange() );
        setLastQty( src.getLastQty() );
        setLastPx( src.getLastPx() );
        setExecId( src.getExecId() );
        setMktMech( src.getMktMech() );
        setTradingMode( src.getTradingMode() );
        setTranCat( src.getTranCat() );
        setRefPriceInd( src.getRefPriceInd() );
        setAlgoTrade( src.getAlgoTrade() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( PitchOffBookTrade src ) {
        if ( Constants.UNSET_LONG != src.getOrderId() ) setOrderId( src.getOrderId() );
        setSide( src.getSide() );
        if ( src.getSecurityId().length() > 0 ) getSecurityIdForUpdate().copy( src.getSecurityId() );
        if ( getSecurityIdSrc() != null )  setSecurityIdSrc( src.getSecurityIdSrc() );
        if ( getSecurityExchange() != null )  setSecurityExchange( src.getSecurityExchange() );
        if ( Constants.UNSET_INT != src.getLastQty() ) setLastQty( src.getLastQty() );
        if ( Utils.hasVal( src.getLastPx() ) ) setLastPx( src.getLastPx() );
        if ( Constants.UNSET_LONG != src.getExecId() ) setExecId( src.getExecId() );
        setMktMech( src.getMktMech() );
        setTradingMode( src.getTradingMode() );
        setTranCat( src.getTranCat() );
        setRefPriceInd( src.getRefPriceInd() );
        setAlgoTrade( src.getAlgoTrade() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
