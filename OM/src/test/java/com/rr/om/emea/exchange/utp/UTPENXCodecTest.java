/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.utp;

import com.rr.core.codec.AbstractFixDecoder;
import com.rr.core.lang.*;
import com.rr.core.model.Event;
import com.rr.model.generated.codec.UTPEuronextCashDecoder;
import com.rr.model.generated.codec.UTPEuronextCashEncoder;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderAckImpl;
import com.rr.model.generated.internal.events.interfaces.NewOrderAck;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.model.generated.internal.type.OrderCapacity;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UTPENXCodecTest extends BaseTestCase {

    private AbstractFixDecoder     _fixDecoder;
    private UTPEuronextCashDecoder _decoder;
    private UTPEuronextCashEncoder _encoder;
    private String                 _dateStr = "20100510";
    private TimeUtils              _calc;
    private byte[]                 _buf;

    public MarketNewOrderAckImpl doTestACK( byte[] fixMsg, int offset, int len ) {

        Event msg = _fixDecoder.decode( fixMsg, offset, len );

        assertTrue( msg.getClass() == MarketNewOrderAckImpl.class );

        MarketNewOrderAckImpl ack = (MarketNewOrderAckImpl) msg;

        return ack;
    }

    @Before
    public void setUp() {
        _fixDecoder = FixTestUtils.getOMSDecoder44();
        _calc       = TimeUtilsFactory.createTimeUtils();
        _calc.setTodayFromLocalStr( _dateStr );
        _fixDecoder.setTimeUtils( _calc );

        _decoder = UTPTestUtils.getDecoder( _dateStr );

        _buf     = new byte[ 8192 ];
        _encoder = new UTPEuronextCashEncoder( _buf, 0 );
    }

    @Test
    public void testACKDecodeReEncode() {

        // decode ack string to market ack pojo
        String ack = "8=FIX.4.4; 9=144; 35=8; 43=N; 34=12243; 49=PROPA; 52=" + _dateStr + "-12:01:01.232; " +
                     "56=ME; 37=8800000000123; 39=0; 150=0; 40=1; 54=1; 55=BT.XLON; 11=99000000001; 21=1; 60=" + _dateStr + "-12:01:01; 38=10; " +
                     "59=0; 22=R; 48=BT.XLON; 1111=PR; 10=233; ";

        ack = FixTestUtils.toFixDelim( ack );

        _fixDecoder.setCompIds( "PROPA", null, "ME", null );

        MarketNewOrderAckImpl ackEvent = doTestACK( ack.getBytes(), 0, ack.length() );

        _encoder.setOnBehalfOfId( new ViewString( "ME01" ) );

        _encoder.encode( ackEvent );

        Event decodedACK = _decoder.decode( _buf, 0, _encoder.getLength() );

        assertTrue( decodedACK instanceof NewOrderAck );

        NewOrderAck utpACK = (NewOrderAck) decodedACK;

        assertEquals( ackEvent.getClOrdId(), utpACK.getClOrdId() );
        assertEquals( ackEvent.getOrderId(), utpACK.getOrderId() );
        assertEquals( ackEvent.getMsgSeqNum(), utpACK.getMsgSeqNum() );
    }

    @Test
    public void testNOSDecodeReEncode() {
        String nos = "8=FIX.4.4; 9=162; 35=D; 43=N; 49=PROPA; 56=ME; 34=12243; 52=" + _dateStr + "-12:01:01.100; " +
                     "11=99000000001; 59=0; 22=R; 48=BT.XLON; 38=50; 44=10.23; 15=GBP; 40=2; 54=1; 55=BT.XLON; 21=1; " +
                     "60=" + _dateStr + "-12:01:01.000; 10=006; ";

        nos = FixTestUtils.toFixDelim( nos );

        _fixDecoder.setCompIds( new ViewString( "ME" ), null, new ViewString( "PROPA" ), null );
        _encoder.setOnBehalfOfId( new ViewString( "ME01" ) );

        Event                    msg      = _fixDecoder.decode( nos.getBytes(), 0, nos.length() );
        ClientNewOrderSingleImpl nosEvent = (ClientNewOrderSingleImpl) msg;

        _encoder.encode( nosEvent );

        Event decodedNOS = _decoder.decode( _buf, 0, _encoder.getLength() );

        assertTrue( decodedNOS instanceof NewOrderSingle );

        NewOrderSingle utpNOS = (NewOrderSingle) decodedNOS;

        assertEquals( nosEvent.getClOrdId(), utpNOS.getClOrdId() );
        assertEquals( nosEvent.getOrderQty(), utpNOS.getOrderQty(), Constants.TICK_WEIGHT );
        assertEquals( nosEvent.getPrice(), utpNOS.getPrice(), Constants.TICK_WEIGHT );
        assertEquals( nosEvent.getOrdType(), utpNOS.getOrdType() );
        assertEquals( nosEvent.getSide(), utpNOS.getSide() );
        assertEquals( nosEvent.getMsgSeqNum(), utpNOS.getMsgSeqNum() );
        assertEquals( nosEvent.getSymbol(), utpNOS.getSymbol() );
        assertEquals( nosEvent.getTimeInForce(), utpNOS.getTimeInForce() );
        assertEquals( nosEvent.getAccount(), utpNOS.getAccount() );

        // fields defaulted
        assertEquals( OrderCapacity.Principal, utpNOS.getOrderCapacity() );

        // fields not encoded in UTP should be unset when decoded
        assertEquals( 0, utpNOS.getSecurityId().length() );
        assertEquals( null, utpNOS.getSecurityIDSource() );
        assertEquals( null, utpNOS.getHandlInst() );
    }

    @Test
    public void testNOSDecodeReEncodeNoPrice() {
        String nos = "8=FIX.4.4; 9=153; 35=D; 43=N; 49=PROPA; 56=ME; 34=12243; 52=" + _dateStr + "-12:01:01.100; " +
                     "11=99000000001; 59=0; 22=R; 48=BT.XLON; 38=50; 15=GBP; 40=2; 54=1; 55=BT.XLON; 21=1; " +
                     "60=" + _dateStr + "-12:01:01.000; 10=108; ";

        nos = FixTestUtils.toFixDelim( nos );

        _fixDecoder.setCompIds( new ViewString( "ME" ), null, new ViewString( "PROPA" ), null );
        _encoder.setOnBehalfOfId( new ViewString( "ME01" ) );

        Event                    msg      = _fixDecoder.decode( nos.getBytes(), 0, nos.length() );
        ClientNewOrderSingleImpl nosEvent = (ClientNewOrderSingleImpl) msg;

        _encoder.encode( nosEvent );

        Event decodedNOS = _decoder.decode( _buf, 0, _encoder.getLength() );

        assertTrue( decodedNOS instanceof NewOrderSingle );

        NewOrderSingle utpNOS = (NewOrderSingle) decodedNOS;

        assertEquals( nosEvent.getClOrdId(), utpNOS.getClOrdId() );
        assertEquals( nosEvent.getOrderQty(), utpNOS.getOrderQty(), Constants.TICK_WEIGHT );
        assertEquals( 0.0, utpNOS.getPrice(), Constants.TICK_WEIGHT );
        assertEquals( nosEvent.getOrdType(), utpNOS.getOrdType() );
        assertEquals( nosEvent.getSide(), utpNOS.getSide() );
        assertEquals( nosEvent.getMsgSeqNum(), utpNOS.getMsgSeqNum() );
        assertEquals( nosEvent.getSymbol(), utpNOS.getSymbol() );
        assertEquals( nosEvent.getTimeInForce(), utpNOS.getTimeInForce() );
        assertEquals( nosEvent.getAccount(), utpNOS.getAccount() );

        // fields defaulted
        assertEquals( OrderCapacity.Principal, utpNOS.getOrderCapacity() );

        // fields not encoded in UTP should be unset when decoded
        assertEquals( 0, utpNOS.getSecurityId().length() );
        assertEquals( null, utpNOS.getSecurityIDSource() );
        assertEquals( null, utpNOS.getHandlInst() );
    }
}
