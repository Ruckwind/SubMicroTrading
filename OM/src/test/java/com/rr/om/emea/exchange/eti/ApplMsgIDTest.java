/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.eti;

import com.rr.core.lang.BaseTestCase;
import com.rr.om.emea.exchange.eti.trading.ApplMsgID;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ApplMsgIDTest extends BaseTestCase {

    @Test
    public void testInitial() {
        doTest( 0, 0, 0, 1 );
        doTest( 0, 0, 0, 100 );
        doTest( 0, 0, 0, -1 );
        doTest( 0, 0, 0, -1000 );
        doTest( 0, 0, 100, 0 );
        doTest( 0, 0, 100, 1 );
        doTest( 0, 0, -1000, 1 );
        doTest( 0, 0, 0, 0 );
    }

    @Test
    public void testInitialFail() {
        doTestFail( -1, 0, 0, 0 );
        doTestFail( 0, 1, 0, 0 );
        doTestFail( 1, 1, 0, 0 );
        doTestFail( 0, -1, 0, 0 );
    }

    @Test
    public void testSimple() {
        doTest( 0, 1, 0, 2 );
        doTest( 100, 2, 100, 3 );
        doTest( 10, -1, 11, 0 );
        doTest( -2, -1, -1, 0 );
        doTest( 1, Long.MAX_VALUE, 1, Long.MIN_VALUE );
        doTest( 1, Long.MIN_VALUE, 1, Long.MIN_VALUE + 1 );
        doTest( -1, Long.MAX_VALUE, -1, Long.MIN_VALUE );
        doTest( -1, Long.MIN_VALUE, -1, Long.MIN_VALUE + 1 );
    }

    @Test
    public void testSimpleFail() {
        doTestFail( 0, 2, 0, 2 );
        doTestFail( 0, 2, -1, 3 );
        doTestFail( 0, 2, Long.MIN_VALUE, 3 );
        doTestFail( 100, 2, 100, 1 );
        doTestFail( 10, -1, 9, 0 );
        doTestFail( 10, -1, 12, 0 );
        doTestFail( 10, -1, 10, 0 );
        doTestFail( -2, -1, -2, 0 );
        doTestFail( -2, -1, -2, -1 );
        doTestFail( 1, Long.MAX_VALUE, 1, Long.MAX_VALUE );
        doTestFail( 1, Long.MIN_VALUE, 1, Long.MIN_VALUE );
        doTestFail( 1, 10, 1, (11 | 0x8000000000000000L) );
        doTestFail( 1, 10, 1, -11 );
        doTestFail( -1, Long.MIN_VALUE, -1, Long.MIN_VALUE + 2 );
    }

    private void doTest( long oldUpper, long oldLower, long newUpper, long newLower ) {
        ApplMsgID oldId = new ApplMsgID( oldUpper, oldLower );
        ApplMsgID newId = new ApplMsgID( newUpper, newLower );

        assertTrue( newId.isSequential( oldId ) );
    }

    private void doTestFail( long oldUpper, long oldLower, long newUpper, long newLower ) {
        ApplMsgID oldId = new ApplMsgID( oldUpper, oldLower );
        ApplMsgID newId = new ApplMsgID( newUpper, newLower );

        assertFalse( newId.isSequential( oldId ) );
    }
}
