package com.rr.core.model;

import com.rr.core.component.SMTComponent;
import com.rr.core.model.Instrument;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GlobalSubs implements SMTComponent {

    private final String _id;
    private Map<Instrument, AtomicInteger> _globalSubs = new ConcurrentHashMap<>();

    public GlobalSubs( final String id ) {
        _id = id;
    }

    @Override public String getComponentId() { return _id; }

    public void add( final Instrument i, final Object subscriber ) {
        AtomicInteger ai = _globalSubs.computeIfAbsent( i, ( inst ) -> new AtomicInteger( 0 ) );

        ai.getAndIncrement();
    }

    public boolean isSubscribed( Instrument i ) { return _globalSubs.containsKey( i ); }

    public void remove( final Instrument i, final Object subscriber ) {
        AtomicInteger ai = _globalSubs.computeIfAbsent( i, ( inst ) -> new AtomicInteger( 0 ) );

        if ( ai.get() > 0 ) {
            ai.getAndDecrement();
        }
    }
}
