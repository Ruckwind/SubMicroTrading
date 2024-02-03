/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.utils.*;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.model.generated.fix.codec.Standard44Encoder;
import com.rr.model.generated.internal.events.impl.ClientNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderSingleImpl;
import com.rr.model.generated.internal.events.recycle.ClientNewOrderAckRecycler;
import com.rr.model.generated.internal.events.recycle.MarketNewOrderSingleRecycler;
import com.rr.om.order.OrderImpl;
import com.rr.om.order.OrderVersion;
import com.rr.om.order.collections.HashEntry;
import com.rr.om.processor.BaseProcessorTestCase;
import com.rr.om.processor.EventProcessorImpl;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

public class PerfTestOrderMapSingleThread extends BaseProcessorTestCase {

    private long[] _roundTrip;
    private long[] _toMKt;
    private long[] _omNOS;
    private long[] _omACK;

    private ViewString _baseOrderId = new ReusableString( "ORDID" );
    private ViewString _baseExecId  = new ReusableString( "EXECID" );

    private EventProcessorImpl _localProc;

    public static void main( String[] arg ) throws Exception {
        int count = Integer.parseInt( arg[ 0 ] );

        PerfTestOrderMapSingleThread t = new PerfTestOrderMapSingleThread();

        ThreadUtilsFactory.get().setPriority( Thread.currentThread(), ThreadPriority.Processor );

        for ( int i = 0; i < 3; i++ ) {
            System.out.println( "TEST RoundTrip with OM " + i + " ====================================================================" );
            ReflectUtils.invoke( "setUp", t );
            t.doRunOrderMap( count, 0, 0 );
            ReflectUtils.invoke( "tearDown", t );
        }
    }

    private static void mkKey( ReusableString key, boolean isClient, int i ) {
        key.reset();

        key.append( (isClient) ? 'C' : 'M' );
        key.append( "SOMEKEY" );
        key.append( 1000000 + i );
    }

    @Test
    public void testAckWithUniqueExecId() {

        doRunOrderMap( 100, 2, 5 );
        doRunOrderMap( 10000, 0, 0 );
        doRunOrderMap( 10000, 0, 1 );
    }

    private void doRunOrderMap( int total, int genDelayMS, int ackDelayMS ) {
        presizePool( total );

        doTest( "HASHMAP", total, genDelayMS, ackDelayMS, getDirectProcesssor( total, false ) );
    }

    private void doTest( String desc, int total, int genDelayMS, int ackDelayMS, EventProcessorImpl proc ) {
        _roundTrip = new long[ total ];
        _toMKt     = new long[ total ];
        _omNOS     = new long[ total ];
        _omACK     = new long[ total ];

        _localProc = proc;

        Standard44DecoderOMS decoder      = FixTestUtils.getOMSDecoder44();
        ReusableString       decodeBuffer = new ReusableString( 250 );

        ReusableString key = new ReusableString( 20 );

        SuperpoolManager             spm         = SuperpoolManager.instance();
        MarketNewOrderSingleRecycler nosRecycler = spm.getRecycler( MarketNewOrderSingleRecycler.class, MarketNewOrderSingleImpl.class );
        ClientNewOrderAckRecycler    ackRecycler = spm.getRecycler( ClientNewOrderAckRecycler.class, ClientNewOrderAckImpl.class );

        MarketNewOrderAckImpl mack;

        ReusableString mkDecBuf = new ReusableString( 250 );
        ReusableString orderId  = new ReusableString( SizeConstants.DEFAULT_MARKETORDERID_LENGTH );
        ReusableString execId   = new ReusableString( SizeConstants.DEFAULT_EXECID_LENGTH );

        final byte[]      bufMKt        = new byte[ 250 ];
        final byte[]      bufClient     = new byte[ 250 ];
        Standard44Encoder mktEncoder    = new Standard44Encoder( (byte) '4', (byte) '4', bufMKt );
        Standard44Encoder clientEncoder = new Standard44Encoder( (byte) '4', (byte) '4', bufClient );

        clientEncoder.setNanoStats( false );
        mktEncoder.setNanoStats( false );

        SuperpoolManager.instance().resetPoolStats();

        Utils.invokeGC();

        long preNOS, postNOS;
        long preACK, postACK;

        for ( int i = 0; i < total; ++i ) {

            if ( genDelayMS > 0 ) {
                ThreadUtilsFactory.get().sleep( genDelayMS );
            }

            decoder.setReceived( Utils.nanoTime() );
            mkKey( key, true, i );

            ClientNewOrderSingleImpl cnos = FixTestUtils.getClientNOS( decodeBuffer, decoder, key, 1, 1, _upMsgHandler );
            preNOS = Utils.nanoTime();
            _localProc.handle( cnos );
            postNOS = Utils.nanoTime();
            MarketNewOrderSingleImpl mnos = (MarketNewOrderSingleImpl) _downQ.poll();

            mktEncoder.encode( mnos );
            long nowNano = Utils.nanoTime();
            mnos.getSrcEvent().setOrderSent( nowNano );

            long delay = (nowNano - mnos.getOrderReceived());
            _toMKt[ i ] = delay;

            orderId.copy( _baseOrderId );
            orderId.append( i );
            execId.copy( _baseExecId );
            execId.append( i );

            if ( ackDelayMS > 0 ) {
                ThreadUtilsFactory.get().sleep( ackDelayMS );
            }

            clearQueues();

            mack = FixTestUtils.getMarketACK( mkDecBuf, decoder, mnos.getClOrdId(), mnos.getOrderQty(), mnos.getPrice(), orderId, execId );
            nosRecycler.recycle( mnos );

            preACK = Utils.nanoTime();
            _localProc.handle( mack );
            postACK = Utils.nanoTime();
            ClientNewOrderAckImpl cack = (ClientNewOrderAckImpl) _upQ.poll();

            clientEncoder.encode( cack );
            nowNano = Utils.nanoTime();

            long nosLat     = cack.getOrderSent() - cack.getOrderReceived();
            long ackLat     = nowNano - cack.getAckReceived();
            long nosProcLat = postNOS - preNOS;
            long ackProcLat = postACK - preACK;

            _roundTrip[ i ] = (nosLat + ackLat);
            _omNOS[ i ]     = nosProcLat;
            _omACK[ i ]     = ackProcLat;

            ackRecycler.recycle( cack );

            clearQueues();
        }

        logStats( desc + ": TO_MARKET", _toMKt, total, genDelayMS, ackDelayMS );
        logStats( desc + ": OM_NOS", _omNOS, total, genDelayMS, ackDelayMS );
        logStats( desc + ": OM_ACK", _omACK, total, genDelayMS, ackDelayMS );
        logStats( desc + ": ROUNDTRIP", _roundTrip, total, genDelayMS, ackDelayMS );

        logPoolStats();
    }

