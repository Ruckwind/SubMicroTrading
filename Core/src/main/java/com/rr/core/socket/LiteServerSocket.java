/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.socket;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;

public interface LiteServerSocket extends Closeable {

    LiteSocket accept() throws IOException;

    void bind( SocketAddress saddr ) throws IOException;

    void configureBlocking( boolean isBlocking ) throws IOException;

    void configureReuseAddress( boolean isReuse ) throws IOException;

}
