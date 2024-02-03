/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.recovery;

import com.rr.core.lang.Constants;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.model.generated.internal.events.interfaces.OrderRequest;
import com.rr.om.order.Order;
import com.rr.om.order.OrderVersion;

import static org.junit.Assert.*;

/**
 * Base Recovery Test functions for passive (non HFT trading) Recovery
 */
public abstract class BasePassiveRecoveryTst extends BaseRecoveryTst {

    protected void checkPendingNewOrderRejected( NewOrderSingle req, Order order, int cumQty, double totalTraded ) {

        assertSame( _proc.getStateCompleted(), order.getState() );
        assertSame( req.getClient(), order.getClientProfile() );

        assertNull( order.getLastAckedVerion() );

        assertEquals( req.getClOrdId(), order.getClientClOrdIdChain() );
        assertNull( order.getMarketClOrdIdChain() );

        OrderVersion v = order.getPendingVersion();

        checkFullVersion( req, v, cumQty, totalTraded );
    }

    private void checkFullVersion( OrderRequest expReq, OrderVersion v, int cumQty, double totalTraded ) {
        assertEquals( cumQty, v.getCumQty(), Constants.TICK_WEIGHT );
        assertEquals( totalTraded, v.getTotalTraded(), 0.0000005 );
        assertEquals( (totalTraded / cumQty), v.getAvgPx(), 0.0000005 );

        OrderRequest req = (OrderRequest) v.getBaseOrderRequest();

        assertEquals( expReq.getClOrdId(), req.getClOrdId() );
        assertEquals( expReq.getOrderQty(), req.getOrderQty(), Constants.TICK_WEIGHT );
        assertEquals( expReq.getPrice(), req.getPrice(), 0.0000005 );
        assertEquals( expReq.getOrderCapacity(), req.getOrderCapacity() );
        assertEquals( expReq.getOrdType(), req.getOrdType() );
        assertEquals( expReq.getOrigClOrdId(), req.getOrigClOrdId() );
        assertEquals( expReq.getClient(), req.getClient() );
        assertEquals( expReq.getSide(), req.getSide() );
        assertSame( expReq.getInstrument(), req.getInstrument() );
        assertSame( expReq.getTimeInForce(), req.getTimeInForce() );
        assertSame( expReq.getSymbol(), req.getSymbol() );
        assertSame( expReq.getParentClOrdId(), req.getParentClOrdId() );
    }
}
