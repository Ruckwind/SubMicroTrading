/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket.multisess;

import org.junit.Test;

public class TestFixMultiSocketSessions8 extends BaseTestFixMultiSocketSessions {

    @Test
    public void testS8() {
        doTestMultiSessionSocket( "D", 3000, 8 );
    }
}

