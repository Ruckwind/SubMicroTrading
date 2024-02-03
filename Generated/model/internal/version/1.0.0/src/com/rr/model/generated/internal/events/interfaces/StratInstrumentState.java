package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;
import com.rr.model.internal.type.SubEvent;

@SuppressWarnings( { "unused", "override"  })

public interface StratInstrumentState extends SubEvent, com.rr.core.model.PointInTime {

   // Getters and Setters
    long getStratTimestamp();

    Instrument getInstrument();

    /**
     *units of instrument currently held
     */
    double getPosition();

    double getUnrealisedTotalPnL();

    double getFromLongRealisedTotalPnL();

    double getFromShortRealisedTotalPnL();

    double getUnrealisedTotalPnLMin();

    double getFromLongRealisedTotalPnLMin();

    double getFromShortRealisedTotalPnLMin();

    double getUnrealisedTotalPnLMax();

    double getFromLongRealisedTotalPnLMax();

    double getFromShortRealisedTotalPnLMax();

    boolean getIsActiveTracker();

    long getId();

    double getFromLongRealisedTotalLongValue();

    double getFromLongRealisedTotalShortValue();

    double getFromShortRealisedTotalLongValue();

    double getFromShortRealisedTotalShortValue();

    double getUnrealisedTotalValue();

    double getLastPrice();

    double getTotalTradeQty();

    double getTotalTradeVal();

    double getPointValue();

    int getTotalLongOrders();

    int getTotalShortOrders();

    double getBidPx();

    double getAskPx();

    double getTotLongOpenQty();

    double getTotShortOpenQty();

    int getNumTrades();

    /**
     *used for pending split qty
     */
    double getSplitAccrualQty();

    /**
     *used for pending split
     */
    double getSplitAccrualVal();

    /**
     *used for pending cash dividend
     */
    double getDivAccrualVal();

    int getPublishSeqNum();

    int getSicFlags();

    @Override void dump( ReusableString out );

    void setStratTimestamp( long val );

    void setInstrument( Instrument val );

    void setPosition( double val );

    void setUnrealisedTotalPnL( double val );

    void setFromLongRealisedTotalPnL( double val );

    void setFromShortRealisedTotalPnL( double val );

    void setUnrealisedTotalPnLMin( double val );

    void setFromLongRealisedTotalPnLMin( double val );

    void setFromShortRealisedTotalPnLMin( double val );

    void setUnrealisedTotalPnLMax( double val );

    void setFromLongRealisedTotalPnLMax( double val );

    void setFromShortRealisedTotalPnLMax( double val );

    void setIsActiveTracker( boolean val );

    void setId( long val );

    void setFromLongRealisedTotalLongValue( double val );

    void setFromLongRealisedTotalShortValue( double val );

    void setFromShortRealisedTotalLongValue( double val );

    void setFromShortRealisedTotalShortValue( double val );

    void setUnrealisedTotalValue( double val );

    void setLastPrice( double val );

    void setTotalTradeQty( double val );

    void setTotalTradeVal( double val );

    void setPointValue( double val );

    void setTotalLongOrders( int val );

    void setTotalShortOrders( int val );

    void setBidPx( double val );

    void setAskPx( double val );

    void setTotLongOpenQty( double val );

    void setTotShortOpenQty( double val );

    void setNumTrades( int val );

    void setSplitAccrualQty( double val );

    void setSplitAccrualVal( double val );

    void setDivAccrualVal( double val );

    void setPublishSeqNum( int val );

    void setSicFlags( int val );

}
