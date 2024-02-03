/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils.lock;

/**
 * Abstraction of StampedLock so we can have DummyLock and switch in alternative implementations in future
 */
public interface OptimisticReadWriteLock {

    long readLock();

    /**
     * Returns a stamp that can later be validated, or zero
     * if exclusively locked.
     *
     * @return a stamp, or zero if exclusively locked
     */
    long tryOptimisticRead();

    void unlock( long stamp );

    void unlockRead( long stamp );

    void unlockWrite( long stamp );

    boolean validate( long stamp );

    long writeLock();
}
