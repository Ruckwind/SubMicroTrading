/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.client;

import com.rr.core.model.Enricher;
import com.rr.model.generated.internal.events.interfaces.MarketCancelReplaceRequestUpdate;
import com.rr.model.generated.internal.events.interfaces.MarketNewOrderSingleUpdate;
import com.rr.om.order.Order;

public interface OMEnricher extends Enricher {

    /**
     * run exchange specific enrichment for client to market messages
     * <p>
     * eg set market specific order capacity
     * <p>
     * if change event fields in mnos then must update the version copies as well
     *
     * @param order
     * @param mnos
     */
    void enrich( Order order, MarketNewOrderSingleUpdate mnos );

    /**
     * run exchange specific enrichment for client to market messages
     * <p>
     * eg set market specific order capacity
     * <p>
     * if change event fields in mrep then must update the version copies as well
     *
     * @param order
     * @param mrep
     */
    void enrich( Order order, MarketCancelReplaceRequestUpdate mrep );
}
