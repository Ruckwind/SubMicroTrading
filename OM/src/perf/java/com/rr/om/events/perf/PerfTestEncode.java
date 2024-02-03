/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.codec.FixEncodeBuilder;
import com.rr.core.codec.FixEncodeBuilderImpl;
import com.rr.core.lang.*;
import com.rr.core.model.Currency;
import com.rr.core.model.Event;
import com.rr.core.model.SecurityIDSource;
import com.rr.core.utils.Utils;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.model.generated.fix.model.defn.FixDictionary44;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderSingleImpl;
import com.rr.model.generated.internal.type.HandlInst;
import com.rr.model.generated.internal.type.OrderCapacity;
import com.rr.model.generated.internal.type.TimeInForce;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

/**
 * test the performance difference between using ReusableStrings in a NOS and ViewStrings
 *
 * @author Richard Rose
 */
public class PerfTestEncode extends BaseTestCase {

    private static final byte NOS = (byte) 'D';

    private final byte[]           buf          = new byte[ 8192 ];
    private final FixEncodeBuilder encoder      = new FixEncodeBuilderImpl( buf, 0, (byte) '4', (byte) '4' );
    private final TimeUtils        tzCalculator = TimeUtilsFactory.createTimeUtils();

    private ViewString _senderCompID = new ViewString( "ME" );
    private ViewString _targetCompID = new ViewString( "PROPA" );

