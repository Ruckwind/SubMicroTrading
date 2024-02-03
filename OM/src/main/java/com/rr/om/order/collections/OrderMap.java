/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.order.collections;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.om.order.Order;
import com.rr.om.processor.EventProcessor;

/**
 * a zero GC version of Sun HashMap optimised for use with OrderImpl
 * <p>
 * need a putIfAbsent which returns false if put failed due to existing entry, this allows keys with null values
 * which is required for recovery/orderEviction of old orders to avoid future dups
 *
 * @NOTE assumes the order clOrdId never changes ... NEVER recycle order without first removing from map
 * @NOTE NOT THREADSAFE DESIGNED FOR USE ONLY BY THE ORDER PROCESSOR IN SINGLE THREAD MODE
 */
public interface OrderMap {

    /**
     * clear  the map, recycling orders/versions/bases using processor
     */
    void clear();

    boolean containsKey( ViewString key );

    Order get( ViewString key );

    boolean isEmpty();

    /**
     * logger stats for the map
     *
     * @NOTE LONG OPERATION, intended for end of day logging
     */
    void logStats( ReusableString out );

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @return the previous value associated with <tt>key</tt>, IF not same object
     * @NOTE the map does NOT own the key, thats owned by the order key chains
     */
    Order put( ViewString id, Order order );

    /**
     * Associates the specified value with the specified key in this map only if the key has no entry in map
     * If the key has an value of null it will return false and the put will not take place
     *
     * @return true if the put was successful (ie key didnt already exist in map)
     * @NOTE the map does NOT own the key, thats owned by the order key chains
     */
    boolean putIfKeyAbsent( ViewString id, Order order );

    /**
     * rather than own its own recycle factories the order map
     * will delegate to processor
     *
     * @param proc
     */
    void setRecycleProcessor( EventProcessor proc );

    int size();
}
