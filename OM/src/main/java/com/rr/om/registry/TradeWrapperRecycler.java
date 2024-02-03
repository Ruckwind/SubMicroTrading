/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.registry;

import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.om.order.OrderReusableType;

public class TradeWrapperRecycler {

    private final Recycler<TradeWrapperImpl>        _tradeWrapperRecycler;
    private final Recycler<TradeCorrectWrapperImpl> _tradeCorrectWrapperRecycler;

    public TradeWrapperRecycler() {

        SuperPool<TradeWrapperImpl>        spt = SuperpoolManager.instance().getSuperPool( TradeWrapperImpl.class );
        SuperPool<TradeCorrectWrapperImpl> spc = SuperpoolManager.instance().getSuperPool( TradeCorrectWrapperImpl.class );

        _tradeWrapperRecycler        = spt.getRecycleFactory();
        _tradeCorrectWrapperRecycler = spc.getRecycleFactory();
    }

    public void recycle( TradeWrapper w ) {
        if ( w.getReusableType() == OrderReusableType.TradeWrapper ) {
            _tradeWrapperRecycler.recycle( (TradeWrapperImpl) w );
        } else {
            _tradeCorrectWrapperRecycler.recycle( (TradeCorrectWrapperImpl) w );
        }
    }
}