    public final void encodeNewOrderSingle( final MarketNewOrderSingleImpl msg ) {
        final long now = CommonTimeUtils.unixTimeToInternalTime( ClockFactory.get().currentTimeMillis() );
        encoder.start();
        encoder.encodeByte( 35, NOS );
        encoder.encodeInt( FixDictionary44.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        encoder.encodeString( 49, _senderCompID );
        encoder.encodeString( 56, _targetCompID );
        encoder.encodeString( FixDictionary44.Symbol, msg.getSymbol() );        // tag55
        encoder.encodeUTCTimestamp( FixDictionary44.SendingTime, now );        // tag52
        encoder.encodeUTCTimestamp( FixDictionary44.TransactTime, now );        // tag60
        final TimeInForce tTimeInForce = msg.getTimeInForce();
        if ( tTimeInForce != null ) encoder.encodeByte( FixDictionary44.TimeInForce, tTimeInForce.getVal() );        // tag59
        final SecurityIDSource tSecurityIDSource = msg.getSecurityIDSource();
        if ( tSecurityIDSource != null ) encoder.encodeByte( FixDictionary44.SecurityIDSource, tSecurityIDSource.getVal() );        // tag22
        encoder.encodeString( FixDictionary44.ClOrdId, msg.getClOrdId() );        // tag11
        final HandlInst tHandlInst = msg.getHandlInst();
        if ( tHandlInst != null ) encoder.encodeByte( FixDictionary44.HandlInst, tHandlInst.getVal() );        // tag21
        encoder.encodePrice( FixDictionary44.OrderQty, msg.getOrderQty() );        // tag38
        encoder.encodePrice( FixDictionary44.Price, msg.getPrice() );        // tag44

        encoder.encodeString( 49, _senderCompID );
        encoder.encodeString( 56, _targetCompID );
        encoder.encodeString( FixDictionary44.SecurityID, msg.getSecurityId() );        // tag48
        encoder.encodeByte( FixDictionary44.OrdType, msg.getOrdType().getVal() );        // tag40
        encoder.encodeByte( FixDictionary44.Side, msg.getSide().getVal() );        // tag54
        encoder.encodeString( FixDictionary44.ExDest, msg.getExDest() );        // tag100
        encoder.encodeString( FixDictionary44.Account, msg.getAccount() );        // tag1
        encoder.encodeString( FixDictionary44.Text, msg.getText() );        // tag58
        encoder.encodeString( FixDictionary44.SecurityExchange, msg.getSecurityExchange().getMIC() );        // tag207
        final Currency tCurrency = msg.getCurrency();
        if ( tCurrency != null ) encoder.encodeBytes( FixDictionary44.Currency, tCurrency.getVal() );        // tag15
        final OrderCapacity tOrderCapacity = msg.getOrderCapacity();
        if ( tOrderCapacity != null ) encoder.encodeByte( FixDictionary44.OrderCapacity, tOrderCapacity.getVal() );        // tag528

        final long sent = Utils.nanoTime(); // 1usec hit

        msg.setOrderSent( sent );        // HOOK
        encoder.encodeEnvelope();
    }

    public long fullTest( int iterations ) {

        MarketNewOrderSingleImpl msg = getNOS();

        long startTime = Utils.nanoTime();

        for ( int i = 0; i < iterations; i++ ) {
            encodeNewOrderSingle( msg );
        }

        long endTime = Utils.nanoTime();

        long duration = endTime - startTime;

        return duration;
    }

    public long perfTestReusableString( int iterations ) {

        byte             msgType      = 'D';
        int              seqNum       = 12243;                              // dummy val
        ZString          senderCompId = new ViewString( "PROPA" );
        ZString          targetCompId = new ViewString( "CLIENTXY" );
        int              sendTimeMS   = (int) (ClockFactory.get().currentTimeMillis() % Constants.MS_IN_DAY);
        TimeInForce      tif          = TimeInForce.Day;
        SecurityIDSource secTypeId    = SecurityIDSource.ExchangeSymbol;
        ZString          symbol       = new ViewString( "ICAD.PA" );
        ZString          clOrdId      = new ViewString( "100621100514021" );
        HandlInst        handlInst    = HandlInst.AutoExecPublic;
        //int              txnTime      = (int)(ClockFactory.get().currentTimeMillis() % Constants.MS_IN_DAY);
        int    qty   = 133;
        double price = 73.4500;

        long startTime = Utils.nanoTime();

        for ( int i = 0; i < iterations; i++ ) {
            final long txnTime = CommonTimeUtils.unixTimeToInternalTime( ClockFactory.get().currentTimeMillis() );

            encoder.start();

            encoder.encodeInt( 35, msgType );
            encoder.encodeInt( 34, seqNum );
            encoder.encodeString( 49, senderCompId );
            encoder.encodeUTCTimestamp( 52, sendTimeMS );
            encoder.encodeString( 56, targetCompId );
            encoder.encodeByte( 59, tif.getVal() );
            encoder.encodeByte( 22, secTypeId.getVal() );
            encoder.encodeString( 55, symbol );
            encoder.encodeString( 11, clOrdId );
            encoder.encodeByte( 21, handlInst.getVal() );
            encoder.encodeUTCTimestamp( 60, txnTime );
            encoder.encodeInt( 38, qty );
            encoder.encodePrice( 44, price );

            Utils.nanoTime();

            encoder.encodeEnvelope();
        }

        long endTime = Utils.nanoTime();

        long duration = endTime - startTime;

        return duration;
    }

    @Test
    public void testStringPerf() {

        int runs       = 5;
        int iterations = 100000;

        for ( int idx = 0; idx < runs; idx++ ) {

            long duration = perfTestReusableString( iterations );

            System.out.println( "MIN TEST Run " + idx + " duration=" + duration + ", aveNano=" + (duration / iterations) );
        }

        for ( int idx = 0; idx < runs; idx++ ) {

            long duration = fullTest( iterations );

            System.out.println( "FULL TEST Run " + idx + " duration=" + duration + ", aveNano=" + (duration / iterations) );
        }
    }

    public final void tmp2_encodeNewOrderSingle( final MarketNewOrderSingleImpl msg ) {
        final long now = CommonTimeUtils.unixTimeToInternalTime( ClockFactory.get().currentTimeMillis() );
        encoder.start();
        encoder.encodeByte( 35, NOS );
        encoder.encodeInt( FixDictionary44.MsgSeqNum, msg.getMsgSeqNum() );        // tag34
        encoder.encodeString( 49, _senderCompID );
        encoder.encodeString( 56, _targetCompID );
        encoder.encodeString( FixDictionary44.Symbol, msg.getSymbol() );        // tag55
        encoder.encodeUTCTimestamp( FixDictionary44.SendingTime, now );        // tag52
        encoder.encodeUTCTimestamp( FixDictionary44.TransactTime, now );        // tag60
        encoder.encodeString( FixDictionary44.ClOrdId, msg.getClOrdId() );        // tag11
        encoder.encodePrice( FixDictionary44.OrderQty, msg.getOrderQty() );        // tag38
        encoder.encodePrice( FixDictionary44.Price, msg.getPrice() );        // tag44

        final long sent = Utils.nanoTime(); // 1usec hit

        msg.setOrderSent( sent );        // HOOK
        encoder.encodeEnvelope();
    }

    public final void tmp_encodeNewOrderSingle( final MarketNewOrderSingleImpl msg ) {
        final long txnTime = CommonTimeUtils.unixTimeToInternalTime( ClockFactory.get().currentTimeMillis() );

        encoder.start();

        encoder.encodeInt( 35, NOS );
        encoder.encodeInt( 34, msg.getMsgSeqNum() );
        encoder.encodeString( 49, _senderCompID );
        encoder.encodeString( 56, _targetCompID );
        encoder.encodeString( 55, msg.getSymbol() );
        encoder.encodeUTCTimestamp( 52, txnTime );
        encoder.encodeUTCTimestamp( 60, txnTime );
        encoder.encodeByte( 59, msg.getTimeInForce().getVal() );
        encoder.encodeByte( 22, msg.getSecurityIDSource().getVal() );
        encoder.encodeString( 11, msg.getClOrdId() );
        encoder.encodePrice( 38, msg.getOrderQty() );
        encoder.encodeByte( 21, msg.getHandlInst().getVal() );
        encoder.encodePrice( 44, msg.getPrice() );

        Utils.nanoTime();

        encoder.encodeEnvelope();

    }

    private MarketNewOrderSingleImpl getNOS() {
        byte[] cnos = FixTestUtils.semiColonToFixDelim( "8=FIX.4.2;9=209;35=D;49=CLIENTXY;56=XXWWZZQQ1;34=85299;52=" + FixTestUtils.getDateStr() +
                                                        "-08:29:08.618;15=GBP;60=" + FixTestUtils.getDateStr() + "-08:29:08.618;1=CLNT_JSGT33;48=ICAD.XPAR;" +
                                                        "21=1;22=R;38=133;54=1;40=2;55=ICAD.XPAR;100=CHIX;11=100621100514021;58=SWP;59=0;" +
                                                        "44=73.4500;10=129;" );

        Standard44DecoderOMS decoder = FixTestUtils.getOMSDecoder44();

        TimeUtils calc = TimeUtilsFactory.createTimeUtils();

        calc.setTodayFromLocalStr( FixTestUtils.getDateStr() );
        decoder.setTimeUtils( calc );
        decoder.setReceived( Utils.nanoTime() );

        Event m1 = decoder.decode( cnos, 0, cnos.length );

        ClientNewOrderSingleImpl clientNos = (ClientNewOrderSingleImpl) m1;
        MarketNewOrderSingleImpl mktNos    = FixTestUtils.getMarketNOS( clientNos );

        return mktNos;
    }
}
