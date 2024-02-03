/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.chix;

import com.rr.core.codec.AbstractFixDecoder;
import com.rr.core.lang.*;
import com.rr.core.model.Event;
import com.rr.core.time.BackTestClock;
import com.rr.model.generated.fix.codec.Standard42Encoder;
import com.rr.model.generated.internal.events.impl.ClientNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderAckImpl;
import com.rr.model.internal.type.IllegalFieldAccess;
import com.rr.om.warmup.FixTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class CHIXCodecTest extends BaseTestCase {

    private String _dateStr     = "20100510";
    private String _timeNos     = "-13:00:50.232";
    private String _nosDateTime = _dateStr + _timeNos;
    // NOS - use same time for tag52/60 as round trip encode will use same clock time for both fields
    private String mnos = "8=FIX.4.2; 9=173; 35=D; 49=LLT1_CHIX; 56=CHIX_TST; 34=963; 43=N; 52=" + _nosDateTime + "; 60=" + _nosDateTime
                          + "; 11=A0TUQG; 55=VODl; 15=GBp; 21=1; 38=124; 40=2; 44=12; 54=1; 59=0; 47=P; 207=TST; 58=A0TUQG; 10=007; ";
    private String _timeAck     = "-13:00:50.232";
    private String _ackDateTime = _dateStr + _timeAck;
    private String mack = "8=FIX.4.2; 9=226; 35=8; 49=CHIX_TST; 56=LLT1_CHIX; 34=963; 43=N; 52=" + _ackDateTime + "; 20=0; 60=" + _ackDateTime
                          + "; 37=236117; 150=0; 39=0; 40=2; 54=1; 38=124; 55=VODl; 44=12.00; 47=P; 59=0; 14=0; 17=A236117; 32=0; 31=0.00; 151=124; 11=A0TUQG; 10=093; ";

    private AbstractFixDecoder _testDecoder;
    private TimeUtils          _calc;
    private ReusableString     _tmp1 = new ReusableString( 30 );
    private ReusableString     _tmp2 = new ReusableString( 30 );
    private byte[]             _buf;
    private Standard42Encoder  _testEncoder;
    private BackTestClock      _clock;
    private Clock              _origClock;

    public MarketNewOrderAckImpl doTestACK( byte[] fixMsg, int offset, int len, Map<String, String> vals ) {

        Event msg = _testDecoder.decode( fixMsg, offset, len );

        assertTrue( msg.getClass() == MarketNewOrderAckImpl.class );

        MarketNewOrderAckImpl ack = (MarketNewOrderAckImpl) msg;

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
        Event msg = _testDecoder.decode( fixMsg, offset, len );

        assertTrue( msg.getClass() == ClientNewOrderSingleImpl.class );

        ClientNewOrderSingleImpl nos = (ClientNewOrderSingleImpl) msg;

        // must toString ZString's

        assertEquals( vals.get( "11" ), nos.getClOrdId().toString() );
        assertEquals( vals.get( "38" ), Long.toString( (long) nos.getOrderQty() ) );
        assertEquals( vals.get( "59" ), "" + (char) nos.getTimeInForce().getVal() );
        assertEquals( vals.get( "40" ), "" + (char) nos.getOrdType().getVal() );
        assertEquals( vals.get( "54" ), "" + (char) nos.getSide().getVal() );
        assertEquals( vals.get( "55" ), nos.getSymbol().toString() );
        assertEquals( vals.get( "21" ), "" + (char) nos.getHandlInst().getVal() );
        assertEquals( Double.parseDouble( vals.get( "44" ) ), nos.getPrice(), Constants.TICK_WEIGHT );
        assertEquals( vals.get( "21" ), "" + (char) nos.getHandlInst().getVal() );
        assertEquals( vals.get( "52" ), _calc.internalTimeToFixStrMillis( _tmp1, nos.getEventTimestamp() ).toString() );
        assertEquals( vals.get( "60" ), _calc.internalTimeToFixStrMillis( _tmp2, nos.getTransactTime() ).toString() );
        assertEquals( vals.get( "47" ), "" + (char) nos.getOrderCapacity().getVal() );

        // another test will check encode & decode of date/time strings for 52 and 60

        return nos;
    }

    @Before
    public void setUp() {
        _clock     = new BackTestClock();
        _origClock = ClockFactory.set( _clock );

        _buf         = new byte[ 8192 ];
        _testDecoder = FixTestUtils.getOMSDecoder42();
        _testEncoder = new Standard42Encoder( (byte) '4', (byte) '2', _buf );
        _calc        = TimeUtilsFactory.createTimeUtils();
        _calc.setTodayFromLocalStr( _dateStr );
        _testDecoder.setTimeUtils( _calc );
    }

    @After
    public void tearDown() {
        ClockFactory.set( _origClock );
    }

    @Test
    public void testACKDecodeReEncode() {

        _clock.setCurrentTimeMillis( _ackDateTime );

        // get a NOS to use as src in ClientACK

        String nos = FixTestUtils.toFixDelim( mnos );

        ClientNewOrderSingleImpl nosEvent = (ClientNewOrderSingleImpl) _testDecoder.decode( nos.getBytes(), 0, nos.length() );

        // decode ack string to market ack pojo
        String ack = FixTestUtils.toFixDelim( mack );

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
        tagsToIgnore.add( "10" );
        tagsToIgnore.add( "14" );
        tagsToIgnore.add( "40" );
        tagsToIgnore.add( "47" );
        tagsToIgnore.add( "21" );
        tagsToIgnore.add( "59" );
        tagsToIgnore.add( "1111" );

        FixTestUtils.compare( ack, resultEncoded, tagsToIgnore, true, false );
    }

    @Test
    public void testNOSDecodeReEncode() {
        _clock.setCurrentTimeMillis( _nosDateTime );

        String nos = FixTestUtils.toFixDelim( mnos );

        Map<String, String> vals = FixTestUtils.msgToMap( nos );

        _testDecoder.setCompIds( vals.get( "56" ), null, vals.get( "49" ), null );
        _testEncoder.setCompIds( vals.get( "49" ), null, vals.get( "56" ), null );

        ClientNewOrderSingleImpl nosEvent = doTestNOS( nos.getBytes(), 0, nos.length(), vals );

        _testEncoder.encode( nosEvent );

        String resultEncoded = new String( _buf, _testEncoder.getOffset(), _testEncoder.getLength() );

        FixTestUtils.compare( nos, resultEncoded, false );
    }
}
