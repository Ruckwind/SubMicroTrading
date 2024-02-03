/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.collections.IntHashMap;
import com.rr.core.collections.IntMap;
import com.rr.core.collections.LongHashMap;
import com.rr.core.collections.LongMap;
import com.rr.core.model.Exchange;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.FXInstrument;
import com.rr.core.model.FXPair;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * NON thread safe instrument store
 * <p>
 * assumes only single destination, so dont need segregate instruments by exchange
 */
public final class SingleExchangeInstrumentStore extends BaseInstrumentSecDefStore {

    private final Indexes                _indices;
    private final Exchange               _exchange;
    private       Map<Exchange, Indexes> _exchanges;

    public SingleExchangeInstrumentStore( Exchange exchange, int preSize ) {
        this( "instrumentLocator", exchange, preSize );
    }

    public SingleExchangeInstrumentStore( String id, Exchange exchange, int preSize ) {
        super( id, preSize );

        _exchange  = exchange;
        _indices   = new Indexes( preSize );
        _exchanges = Collections.singletonMap( exchange, _indices );
    }

    @Override protected Map<FXPair, FXInstrument> createFXMap()                                      { return new HashMap<>(); }

    @Override protected Indexes getExchangeMap( Exchange ex, int preSize ) {
        return _indices;
    }

    @Override protected Exchange getExchange( ExchangeCode mic ) {
        return _exchange;
    }

    @Override protected final <T> LongMap<T> createLongMap( final int preSize )                      { return new LongHashMap<>( preSize, 0.75f ); }

    @Override protected final IntMap<InstrumentSecurityDefWrapper> createIntMap( final int preSize ) { return new IntHashMap<>( preSize, 0.75f ); }

    @Override protected final <K, V> Map<K, V> createMap( final int preSize )                        { return new HashMap<>( preSize, 0.75f ); }

    @Override protected Map<Exchange, Indexes> getExchangeMaps() {
        return _exchanges;
    }
}
