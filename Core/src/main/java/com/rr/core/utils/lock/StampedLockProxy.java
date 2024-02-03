package com.rr.core.utils.lock;

import java.util.concurrent.locks.StampedLock;

public class StampedLockProxy implements OptimisticReadWriteLock {

    private final transient StampedLock _lock = new StampedLock();

    @Override public long readLock() {
        return _lock.readLock();
    }

    @Override public long tryOptimisticRead() {
        return _lock.tryOptimisticRead();
    }

    @Override public void unlock( long stamp ) {
        _lock.unlock( stamp );
    }

    @Override public void unlockRead( long stamp ) {
        _lock.unlockRead( stamp );
    }

    @Override public void unlockWrite( long stamp ) {
        _lock.unlockWrite( stamp );
    }

    @Override public boolean validate( long stamp ) {
        return _lock.validate( stamp );
    }

    @Override public long writeLock() {
        return _lock.writeLock();
    }

}
