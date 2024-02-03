package com.rr.core.lang;

public interface Snapable<T> extends Resetable, Lockable {

    /**
     * write copy of market data to destination upto max levels as supported in the destination book
     * <p>
     * if book is threadsafe may spinlock against mutating thread
     * <p>
     * only copies the book, lastTickId and lastTickInNanos
     *
     * @param dest
     */
    void snapTo( T dest );
}
