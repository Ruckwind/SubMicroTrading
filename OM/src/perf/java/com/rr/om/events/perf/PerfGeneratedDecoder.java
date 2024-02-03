/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Logger;
import com.rr.core.model.Event;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.utils.Utils;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.recycle.ClientNewOrderSingleRecycler;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * test the performance difference between using ReusableStrings in a NOS and ViewStrings
 *
 * @author Richard Rose
 */
public class PerfGeneratedDecoder extends BaseTestCase {

    private final static Logger _log = ConsoleFactory.console( PerfGeneratedDecoder.class );

    private Standard44DecoderOMS _testDecoder;
    private String               _dateStr = "20100510";
    private TimeUtils            _calc;

    public long perfTestReusableDecode( int iterations, int chains, int chainSize, int extraAlloc, boolean recycle ) {

        SuperPool<ClientNewOrderSingleImpl> newOrderSinglePool = SuperpoolManager.instance().getSuperPool( ClientNewOrderSingleImpl.class );
        newOrderSinglePool.init( chains, chainSize, extraAlloc );

        String nos = "8=FIX.4.4; 9=158; 35=D; 34=12243; 49=PROPA; 52=" + _dateStr + "-12:01:01.100; " +
                     "56=ME; 59=0; 22=R; 48=BT.XLON; 40=2; 54=1; 55=BT.XLON; 11=XX2100; 21=1; " +
                     "60=" + _dateStr + "-12:01:01.000; 38=50; 44=10.23; 10=229; ";

        nos = FixTestUtils.toFixDelim( nos );
        // Map<String,String> vals = FixTestUtils.msgToMap( nos );

        byte[] fixMsg = nos.getBytes();

        _testDecoder = FixTestUtils.getOMSDecoder44();
        _calc        = TimeUtilsFactory.createTimeUtils();
        _calc.setTodayFromLocalStr( _dateStr );
        _testDecoder.setTimeUtils( _calc );
        _testDecoder.setReceived( Utils.nanoTime() );

        System.gc();

        long duration;

        if ( recycle ) {
            duration = doDecodeWithRecycle( iterations, fixMsg );
        } else {
            duration = doDecode( iterations, fixMsg );
        }

        _testDecoder = null;

        System.gc();

        return duration;
    }

    @Test
    public void testStringPerf() {

        int runs       = 4;
        int iterations = 100000;

        int[] poolSizes       = { 100, 100, 100, 100 };
        int[] extraAllocSizes = { 10, 10, 10, 10 };

        for ( int idx = 0; idx < runs; idx++ ) {

            int chainSize = poolSizes[ idx ];
            int chains    = iterations / chainSize + 1;
            int extraSize = extraAllocSizes[ idx ];

            long duration = perfTestReusableDecode( iterations, chains, chainSize, extraSize, false );

            _log.info( "Run " + idx + ", NO recycling, poolSize=" + chainSize + ", extra=" + extraSize +
                       ", duration=" + duration + ", aveNano=" + (duration / iterations) );

//            duration = perfTestReusableDecode( iterations, chains, chainSize, extraSize, true );
//
//            _log.info( "Run " + idx + ", RECYCLING ON, poolSize=" + chainSize + ", extra=" + extraSize +
//                       ", duration=" + duration + ", aveNano=" + (duration / iterations) );
        }
    }

    private long doDecode( int iterations, byte[] fixMsg ) {
        long startTime = Utils.nanoTime();

        int errors = 0;

        for ( int i = 0; i < iterations; i++ ) {
            try {
                _testDecoder.decode( fixMsg, 0, fixMsg.length );
            } catch( Exception e ) {
                ++errors;
            }
        }

        long endTime  = Utils.nanoTime();
        long duration = endTime - startTime;

        assertEquals( 0, errors );

        return duration;
    }

    private long doDecodeWithRecycle( int iterations, byte[] fixMsg ) {
        final SuperPool<ClientNewOrderSingleImpl> sp       = SuperpoolManager.instance().getSuperPool( ClientNewOrderSingleImpl.class );
        final ClientNewOrderSingleRecycler        recycler = new ClientNewOrderSingleRecycler( sp.getChainSize(), sp );

        long errors    = 0;
        long startTime = Utils.nanoTime();

        for ( int i = 0; i < iterations; i++ ) {
            try {
                Event msg = _testDecoder.decode( fixMsg, 0, fixMsg.length );
                recycler.recycle( (ClientNewOrderSingleImpl) msg );
            } catch( Exception e ) {
                ++errors;
            }
        }

        long endTime  = Utils.nanoTime();
        long duration = endTime - startTime;

        assertEquals( 0, errors );

        return duration;
    }
}
