package com.rr.core.utils.lock;

/**
 * Dummy lock used where it is known all readers / writers to the lock are on the same thread
 */
public class DummyOptimisticReadWriteLock implements OptimisticReadWriteLock {

    @Override public long readLock() {
        return 1;
    }

    @Override public long writeLock() {
        return 2;
    }

    @Override public void unlockRead( long stamp )  { /* nothing */ }

    @Override public void unlockWrite( long stamp ) { /* nothing */ }

    /**
     * Returns a stamp that can later be validated, or zero
     * if exclusively locked.
     *
     * @return a stamp, or zero if exclusively locked
     */
    @Override public long tryOptimisticRead() { return 3; }

    @Override public void unlock( long stamp )      { /* nothing */ }

    @Override public boolean validate( long stamp ) { return true; }
}
