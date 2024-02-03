package com.rr.core.lang;

import com.rr.core.utils.lock.DummyOptimisticReadWriteLock;
import com.rr.core.utils.lock.OptimisticReadWriteLock;

public interface Lockable {

    DummyOptimisticReadWriteLock DUMMY_LOCK = new DummyOptimisticReadWriteLock();

    /**
     * @return the lock which will be either proxy to StampedLock or a Dummy lock depending on wether book has safe or unsafe concurrency threading requirements
     */
    default OptimisticReadWriteLock getLock() { return DUMMY_LOCK; }
}
