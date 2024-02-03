/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf.generics.maps;

import com.rr.core.lang.ReusableString;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;

/**
 * not real map implementation, just looking at performance
 * of map type structure using generics
 *
 * @author Richard Rose
 */
public class DummyMapExplicitWrapperToGenerics extends
                                               DummyMapWithGenerics<ReusableString, ClientNewOrderSingleImpl> {

    public DummyMapExplicitWrapperToGenerics( int size ) {
        super( size );
    }

    @Override
    public ReusableString getKey( int idx ) {
        return _array[ idx ].key;
    }

    @Override
    public ClientNewOrderSingleImpl getValue( int idx ) {
        return _array[ idx ].value;
    }

    @Override
    public void set( ReusableString key, ClientNewOrderSingleImpl value, int idx ) {
        MapEntry<ReusableString, ClientNewOrderSingleImpl> entry = _array[ idx ];
        entry.key   = key;
        entry.value = value;
    }
}
