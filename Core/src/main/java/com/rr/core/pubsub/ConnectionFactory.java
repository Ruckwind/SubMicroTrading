package com.rr.core.pubsub;

import com.rr.core.component.SMTControllableComponent;

import java.io.Closeable;

public interface ConnectionFactory extends SMTControllableComponent, Closeable {

    interface ConnectionListener {

        void connected( Connection c, ConnectionEvent e );
    }

    enum ConnectionEvent {
        Connected, Disconnected;
    }

    /**
     * return the default connection
     *
     * @return
     * @throws com.rr.core.utils.SMTRuntimeException if any exceptions
     */
    Connection getConnection();

    /**
     * create a connection using the component properties identified by connId
     *
     * @param connId
     * @return
     */
    Connection getConnection( String connId ) throws Exception;
}
