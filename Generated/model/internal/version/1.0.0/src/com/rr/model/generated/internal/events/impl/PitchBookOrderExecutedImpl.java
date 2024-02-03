package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.MMTMarketMechanism;
import com.rr.model.generated.internal.type.MMTTradingMode;
import com.rr.model.generated.internal.type.MMTDividend;
import com.rr.model.generated.internal.type.MMTAlgorithmicTrade;
import com.rr.model.generated.internal.type.MMTTransactionCategory;
import com.rr.model.generated.internal.type.MMTReferencePriceIndicator;
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

public final class PitchBookOrderExecutedImpl implements BaseCboePitch, PitchBookOrderExecutedWrite, Copyable<PitchBookOrderExecuted>, Reusable<PitchBookOrderExecutedImpl> {

   // Attrs

    private transient          PitchBookOrderExecutedImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private long _orderId = Constants.UNSET_LONG;
    private int _lastQty = Constants.UNSET_INT;
    private long _execId = Constants.UNSET_LONG;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private MMTMarketMechanism _mktMech;
    private MMTTradingMode _tradingMode;
    private MMTDividend _dividend;
    private MMTAlgorithmicTrade _algoTrade;
    private MMTTransactionCategory _tranCat;
    private MMTReferencePriceIndicator _refPriceInd;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final long getOrderId() { return _orderId; }
    @Override public final void setOrderId( long val ) { _orderId = val; }

    @Override public final int getLastQty() { return _lastQty; }
    @Override public final void setLastQty( int val ) { _lastQty = val; }

    @Override public final long getExecId() { return _execId; }
    @Override public final void setExecId( long val ) { _execId = val; }

    @Override public final MMTMarketMechanism getMktMech() { return _mktMech; }
    @Override public final void setMktMech( MMTMarketMechanism val ) { _mktMech = val; }

    @Override public final MMTTradingMode getTradingMode() { return _tradingMode; }
    @Override public final void setTradingMode( MMTTradingMode val ) { _tradingMode = val; }

    @Override public final MMTDividend getDividend() { return _dividend; }
    @Override public final void setDividend( MMTDividend val ) { _dividend = val; }

    @Override public final MMTAlgorithmicTrade getAlgoTrade() { return _algoTrade; }
    @Override public final void setAlgoTrade( MMTAlgorithmicTrade val ) { _algoTrade = val; }

    @Override public final MMTTransactionCategory getTranCat() { return _tranCat; }
    @Override public final void setTranCat( MMTTransactionCategory val ) { _tranCat = val; }

    @Override public final MMTReferencePriceIndicator getRefPriceInd() { return _refPriceInd; }
    @Override public final void setRefPriceInd( MMTReferencePriceIndicator val ) { _refPriceInd = val; }

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
        _lastQty = Constants.UNSET_INT;
        _execId = Constants.UNSET_LONG;
        _mktMech = null;
        _tradingMode = null;
        _dividend = null;
        _algoTrade = null;
        _tranCat = null;
        _refPriceInd = null;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.PitchBookOrderExecuted;
    }

    @Override
    public final PitchBookOrderExecutedImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( PitchBookOrderExecutedImpl nxt ) {
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
        out.append( "PitchBookOrderExecutedImpl" ).append( ' ' );
        if ( Constants.UNSET_LONG != getOrderId() && 0 != getOrderId() )             out.append( ", orderId=" ).append( getOrderId() );
        if ( Constants.UNSET_INT != getLastQty() && 0 != getLastQty() )             out.append( ", lastQty=" ).append( getLastQty() );
        if ( Constants.UNSET_LONG != getExecId() && 0 != getExecId() )             out.append( ", execId=" ).append( getExecId() );
        if ( getMktMech() != null )             out.append( ", mktMech=" ).append( getMktMech() );
        if ( getTradingMode() != null )             out.append( ", tradingMode=" ).append( getTradingMode() );
        if ( getDividend() != null )             out.append( ", dividend=" ).append( getDividend() );
        if ( getAlgoTrade() != null )             out.append( ", algoTrade=" ).append( getAlgoTrade() );
        if ( getTranCat() != null )             out.append( ", tranCat=" ).append( getTranCat() );
        if ( getRefPriceInd() != null )             out.append( ", refPriceInd=" ).append( getRefPriceInd() );
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

    @Override public final void snapTo( PitchBookOrderExecuted dest ) {
        ((PitchBookOrderExecutedImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( PitchBookOrderExecuted src ) {
        setOrderId( src.getOrderId() );
        setLastQty( src.getLastQty() );
        setExecId( src.getExecId() );
        setMktMech( src.getMktMech() );
        setTradingMode( src.getTradingMode() );
        setDividend( src.getDividend() );
        setAlgoTrade( src.getAlgoTrade() );
        setTranCat( src.getTranCat() );
        setRefPriceInd( src.getRefPriceInd() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( PitchBookOrderExecuted src ) {
        setOrderId( src.getOrderId() );
        setLastQty( src.getLastQty() );
        setExecId( src.getExecId() );
        setMktMech( src.getMktMech() );
        setTradingMode( src.getTradingMode() );
        setDividend( src.getDividend() );
        setAlgoTrade( src.getAlgoTrade() );
        setTranCat( src.getTranCat() );
        setRefPriceInd( src.getRefPriceInd() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( PitchBookOrderExecuted src ) {
        if ( Constants.UNSET_LONG != src.getOrderId() ) setOrderId( src.getOrderId() );
        if ( Constants.UNSET_INT != src.getLastQty() ) setLastQty( src.getLastQty() );
        if ( Constants.UNSET_LONG != src.getExecId() ) setExecId( src.getExecId() );
        setMktMech( src.getMktMech() );
        setTradingMode( src.getTradingMode() );
        setDividend( src.getDividend() );
        setAlgoTrade( src.getAlgoTrade() );
        setTranCat( src.getTranCat() );
        setRefPriceInd( src.getRefPriceInd() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
