/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.model.Instrument;
import com.rr.core.utils.lock.DummyOptimisticReadWriteLock;
import com.rr.core.utils.lock.OptimisticReadWriteLock;

/**
 * unsafe L1 book which ignore requests to apply locking
 */
public final class UnsafeL1Book extends BaseL1Book {

    private static final DummyOptimisticReadWriteLock _dummy = new DummyOptimisticReadWriteLock();

    public UnsafeL1Book( Instrument instrument ) {
        super( instrument );
    }

    @Override public OptimisticReadWriteLock getLock() {
        return _dummy;
    }
}
