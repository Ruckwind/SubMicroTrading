/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix;

import com.rr.core.lang.ZString;
import com.rr.core.recycler.EventRecycler;
import com.rr.core.session.socket.SocketConfig;

public class FastSocketConfig extends SocketConfig {

    public FastSocketConfig( String id ) {
        super( id );
    }

    public FastSocketConfig( Class<? extends EventRecycler> recycler,
                             boolean isServer,
                             ZString host,
                             ZString nic,
                             int port ) {

        super( recycler, isServer, host, nic, port );
    }

    public FastSocketConfig( Class<? extends EventRecycler> recycler ) {
        super( recycler );
    }
}
