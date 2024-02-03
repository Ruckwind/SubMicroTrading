/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.millenium;

import com.rr.core.codec.AbstractFixDecoder;
import com.rr.core.lang.*;
import com.rr.core.model.Event;
import com.rr.core.time.BackTestClock;
import com.rr.model.generated.codec.MilleniumLSEDecoder;
import com.rr.model.generated.codec.MilleniumLSEEncoder;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderAckImpl;
import com.rr.model.generated.internal.events.interfaces.NewOrderAck;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.model.generated.internal.type.OrderCapacity;
import com.rr.om.warmup.FixTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MilleniumLSECodecTest extends BaseTestCase {

    private String _dateStr     = "20100510";
    private String _timeNos     = "-13:00:50.232";
    private String _nosDateTime = _dateStr + _timeNos;
    private String _timeAck     = "-13:00:50.232";
    private String _ackDateTime = _dateStr + _timeAck;

    private AbstractFixDecoder  _fixDecoder;
    private MilleniumLSEDecoder _decoder;
    private MilleniumLSEEncoder _encoder;
    private TimeUtils           _calc;
    private byte[]              _buf;

    private BackTestClock _clock;
    private Clock         _origClock;

    public MarketNewOrderAckImpl doTestACK( byte[] fixMsg, int offset, int len ) {

        Event msg = _fixDecoder.decode( fixMsg, offset, len );

        assertTrue( msg.getClass() == MarketNewOrderAckImpl.class );

        MarketNewOrderAckImpl ack = (MarketNewOrderAckImpl) msg;

        return ack;
    }

    @Before
    public void setUp() {
        _clock     = new BackTestClock();
        _origClock = ClockFactory.set( _clock );

        _fixDecoder = FixTestUtils.getOMSDecoder44();
        _calc       = TimeUtilsFactory.createTimeUtils();
        _calc.setTodayFromLocalStr( _dateStr );
        _fixDecoder.setTimeUtils( _calc );

        _decoder = MilleniumTestUtils.getDecoder( _dateStr );
        _decoder.setInstrumentLocator( _fixDecoder.getInstrumentLocator() );

        _buf     = new byte[ 8192 ];
        _encoder = new MilleniumLSEEncoder( _buf, 0 );

        _calc = TimeUtilsFactory.createTimeUtils();
        _calc.setTodayFromLocalStr( _dateStr );
        _fixDecoder.setTimeUtils( _calc );
        _decoder.setTimeUtils( _calc );
        _encoder.setTimeUtils( _calc );
    }

    @After
    public void tearDown() {
        ClockFactory.set( _origClock );
    }

    @Test
    public void testACKDecodeReEncode() {

        _clock.setCurrentTimeMillis( _ackDateTime );

        // decode ack string to market ack pojo
        String ack = "8=FIX.4.4; 9=200; 35=8; 43=N; 34=12243; 49=PROPA; 52=" + _ackDateTime + "; 17=12345678; " +
                     "56=ME; 37=88000000001; 39=0; 150=0; 40=1; 54=1; 55=BT.XLON; 11=99000000001; 21=1; 60=" + _ackDateTime + "; 38=10; " +
                     "59=0; 22=R; 48=BT.XLON; 1111=PR; 10=233; ";

        ack = FixTestUtils.toFixDelim( ack );

        _fixDecoder.setCompIds( "PROPA", null, "ME", null );

        MarketNewOrderAckImpl ackEvent = doTestACK( ack.getBytes(), 0, ack.length() );

        _encoder.encode( ackEvent );

        Event decodedACK = _decoder.decode( _buf, 0, _encoder.getLength() );

        assertTrue( decodedACK instanceof NewOrderAck );

        NewOrderAck lseACK = (NewOrderAck) decodedACK;

        assertEquals( ackEvent.getClOrdId(), lseACK.getClOrdId() );
        assertEquals( ackEvent.getOrderId(), lseACK.getOrderId() );
        assertEquals( ackEvent.getMsgSeqNum(), lseACK.getMsgSeqNum() );
    }

    @Test
    public void testNOSDecodeReEncode() {
        _clock.setCurrentTimeMillis( _nosDateTime );

        String nos = "8=FIX.4.4; 9=162; 35=D; 43=N; 49=PROPA; 56=ME; 34=12243; 52=" + _nosDateTime + "; " +
                     "11=99000000001; 59=0; 22=R; 48=BT.XLON; 38=50; 44=10.23; 15=GBP; 40=2; 54=1; 55=BT.XLON; 21=1; " +
                     "60=" + _nosDateTime + "; 100=L; 10=027; ";

        nos = FixTestUtils.toFixDelim( nos );

        _fixDecoder.setCompIds( new ViewString( "ME" ), null, new ViewString( "PROPA" ), null );

        Event                    msg      = _fixDecoder.decode( nos.getBytes(), 0, nos.length() );
        ClientNewOrderSingleImpl nosEvent = (ClientNewOrderSingleImpl) msg;

        _encoder.encode( nosEvent );

        Event decodedNOS = _decoder.decode( _buf, 0, _encoder.getLength() );

        assertTrue( decodedNOS instanceof NewOrderSingle );

        NewOrderSingle lseNOS = (NewOrderSingle) decodedNOS;

        assertEquals( nosEvent.getClOrdId(), lseNOS.getClOrdId() );
        assertEquals( nosEvent.getOrderQty(), lseNOS.getOrderQty(), Constants.TICK_WEIGHT );
        assertEquals( nosEvent.getPrice(), lseNOS.getPrice(), Constants.TICK_WEIGHT );
        assertEquals( nosEvent.getOrdType(), lseNOS.getOrdType() );
        assertEquals( nosEvent.getSide(), lseNOS.getSide() );
        assertEquals( nosEvent.getSymbol(), lseNOS.getSymbol() );
        assertEquals( nosEvent.getTimeInForce(), lseNOS.getTimeInForce() );
        assertEquals( nosEvent.getAccount(), lseNOS.getAccount() );

        // fields defaulted
        assertEquals( OrderCapacity.Principal, lseNOS.getOrderCapacity() );

        // fields not encoded in Millenium should be unset when decoded
        assertTrue( lseNOS.getMsgSeqNum() == 0 || lseNOS.getMsgSeqNum() == Constants.UNSET_INT );

        assertEquals( 0, lseNOS.getSecurityId().length() );
        assertEquals( null, lseNOS.getSecurityIDSource() );
        assertEquals( null, lseNOS.getHandlInst() );
    }

    @Test
    public void testNOSDecodeReEncodeNoPrice() {
        _clock.setCurrentTimeMillis( _nosDateTime );

        String nos = "8=FIX.4.4; 9=153; 35=D; 43=N; 49=PROPA; 56=ME; 34=12243; 52=" + _nosDateTime + "; " +
                     "11=99000000001; 59=0; 22=R; 48=BT.XLON; 38=50; 15=GBP; 40=2; 54=1; 55=BT.XLON; 21=1; " +
                     "60=" + _nosDateTime + "; 10=129; ";

        nos = FixTestUtils.toFixDelim( nos );

        _fixDecoder.setCompIds( new ViewString( "ME" ), null, new ViewString( "PROPA" ), null );

        Event                    msg      = _fixDecoder.decode( nos.getBytes(), 0, nos.length() );
        ClientNewOrderSingleImpl nosEvent = (ClientNewOrderSingleImpl) msg;

        _encoder.encode( nosEvent );

        Event decodedNOS = _decoder.decode( _buf, 0, _encoder.getLength() );

        assertTrue( decodedNOS instanceof NewOrderSingle );

        NewOrderSingle lseNOS = (NewOrderSingle) decodedNOS;

        assertEquals( nosEvent.getClOrdId(), lseNOS.getClOrdId() );
        assertEquals( nosEvent.getOrderQty(), lseNOS.getOrderQty(), Constants.TICK_WEIGHT );
        assertEquals( 0.0, lseNOS.getPrice(), Constants.TICK_WEIGHT );
        assertEquals( nosEvent.getOrdType(), lseNOS.getOrdType() );
        assertEquals( nosEvent.getSide(), lseNOS.getSide() );
        assertEquals( nosEvent.getSymbol(), lseNOS.getSymbol() );
        assertEquals( nosEvent.getTimeInForce(), lseNOS.getTimeInForce() );
        assertEquals( nosEvent.getAccount(), lseNOS.getAccount() );

        // fields defaulted
        assertEquals( OrderCapacity.Principal, lseNOS.getOrderCapacity() );

        // fields not encoded in UTP should be unset when decoded
        assertEquals( Constants.UNSET_INT, lseNOS.getMsgSeqNum() );
        assertEquals( 0, lseNOS.getSecurityId().length() );
        assertEquals( null, lseNOS.getSecurityIDSource() );
        assertEquals( null, lseNOS.getHandlInst() );
    }
}
