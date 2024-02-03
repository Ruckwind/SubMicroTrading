/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om;

import com.rr.model.generated.internal.events.interfaces.MarketCancelReplaceRequestUpdate;
import com.rr.model.generated.internal.events.interfaces.MarketNewOrderSingleUpdate;
import com.rr.model.generated.internal.type.OrderCapacity;
import com.rr.om.client.OMEnricher;
import com.rr.om.order.Order;

public class BaseEnricher implements OMEnricher {

    @Override
    public void enrich( Order order, MarketNewOrderSingleUpdate mnos ) {
        OrderCapacity overrideCap = OrderCapacity.Principal;

        mnos.setOrderCapacity( overrideCap );
        order.getPendingVersion().setMarketCapacity( overrideCap );
    }

    @Override
    public void enrich( Order order, MarketCancelReplaceRequestUpdate mrep ) {
        OrderCapacity overrideCap = OrderCapacity.Principal;

        mrep.setOrderCapacity( overrideCap );
        order.getPendingVersion().setMarketCapacity( overrideCap );
    }
}
