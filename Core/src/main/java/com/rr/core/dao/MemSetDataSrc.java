package com.rr.core.dao;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.thread.RunState;

import java.util.Set;

public class MemSetDataSrc<T> implements SetDataSrc<T> {

    private transient final String _id;
    private transient final Set<T> _set;

    private transient RunState _runState = RunState.Created;

    public MemSetDataSrc( final String id, final Set<T> set ) {
        _id  = id;
        _set = set;
    }

    @Override public boolean contains( final T v )                                             { return _set.contains( v ); }

    @Override public Set<T> getSet()                                                           { return _set; }

    @Override public String getComponentId()                                                   { return _id; }

    @Override public RunState getRunState()                                                    { return _runState; }

    @Override public void init( final SMTStartContext ctx, final CreationPhase creationPhase ) { /* nothing */ }

    @Override public RunState setRunState( final RunState newState )                           { return _runState = newState; }

    @Override public void refresh()                                                            { /* nothing */ }
}
