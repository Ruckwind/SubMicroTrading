/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.utils.Utils;

public final class DoubleSidedBookEntryImpl implements DoubleSidedBookEntry {

    private static final double UNSET = Constants.UNSET_DOUBLE;
    public  double  _askPrice   = UNSET;
    public  double  _askQty;
    public  double  _bidPrice   = UNSET;
    public  double  _bidQty;
    public  int     _numBuyOrders;
    public  int     _numSellOrders;
    private boolean _bidIsDirty = true;
    private boolean _askIsDirty = true;

    @Override public double getBidPx() {
        return _bidPrice;
    }

    @Override public double getBidQty() {
        return _bidQty;
    }

    public void setBidQty( double bidQty ) {
        _bidQty = bidQty;
    }

    @Override public int getNumBuyOrders()  { return _numBuyOrders; }

    @Override public double getAskPx() {
        return _askPrice;
    }

    @Override public double getAskQty() {
        return _askQty;
    }

    public void setAskQty( double askQty ) {
        _askQty = askQty;
    }

    @Override public int getNumSellOrders() { return _numSellOrders; }

    public void setNumSellOrders( final int numSellOrders ) { _numSellOrders = numSellOrders; }

    @Override public void set( final int numBuyOrders, final double bidQty, final double bidPrice, final boolean bidIsDirty, final int numSellOrders, final double askQty, final double askPrice, final boolean askIsDirty ) {
        _bidQty       = bidQty;
        _bidPrice     = bidPrice;
        _bidIsDirty   = bidIsDirty;
        _numBuyOrders = numBuyOrders;

        _askQty        = askQty;
        _askPrice      = askPrice;
        _askIsDirty    = askIsDirty;
        _numSellOrders = numSellOrders;
    }

    @Override public void set( final double bidQty, final double bidPrice, final boolean bidIsDirty, final double askQty, final double askPrice, final boolean askIsDirty ) {
        set( Constants.UNSET_INT, bidQty, bidPrice, bidIsDirty, Constants.UNSET_INT, askQty, askPrice, askIsDirty );
    }

    @Override public void set( BookLevelEntry bid, BookLevelEntry ask ) {
        _numBuyOrders = bid.getNumOrders();
        _bidQty       = bid.getQty();
        _bidPrice     = bid.getPrice();
        _bidIsDirty   = bid.isDirty();

        _numSellOrders = ask.getNumOrders();
        _askQty        = ask.getQty();
        _askPrice      = ask.getPrice();
        _askIsDirty    = ask.isDirty();
    }

    @Override public void reset() {
        _bidQty        = 0;
        _numBuyOrders  = Constants.UNSET_INT;
        _numSellOrders = Constants.UNSET_INT;
        _bidPrice      = UNSET;
        _askQty        = 0;
        _askPrice      = UNSET;
        _askIsDirty    = true;
        _bidIsDirty    = true;
    }

    @Override public void dump( final ReusableString dest ) {
        dest.append( _bidIsDirty ? "d " : "  " );
        dest.append( _bidQty ).append( " x " ).append( _bidPrice ).append( "  :  " );
        dest.append( _askPrice ).append( " x " ).append( _askQty );
        dest.append( _askIsDirty ? " d" : "  " );
    }

    @Override public boolean isValid() {
        return ((_bidPrice != UNSET) && (_askPrice != UNSET) && (_askIsDirty == false) && (_bidIsDirty == false));
    }

    @Override public final boolean isBidIsDirty() {
        return _bidIsDirty;
    }

    @Override public final boolean isAskIsDirty() {
        return _askIsDirty;
    }

    public void setAskIsDirty( final boolean askIsDirty ) { _askIsDirty = askIsDirty; }

    public void setBidIsDirty( final boolean bidIsDirty ) { _bidIsDirty = bidIsDirty; }

    public void setNumBuyOrders( final int numBuyOrders )   { _numBuyOrders = numBuyOrders; }

    @Override public int hashCode() {
        int  result;
        long temp;
        temp   = Double.doubleToLongBits( _bidQty );
        result = (int) (temp ^ (temp >>> 32));
        temp   = Double.doubleToLongBits( _bidPrice );
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp   = Double.doubleToLongBits( _askQty );
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp   = Double.doubleToLongBits( _askPrice );
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (_bidIsDirty ? 1 : 0);
        result = 31 * result + (_askIsDirty ? 1 : 0);
        return result;
    }

    @Override public boolean equals( final Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        final DoubleSidedBookEntryImpl that = (DoubleSidedBookEntryImpl) o;

        if ( Utils.compare( that._bidQty, _bidQty ) != 0 ) return false;
        if ( Utils.compare( that._bidPrice, _bidPrice ) != 0 ) return false;
        if ( Utils.compare( that._askQty, _askQty ) != 0 ) return false;
        if ( Utils.compare( that._askPrice, _askPrice ) != 0 ) return false;

        if ( that._numBuyOrders != _numBuyOrders ) return false;
        if ( that._numSellOrders != _numSellOrders ) return false;

        if ( _bidIsDirty != that._bidIsDirty ) return false;
        return _askIsDirty == that._askIsDirty;
    }

    @Override public String toString() {
        ReusableString buf = new ReusableString();
        dump( buf );
        return buf.toString();
    }

    public void setAskPrice( double askPrice ) {
        _askPrice = askPrice;
    }

    public void setBidPrice( double bidPrice ) {
        _bidPrice = bidPrice;
    }
}
