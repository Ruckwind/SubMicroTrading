package com.rr.core.pubsub;

/**
 * idea is to self heal within the wrappers, if that fails to work correctly will need to expose events via this interface
 */
public interface ConnectionListener {

    void invoke( Connection c, ConnectionEvent e );
}
