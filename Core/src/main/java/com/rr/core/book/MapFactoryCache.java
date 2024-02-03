/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.book;

import com.rr.core.collections.SMTHashMap;
import com.rr.core.factories.Factory;
import com.rr.core.factories.FactoryCache;

/**
 * Non thread safe cache which wraps a factory for new instance creation
 *
 * @author Richard Rose
 */
public final class MapFactoryCache<KTYPE, VTYPE> implements FactoryCache<KTYPE, VTYPE> {

    private final SMTHashMap<KTYPE, VTYPE> _map;
    private final Factory<KTYPE, VTYPE>    _factory;

    public MapFactoryCache( int numSymbols, Factory<KTYPE, VTYPE> factory ) {
        _map     = new SMTHashMap<>( numSymbols, 0.75f );
        _factory = factory;
    }

    @Override public VTYPE getItem( KTYPE id ) {
        VTYPE val = _map.get( id );

        if ( val == null ) {
            val = _factory.create( id );

            _map.put( id, val );
        }

        return val;
    }
}
