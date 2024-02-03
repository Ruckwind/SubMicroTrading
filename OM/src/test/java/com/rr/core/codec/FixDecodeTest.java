/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.model.Event;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FixDecodeTest extends BaseTestCase {

    private AbstractFixDecoder _testDecoder;
    private String             _dateStr = "20100510";
    private TimeUtils          _calc;
    private ReusableString     _tmp1    = new ReusableString( 30 );
    private ReusableString     _tmp2    = new ReusableString( 30 );

    @Before
    public void setUp() {
        _testDecoder = new TstFixDecoder( (byte) '4', (byte) '4' );
        _calc        = TimeUtilsFactory.createTimeUtils();
        _calc.setTodayFromLocalStr( _dateStr );
        _testDecoder.setTimeUtils( _calc );
    }

    @Test
    public void testDecodeNos() {
        String nos = "8=FIX.4.4; 9=158; 35=D; 34=12243; 49=PROPA; 52=" + _dateStr + "-12:01:01.100; " +
                     "56=ME; 59=0; 22=R; 48=BT.XLON; 40=2; 54=1; 55=BT.XLON; 11=XX2100; 21=1; " +
                     "60=" + _dateStr + "-12:01:01.000; 38=50; 44=10.23; 10=345; ";

        nos = FixTestUtils.toFixDelim( nos );

        Map<String, String> vals = FixTestUtils.msgToMap( nos );

        _testDecoder.setCompIds( vals.get( "56" ), null, vals.get( "49" ), null );
        doTestDecodeNOS( nos.getBytes(), 0, nos.length(), vals );
    }

    @Test
    public void testMismatchSenderCompID() {
        String nos = "8=FIX.4.4; 9=158; 35=D; 34=12243; 49=PROPA; 52=" + _dateStr + "-12:01:01.100; " +
                     "56=ME; 59=0; 22=R; 48=BT.XLON; 40=2; 54=1; 55=BT.XLON; 11=XX2100; 21=1; " +
                     "60=" + _dateStr + "-12:01:01.000; 38=50; 44=10.23; 10=345; ";

        nos = FixTestUtils.toFixDelim( nos );

        Map<String, String> vals = FixTestUtils.msgToMap( nos );

        // test supplied name different to supplied name
        _testDecoder.setCompIds( vals.get( "56" ), null, "LL", null );
        doTestMismatchCompId( nos.getBytes(), 0, nos.length(), vals, "Invalid senderCompId, expected=LL, received=PROPA" );

        // test supplied name is subset of expected name
        _testDecoder.setCompIds( vals.get( "56" ), null, "ME1", null );
        doTestMismatchCompId( nos.getBytes(), 0, nos.length(), vals, "Invalid senderCompId, expected=ME1, received=PROPA" );

        // test supplied name includes exp name but has extra
        _testDecoder.setCompIds( vals.get( "56" ), null, "M", null );
        doTestMismatchCompId( nos.getBytes(), 0, nos.length(), vals, "Invalid senderCompId, expected=M, received=PROPA" );
    }

    @Test
    public void testMismatchTargetCompID() {
        String nos = "8=FIX.4.4; 9=158; 35=D; 34=12243; 49=PROPA; 52=" + _dateStr + "-12:01:01.100; " +
                     "56=ME; 59=0; 22=R; 48=BT.XLON; 40=2; 54=1; 55=BT.XLON; 11=XX2100; 21=1; " +
                     "60=" + _dateStr + "-12:01:01.000; 38=50; 44=10.23; 10=345; ";

        nos = FixTestUtils.toFixDelim( nos );

        Map<String, String> vals = FixTestUtils.msgToMap( nos );

        // test supplied name different to supplied name
        _testDecoder.setCompIds( "MEZZZ", null, vals.get( "49" ), null );
        doTestMismatchCompId( nos.getBytes(), 0, nos.length(), vals, "Invalid targetCompId, expected=MEZZZ, received=ME" );

        // test supplied name is subset of expected name
        _testDecoder.setCompIds( "MEAZ", null, vals.get( "49" ), null );
        doTestMismatchCompId( nos.getBytes(), 0, nos.length(), vals, "Invalid targetCompId, expected=MEAZ, received=ME" );
    }

    private void doTestDecodeNOS( byte[] fixMsg, int offset, int len, Map<String, String> vals ) {

        Event msg = _testDecoder.decode( fixMsg, offset, len );

        assertTrue( msg.getClass() == ClientNewOrderSingleImpl.class );

        ClientNewOrderSingleImpl nos = (ClientNewOrderSingleImpl) msg;

        /*
        8=FIX.4.4; 9=152; 35=D; 34=12243; 49=PROPA; 52=20100510-12:01:01.100;
        56=ME; 59=0; 22=R; 48=BT.XLON; 40=2; 54=1; 55=BT.XLON; 11=XX2100; 21=1;
        60=20100510-12:01:01; 38=50; 44=10.23; 10=345;
        */

        // must toString ZString's

        assertEquals( vals.get( "11" ), nos.getClOrdId().toString() );
        assertEquals( vals.get( "38" ), Long.toString( (long) nos.getOrderQty() ) );
        assertEquals( vals.get( "59" ), "" + (char) nos.getTimeInForce().getVal() );
        assertEquals( vals.get( "40" ), "" + (char) nos.getOrdType().getVal() );
        assertEquals( vals.get( "54" ), "" + (char) nos.getSide().getVal() );
        assertEquals( vals.get( "55" ), nos.getSymbol().toString() );
        assertEquals( vals.get( "21" ), "" + (char) nos.getHandlInst().getVal() );
        assertEquals( vals.get( "44" ), Double.toString( nos.getPrice() ) );
        assertEquals( vals.get( "21" ), "" + (char) nos.getHandlInst().getVal() );
        assertEquals( vals.get( "52" ), _calc.internalTimeToFixStrMillis( _tmp1, nos.getEventTimestamp() ).toString() );
        assertEquals( vals.get( "60" ), _calc.internalTimeToFixStrMillis( _tmp2, nos.getTransactTime() ).toString() );

        // another test will check encode & decode of date/time strings for 52 and 60

    }

    private void doTestMismatchCompId( byte[] fixMsg, int offset, int len, Map<String, String> vals, String expErr ) {

        Event msg = _testDecoder.decode( fixMsg, offset, len );

        assertTrue( msg.getClass() == RejectDecodeException.class );

        RejectDecodeException err = (RejectDecodeException) msg;

        assertEquals( expErr, err.getMessage() );
    }

}
