/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book.l3;

import com.rr.core.lang.*;

/**
 * orderId not included as thats the key in the orderEntry map
 *
 * @author Richard Rose
 */
public final class OrderBookEntry implements Reusable<OrderBookEntry> {

    // this type will not be used in a generic recycler
    private static final ReusableType _type = new SingularReusableType( "OrderBookEntry", ReusableCategoryEnum.MDS );

    private double         _price;
    private double         _qty;
    private OrderBookEntry _next;
    private boolean        _isBuySide;

    public OrderBookEntry() {
    }

    @Override public OrderBookEntry getNext() {
        return _next;
    }

    @Override public void setNext( OrderBookEntry nxt ) {
        _next = nxt;
    }

    @Override public ReusableType getReusableType() {
        return _type;
    }

    @Override public void reset() {
        _price     = Constants.UNSET_DOUBLE;
        _qty       = 0;
        _next      = null;
        _isBuySide = isBuySide();
    }

    public double getPrice()                   { return _price; }

    public void setPrice( final double price ) { _price = price; }

    public double getQty() {
        return _qty;
    }

    public void setQty( double qty )           { _qty = qty; }

    public boolean isBuySide() { return _isBuySide; }

    public void setBuySide( boolean isBuySide ) {
        _isBuySide = isBuySide;
    }
}
