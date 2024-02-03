/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.collections.IntHashMap;
import com.rr.core.collections.IntMap;
import com.rr.core.component.SMTComponent;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.ZString;

import java.util.HashMap;
import java.util.Map;

public class TickManager implements SMTComponent {

    private final Map<ExchangeCode, Map<ZString, TickType>> _map      = new HashMap<>();
    private final Map<ZString, FixedTickSize>               _fixedMap = new HashMap<>();

    private final String _id;

    private IntMap<TickType> _universalMap = new IntHashMap<TickType>( 256, 0.75f );

    public TickManager( String id ) {
        _id = id;
    }

    @Override public String getComponentId() {
        return _id;
    }

    public FixedTickSize addFixedTickSize( double val ) {
        ReusableString s = TLC.instance().pop();

        s.append( val );

        FixedTickSize f = _fixedMap.get( s );

        if ( f == null ) {
            f = new FixedTickSize( val );
            _fixedMap.put( s, f );
        } else {
            TLC.instance().pushback( s );
        }

        return f;
    }

    public void addTickType( ExchangeCode operatingMic, TickType tt ) {

        Map<ZString, TickType> map = _map.get( operatingMic );

        if ( map == null ) {
            map = new HashMap<>();
            _map.put( operatingMic, map );
        }

        map.put( tt.getId(), tt );
    }

    public void addUniversalTickType( int id, final TickScale ts ) {
        _universalMap.put( id, ts );
    }

    public TickType getTickType( ExchangeCode operatingMic, ZString id ) {
        Map<ZString, TickType> map = _map.get( operatingMic );

        return (map == null) ? null : map.get( id );
    }

    public TickType getUniversalTickType( int id ) {
        return _universalMap.get( id );
    }
}
