package com.rr.core.pubsub;

import com.rr.core.component.SMTStartContext;

import java.io.IOException;

public class DummyConnection implements Connection {

    @Override public void close() throws IOException {
        /* nothing */
    }

    @Override public PubSubSess create( final String sessionId, final SMTStartContext ctx ) {
        return new DummyPubSubSess( sessionId );
    }

    @Override public boolean isConnected() {
        return true;
    }
}
