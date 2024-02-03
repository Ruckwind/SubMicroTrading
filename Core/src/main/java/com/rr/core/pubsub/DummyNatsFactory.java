package com.rr.core.pubsub;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.thread.RunState;

import java.io.IOException;

public class DummyNatsFactory implements ConnectionFactory {

    private static final Connection _dummy    = new DummyConnection();
    private final        String     _id;
    private              RunState   _runState = RunState.Unknown;

    public DummyNatsFactory( String id )                                              { _id = id; }

    @Override public void close() throws IOException {
        /* nothing */
    }

    @Override public String getComponentId() {
        return _id;
    }

    @Override public Connection getConnection()                                       { return _dummy; }

    @Override public Connection getConnection( final String connId ) throws Exception { return _dummy; }

    @Override public RunState getRunState()                                           { return _runState; }

    @Override public RunState setRunState( final RunState newState ) {
        final RunState prev = _runState;
        _runState = newState;
        return prev;
    }

    @Override public void init( final SMTStartContext ctx, final CreationPhase creationPhase ) {
        /* nothing */
    }
}
