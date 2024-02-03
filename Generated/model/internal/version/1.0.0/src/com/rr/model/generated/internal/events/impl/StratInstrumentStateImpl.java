package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

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

public final class StratInstrumentStateImpl implements StratInstrumentState, Reusable<StratInstrumentStateImpl>, Copyable<StratInstrumentState> {

   // Attrs

    private transient          StratInstrumentStateImpl _next = null;
    @TimestampMS private long _stratTimestamp = Constants.UNSET_LONG;
    private double _position = 0;
    private double _unrealisedTotalPnL = 0;
    private double _fromLongRealisedTotalPnL = 0;
    private double _fromShortRealisedTotalPnL = 0;
    private double _unrealisedTotalPnLMin = Constants.UNSET_DOUBLE;
    private double _fromLongRealisedTotalPnLMin = Constants.UNSET_DOUBLE;
    private double _fromShortRealisedTotalPnLMin = Constants.UNSET_DOUBLE;
    private double _unrealisedTotalPnLMax = 0;
    private double _fromLongRealisedTotalPnLMax = 0;
    private double _fromShortRealisedTotalPnLMax = 0;
    private boolean _isActiveTracker = true;
    private long _id = 0;
    private double _fromLongRealisedTotalLongValue = 0;
    private double _fromLongRealisedTotalShortValue = 0;
    private double _fromShortRealisedTotalLongValue = 0;
    private double _fromShortRealisedTotalShortValue = 0;
    private double _unrealisedTotalValue = 0;
    private double _lastPrice = 0;
    private double _totalTradeQty = 0;
    private double _totalTradeVal = 0;
    private double _pointValue = 1;
    private int _totalLongOrders = 0;
    private int _totalShortOrders = 0;
    private double _bidPx = Constants.UNSET_DOUBLE;
    private double _askPx = Constants.UNSET_DOUBLE;
    private double _totLongOpenQty = 0;
    private double _totShortOpenQty = 0;
    private int _numTrades = 0;
    private double _splitAccrualQty = 0;
    private double _splitAccrualVal = 0;
    private double _divAccrualVal = 0;
    private int _publishSeqNum = 0;
    private int _sicFlags = 0;

    private Instrument _instrument;

    private int           _flags          = 0;

    // hooks
    @Override public long getEventTimestamp() { return getStratTimestamp(); };


   // Getters and Setters
    @Override public final long getStratTimestamp() { return _stratTimestamp; }
    @Override public final void setStratTimestamp( long val ) { _stratTimestamp = val; }

    @Override public final Instrument getInstrument() { return _instrument; }
    @Override public final void setInstrument( Instrument val ) { _instrument = val; }

    @Override public final double getPosition() { return _position; }
    @Override public final void setPosition( double val ) { _position = val; }

    @Override public final double getUnrealisedTotalPnL() { return _unrealisedTotalPnL; }
    @Override public final void setUnrealisedTotalPnL( double val ) { _unrealisedTotalPnL = val; }

    @Override public final double getFromLongRealisedTotalPnL() { return _fromLongRealisedTotalPnL; }
    @Override public final void setFromLongRealisedTotalPnL( double val ) { _fromLongRealisedTotalPnL = val; }

    @Override public final double getFromShortRealisedTotalPnL() { return _fromShortRealisedTotalPnL; }
    @Override public final void setFromShortRealisedTotalPnL( double val ) { _fromShortRealisedTotalPnL = val; }

    @Override public final double getUnrealisedTotalPnLMin() { return _unrealisedTotalPnLMin; }
    @Override public final void setUnrealisedTotalPnLMin( double val ) { _unrealisedTotalPnLMin = val; }

    @Override public final double getFromLongRealisedTotalPnLMin() { return _fromLongRealisedTotalPnLMin; }
    @Override public final void setFromLongRealisedTotalPnLMin( double val ) { _fromLongRealisedTotalPnLMin = val; }

    @Override public final double getFromShortRealisedTotalPnLMin() { return _fromShortRealisedTotalPnLMin; }
    @Override public final void setFromShortRealisedTotalPnLMin( double val ) { _fromShortRealisedTotalPnLMin = val; }

    @Override public final double getUnrealisedTotalPnLMax() { return _unrealisedTotalPnLMax; }
    @Override public final void setUnrealisedTotalPnLMax( double val ) { _unrealisedTotalPnLMax = val; }

