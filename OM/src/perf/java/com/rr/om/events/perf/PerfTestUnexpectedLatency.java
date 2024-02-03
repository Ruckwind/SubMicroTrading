/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Logger;
import com.rr.core.model.Event;
import com.rr.core.utils.Utils;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.impl.ClientNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderSingleImpl;
import org.junit.Test;

/**
 * Why is the first caall 20% slower than the second
 * <p>
 * switch( msg.getEventIdWithinCategory() ) {
 * <p>
 * <p>
 * switch( msg.getReusableType().getIdWithinCategory() ) {
 * <p>
 * This perf test doesnt correlate the findings !!
 */
public class PerfTestUnexpectedLatency extends BaseTestCase {

    private final static Logger _log = ConsoleFactory.console( PerfTestUnexpectedLatency.class );

    @SuppressWarnings( "unused" )
    @Test
    public void test() {

        Event nos = new MarketNewOrderSingleImpl();
        Event ack = new ClientNewOrderAckImpl();

        long t1 = testEventIdDirect( 1000000, nos, ack );
        long t2 = testEventIdIndirect( 1000000, nos, ack );

        long timeByRS   = testEventIdIndirect( 1000000, nos, ack );
        long timeDirect = testEventIdDirect( 1000000, nos, ack );

        _log.info( "typeId via interface                  =" + timeDirect );
        _log.info( "typeId via reusable type and final var=" + timeByRS );
    }

    private long doDirect( Event m, long x ) {
        switch( m.getReusableType().getSubId() ) {
        case EventIds.ID_NEWORDERSINGLE:
            x += 1;
            break;
        case EventIds.ID_NEWORDERACK:
            x += 2;
            break;
        case EventIds.ID_TRADENEW:
            x += 3;
            break;
        case EventIds.ID_CANCELREPLACEREQUEST:
            x += 4;
            break;
        case EventIds.ID_CANCELREQUEST:
            x += 5;
            break;
        case EventIds.ID_CANCELREJECT:
            x += 6;
            break;
        case EventIds.ID_REJECTED:
            x += 7;
            break;
        case EventIds.ID_CANCELLED:
            x += 8;
            break;
        case EventIds.ID_REPLACED:
            x += 9;
            break;
        case EventIds.ID_DONEFORDAY:
            x += 10;
            break;
        case EventIds.ID_STOPPED:
            x += 11;
            break;
        case EventIds.ID_EXPIRED:
            x += 12;
            break;
        case EventIds.ID_SUSPENDED:
            x += 13;
            break;
        case EventIds.ID_RESTATED:
            x += 14;
            break;
        case EventIds.ID_TRADECORRECT:
            x += 15;
            break;
        case EventIds.ID_TRADECANCEL:
            x += 16;
            break;
        case EventIds.ID_ORDERSTATUS:
            x += 17;
            break;
        }

        for ( int j = 0; j < 1000; ++j ) {
            //delay
        }
        return x;
    }

    private long doIndirect( Event m, long x ) {
        switch( m.getReusableType().getSubId() ) {
        case EventIds.ID_NEWORDERSINGLE:
            x += 1;
            break;
        case EventIds.ID_NEWORDERACK:
            x += 2;
            break;
        case EventIds.ID_TRADENEW:
            x += 3;
            break;
        case EventIds.ID_CANCELREPLACEREQUEST:
            x += 4;
            break;
        case EventIds.ID_CANCELREQUEST:
            x += 5;
            break;
        case EventIds.ID_CANCELREJECT:
            x += 6;
            break;
        case EventIds.ID_REJECTED:
            x += 7;
            break;
        case EventIds.ID_CANCELLED:
            x += 8;
            break;
        case EventIds.ID_REPLACED:
            x += 9;
            break;
        case EventIds.ID_DONEFORDAY:
            x += 10;
            break;
        case EventIds.ID_STOPPED:
            x += 11;
            break;
        case EventIds.ID_EXPIRED:
            x += 12;
            break;
        case EventIds.ID_SUSPENDED:
            x += 13;
            break;
        case EventIds.ID_RESTATED:
            x += 14;
            break;
        case EventIds.ID_TRADECORRECT:
            x += 15;
            break;
        case EventIds.ID_TRADECANCEL:
            x += 16;
            break;
        case EventIds.ID_ORDERSTATUS:
            x += 17;
            break;
        }
        for ( int j = 0; j < 1000; ++j ) {
            //delay
        }
        return x;
    }

    private long testEventIdDirect( long tot, Event m, Event ack ) {

        long x = 0;

        long start = Utils.nanoTime();

        for ( long i = 0; i < tot; i++ ) {

            x = doDirect( m, x );
            x = doDirect( ack, x );
        }

        long end = Utils.nanoTime();

        return end - start;
    }

    private long testEventIdIndirect( long tot, Event m, Event ack ) {

        long x = 0;

        long start = Utils.nanoTime();

        for ( long i = 0; i < tot; i++ ) {

            x = doIndirect( m, x );
            x = doIndirect( ack, x );
        }

        long end = Utils.nanoTime();

        return end - start;
    }

}
