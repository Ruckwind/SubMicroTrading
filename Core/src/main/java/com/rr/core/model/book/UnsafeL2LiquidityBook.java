/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableType;
import com.rr.core.model.DeepCopy;
import com.rr.core.model.Instrument;
import com.rr.core.utils.lock.DummyOptimisticReadWriteLock;
import com.rr.core.utils.lock.OptimisticReadWriteLock;

/**
 * for use when only read and write to book on same thread
 */
public final class UnsafeL2LiquidityBook extends BaseL2LiquidityBook<UnsafeL2LiquidityBook> implements DeepCopy<UnsafeL2LiquidityBook> {

    private final transient DummyOptimisticReadWriteLock _dummy = new DummyOptimisticReadWriteLock();

    public UnsafeL2LiquidityBook() {
        super();
    }

    public UnsafeL2LiquidityBook( Instrument instrument, int maxLevels ) {
        super( instrument, maxLevels, maxLevels );
    }

    public UnsafeL2LiquidityBook( Instrument instrument, int maxBookLevels, int maxLiqLevels ) {
        super( instrument, maxBookLevels, maxLiqLevels );
    }

    @Override public void deepCopyFrom( final UnsafeL2LiquidityBook src ) {
        if ( src != null ) {
            src.snapTo( this );
        } else {
            reset();
        }
    }

    @Override public OptimisticReadWriteLock getLock() {
        return _dummy;
    }

    @Override public ReusableType getReusableType() { return CoreReusableType.LiqBookNoLock; }
}
