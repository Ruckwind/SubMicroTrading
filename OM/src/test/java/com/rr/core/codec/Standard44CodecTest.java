/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.lang.*;
import com.rr.core.model.Event;
import com.rr.core.time.BackTestClock;
import com.rr.model.generated.fix.codec.Standard44Encoder;
import com.rr.model.generated.internal.events.impl.ClientNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderAckImpl;
import com.rr.model.internal.type.IllegalFieldAccess;
import com.rr.om.warmup.FixTestUtils;
import com.rr.om.warmup.sim.WarmupUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class Standard44CodecTest extends BaseTestCase {

    private AbstractFixDecoder _testDecoder;
    private String             _dateStr = "20100510";
    private TimeUtils          _calc;
    private ReusableString     _tmp1    = new ReusableString( 30 );
    private ReusableString     _tmp2    = new ReusableString( 30 );
    private byte[]             _buf;
    private Standard44Encoder  _testEncoder;
    private BackTestClock      _clock;
    private Clock              _origClock;

    public MarketNewOrderAckImpl doTestACK( byte[] fixMsg, int offset, int len, Map<String, String> vals ) {

        Event msg = _testDecoder.decode( fixMsg, offset, len );

        assertTrue( msg.getClass() == MarketNewOrderAckImpl.class );

        MarketNewOrderAckImpl ack = (MarketNewOrderAckImpl) msg;

        /*
        8=FIX.4.4; 9=149; 35=8; 34=12243; 49=PROPA; 52=" + _dateStr + "-12:01:01.232;
        56=ME; 39=0; 150=0; 40=1; 54=1; 55=BT.XLON; 11=CL0001; 21=1; 60=" + _dateStr + "-12:01:01; 38=10;
        59=0; 22=R; 48=BT.XLON; 1111=DUF; 10=233;
        */

        // must toString ZString's

        assertEquals( vals.get( "34" ), "" + ack.getMsgSeqNum() );
        assertEquals( vals.get( "11" ), ack.getClOrdId().toString() );
        assertEquals( vals.get( "37" ), ack.getOrderId().toString() );
        assertEquals( vals.get( "150" ), "" + (char) ack.getExecType().getVal() );
        assertEquals( vals.get( "39" ), "" + (char) ack.getOrdStatus().getVal() );
        assertEquals( vals.get( "54" ), "" + (char) ack.getSide().getVal() );

        try {
            ack.getOrderQty();
            fail();
        } catch( IllegalFieldAccess e ) { /* expected */ }

        try {
            ack.getPrice();
            fail();
        } catch( IllegalFieldAccess e ) { /* expected */ }

        try {
            ack.getCurrency();
            fail();
        } catch( IllegalFieldAccess e ) { /* expected */ }

        // another test will check encode & decode of date/time strings for 52 and 60

        return ack;
    }

    public ClientNewOrderSingleImpl doTestNOS( byte[] fixMsg, int offset, int len, Map<String, String> vals ) {

        Event msg = WarmupUtils.doDecode( _testDecoder, fixMsg, offset, len );
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

        return nos;
    }

    @Before public void setUp() {
        _clock     = new BackTestClock();
        _origClock = ClockFactory.set( _clock );

        _buf         = new byte[ 8192 ];
        _testDecoder = FixTestUtils.getOMSDecoder44();
        _testEncoder = new Standard44Encoder( (byte) '4', (byte) '4', _buf );
        _calc        = TimeUtilsFactory.createTimeUtils();
        _calc.setTodayFromLocalStr( _dateStr );
        _testDecoder.setTimeUtils( _calc );
    }

    @After public void tearDown() {
        ClockFactory.set( _origClock );
    }

    @Test
    public void testACKDecodeReEncode() {

        // get a NOS to use as src in ClientACK

        String nos = "8=FIX.4.4; 9=163; 35=D; 43=N; 49=PROPA; 56=ME; 34=12243; 52=" + _dateStr + "-12:01:01.100; " +
                     "11=XX2100; 59=0; 22=R; 48=BT.XLON; 38=10; 44=10.23; 40=2; 54=1; 55=BT.XLON; 21=1; " +
                     "60=" + _dateStr + "-12:01:01.000; 10=211; ";

        nos = FixTestUtils.toFixDelim( nos );

        ClientNewOrderSingleImpl nosEvent = (ClientNewOrderSingleImpl) WarmupUtils.doDecode( _testDecoder, nos.getBytes(), 0, nos.length() );

        // decode ack string to market ack pojo
        String ack = "8=FIX.4.4; 9=156; 35=8; 43=N; 34=12243; 49=PROPA; 52=" + _dateStr + "-12:01:01.232; " +
                     "56=ME; 37=XXYYZZ123; 39=0; 150=0; 40=1; 54=1; 55=BT.XLON; 11=XX2100; 21=1; 60=" + _dateStr + "-12:01:01; 38=10; " +
                     "59=0; 22=R; 48=BT.XLON; 1111=PR; 10=233; ";

        ack = FixTestUtils.toFixDelim( ack );

        Map<String, String> vals = FixTestUtils.msgToMap( ack );

        _testDecoder.setCompIds( vals.get( "49" ), null, vals.get( "56" ), null );
        _testEncoder.setCompIds( vals.get( "49" ), null, vals.get( "56" ), null );

        MarketNewOrderAckImpl ackEvent = doTestACK( ack.getBytes(), 0, ack.length(), vals );

        // create a client ack based on the market ack
        // (as market ack optimises out decoding of attrs on the exec report)
        ClientNewOrderAckImpl clientAck = FixTestUtils.getClientAck( ackEvent, nosEvent );

        _testEncoder.encode( clientAck );

        String resultEncoded = new String( _buf, _testEncoder.getOffset(), _testEncoder.getLength() );

        // optional tags are not encoded
        Set<String> tagsToIgnore = new LinkedHashSet<>();
        tagsToIgnore.add( "9" );
        tagsToIgnore.add( "40" );
        tagsToIgnore.add( "21" );
        tagsToIgnore.add( "59" );
        tagsToIgnore.add( "1111" );

        FixTestUtils.compare( ack, resultEncoded, tagsToIgnore, true, true );
    }

    @Test
    public void testNOSDecodeReEncode() {
        String timeStr = _dateStr + "-12:01:01.100";
        _clock.setCurrentTimeMillis( timeStr );

        String nos = "8=FIX.4.4; 9=174; 35=D; 43=N; 49=PROPA; 56=ME; 34=12243; 52=" + timeStr + "; " +
                     "11=XX2100; 59=0; 22=R; 48=BT.XLON; 38=50; 44=10.23; 15=GBP; 40=2; 54=1; 55=BT.XLON; 21=1; " +
                     "60=" + timeStr + "; 526=XX2100; 10=171; ";

        nos = FixTestUtils.toFixDelim( nos );

        Map<String, String> vals = FixTestUtils.msgToMap( nos );

        _testDecoder.setCompIds( vals.get( "56" ), null, vals.get( "49" ), null );
        _testEncoder.setCompIds( vals.get( "49" ), null, vals.get( "56" ), null );

        ClientNewOrderSingleImpl nosEvent = doTestNOS( nos.getBytes(), 0, nos.length(), vals );

        _testEncoder.encode( nosEvent );

        String resultEncoded = new String( _buf, _testEncoder.getOffset(), _testEncoder.getLength() );

        FixTestUtils.compare( nos, resultEncoded, false );
    }
}