    @Override public final double getFromLongRealisedTotalPnLMax() { return _fromLongRealisedTotalPnLMax; }
    @Override public final void setFromLongRealisedTotalPnLMax( double val ) { _fromLongRealisedTotalPnLMax = val; }

    @Override public final double getFromShortRealisedTotalPnLMax() { return _fromShortRealisedTotalPnLMax; }
    @Override public final void setFromShortRealisedTotalPnLMax( double val ) { _fromShortRealisedTotalPnLMax = val; }

    @Override public final boolean getIsActiveTracker() { return _isActiveTracker; }
    @Override public final void setIsActiveTracker( boolean val ) { _isActiveTracker = val; }

    @Override public final long getId() { return _id; }
    @Override public final void setId( long val ) { _id = val; }

    @Override public final double getFromLongRealisedTotalLongValue() { return _fromLongRealisedTotalLongValue; }
    @Override public final void setFromLongRealisedTotalLongValue( double val ) { _fromLongRealisedTotalLongValue = val; }

    @Override public final double getFromLongRealisedTotalShortValue() { return _fromLongRealisedTotalShortValue; }
    @Override public final void setFromLongRealisedTotalShortValue( double val ) { _fromLongRealisedTotalShortValue = val; }

    @Override public final double getFromShortRealisedTotalLongValue() { return _fromShortRealisedTotalLongValue; }
    @Override public final void setFromShortRealisedTotalLongValue( double val ) { _fromShortRealisedTotalLongValue = val; }

    @Override public final double getFromShortRealisedTotalShortValue() { return _fromShortRealisedTotalShortValue; }
    @Override public final void setFromShortRealisedTotalShortValue( double val ) { _fromShortRealisedTotalShortValue = val; }

    @Override public final double getUnrealisedTotalValue() { return _unrealisedTotalValue; }
    @Override public final void setUnrealisedTotalValue( double val ) { _unrealisedTotalValue = val; }

    @Override public final double getLastPrice() { return _lastPrice; }
    @Override public final void setLastPrice( double val ) { _lastPrice = val; }

    @Override public final double getTotalTradeQty() { return _totalTradeQty; }
    @Override public final void setTotalTradeQty( double val ) { _totalTradeQty = val; }

    @Override public final double getTotalTradeVal() { return _totalTradeVal; }
    @Override public final void setTotalTradeVal( double val ) { _totalTradeVal = val; }

    @Override public final double getPointValue() { return _pointValue; }
    @Override public final void setPointValue( double val ) { _pointValue = val; }

    @Override public final int getTotalLongOrders() { return _totalLongOrders; }
    @Override public final void setTotalLongOrders( int val ) { _totalLongOrders = val; }

    @Override public final int getTotalShortOrders() { return _totalShortOrders; }
    @Override public final void setTotalShortOrders( int val ) { _totalShortOrders = val; }

    @Override public final double getBidPx() { return _bidPx; }
    @Override public final void setBidPx( double val ) { _bidPx = val; }

    @Override public final double getAskPx() { return _askPx; }
    @Override public final void setAskPx( double val ) { _askPx = val; }

    @Override public final double getTotLongOpenQty() { return _totLongOpenQty; }
    @Override public final void setTotLongOpenQty( double val ) { _totLongOpenQty = val; }

    @Override public final double getTotShortOpenQty() { return _totShortOpenQty; }
    @Override public final void setTotShortOpenQty( double val ) { _totShortOpenQty = val; }

    @Override public final int getNumTrades() { return _numTrades; }
    @Override public final void setNumTrades( int val ) { _numTrades = val; }

    @Override public final double getSplitAccrualQty() { return _splitAccrualQty; }
    @Override public final void setSplitAccrualQty( double val ) { _splitAccrualQty = val; }

    @Override public final double getSplitAccrualVal() { return _splitAccrualVal; }
    @Override public final void setSplitAccrualVal( double val ) { _splitAccrualVal = val; }

    @Override public final double getDivAccrualVal() { return _divAccrualVal; }
    @Override public final void setDivAccrualVal( double val ) { _divAccrualVal = val; }

    @Override public final int getPublishSeqNum() { return _publishSeqNum; }
    @Override public final void setPublishSeqNum( int val ) { _publishSeqNum = val; }

    @Override public final int getSicFlags() { return _sicFlags; }
    @Override public final void setSicFlags( int val ) { _sicFlags = val; }


   // Reusable Contract

