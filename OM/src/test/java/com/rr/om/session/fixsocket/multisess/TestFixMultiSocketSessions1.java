/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket.multisess;

import org.junit.Test;

public class TestFixMultiSocketSessions1 extends BaseTestFixMultiSocketSessions {

    @Test
    public void testS1() {
        doTestMultiSessionSocket( "A", 3000, 1 );
    }
}

