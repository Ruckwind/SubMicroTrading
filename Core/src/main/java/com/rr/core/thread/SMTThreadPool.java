package com.rr.core.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface SMTThreadPool {

    <T> Future<T> execute( Callable<T> func );

    /**
     * @return number of thread workers in pool
     */
    int size();

    <T> T waitForResult( Future<T> populationFuture );
}
