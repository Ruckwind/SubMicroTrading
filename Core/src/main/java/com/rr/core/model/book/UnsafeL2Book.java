/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.model.Instrument;
import com.rr.core.utils.lock.DummyOptimisticReadWriteLock;
import com.rr.core.utils.lock.OptimisticReadWriteLock;

/**
 * for use when only read and write to book on same thread
 */
public final class UnsafeL2Book extends BaseL2Book {

    private final transient DummyOptimisticReadWriteLock _dummy = new DummyOptimisticReadWriteLock();

    public UnsafeL2Book( Instrument instrument, int maxLevels ) {
        super( instrument, maxLevels );
    }

    @Override public OptimisticReadWriteLock getLock() {
        return _dummy;
    }
}
