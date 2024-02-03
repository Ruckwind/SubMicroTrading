/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket.multisess;

import org.junit.Test;

public class TestFixMultiSocketSessions4 extends BaseTestFixMultiSocketSessions {

    @Test
    public void testS4() {
        doTestMultiSessionSocket( "C", 3000, 4 );
    }
}

