/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.mds.common;

import com.rr.core.lang.HasReusableType;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.recycler.EventRecycler;
import com.rr.mds.common.events.Subscribe;
import com.rr.mds.common.events.TradingRangeUpdate;
import com.rr.mds.common.events.TradingRangeUpdateRecycler;

public class MDSEventRecycler implements EventRecycler {

    private TradingRangeUpdateRecycler _tradingRangeUpdateRecycler;
    private Recycler<Subscribe>        _subscribeRecycler;

    public MDSEventRecycler() {
        SuperpoolManager sp = SuperpoolManager.instance();
        _tradingRangeUpdateRecycler = sp.getRecycler( TradingRangeUpdateRecycler.class, TradingRangeUpdate.class );
        _subscribeRecycler          = sp.getRecycler( Subscribe.class );
    }

    @Override
    public void recycle( HasReusableType msg ) {
        switch( msg.getReusableType().getSubId() ) {
        case MDSReusableTypeConstants.SUB_ID_SUBSCRIBE:
            _subscribeRecycler.recycle( (Subscribe) msg );
            break;
        case MDSReusableTypeConstants.SUB_ID_TRADING_BAND_UPDATE:
            _tradingRangeUpdateRecycler.recycle( (TradingRangeUpdate) msg );
            break;
        case MDSReusableTypeConstants.SUB_ID_FX_SNAPSHOT:
            break;
        case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_ACTIVE_BBO:
            break;
        case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_ACTIVE_DEPTH:
            break;
        case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_SNAPSHOT_BBO:
            break;
        case MDSReusableTypeConstants.SUB_ID_MARKET_DATA_SNAPSHOT_DEPTH:
            break;
        }
    }

    public void recycle( TradingRangeUpdate msg ) {
        _tradingRangeUpdateRecycler.recycle( msg );
    }
}
