/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

public interface BookLevelEntry {

    /**
     * @return number of orders or Constants.UNSET_INT if unknown
     */
    int getNumOrders();

    void setNumOrders( int numOrders );

    double getPrice();

    double getQty();

    boolean isDirty();

    boolean isValid();

    void set( int numOrders, double qty, double price );

    void set( double qty, double price );

    void set( double qty, double price, boolean isDirty );

    void set( int numOrders, double qty, double price, boolean isDirty );

    void set( BookLevelEntry that );
}
