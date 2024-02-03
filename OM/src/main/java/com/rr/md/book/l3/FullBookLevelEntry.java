/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book.l3;

import com.rr.core.lang.*;
import com.rr.core.model.book.BookLevelEntry;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.Utils;

import static com.rr.md.book.l3.PitchL3OrderBook.DUMMY_ASK;
import static com.rr.md.book.l3.PitchL3OrderBook.DUMMY_BID;

/**
 * Order book entry for L3 order books
 *
 * @author Richard Rose
 * @TBI number of orders
 */
public final class FullBookLevelEntry implements Reusable<FullBookLevelEntry>, BookLevelEntry {

    // this type will not be used in a generic recycler
    private static final ReusableType _type = new SingularReusableType( "FullOrderBookEntry", ReusableCategoryEnum.MDS );

    private double             _price;
    private double             _totalQty = 0;
    private FullBookLevelEntry _next     = null;
    private FullBookLevelEntry _prev     = null;

    public FullBookLevelEntry() {
    }

    public FullBookLevelEntry( double price ) {
        _price = price;
    }

    @Override public FullBookLevelEntry getNext() { return _next; }

    @Override public void setNext( FullBookLevelEntry next ) {
        _next = next;
    }

    @Override public int getNumOrders() { return Constants.UNSET_INT; }

    @Override public double getPrice() { return _price; }

    public void setPrice( double price ) {
        if ( price == DUMMY_BID || price == DUMMY_ASK ) {
            throw new SMTRuntimeException( "overriding fixed element" );
        }
        _price = price;
    }

    @Override public double getQty()              { return _totalQty; }

    void setQty( double qty ) {
        if ( qty < 0 ) {
            qty = 0;
        }
        _totalQty = qty;
    }

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
        setQty( qty );
        setPrice( price );
    }

    @Override
    public void set( BookLevelEntry that ) {
        setPrice( that.getPrice() );
        setQty( that.getQty() );
    }

    @Override public void setNumOrders( final int numOrders ) { /*nothing */ }

    @Override public boolean isDirty() {
        return false;
    }

    @Override public boolean isValid() {
        return Utils.hasVal( _price ) && _totalQty > 0;
    }

    @Override public ReusableType getReusableType() {
        return _type;
    }

    @Override public void reset() {
        _price    = 0.0;
        _totalQty = 0;
        _next     = null;
        _prev     = null;
    }

    @Override public String toString() {
        return "FullBookLevelEntry{" +
               "_price=" + _price +
               ", _totalQty=" + _totalQty +
               '}';
    }

    void addQty( double qty ) {
        setQty( _totalQty + qty );
    }

    FullBookLevelEntry getPrev()                  { return _prev; }

    void setPrev( FullBookLevelEntry prev ) {
        _prev = prev;
    }

    void removeQty( double qty ) {
        setQty( _totalQty - qty );
    }
}
