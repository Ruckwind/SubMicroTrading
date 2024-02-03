/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.BaseTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MsgFlagTest extends BaseTestCase {

    @Test
    public void testTypes() {
        int flags    = MsgFlag.setFlag( 0, MsgFlag.Reconciliation, true );
        int expFlags = 1 << MsgFlag.Reconciliation.ordinal();
        assertEquals( expFlags, flags );

        flags = MsgFlag.setFlag( flags, MsgFlag.PossDupFlag, true );
        expFlags += 1 << MsgFlag.PossDupFlag.ordinal();
        assertEquals( expFlags, flags );

        flags = MsgFlag.setFlag( flags, MsgFlag.PossDupFlag, false );
        expFlags -= 1 << MsgFlag.PossDupFlag.ordinal();
        assertEquals( expFlags, flags );

        flags = MsgFlag.setFlag( flags, MsgFlag.Reconciliation, false );
        assertEquals( 0, flags );
    }
}