    private void logPoolStats() {
        SuperpoolManager.instance().getSuperPool( ClientNewOrderSingleImpl.class ).logStats();
        SuperpoolManager.instance().getSuperPool( MarketNewOrderSingleImpl.class ).logStats();
        SuperpoolManager.instance().getSuperPool( MarketNewOrderAckImpl.class ).logStats();
        SuperpoolManager.instance().getSuperPool( ClientNewOrderAckImpl.class ).logStats();
        SuperpoolManager.instance().getSuperPool( ReusableString.class ).logStats();
        SuperpoolManager.instance().getSuperPool( OrderImpl.class ).logStats();
        SuperpoolManager.instance().getSuperPool( OrderVersion.class ).logStats();
    }

    private void logStats( String comment, long[] stats, long total, long genDelayMS, long ackDelayMS ) {
        Percentiles p = new Percentiles( stats );

        System.out.println( "[" + comment + "]  ROUND TRIP NanoSecond stats " + " count=" + total + ", genDelayMS=" + genDelayMS +
                            ", acckDelayMS=" + ackDelayMS +
                            ", med=" + p.median() +
                            ", ave=" + p.getAverage() +
                            ", min=" + p.getMinimum() +
                            ", max=" + p.getMaximum() +
                            "\n                 " +
                            ", p99=" + p.calc( 99 ) +
                            ", p95=" + p.calc( 95 ) +
                            ", p90=" + p.calc( 90 ) +
                            ", p80=" + p.calc( 80 ) +
                            ", p70=" + p.calc( 70 ) +
                            ", p50=" + p.calc( 50 ) + "\n" );
    }

    private void presizePool( int count ) {
        int chainSize = 1000;
        int numChains = (count / chainSize) + 2;
        int extra     = 50;

        SuperpoolManager.instance().getSuperPool( HashEntry.class ).init( numChains, chainSize, extra );
        SuperpoolManager.instance().getSuperPool( OrderImpl.class ).init( numChains, chainSize, extra );
        SuperpoolManager.instance().getSuperPool( OrderVersion.class ).init( numChains, chainSize, extra );
        SuperpoolManager.instance().getSuperPool( ClientNewOrderSingleImpl.class ).init( numChains, chainSize, extra );
        SuperpoolManager.instance().getSuperPool( MarketNewOrderSingleImpl.class ).init( numChains, chainSize, extra );
        SuperpoolManager.instance().getSuperPool( ClientNewOrderAckImpl.class ).init( numChains, chainSize, extra );
        SuperpoolManager.instance().getSuperPool( MarketNewOrderAckImpl.class ).init( numChains, chainSize, extra );
        SuperpoolManager.instance().getSuperPool( ReusableString.class ).init( numChains, chainSize, extra );
    }
}
