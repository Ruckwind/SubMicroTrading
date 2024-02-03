/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.model.Instrument;

import java.util.HashMap;
import java.util.Map;

/**
 * non threadsafe cache of books
 * <p>
 * intended for use via TLC eg
 * <p>
 * Class<?>[]                          pClass  = { Function.class };
 * Function<Instrument, BarMktData>    factory = (inst) -> new BarMktData();
 * Object[]                            pArgs   = { factory };
 * <p>
 * BookCache<ApiMutatableBook> bookCache = TLC.instance().getInstanceOf( BookCache.class );
 */
public class UnsafeBookCache {

    private static class CacheKey {

        private Instrument _inst;
        private int        _numLevels;

        public CacheKey() {
            // nothing
        }

        public CacheKey( CacheKey that ) {
            _inst      = that._inst;
            _numLevels = that._numLevels;
        }

        @Override
        public int hashCode() {
            return _inst.hashCode();
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            CacheKey other = (CacheKey) obj;
            if ( _inst != other._inst ) return false;
            return _numLevels == other._numLevels;
        }

        public void set( Instrument inst, int levels ) {
            _inst      = inst;
            _numLevels = levels;
        }
    }

    private final Map<CacheKey, ApiMutatableBook> _books = new HashMap<>( 128 );

    private final CacheKey _tmpKey = new CacheKey();

    public ApiMutatableBook get( Instrument inst, int levels ) {
        _tmpKey.set( inst, levels );

        ApiMutatableBook book = _books.get( _tmpKey );

        if ( book == null ) {
            book = create( inst, levels );

            _books.put( new CacheKey( _tmpKey ), book );
        }

        return book;
    }

    private ApiMutatableBook create( Instrument inst, int levels ) {
        return ((levels == 1) ? new UnsafeL1Book( inst ) : new UnsafeL2Book( inst, levels ));
    }
}