    @Override
    public final void reset() {
        _stratTimestamp = Constants.UNSET_LONG;
        _instrument = null;
        _position = 0;
        _unrealisedTotalPnL = 0;
        _fromLongRealisedTotalPnL = 0;
        _fromShortRealisedTotalPnL = 0;
        _unrealisedTotalPnLMin = Constants.UNSET_DOUBLE;
        _fromLongRealisedTotalPnLMin = Constants.UNSET_DOUBLE;
        _fromShortRealisedTotalPnLMin = Constants.UNSET_DOUBLE;
        _unrealisedTotalPnLMax = 0;
        _fromLongRealisedTotalPnLMax = 0;
        _fromShortRealisedTotalPnLMax = 0;
        _isActiveTracker = true;
        _id = 0;
        _fromLongRealisedTotalLongValue = 0;
        _fromLongRealisedTotalShortValue = 0;
        _fromShortRealisedTotalLongValue = 0;
        _fromShortRealisedTotalShortValue = 0;
        _unrealisedTotalValue = 0;
        _lastPrice = 0;
        _totalTradeQty = 0;
        _totalTradeVal = 0;
        _pointValue = 1;
        _totalLongOrders = 0;
        _totalShortOrders = 0;
        _bidPx = Constants.UNSET_DOUBLE;
        _askPx = Constants.UNSET_DOUBLE;
        _totLongOpenQty = 0;
        _totShortOpenQty = 0;
        _numTrades = 0;
        _splitAccrualQty = 0;
        _splitAccrualVal = 0;
        _divAccrualVal = 0;
        _publishSeqNum = 0;
        _sicFlags = 0;
        _flags = 0;
        _next = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.StratInstrumentState;
    }

    @Override
    public final StratInstrumentStateImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( StratInstrumentStateImpl nxt ) {
        _next = nxt;
    }


