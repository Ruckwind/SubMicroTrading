/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

/**
 * the pooling pattern ues chaining instead of arrays so every obbject that
 * uses pools must implement a next pointer
 * Note if arrays were used more space would be taken by the pointers there
 *
 * @param <T>
 * @author Richard Rose
 */
public interface Reusable<T> extends Chainable<T>, HasReusableType {

    void reset();
}
