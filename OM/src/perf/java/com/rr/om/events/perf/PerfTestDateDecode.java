/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.time.TimeTables;
import com.rr.core.utils.NumberFormatUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PerfTestDateDecode extends BaseTestCase {

    protected final byte[] _today = new byte[ TimeUtils.DATE_STR_LEN ];

    protected TimeUtils _tzCalculator = TimeUtilsFactory.createTimeUtils();

    protected int    _idx = 0;
    protected byte[] _fixMsg;
    protected int    _maxIdx;
    protected int    _offset;

    @Test
    public void testDecodeDateTime() {

        int runs       = 5;
        int iterations = 10000000;

        doRunDateTimeDecode( runs, iterations );
    }

    private void doRunDateTimeDecode( int runs, int iterations ) {
        for ( int idx = 0; idx < runs; idx++ ) {

            long mult  = mult( iterations );
            long table = table( iterations );

            System.out.println( "Run " + idx + " mult=" + mult + ", table=" + table );
        }
    }

    private long mult( int iterations ) {

        _tzCalculator.getToday( _today );
        _fixMsg = (new String( _today ) + "12:01:01.100; ").getBytes();
        _maxIdx = _fixMsg.length;
        _offset = 0;

        long startTime = ClockFactory.get().currentTimeMillis();

        for ( int i = 0; i < iterations; ++i ) {

            _idx = 0;

            int todayLen = _today.length;

            for ( int j = 0; j < todayLen; ++j ) {
                if ( _fixMsg[ _idx + j ] != _today[ j ] ) {
                    assertEquals( _fixMsg[ _idx + j ], _today[ j ] );
                }
            }

            _idx += todayLen;

            int hTen = _fixMsg[ _idx++ ] - '0';
            int hDig = _fixMsg[ _idx++ ] - '0';
            _idx++;
            int mTen = _fixMsg[ _idx++ ] - '0';
            int mDig = _fixMsg[ _idx++ ] - '0';
            _idx++;
            int sTen = _fixMsg[ _idx++ ] - '0';
            int sDig = _fixMsg[ _idx++ ] - '0';

            int hour = ((hTen) * 10) + (hDig);
            int min  = ((mTen) * 10) + (mDig);
            int sec  = ((sTen) * 10) + (sDig);

            if ( hour < 0 || hour > 23 ) {
                assertTrue( hour >= 0 && hour < 24 );
            }

            if ( min < 0 || min > 59 ) {
                assertTrue( min >= 0 && min < 60 );
            }

            if ( sec < 0 || sec > 59 ) {
                assertTrue( sec >= 0 && sec < 60 );
            }

            int ms = ((hour * 3600) + (min * 60) + sec) * 1000 + _tzCalculator.getOffset();

            if ( _fixMsg[ _idx ] == '.' ) {
                _idx++;
                int msHun = _fixMsg[ _idx++ ] - '0';
                int msTen = _fixMsg[ _idx++ ] - '0';
                int msDig = _fixMsg[ _idx++ ] - '0';

                ms += ((msHun * 100) + (msTen * 10) + msDig);
            }

            if ( ms < 0 || ms > 999 ) {
                assertTrue( ms >= 0 && ms < 1000 );
            }
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        return endTime - startTime;
    }

    private long table( int iterations ) {

        _tzCalculator.getToday( _today );
        _fixMsg = (new String( _today ) + "12:01:01.100; ").getBytes();
        _maxIdx = _fixMsg.length;
        _offset = 0;

        long startTime = ClockFactory.get().currentTimeMillis();

        for ( int i = 0; i < iterations; ++i ) {

            _idx = 0;

            int todayLen = _today.length;

            for ( int j = 0; j < todayLen; ++j ) {
                if ( _fixMsg[ _idx + j ] != _today[ j ] ) {
                    assertEquals( _fixMsg[ _idx + j ], _today[ j ] );
                }
            }

            _idx += todayLen;

            int hTen = _fixMsg[ _idx++ ] - '0';
            int hDig = _fixMsg[ _idx++ ] - '0';
            _idx++;
            int mTen = _fixMsg[ _idx++ ] - '0';
            int mDig = _fixMsg[ _idx++ ] - '0';
            _idx++;
            int sTen = _fixMsg[ _idx++ ] - '0';
            int sDig = _fixMsg[ _idx++ ] - '0';

            int hour = NumberFormatUtils._tens[ hTen ] + hDig;
            int min  = NumberFormatUtils._tens[ mTen ] + mDig;
            int sec  = NumberFormatUtils._tens[ sTen ] + sDig;

            if ( hour < 0 || hour > 23 ) {
                assertTrue( hour >= 0 && hour < 24 );
            }

            if ( min < 0 || min > 59 ) {
                assertTrue( min >= 0 && min < 60 );
            }

            if ( sec < 0 || sec > 59 ) {
                assertTrue( sec >= 0 && sec < 60 );
            }

            int ms = TimeTables._hourToMS[ hour ] +
                     TimeTables._minToMS[ min ] +
                     TimeTables._secToMS[ sec ] +
                     _tzCalculator.getOffset();

            if ( _fixMsg[ _idx ] == '.' ) {
                _idx++;
                int msHun = _fixMsg[ _idx++ ] - '0';
                int msTen = _fixMsg[ _idx++ ] - '0';
                int msDig = _fixMsg[ _idx++ ] - '0';

                ms += NumberFormatUtils._hundreds[ msHun ] + NumberFormatUtils._tens[ msTen ] + msDig;
            }

            if ( ms < 0 || ms > 999 ) {
                assertTrue( ms >= 0 && ms < 1000 );
            }
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        return endTime - startTime;
    }
}