   // Helper methods
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
        out.append( "StratInstrumentStateImpl" ).append( ' ' );
        if ( Constants.UNSET_LONG != getStratTimestamp() && 0 != getStratTimestamp() ) {
            out.append( ", stratTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getStratTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getStratTimestamp() );
            out.append( " ( " );
            out.append( getStratTimestamp() ).append( " ) " );
        }
        if ( getInstrument() != null )             out.append( ", instrument=" );
        if ( getInstrument() != null ) out.append( getInstrument().id() );
        if ( Utils.hasVal( getPosition() ) ) out.append( ", position=" ).append( getPosition() );
        if ( Utils.hasVal( getUnrealisedTotalPnL() ) ) out.append( ", unrealisedTotalPnL=" ).append( getUnrealisedTotalPnL() );
        if ( Utils.hasVal( getFromLongRealisedTotalPnL() ) ) out.append( ", fromLongRealisedTotalPnL=" ).append( getFromLongRealisedTotalPnL() );
        if ( Utils.hasVal( getFromShortRealisedTotalPnL() ) ) out.append( ", fromShortRealisedTotalPnL=" ).append( getFromShortRealisedTotalPnL() );
        if ( Utils.hasVal( getUnrealisedTotalPnLMin() ) ) out.append( ", unrealisedTotalPnLMin=" ).append( getUnrealisedTotalPnLMin() );
        if ( Utils.hasVal( getFromLongRealisedTotalPnLMin() ) ) out.append( ", fromLongRealisedTotalPnLMin=" ).append( getFromLongRealisedTotalPnLMin() );
        if ( Utils.hasVal( getFromShortRealisedTotalPnLMin() ) ) out.append( ", fromShortRealisedTotalPnLMin=" ).append( getFromShortRealisedTotalPnLMin() );
        if ( Utils.hasVal( getUnrealisedTotalPnLMax() ) ) out.append( ", unrealisedTotalPnLMax=" ).append( getUnrealisedTotalPnLMax() );
        if ( Utils.hasVal( getFromLongRealisedTotalPnLMax() ) ) out.append( ", fromLongRealisedTotalPnLMax=" ).append( getFromLongRealisedTotalPnLMax() );
        if ( Utils.hasVal( getFromShortRealisedTotalPnLMax() ) ) out.append( ", fromShortRealisedTotalPnLMax=" ).append( getFromShortRealisedTotalPnLMax() );
        out.append( ", isActiveTracker=" ).append( getIsActiveTracker() );
        if ( Constants.UNSET_LONG != getId() && 0 != getId() )             out.append( ", id=" ).append( getId() );
        if ( Utils.hasVal( getFromLongRealisedTotalLongValue() ) ) out.append( ", fromLongRealisedTotalLongValue=" ).append( getFromLongRealisedTotalLongValue() );
        if ( Utils.hasVal( getFromLongRealisedTotalShortValue() ) ) out.append( ", fromLongRealisedTotalShortValue=" ).append( getFromLongRealisedTotalShortValue() );
        if ( Utils.hasVal( getFromShortRealisedTotalLongValue() ) ) out.append( ", fromShortRealisedTotalLongValue=" ).append( getFromShortRealisedTotalLongValue() );
        if ( Utils.hasVal( getFromShortRealisedTotalShortValue() ) ) out.append( ", fromShortRealisedTotalShortValue=" ).append( getFromShortRealisedTotalShortValue() );
        if ( Utils.hasVal( getUnrealisedTotalValue() ) ) out.append( ", unrealisedTotalValue=" ).append( getUnrealisedTotalValue() );
        if ( Utils.hasVal( getLastPrice() ) ) out.append( ", lastPrice=" ).append( getLastPrice() );
        if ( Utils.hasVal( getTotalTradeQty() ) ) out.append( ", totalTradeQty=" ).append( getTotalTradeQty() );
        if ( Utils.hasVal( getTotalTradeVal() ) ) out.append( ", totalTradeVal=" ).append( getTotalTradeVal() );
        if ( Utils.hasVal( getPointValue() ) ) out.append( ", pointValue=" ).append( getPointValue() );
        if ( Constants.UNSET_INT != getTotalLongOrders() && 0 != getTotalLongOrders() )             out.append( ", totalLongOrders=" ).append( getTotalLongOrders() );
        if ( Constants.UNSET_INT != getTotalShortOrders() && 0 != getTotalShortOrders() )             out.append( ", totalShortOrders=" ).append( getTotalShortOrders() );
        if ( Utils.hasVal( getBidPx() ) ) out.append( ", bidPx=" ).append( getBidPx() );
        if ( Utils.hasVal( getAskPx() ) ) out.append( ", askPx=" ).append( getAskPx() );
        if ( Utils.hasVal( getTotLongOpenQty() ) ) out.append( ", totLongOpenQty=" ).append( getTotLongOpenQty() );
        if ( Utils.hasVal( getTotShortOpenQty() ) ) out.append( ", totShortOpenQty=" ).append( getTotShortOpenQty() );
        if ( Constants.UNSET_INT != getNumTrades() && 0 != getNumTrades() )             out.append( ", numTrades=" ).append( getNumTrades() );
        if ( Utils.hasVal( getSplitAccrualQty() ) ) out.append( ", splitAccrualQty=" ).append( getSplitAccrualQty() );
        if ( Utils.hasVal( getSplitAccrualVal() ) ) out.append( ", splitAccrualVal=" ).append( getSplitAccrualVal() );
        if ( Utils.hasVal( getDivAccrualVal() ) ) out.append( ", divAccrualVal=" ).append( getDivAccrualVal() );
        if ( Constants.UNSET_INT != getPublishSeqNum() && 0 != getPublishSeqNum() )             out.append( ", publishSeqNum=" ).append( getPublishSeqNum() );
        if ( Constants.UNSET_INT != getSicFlags() && 0 != getSicFlags() )             out.append( ", sicFlags=" ).append( getSicFlags() );
    }

