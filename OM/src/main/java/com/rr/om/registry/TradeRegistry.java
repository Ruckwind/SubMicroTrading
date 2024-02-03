/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.registry;

import com.rr.core.lang.ViewString;
import com.rr.model.generated.internal.events.interfaces.TradeCancel;
import com.rr.model.generated.internal.events.interfaces.TradeCorrect;
import com.rr.model.generated.internal.events.interfaces.TradeNew;
import com.rr.om.order.Order;

/**
 * trade registry tracks execIds so it can detect dup trades and stop them being sengt to client
 * <p>
 * most exchanges dont support trade correct or cancels, those that do will need to track every trade qty/px and execId
 *
 * @NOTE some exchanges dont provide unique execId, so for those need scope with the order
 */

public interface TradeRegistry {

    /**
     * clear the set recyling any entries
     */
    void clear();

    /**
     * @param execId
     * @return true if the execId is registered
     */
    boolean contains( Order order, ViewString execId );

    /**
     * @param execId
     * @return TradeWrapper for the execId IF the registry supports  trade storage
     * @NOTE dont hang onto or change the wrapper instance which is the object directly from set
     */
    TradeWrapper get( Order order, ViewString execId );

    /**
     * @return true if the execId is registered and the registry has its details
     */
    boolean hasDetails( Order order, ViewString execRefID );

    /**
     * @return true if this registry maintains all trade details
     */
    boolean hasTradeDetails();

    /**
     * register the trade correct
     *
     * @param msg
     * @return true if stored ok, false if DUPLICATE execId
     */
    boolean register( Order order, TradeCorrect msg );

    /**
     * register the trade
     *
     * @param msg
     * @return true if stored ok, false if DUPLICATE execId
     */
    boolean register( Order order, TradeNew msg );

    /**
     * register the trade cancel
     *
     * @param msg
     * @return true if stored ok, false if DUPLICATE execId
     */
    boolean register( Order order, TradeCancel msg );

    /**
     * @return number of entries in registry
     */
    int size();
}
