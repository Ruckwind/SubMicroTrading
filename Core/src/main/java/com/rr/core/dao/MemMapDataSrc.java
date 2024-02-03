package com.rr.core.dao;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.thread.RunState;

import java.util.Map;

/**
 * @param <T>
 */

public class MemMapDataSrc<K, V> implements MapDataSrc<K, V> {

    private transient final String    _id;
    private transient final Map<K, V> _map;

    private transient RunState _runState = RunState.Created;

    public MemMapDataSrc( final String id, final Map<K, V> map ) {
        _id  = id;
        _map = map;
    }

    @Override public V get( final K k )                                                        { return _map.get( k ); }

    @Override public Map<K, V> getMap()                                                        { return _map; }

    @Override public String getComponentId()                                                   { return _id; }

    @Override public RunState getRunState()                                                    { return _runState; }

    @Override public RunState setRunState( final RunState newState )                           { return _runState = newState; }

    @Override public void init( final SMTStartContext ctx, final CreationPhase creationPhase ) { /* nothing */ }

    @Override public void refresh()                                                            { /* nothing */ }
}
