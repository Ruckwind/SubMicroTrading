/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.lang.ReusableString;
import com.rr.om.order.OrderImpl;
import com.rr.om.order.OrderVersion;

public class OMUtils {

    public static OrderImpl mkOrder( ReusableString clOrdId, int cumQty ) {
        OrderImpl    order = new OrderImpl();
        OrderVersion ver   = new OrderVersion();
        ver.setCumQty( cumQty );
        order.setPendingVersion( ver );
        order.setLastAckedVerion( ver );
        order.registerClientClOrdId( new ReusableString( clOrdId ) );

        return order;
    }
}
