/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils.lock;

import com.rr.core.lang.Procedure;
import com.rr.core.lang.ZSupplier;

/**
 * encapsulation of the locking pattern to make locking easier
 */
public class LockUtils {

    /**
     * take out a write lock then invoke the supplier lambda then unlock
     *
     * @param supplier function which takes no args but returns a value
     * @param <T>      type of return result from supplier
     * @return
     */
    public static <T> T applyWithWriteLock( OptimisticReadWriteLock lock, ZSupplier<T> supplier ) {
        long stamp = lock.writeLock();
        try {
            return supplier.get();
        } finally {
            lock.unlockWrite( stamp );
        }
    }

    /**
     * take out a write lock then invoke the supplier lambda then unlock
     *
     * @param procedure function which takes no args and returns no vals
     * @return
     */
    public static void applyWithWriteLock( OptimisticReadWriteLock lock, Procedure procedure ) {
        long stamp = lock.writeLock();
        try {
            procedure.invoke();
        } finally {
            lock.unlockWrite( stamp );
        }
    }

    /**
     * take out a read lock then invoke the supplier lambda then unlock
     *
     * @param supplier function which takes no args but returns a value
     * @param <T>      type of return result from supplier
     * @return
     */
    public static <T> T applyWithReadLock( OptimisticReadWriteLock lock, ZSupplier<T> supplier ) {
        final long stamp = lock.readLock();
        try {
            return supplier.get();
        } finally {
            lock.unlockRead( stamp );
        }
    }

    /**
     * take out a read lock then invoke the supplier lambda then unlock
     *
     * @param procedure function which takes no args but returns a value
     */
    public static void applyWithReadLock( OptimisticReadWriteLock lock, Procedure procedure ) {
        final long stamp = lock.readLock();
        try {
            procedure.invoke();
        } finally {
            lock.unlockRead( stamp );
        }
    }

    /**
     * take out am optimistic read lock then invoke the supplier lambda
     * then check if optimistic lock worked if NOT then
     * take out read lock and invoke the supplier lambda AGAIN !
     * then unlock
     *
     * @param supplier function which takes no args but returns a value
     * @param <T>      type of return result from supplier
     * @return the result of the lamda function
     * @WARNING the lambda function can be invoked TWICE so make sure idempotent and the double invoke is safe !
     */
    public static <T> T applyWithOpimisticReadLock( OptimisticReadWriteLock lock, ZSupplier<T> supplier ) {
        T res = null;

        long stamp = lock.tryOptimisticRead();

        res = supplier.get();

        if ( !lock.validate( stamp ) ) {

            stamp = lock.readLock();

            try {
                res = supplier.get();
            } finally {
                lock.unlockRead( stamp );
            }
        }

        return res;
    }

    /**
     * take out am optimistic read lock then invoke the supplied procedure
     * then check if optimistic lock worked if NOT then
     * take out read lock and invoke the procedure AGAIN !
     * then unlock
     *
     * @param procedure - a lambda block of code
     * @WARNING the lambda function can be invoked TWICE so make sure idempotent and the double invoke is safe !
     */
    public static void applyWithOpimisticReadLock( OptimisticReadWriteLock lock, Procedure procedure ) {
        long stamp = lock.tryOptimisticRead();

        procedure.invoke();

        if ( !lock.validate( stamp ) ) {

            stamp = lock.readLock();

            try {
                procedure.invoke();
            } finally {
                lock.unlockRead( stamp );
            }
        }
    }
}
