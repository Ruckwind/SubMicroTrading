package com.rr.core.collections;

/**
 * non threadsafe ordered event list
 * efficient as long as events inserted in mostly timeorder
 * <p>
 * WORST CASE is O(N) for insert
 */
public interface TimeOrderedEventList extends EventQueue {

    /**
     * @param <T>
     * @return the last element in the list or null if none .. doesnt change the list
     */
    <T> T getLast();

    /**
     * move all entries in queue to destination queue
     *
     * @param dest
     * @WARNING wipes any entries already in dest
     */
    void moveTo( DoubleLinkedEventQueue dest );

    <T> T removeLast();
}
