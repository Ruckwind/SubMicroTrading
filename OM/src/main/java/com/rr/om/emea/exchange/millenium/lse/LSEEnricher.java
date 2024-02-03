/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.millenium.lse;

import com.rr.model.generated.internal.events.interfaces.MarketCancelReplaceRequestUpdate;
import com.rr.model.generated.internal.events.interfaces.MarketNewOrderSingleUpdate;
import com.rr.om.client.OMEnricher;
import com.rr.om.order.Order;

public class LSEEnricher implements OMEnricher {

    @Override
    public void enrich( Order order, MarketNewOrderSingleUpdate mnos ) {
        // TODO
    }

    @Override
    public void enrich( Order order, MarketCancelReplaceRequestUpdate mrep ) {
        // TODO Auto-generated method stub

    }
}
