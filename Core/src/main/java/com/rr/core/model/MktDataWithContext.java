/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.model.book.ListenerMktDataContextWrapper;

/**
 * Book interface
 * <p>
 * top level book abstraction representing pricing information for an instrument
 *
 * @NOTE book is used in maps, so the equals and hashCode methods are java default
 * do NOT implement to compare prices in two books .... create new compare methods for that
 * <p>
 * Changed lock from simple CAS to StampedLock for optimistic locking on concurrent snapshotting
 */
public interface MktDataWithContext<T extends MktDataWithContext, C extends Context> extends SnapableRefPrice<T> {

    ListenerMktDataContextWrapper<T, C> getContextWrapper();

    /**
     * allow association of a context object with the book
     * <p>
     * only one context block allowed per book
     *
     * @param context new context block
     * @return previous context block
     */
    ListenerMktDataContextWrapper<T, C> setContextWrapper( ListenerMktDataContextWrapper<T, C> context );
}
