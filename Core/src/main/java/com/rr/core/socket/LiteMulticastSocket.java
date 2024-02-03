/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.socket;

import java.io.IOException;

public interface LiteMulticastSocket extends LiteSocket {

    void joinGroup( String mcastGroupAddrIP, String localInterfaceIP ) throws IOException;
}
