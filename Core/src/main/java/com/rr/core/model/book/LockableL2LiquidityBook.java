/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableType;
import com.rr.core.model.Instrument;
import com.rr.core.utils.lock.OptimisticReadWriteLock;
import com.rr.core.utils.lock.StampedLockProxy;

/**
 * lockable book .. use this when book consumer is async and needs to snap book
 * <p>
 * locks are not reentrant !
 * <p>
 * appropriate locking needs to be done at a higher level
 * eg so BID/ASK sides can be updated atomically
 */
public final class LockableL2LiquidityBook extends BaseL2LiquidityBook {

    private final transient StampedLockProxy _lock = new StampedLockProxy();

    public LockableL2LiquidityBook() {
        super(); // for reflective use
    }

    public LockableL2LiquidityBook( Instrument inst, int numLevels ) {
        super( inst, numLevels );
    }

    @Override public OptimisticReadWriteLock getLock() {
        return _lock;
    }

    @Override public ReusableType getReusableType() { return CoreReusableType.LiqBookWithLock; }
}
