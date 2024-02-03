/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book;

import com.rr.core.model.Context;
import com.rr.core.model.Instrument;
import com.rr.core.model.MktDataWithContext;

/**
 * MarketDataSourceManager for a single MarketDataSubscriptionMgr
 * <p>
 * As used in backtesting and non ULL envs
 *
 * @param <T>
 * @param <C>
 */
public class SingleMDSrcManager<T extends MktDataWithContext, C extends Context> implements MktDataSrcMgr<T> {

    private final     String            _id;
    private transient MktDataSubsMgr<T> _mdSubMgr;

    public SingleMDSrcManager( String id ) {
        _id = id;
    }

    public SingleMDSrcManager( String id, MktDataSubsMgr<T> subsMgr ) {
        _id       = id;
        _mdSubMgr = subsMgr;
    }

    @Override public MktDataSubsMgr<T> findSubsMgr( Instrument inst, String pipeLineId ) { return _mdSubMgr; }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public String toString() {
        return "SingleMDSrcManager{ " + _id + '\'' + " }";
    }
}
