/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book;

import com.rr.core.component.SMTComponent;
import com.rr.core.model.Instrument;
import com.rr.core.model.SnapableMktData;

/**
 * MarketDataSourceManager is used to find MarketDataSubscriptionManager for requested pipeline
 * <p>
 * For CME the pipelines provide a way to load balance and optimially process L3 updates
 * <p>
 * For non ULL envs and backtesting pipeLineId can be null ... tho for backtesting each backtest pipeline will have its own MDSrcMgr and subsMgr
 *
 * @param <T>
 */
public interface MktDataSrcMgr<T extends SnapableMktData> extends SMTComponent {

    default MktDataSubsMgr<T> findSubsMgr( Instrument inst ) { return findSubsMgr( inst, null ); }

    /**
     * find subscription manager for the instrument and pipeline
     *
     * @param inst
     * @param pipeLineId or null if not known or N/A
     * @return
     */
    MktDataSubsMgr<T> findSubsMgr( Instrument inst, String pipeLineId );
}
