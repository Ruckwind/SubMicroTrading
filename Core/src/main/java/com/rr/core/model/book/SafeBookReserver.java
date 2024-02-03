/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.model.BookReserver;

public final class SafeBookReserver implements BookReserver {

    public static final int MIN_RESET_DELAY_NANOS = 1000 * 1000;

    private static final class SingleThreadLetter implements BookReserver {

        /**
         * NANO_AGE_MIN_RESET disables age reset unless 1ms has elapsed
         * <p>
         * realistically exchange will take 100usecs to get reply to order so should be good enough
         * <p>
         * required to provide protection from book change while 1 thread has grabbed liquidity and another thread may be about to overlap
         */
        private long _minResetDelayNanos = MIN_RESET_DELAY_NANOS;

        private int _curReserve;

        private long _lastTickNanos;

        public SingleThreadLetter()                                 { /* nothing */ }

        @Override public void setMinResetDelayNANOS( long nanos ) {
            _minResetDelayNanos = nanos;
        }

        @Override public void attachReserveWorkerThread( Thread t ) { /* nothing */ }

        @Override public int grabQty( int requestedQty, int currentQtyFromBook, long timeNanos ) {

            checkAgeReset( timeNanos );

            int qty = currentQtyFromBook - _curReserve;

            if ( qty > requestedQty ) qty = requestedQty;

            if ( qty <= 0 ) return 0;

            _curReserve += qty;

            return qty;
        }

        @Override public void completed( double orderQty ) {
            _curReserve -= orderQty;

            if ( _curReserve < 0 ) _curReserve = 0;
        }

        @Override public void reset() {
            _lastTickNanos = 0;
            _curReserve    = 0;
        }

        @Override public int getReserved() {
            return _curReserve;
        }

        @Override public int getAttachedWorkerThreads() { return 0; }

        private void checkAgeReset( long timeNanos ) {
            if ( Math.abs( _lastTickNanos - timeNanos ) > _minResetDelayNanos ) {
                _curReserve    = 0;
                _lastTickNanos = timeNanos;
            }
        }
    }

    private static final class MultiThreadLetter implements BookReserver {

        private final BookReserver _mletter;

        public MultiThreadLetter( BookReserver letter ) {
            _mletter = letter;
        }

        @Override
        public void setMinResetDelayNANOS( long nanos ) {
            _mletter.setMinResetDelayNANOS( nanos );
        }

        @Override public void attachReserveWorkerThread( Thread t ) {
            _mletter.attachReserveWorkerThread( t );
        }

        @Override public synchronized int grabQty( int requestedQty, int currentQtyFromBook, long timeNanos ) {
            return _mletter.grabQty( requestedQty, currentQtyFromBook, timeNanos );
        }

        @Override public synchronized void completed( double orderQty ) {
            _mletter.completed( orderQty );
        }

        @Override public synchronized void reset() {
            _mletter.reset();
        }

        @Override public synchronized int getReserved() {
            return _mletter.getReserved();
        }

        @Override public int getAttachedWorkerThreads() { return 0; }
    }

    private Thread _firstThread = null;
    private int    _threadCount = 0;

    private BookReserver _letter = new SingleThreadLetter();

    @Override
    public void setMinResetDelayNANOS( long nanos ) {
        _letter.setMinResetDelayNANOS( nanos );
    }

    @Override
    public synchronized void attachReserveWorkerThread( Thread t ) {
        if ( _firstThread == null ) {
            ++_threadCount;
            _firstThread = t;

            return;

        }

        if ( t == _firstThread ) {
            return;
        }

        ++_threadCount;

        if ( _threadCount == 2 ) {
            _letter = new MultiThreadLetter( _letter );
        }
    }

    @Override
    public int grabQty( int requestedQty, int currentQtyFromBook, long timeNanos ) {
        return _letter.grabQty( requestedQty, currentQtyFromBook, timeNanos );
    }

    @Override
    public void completed( double orderQty ) {
        _letter.completed( orderQty );
    }

    @Override
    public void reset() {
        _letter.reset();
    }

    @Override
    public int getReserved() {
        return _letter.getReserved();
    }

    @Override
    public synchronized int getAttachedWorkerThreads() {
        return _threadCount;
    }
}
