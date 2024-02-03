/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.lang.Constants;
import com.rr.core.utils.Utils;

/**
 * !!! WARNING !!! CHANGES IN THIS CLASS MUST BE REFLECTED IN LiquidityBookCustomCodec
 */
public final class LiquidityDeltaEntryImpl implements LiquidityDeltaEntry {

    public double _prevQty = Constants.UNSET_DOUBLE; // the originating qty for this delta
    public double _price   = Constants.UNSET_DOUBLE;
    public double _qty     = Constants.UNSET_DOUBLE;
    private double _addedLiquidity;
    private double _removedLiquidity;
    private double _tradedQty;
    private int    _numTrades;

    public LiquidityDeltaEntryImpl() {
    }

    @Override public void clear() {
        setQty( Constants.UNSET_DOUBLE );
        setPrevQty( Constants.UNSET_DOUBLE );
        _price = Constants.UNSET_DOUBLE;
        resetLiquidityCounters();
    }

    @Override public double getAddedLiquidity()              { return _addedLiquidity; }

    @Override public void setAddedLiquidity( final double qty ) {
        _addedLiquidity = qty;
        CHECK();
    }

    @Override public int getNumTrades()                      { return _numTrades; }

    @Override public void setNumTrades( final int qty ) { _numTrades = qty; }

    @Override public double getPrevQty()                     { return _prevQty; }

    @Override public void setPrevQty( final double prevQty ) { _prevQty = prevQty; }

    @Override public double getPrice()                       { return _price; }

    @Override public void setPrice( final double price ) { _price = price; }

    @Override public double getQty()                         { return _qty; }

    @Override public void setQty( final double qty ) {
        _qty = qty;
    }

    @Override public double getRemovedLiquidity()            { return _removedLiquidity; }

    @Override public void setRemovedLiquidity( final double qty ) {
        _removedLiquidity = qty;
        CHECK();
    }

    @Override public double getTradedQty()                   { return _tradedQty; }

    @Override public void setTradedQty( final double qty ) {
        _tradedQty = qty;
        CHECK();
    }

    @Override public boolean isActive() {
        if ( Utils.isNull( _price ) ) return false;

        boolean addedLiq   = Utils.hasNonZeroVal( getAddedLiquidity() );
        boolean removedLiq = Utils.hasNonZeroVal( getRemovedLiquidity() );
        boolean traded     = Utils.hasNonZeroVal( getTradedQty() );
        boolean qty        = Utils.hasNonZeroVal( getQty() );

        return addedLiq || removedLiq || traded || qty;
    }

    @Override public void resetLiquidityCounters() {
        _addedLiquidity   = 0;
        _removedLiquidity = 0;
        CHECK();
    }

    @Override public void resetTradeCounters() {
        _tradedQty = 0;
        _numTrades = 0;
        CHECK();
    }

    @Override public void set( final double latestQty, final double price ) {
        setQty( latestQty );
        _price = price;
    }

    @Override public void setLiquidityEntry( final LiquidityDeltaEntry that ) {

        CHECK();

        _addedLiquidity   = that.getAddedLiquidity();
        _removedLiquidity = that.getRemovedLiquidity();
        _tradedQty        = that.getTradedQty();
        _numTrades        = that.getNumTrades();

        setQty( that.getQty() );
        setPrevQty( that.getPrevQty() );

        _price = that.getPrice();
    }

    @Override public int hashCode() {
        int  result;
        long temp;
        temp   = Double.doubleToLongBits( _qty );
        result = (int) (temp ^ (temp >>> 32));
        temp   = Double.doubleToLongBits( _price );
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp   = Double.doubleToLongBits( _addedLiquidity );
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp   = Double.doubleToLongBits( _removedLiquidity );
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp   = Double.doubleToLongBits( _tradedQty );
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + _numTrades;
        return result;
    }

    @Override public boolean equals( final Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        final LiquidityDeltaEntryImpl that = (LiquidityDeltaEntryImpl) o;

        if ( Double.compare( that._qty, _qty ) != 0 ) return false;
        if ( Double.compare( that._price, _price ) != 0 ) return false;
        if ( Double.compare( that._addedLiquidity, _addedLiquidity ) != 0 ) return false;
        if ( Double.compare( that._removedLiquidity, _removedLiquidity ) != 0 ) return false;
        if ( Double.compare( that._tradedQty, _tradedQty ) != 0 ) return false;
        return _numTrades == that._numTrades;
    }

    @Override public String toString() {
        return "LiquidityDeltaEntryImpl{" +
               "_price=" + _price +
               ", _origQty=" + _prevQty +
               ", _qty=" + _qty +
               ", _addedLiquidity=" + _addedLiquidity +
               ", _removedLiquidity=" + _removedLiquidity +
               ", _tradedQty=" + _tradedQty +
               ", _numTrades=" + _numTrades +
               '}';
    }

    //    int _xxxx=0;
    private void CHECK() {
// ENABLE AND SET BREAKPOINT HERE
//     if ( Math.abs( _price - 3350.0 ) < 0.000005 && Utils.isZero(_addedLiquidity) && Utils.isZero(_removedLiquidity) && Utils.isZero(_tradedQty) ) {
//        ++_xxxx;
//     }
    }
}
