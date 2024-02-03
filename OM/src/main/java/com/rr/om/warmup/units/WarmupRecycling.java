/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup.units;

import com.rr.core.factories.ReusableStringFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;
import com.rr.core.pool.SuperPool;
import com.rr.core.recycler.ReusableStringRecycler;
import com.rr.core.warmup.JITWarmup;

public class WarmupRecycling implements JITWarmup {

    private int _warmupCount;

    public WarmupRecycling( int warmupCount ) {
        _warmupCount = warmupCount;
    }

    @Override
    public String getName() {
        return "Recycling";
    }

    @Override
    public void warmup() {
        int chains   = 2;
        int poolSize = 2;

        SuperPool<ReusableString> sp       = new SuperPool<>( ReusableString.class, chains, poolSize, poolSize );
        ReusableStringFactory     factory  = new ReusableStringFactory( sp );
        ReusableStringRecycler    recycler = new ReusableStringRecycler( sp.getChainSize(), sp );

        @SuppressWarnings( "unused" )
        ReusableType t;

        int cnt = poolSize * chains * _warmupCount;

        for ( int i = 0; i < cnt; i++ ) {
            ReusableString l = factory.get();
            recycler.recycle( l );
        }

        sp.deleteAll();
    }

}
