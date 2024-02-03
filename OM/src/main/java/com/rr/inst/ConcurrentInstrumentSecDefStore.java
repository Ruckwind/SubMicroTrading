/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.collections.*;
import com.rr.core.model.Exchange;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.FXInstrument;
import com.rr.core.model.FXPair;
import com.rr.om.exchange.ExchangeManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * thread safe instrument store
 * <p>
 * will BLOCK on updates ... for intraday updates of instruments use more concurrent version
 * <p>
 * Exchange scoped keys, ie keys unqiue within exchange
 * symbol (tag 55),
 * exchangeSymbol (tag 48 when tag22=ExchangeSymbol (8)
 * exchangeLongId (when exchange flagged as having long key eg CME)
 * securityDesc (fix tag 107)
 * quantHouseLocalStr
 * <p>
 * Note for Futures, the symbol will be stored witin index with maturityDate appended
 *
 * @author Richard Rose
 */
public final class ConcurrentInstrumentSecDefStore extends BaseInstrumentSecDefStore {

    private static boolean _usePooledMap = true;
    private final Map<Exchange, Indexes> _exchangeMaps;

    public ConcurrentInstrumentSecDefStore( final String id, final int preSize ) {
        super( id, preSize );

        _exchangeMaps = new ConcurrentHashMap<>( 16 );
    }

    @Override protected Map<FXPair, FXInstrument> createFXMap() {
        return (_usePooledMap) ? new ConcurrentPooledElementsMap<>( 8 ) : new ConcurrentHashMap<>( 8 );
    }

    @Override protected final Indexes getExchangeMap( Exchange ex, int preSize ) {
        final Map<Exchange, Indexes> exchangeMaps = getExchangeMaps();
        Indexes                      exMap        = exchangeMaps.get( ex );

        if ( exMap == null ) {
            exMap = new Indexes( preSize );
            exchangeMaps.put( ex, exMap );
        }

        return exMap;
    }

    @Override protected final Exchange getExchange( ExchangeCode mic ) {
        Exchange ex = ExchangeManager.instance().getByCode( mic );

        if ( ex == null ) {
            throw new RuntimeException( "ExchangeManager doesnt have mic=" + mic + " loaded" );
        }

        return ex;
    }

    @Override protected final <T> LongMap<T> createLongMap( final int preSize )                      { return new SyncLongHashMap<>( new LongHashMap<>( preSize, 0.75f ) ); }

    @Override protected final IntMap<InstrumentSecurityDefWrapper> createIntMap( final int preSize ) { return new SyncIntHashMap<>( new IntHashMap<>( preSize, 0.75f ) ); }

    @Override protected final <K, V> Map<K, V> createMap( final int preSize ) {
        return (_usePooledMap) ? new ConcurrentPooledElementsMap<>( preSize, 0.75f ) : new ConcurrentHashMap<>( preSize, 0.75f );
    }

    @Override protected Map<Exchange, Indexes> getExchangeMaps() { return _exchangeMaps; }
}
