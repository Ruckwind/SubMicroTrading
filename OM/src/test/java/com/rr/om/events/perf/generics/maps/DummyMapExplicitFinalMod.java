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
public final class DummyMapExplicitFinalMod {

    public final static class MapEntry {

        ReusableString           key;
        ClientNewOrderSingleImpl value;

        MapEntry() {
            // nada
        }

        final ClientNewOrderSingleImpl getClientNewOrderSingleImplalue()                                         { return value; }

        final void setClientNewOrderSingleImplalue( final ClientNewOrderSingleImpl aClientNewOrderSingleImplal ) { value = aClientNewOrderSingleImplal; }

        final ReusableString getReusableStringey()                                                               { return key; }
    }

    private MapEntry[] _array;

    public DummyMapExplicitFinalMod( final int size ) {
        _array = new MapEntry[ size ];

        for ( int i = 0; i < size; i++ ) {
            _array[ i ] = new MapEntry();
        }
    }

    public final ClientNewOrderSingleImpl getClientNewOrderSingleImplalue( final int idx ) {
        return _array[ idx ].value;
    }

    public final ReusableString getKey( final int idx ) {
        return _array[ idx ].key;
    }

    public final void set( final ReusableString key, final ClientNewOrderSingleImpl value, final int idx ) {
        final MapEntry entry = _array[ idx ];
        entry.key   = key;
        entry.value = value;
    }
}
