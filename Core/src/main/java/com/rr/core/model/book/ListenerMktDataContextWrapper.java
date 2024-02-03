/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.model.Context;
import com.rr.core.model.ContextWrapper;
import com.rr.core.model.MktDataListener;
import com.rr.core.model.MktDataWithContext;

/**
 * book context, used to register book specific listeners
 * <p>
 * has a single owner, so designed for by single book consumer
 *
 * @param <T>
 * @author Richard Rose
 */
public interface ListenerMktDataContextWrapper<T extends MktDataWithContext, C extends Context> extends ContextWrapper {

    @Override C getContext();

    boolean addListener( MktDataListener<T> listener );

    void clear();

    MktDataListener<T>[] getListeners();

    Object getOwner();

    boolean removeListener( MktDataListener<T> callback );
}
