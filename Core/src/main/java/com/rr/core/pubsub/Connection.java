package com.rr.core.pubsub;

import com.rr.core.component.SMTStartContext;

import java.io.Closeable;

/**
 * a threadsafe connection
 */
public interface Connection extends Closeable {

    PubSubSess create( String sessionId, final SMTStartContext ctx );

    boolean isConnected();
}
