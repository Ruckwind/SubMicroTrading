/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order;

import com.rr.core.lang.ReusableString;
import com.rr.core.model.EventHandler;
import com.rr.core.model.Exchange;
import com.rr.om.client.OMClientProfile;
import com.rr.om.processor.states.OrderState;

/**
 * Order
 *
 * @NOTE cancels treated specially, when order cancel acked, the cancel version is recycled, we keep the full last accepted version
 * BUT note the clOrdId on this is NOT the one from the cancel so BEWARE  .. will be issue for BUSTS so force BUSTS thru Master process
 */
public interface Order {

    /**
     * write details about current order into the reusable buffer
     * should generate NO temp objects
     *
     * @param buf
     */
    void appendDetails( ReusableString buf );

    /*
     * the current clOrdId, only one pending message is allowed, so first in chain is either pending or the lastAccepted id
     */
    ReusableString getClientClOrdIdChain();

    OMClientProfile getClientProfile();

    EventHandler getDownstreamHandler();

    void setDownstreamHandler( EventHandler stickyDownstreamHandler );

    Exchange getExchange();

    OrderVersion getLastAckedVerion();

    /**
     * set the current / last accepted version, will only be a NEW or AMEND event never the part populated cancel
     *
     * @param ver
     */
    void setLastAckedVerion( OrderVersion ver );

    /**
     * if the mkt clOrdId is same as client then the market clOrdId chain is NULL
     *
     * @return the first link in chain or null
     */
    ReusableString getMarketClOrdIdChain();

    OrderVersion getPendingVersion();

    /**
     * set a pending version, if same as lastAccepted then order is NOT in pending cancel or pending replace state
     *
     * @param ver
     */
    void setPendingVersion( OrderVersion ver );

    /**
     * @return the current order state
     */
    OrderState getState();

    void registerClientClOrdId( ReusableString clientClOrdId );

    void registerMarketClOrdId( ReusableString marketClOrdId );

    /**
     * set the order state, will invoke onExit from previous state
     * then onEnter for this new state
     *
     * @param state
     * @return previous state
     */
    OrderState setState( OrderState state );
}
