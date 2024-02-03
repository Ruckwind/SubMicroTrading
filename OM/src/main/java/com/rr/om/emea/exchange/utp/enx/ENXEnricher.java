/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.utp.enx;

import com.rr.model.generated.internal.events.interfaces.MarketCancelReplaceRequestUpdate;
import com.rr.model.generated.internal.events.interfaces.MarketNewOrderSingleUpdate;
import com.rr.model.generated.internal.type.OrderCapacity;
import com.rr.om.client.OMEnricher;
import com.rr.om.order.Order;

public class ENXEnricher implements OMEnricher {

    @Override
    public void enrich( Order order, MarketNewOrderSingleUpdate mnos ) {
        if ( mnos.getOrderCapacity() == null ) {
            mnos.setOrderCapacity( OrderCapacity.Principal ); // ENX has madantory order capacity
        }
    }

    @Override
    public void enrich( Order order, MarketCancelReplaceRequestUpdate mrep ) {
        if ( mrep.getOrderCapacity() == null ) {
            mrep.setOrderCapacity( OrderCapacity.Principal ); // ENX has madantory order capacity
        }
    }
}
