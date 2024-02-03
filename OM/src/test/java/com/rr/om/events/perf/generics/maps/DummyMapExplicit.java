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
public class DummyMapExplicit {

    public static class MapEntry {

        ReusableString           key;
        ClientNewOrderSingleImpl value;

        MapEntry() {
            // nada
        }

        ClientNewOrderSingleImpl getClientNewOrderSingleImplalue()                                   { return value; }

        void setClientNewOrderSingleImplalue( ClientNewOrderSingleImpl aClientNewOrderSingleImplal ) { value = aClientNewOrderSingleImplal; }

        ReusableString getReusableStringey()                                                         { return key; }
    }

    private MapEntry[] _array;

    public DummyMapExplicit( int size ) {
        _array = new MapEntry[ size ];

        for ( int i = 0; i < size; i++ ) {
            _array[ i ] = new MapEntry();
        }
    }

    public ClientNewOrderSingleImpl getClientNewOrderSingleImplalue( int idx ) {
        return _array[ idx ].value;
    }

    public ReusableString getKey( int idx ) {
        return _array[ idx ].key;
    }

    public void set( ReusableString key, ClientNewOrderSingleImpl value, int idx ) {
        MapEntry entry = _array[ idx ];
        entry.key   = key;
        entry.value = value;
    }
}
