/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.lang.ReusableString;

public interface DoubleSidedBookEntry {

    void dump( ReusableString dest );

    double getAskPx();

    double getAskQty();

    double getBidPx();

    double getBidQty();

    int getNumBuyOrders();

    int getNumSellOrders();

    boolean isAskIsDirty();

    boolean isBidIsDirty();

    /**
     * @return true if bid and ask not dirty and bid < ask
     */
    boolean isValid();

    /**
     * reset values back to zero
     */
    void reset();

    void set( int numBuyOrders, double bidQty, double bidPrice, boolean bidIsDirty, int numSellOrders, double askQty, double askPrice, boolean askIsDirty );

    void set( double bidQty, double bidPrice, boolean bidIsDirty, double askQty, double askPrice, boolean askIsDirty );

    void set( BookLevelEntry bid, BookLevelEntry ask );
}