    @Override public final void snapTo( StratInstrumentState dest ) {
        ((StratInstrumentStateImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( StratInstrumentState src ) {
        setStratTimestamp( src.getStratTimestamp() );
        setInstrument( src.getInstrument() );
        setPosition( src.getPosition() );
        setUnrealisedTotalPnL( src.getUnrealisedTotalPnL() );
        setFromLongRealisedTotalPnL( src.getFromLongRealisedTotalPnL() );
        setFromShortRealisedTotalPnL( src.getFromShortRealisedTotalPnL() );
        setUnrealisedTotalPnLMin( src.getUnrealisedTotalPnLMin() );
        setFromLongRealisedTotalPnLMin( src.getFromLongRealisedTotalPnLMin() );
        setFromShortRealisedTotalPnLMin( src.getFromShortRealisedTotalPnLMin() );
        setUnrealisedTotalPnLMax( src.getUnrealisedTotalPnLMax() );
        setFromLongRealisedTotalPnLMax( src.getFromLongRealisedTotalPnLMax() );
        setFromShortRealisedTotalPnLMax( src.getFromShortRealisedTotalPnLMax() );
        setIsActiveTracker( src.getIsActiveTracker() );
        setId( src.getId() );
        setFromLongRealisedTotalLongValue( src.getFromLongRealisedTotalLongValue() );
        setFromLongRealisedTotalShortValue( src.getFromLongRealisedTotalShortValue() );
        setFromShortRealisedTotalLongValue( src.getFromShortRealisedTotalLongValue() );
        setFromShortRealisedTotalShortValue( src.getFromShortRealisedTotalShortValue() );
        setUnrealisedTotalValue( src.getUnrealisedTotalValue() );
        setLastPrice( src.getLastPrice() );
        setTotalTradeQty( src.getTotalTradeQty() );
        setTotalTradeVal( src.getTotalTradeVal() );
        setPointValue( src.getPointValue() );
        setTotalLongOrders( src.getTotalLongOrders() );
        setTotalShortOrders( src.getTotalShortOrders() );
        setBidPx( src.getBidPx() );
        setAskPx( src.getAskPx() );
        setTotLongOpenQty( src.getTotLongOpenQty() );
        setTotShortOpenQty( src.getTotShortOpenQty() );
        setNumTrades( src.getNumTrades() );
        setSplitAccrualQty( src.getSplitAccrualQty() );
        setSplitAccrualVal( src.getSplitAccrualVal() );
        setDivAccrualVal( src.getDivAccrualVal() );
        setPublishSeqNum( src.getPublishSeqNum() );
        setSicFlags( src.getSicFlags() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( StratInstrumentState src ) {
        setStratTimestamp( src.getStratTimestamp() );
        setInstrument( src.getInstrument() );
        setPosition( src.getPosition() );
        setUnrealisedTotalPnL( src.getUnrealisedTotalPnL() );
        setFromLongRealisedTotalPnL( src.getFromLongRealisedTotalPnL() );
        setFromShortRealisedTotalPnL( src.getFromShortRealisedTotalPnL() );
        setUnrealisedTotalPnLMin( src.getUnrealisedTotalPnLMin() );
        setFromLongRealisedTotalPnLMin( src.getFromLongRealisedTotalPnLMin() );
        setFromShortRealisedTotalPnLMin( src.getFromShortRealisedTotalPnLMin() );
        setUnrealisedTotalPnLMax( src.getUnrealisedTotalPnLMax() );
        setFromLongRealisedTotalPnLMax( src.getFromLongRealisedTotalPnLMax() );
        setFromShortRealisedTotalPnLMax( src.getFromShortRealisedTotalPnLMax() );
        setIsActiveTracker( src.getIsActiveTracker() );
        setId( src.getId() );
        setFromLongRealisedTotalLongValue( src.getFromLongRealisedTotalLongValue() );
        setFromLongRealisedTotalShortValue( src.getFromLongRealisedTotalShortValue() );
        setFromShortRealisedTotalLongValue( src.getFromShortRealisedTotalLongValue() );
        setFromShortRealisedTotalShortValue( src.getFromShortRealisedTotalShortValue() );
        setUnrealisedTotalValue( src.getUnrealisedTotalValue() );
        setLastPrice( src.getLastPrice() );
        setTotalTradeQty( src.getTotalTradeQty() );
        setTotalTradeVal( src.getTotalTradeVal() );
        setPointValue( src.getPointValue() );
        setTotalLongOrders( src.getTotalLongOrders() );
        setTotalShortOrders( src.getTotalShortOrders() );
        setBidPx( src.getBidPx() );
        setAskPx( src.getAskPx() );
        setTotLongOpenQty( src.getTotLongOpenQty() );
        setTotShortOpenQty( src.getTotShortOpenQty() );
        setNumTrades( src.getNumTrades() );
        setSplitAccrualQty( src.getSplitAccrualQty() );
        setSplitAccrualVal( src.getSplitAccrualVal() );
        setDivAccrualVal( src.getDivAccrualVal() );
        setPublishSeqNum( src.getPublishSeqNum() );
        setSicFlags( src.getSicFlags() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( StratInstrumentState src ) {
        if ( Constants.UNSET_LONG != src.getStratTimestamp() ) setStratTimestamp( src.getStratTimestamp() );
        if ( getInstrument() != null )  setInstrument( src.getInstrument() );
        if ( Utils.hasVal( src.getPosition() ) ) setPosition( src.getPosition() );
        if ( Utils.hasVal( src.getUnrealisedTotalPnL() ) ) setUnrealisedTotalPnL( src.getUnrealisedTotalPnL() );
        if ( Utils.hasVal( src.getFromLongRealisedTotalPnL() ) ) setFromLongRealisedTotalPnL( src.getFromLongRealisedTotalPnL() );
        if ( Utils.hasVal( src.getFromShortRealisedTotalPnL() ) ) setFromShortRealisedTotalPnL( src.getFromShortRealisedTotalPnL() );
        if ( Utils.hasVal( src.getUnrealisedTotalPnLMin() ) ) setUnrealisedTotalPnLMin( src.getUnrealisedTotalPnLMin() );
        if ( Utils.hasVal( src.getFromLongRealisedTotalPnLMin() ) ) setFromLongRealisedTotalPnLMin( src.getFromLongRealisedTotalPnLMin() );
        if ( Utils.hasVal( src.getFromShortRealisedTotalPnLMin() ) ) setFromShortRealisedTotalPnLMin( src.getFromShortRealisedTotalPnLMin() );
        if ( Utils.hasVal( src.getUnrealisedTotalPnLMax() ) ) setUnrealisedTotalPnLMax( src.getUnrealisedTotalPnLMax() );
        if ( Utils.hasVal( src.getFromLongRealisedTotalPnLMax() ) ) setFromLongRealisedTotalPnLMax( src.getFromLongRealisedTotalPnLMax() );
        if ( Utils.hasVal( src.getFromShortRealisedTotalPnLMax() ) ) setFromShortRealisedTotalPnLMax( src.getFromShortRealisedTotalPnLMax() );
        setIsActiveTracker( src.getIsActiveTracker() );
        if ( Constants.UNSET_LONG != src.getId() ) setId( src.getId() );
        if ( Utils.hasVal( src.getFromLongRealisedTotalLongValue() ) ) setFromLongRealisedTotalLongValue( src.getFromLongRealisedTotalLongValue() );
        if ( Utils.hasVal( src.getFromLongRealisedTotalShortValue() ) ) setFromLongRealisedTotalShortValue( src.getFromLongRealisedTotalShortValue() );
        if ( Utils.hasVal( src.getFromShortRealisedTotalLongValue() ) ) setFromShortRealisedTotalLongValue( src.getFromShortRealisedTotalLongValue() );
        if ( Utils.hasVal( src.getFromShortRealisedTotalShortValue() ) ) setFromShortRealisedTotalShortValue( src.getFromShortRealisedTotalShortValue() );
        if ( Utils.hasVal( src.getUnrealisedTotalValue() ) ) setUnrealisedTotalValue( src.getUnrealisedTotalValue() );
        if ( Utils.hasVal( src.getLastPrice() ) ) setLastPrice( src.getLastPrice() );
        if ( Utils.hasVal( src.getTotalTradeQty() ) ) setTotalTradeQty( src.getTotalTradeQty() );
        if ( Utils.hasVal( src.getTotalTradeVal() ) ) setTotalTradeVal( src.getTotalTradeVal() );
        if ( Utils.hasVal( src.getPointValue() ) ) setPointValue( src.getPointValue() );
        if ( Constants.UNSET_INT != src.getTotalLongOrders() ) setTotalLongOrders( src.getTotalLongOrders() );
        if ( Constants.UNSET_INT != src.getTotalShortOrders() ) setTotalShortOrders( src.getTotalShortOrders() );
        if ( Utils.hasVal( src.getBidPx() ) ) setBidPx( src.getBidPx() );
        if ( Utils.hasVal( src.getAskPx() ) ) setAskPx( src.getAskPx() );
        if ( Utils.hasVal( src.getTotLongOpenQty() ) ) setTotLongOpenQty( src.getTotLongOpenQty() );
        if ( Utils.hasVal( src.getTotShortOpenQty() ) ) setTotShortOpenQty( src.getTotShortOpenQty() );
        if ( Constants.UNSET_INT != src.getNumTrades() ) setNumTrades( src.getNumTrades() );
        if ( Utils.hasVal( src.getSplitAccrualQty() ) ) setSplitAccrualQty( src.getSplitAccrualQty() );
        if ( Utils.hasVal( src.getSplitAccrualVal() ) ) setSplitAccrualVal( src.getSplitAccrualVal() );
        if ( Utils.hasVal( src.getDivAccrualVal() ) ) setDivAccrualVal( src.getDivAccrualVal() );
        if ( Constants.UNSET_INT != src.getPublishSeqNum() ) setPublishSeqNum( src.getPublishSeqNum() );
        if ( Constants.UNSET_INT != src.getSicFlags() ) setSicFlags( src.getSicFlags() );
    }

}
