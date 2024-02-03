package com.rr.core.component;

import com.rr.core.thread.RunState;

public class KeepAlive implements SMTInitialisableComponent {

    private String   _id;
    private RunState _runState = RunState.Unknown;

    public KeepAlive( final String id ) {
        _id = id;
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState() {
        return _runState;
    }

    @Override public RunState setRunState( final RunState newState ) {
        return _runState = newState;
    }

    @Override public void init( final SMTStartContext ctx, final CreationPhase creationPhase ) {
        // nothing
    }
}
