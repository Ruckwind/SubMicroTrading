/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.registry;

import com.rr.core.lang.ReusableType;
import com.rr.core.lang.ZString;
import com.rr.om.order.Order;

public interface TradeWrapper {

    /**
     * compares the trade wrappers using order and execId
     *
     * @return true if the same order and execId
     */
    boolean equals( TradeWrapper tw );

    ZString getClientExecId();

    /**
     * exchanges that dont provide unique execIds will have to generate unique id for client
     *
     * @param execId
     */
    void setClientExecId( ZString execId );

    ZString getExecId();

    TradeWrapper getNextWrapper();

    void setNextWrapper( TradeWrapper next );

    Order getOrder();

    double getPrice();

    double getQty();

    ReusableType getReusableType();
}
