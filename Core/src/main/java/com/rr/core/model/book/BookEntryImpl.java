/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.lang.Constants;
import com.rr.core.utils.Utils;

public final class BookEntryImpl implements BookLevelEntry {

    public boolean _dirty     = true;
    /**
     * !!! WARNING !!! CHANGES IN THIS CLASS MUST BE REFLECTED IN LiquidityBookCustomCodec & L2BookCustomCodec
     */

    public int     _numOrders = Constants.UNSET_INT;
    public double  _price     = Constants.UNSET_DOUBLE;
    public double  _qty;

    @Override public boolean equals( final Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        final BookEntryImpl bookEntry = (BookEntryImpl) o;

        if ( Utils.compare( bookEntry._qty, _qty ) != 0 ) return false;
        if ( Utils.compare( bookEntry._price, _price ) != 0 ) return false;
        if ( bookEntry._numOrders != _numOrders ) return false;

        return _dirty == bookEntry._dirty;
    }

    @Override public String toString() {
        return "BookEntryImpl [qty=" + _qty + ", price=" + _price + ", dirty=" + _dirty + "]";
    }

    @Override public int getNumOrders() { return _numOrders; }

    @Override public double getPrice() {
        return _price;
    }

    @Override public double getQty()    { return _qty; }

    @Override public void set( final int numOrders, final double qty, final double price ) {
        set( numOrders, qty, price, false );
    }

    @Override public void set( final double qty, final double price ) {
        set( Constants.UNSET_INT, qty, price, false );
    }

    @Override public void set( final double qty, final double price, final boolean isDirty ) {
        set( Constants.UNSET_INT, qty, price, isDirty );
    }

    @Override public void set( final int numOrders, final double qty, final double price, boolean isDirty ) {
        _numOrders = numOrders;
        _qty       = qty;
        _price     = price;
        _dirty     = isDirty;
    }

    @Override public void set( final BookLevelEntry that ) {
        _qty       = that.getQty();
        _price     = that.getPrice();
        _dirty     = that.isDirty();
        _numOrders = that.getNumOrders();
    }

    public void setQty( double qty ) {
        _qty   = qty;
        _dirty = false;
    }

    public void setPrice( double price ) {
        _price = price;
        _dirty = false;
    }

    @Override public void setNumOrders( final int numOrders ) {
        _numOrders = numOrders;
    }

    @Override public boolean isDirty() {
        return _dirty;
    }

    public void setDirty( boolean isDirty ) {
        _dirty = isDirty;
    }

    @Override public boolean isValid() {
        return !_dirty && _price != 0.0;
    }

    public void clear() {
        _qty       = 0;
        _price     = Constants.UNSET_DOUBLE;
        _dirty     = true;
        _numOrders = Constants.UNSET_INT;
    }
}
