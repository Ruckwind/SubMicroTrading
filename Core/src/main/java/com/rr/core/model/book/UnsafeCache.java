/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * non threadsafe cache of books
 * <p>
 * intended for use via TLC eg
 * <p>
 * UnsafeCache<Instrument,BarMktData> barCache = TLC.instance().getInstanceOf( UnsafeCache.class );
 */
public final class UnsafeCache<K, V> {

    private final Map<K, V> _cache = new HashMap<>( 128 );

    private final Function<K, V> _factory;

    /**
     * @param factory a function taking an instrument and creating an item of type T
     */
    public UnsafeCache( final Function<K, V> factory ) {
        _factory = factory;
    }

    public V get( K inst ) {

        V book = _cache.get( inst );

        if ( book == null ) {
            book = create( inst );

            _cache.put( inst, book );
        }

        return book;
    }

    private V create( K inst ) {
        return (_factory.apply( inst ));
    }
}
