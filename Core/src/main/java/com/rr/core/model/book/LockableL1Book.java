/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.model.Instrument;
import com.rr.core.utils.lock.OptimisticReadWriteLock;
import com.rr.core.utils.lock.StampedLockProxy;

public final class LockableL1Book extends BaseL1Book {

    private final transient StampedLockProxy _lock = new StampedLockProxy();

    public LockableL1Book( Instrument inst ) {
        super( inst );
    }

    @Override public OptimisticReadWriteLock getLock() {
        return _lock;
    }
}
