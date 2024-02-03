/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.component.SMTComponent;
import com.rr.core.model.Context;
import com.rr.core.model.MktDataListener;
import com.rr.core.model.MktDataWithContext;
import com.rr.core.utils.Utils;

/**
 * book context, used to register book specific listeners
 * <p>
 * has a single owner, so designed for by single book consumer
 *
 * @param <T>
 * @author Richard Rose
 */
public final class SingleOwnerListenerContextWrapperImpl<T extends MktDataWithContext, C extends Context> implements ListenerMktDataContextWrapper<T, C> {

    private final SMTComponent _owner;

    // dont sync read access
    public MktDataListener<T>[] _listeners;

    private C _context;

    @SuppressWarnings( "unchecked" )
    public SingleOwnerListenerContextWrapperImpl( SMTComponent owner, C context ) {
        _owner     = owner;
        _listeners = new MktDataListener[ 0 ];
        _context   = context;
    }

    @Override public C getContext() { return _context; }

    @Override public synchronized boolean addListener( MktDataListener<T> listener ) {

        for ( MktDataListener<T> l : _listeners ) {
            if ( l == listener ) return false;
        }

        _listeners = Utils.arrayCopyAndAddEntry( _listeners, listener );

        return true;
    }

    @Override @SuppressWarnings( "unchecked" ) public void clear() {
        _listeners = new MktDataListener[ 0 ];
    }

    @Override public MktDataListener<T>[] getListeners() {
        return _listeners;
    }

    @Override public Object getOwner() {
        return _owner;
    }

    @Override public synchronized boolean removeListener( MktDataListener<T> listener ) {
        boolean found = false;

        for ( MktDataListener<T> l : _listeners ) {
            if ( l == listener ) {
                found = true;
                break;
            }
        }

        if ( !found ) return false;

        _listeners = Utils.arrayCopyAndRemoveEntry( _listeners, listener );

        return true;
    }
}
