/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.collections.EventHead;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ClockFactory;
import com.rr.core.model.Event;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

/**
 * test the performance difference in volatile next pointer vs array for use in processor Q
 */
public class PerfTestVolatileNextPointer extends BaseTestCase {

    protected Standard44DecoderOMS _decoder = FixTestUtils.getOMSDecoder44();

    @SuppressWarnings( "unused" )
    private double _dontOpt = 0;

    @Test
    public void testNext() {

        int runs       = 5;
        int iterations = 100000000;

        doRun( runs, iterations );
    }

    private long array( int iterations ) {
        ClientNewOrderSingleImpl o1 = FixTestUtils.getClientNOS( _decoder, "CL00000000001", 100, 1000.0 );

        Event[] msg = new Event[ 4 ];

        long startTime = ClockFactory.get().currentTimeMillis();

        int   size = 0;
        int   i;
        Event tmp2 = o1;

        for ( int j = 0; j < iterations; j++ ) {
            msg[ 0 ] = o1;
            ++size;

            for ( i = 0; i < size; ++i ) {

                tmp2 = msg[ i ];
            }
            size = 0;
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        _dontOpt = ((ClientNewOrderSingleImpl) tmp2).getOrderQty();

        return endTime - startTime;
    }

    private void doRun( int runs, int iterations ) {
        for ( int idx = 0; idx < runs; idx++ ) {

            long link  = queue( iterations );
            long array = array( iterations );

            System.out.println( "Run " + idx + " processor Q, array=" + array + ", queue=" + link );
        }
    }

    private long queue( int iterations ) {
        ClientNewOrderSingleImpl o1 = FixTestUtils.getClientNOS( _decoder, "CL00000000001", 100, 1000.0 );

        EventHead head = new EventHead();
        Event     tail = head;
        Event     tmp;
        Event     tmp2 = o1;

        long startTime = ClockFactory.get().currentTimeMillis();

        for ( int j = 0; j < iterations; j++ ) {
            tail.attachQueue( o1 );

            for ( tmp = head.getNextQueueEntry(); tmp != null; tmp = tmp.getNextQueueEntry() ) {

                tmp2 = tmp;
            }

            head.attachQueue( null );
            tail = head;
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        _dontOpt = ((ClientNewOrderSingleImpl) tmp2).getOrderQty();

        return endTime - startTime;
    }

}
