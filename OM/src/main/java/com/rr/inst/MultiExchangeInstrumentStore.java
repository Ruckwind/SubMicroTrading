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
import com.rr.om.exchange.ExchangeManager;

import java.util.HashMap;
import java.util.Map;

public final class MultiExchangeInstrumentStore extends BaseInstrumentSecDefStore {

    private final Map<Exchange, Indexes> _exchangeMaps = new HashMap<>( 16 );

    public MultiExchangeInstrumentStore( int preSize ) {
        super( "instrumentLocator", preSize );
    }

    public MultiExchangeInstrumentStore( String id, int preSize ) {
        super( id, preSize );
    }

    @Override protected Map<FXPair, FXInstrument> createFXMap()                                      { return new HashMap<>(); }

    @Override
    protected Indexes getExchangeMap( Exchange ex, int preSize ) {
        Indexes exMap = _exchangeMaps.get( ex );

        if ( exMap == null ) {
            exMap = new Indexes( preSize );
            _exchangeMaps.put( ex, exMap );
        }

        return exMap;
    }

    @Override
    protected Exchange getExchange( ExchangeCode mic ) {
        if ( mic == null ) return null;

        Exchange ex = ExchangeManager.instance().getByCode( mic );

        if ( ex == null ) {
            throw new RuntimeException( "ExchangeManager doesnt have mic=[" + mic + "] loaded" );
        }

        return ex;
    }

    @Override protected final <T> LongMap<T> createLongMap( final int preSize )                      { return new LongHashMap<>( preSize, 0.75f ); }

    @Override protected final IntMap<InstrumentSecurityDefWrapper> createIntMap( final int preSize ) { return new IntHashMap<>( preSize, 0.75f ); }

    @Override protected final <K, V> Map<K, V> createMap( final int preSize )                        { return new HashMap<>( preSize, 0.75f ); }

    @Override protected Map<Exchange, Indexes> getExchangeMaps()                                     { return _exchangeMaps; }
}
