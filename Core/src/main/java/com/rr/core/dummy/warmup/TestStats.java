/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.dummy.warmup;

import com.rr.core.lang.stats.SizeType;
import com.rr.core.lang.stats.Stats;

import java.util.HashMap;
import java.util.Map;

public class TestStats implements Stats {

    private static final int DEFAULT_SIZE = 16;

    private Map<SizeType, Integer> _map = new HashMap<>();

    public TestStats() {
        for ( SizeType key : SizeType.values() ) {
            _map.put( key, key.getSize() );
        }
        _map.put( SizeType.DEFAULT_VIEW_NOS_BUFFER, 400 );
    }

    @Override
    public int find( SizeType id ) {
        Integer i = _map.get( id );

        return (i == null ? DEFAULT_SIZE : i);
    }

    @Override
    public void initialise() {
        // NADA
    }

    @Override
    public void reload() {
        // NADA
    }

    @Override
    public void set( SizeType id, int val ) {
        _map.put( id, val );
    }

    @Override
    public void store() {
        // NADA
    }
}
