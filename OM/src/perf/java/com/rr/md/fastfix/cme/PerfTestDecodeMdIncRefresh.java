/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.cme;

import com.rr.core.codec.binary.fastfix.FastFixBuilder;
import com.rr.core.codec.binary.fastfix.FastFixDecodeBuilder;
import com.rr.core.codec.binary.fastfix.PresenceMapReader;
import com.rr.core.codec.binary.fastfix.PresenceMapWriter;
import com.rr.core.codec.binary.fastfix.common.ComponentFactory;
import com.rr.core.codec.binary.fastfix.msgdict.DictComponentFactory;
import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.Reusable;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Logger;
import com.rr.core.model.SecurityIDSource;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.utils.Percentiles;
import com.rr.core.utils.Utils;
import com.rr.md.us.cme.reader.MDIncRefresh_81_Reader;
import com.rr.md.us.cme.writer.MDIncRefresh_81_Writer;
import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.model.generated.internal.events.recycle.MDIncRefreshRecycler;
import com.rr.model.generated.internal.type.MDEntryType;
import com.rr.model.generated.internal.type.MDUpdateAction;
import org.junit.Test;

public class PerfTestDecodeMdIncRefresh extends BaseTestCase {

    private static final Logger _log = ConsoleFactory.console( PerfTestDecodeMdIncRefresh.class );

    private int _iter  = 5;
    private int _count = 100000;

    private static <T extends Reusable<T>> void presize( Class<T> aclass, int chains, int chainSize, int extraAlloc ) {
        SuperPool<T> sp = SuperpoolManager.instance().getSuperPool( aclass );
        sp.init( chains, chainSize, extraAlloc );
    }

    public PerfTestDecodeMdIncRefresh() {
        // nothing
    }

    @Test
    public void testEncoding() {
        doTest( _iter, _count, true, 10, 1, 100 );
        doTest( _iter, _count, false, 10, 1, 100 );
    }

    private void doTest( int iter, int max, boolean nanoTiming, int numMdEntries, int pMapInitSize, int chainSize ) {
        for ( int i = 0; i < iter; ++i ) {
            runTest( max, nanoTiming, numMdEntries, pMapInitSize, chainSize );
        }
    }

    private MDIncRefreshImpl makeUpdate( int numMDEntries ) {

        MDIncRefreshImpl inc = new MDIncRefreshImpl();

        inc.setEventTimestamp( ClockFactory.get().currentTimeMillis() );
        inc.setMsgSeqNum( 1000000 );
        inc.setPossDupFlag( false );

        inc.setNoMDEntries( numMDEntries );

        MDEntryImpl first = new MDEntryImpl();
        MDEntryImpl tmp   = first;

        for ( int i = 0; i < numMDEntries; i++ ) {
            tmp.setSecurityIDSource( SecurityIDSource.ExchangeSymbol );
            tmp.getSecurityIDForUpdate().copy( 12345678 );
            tmp.setMdUpdateAction( MDUpdateAction.New );
            tmp.setRepeatSeq( i + 1 );
            tmp.setNumberOfOrders( i * 10 );
            tmp.setMdPriceLevel( i + 1 );
            tmp.setMdEntryType( MDEntryType.Bid );
            tmp.setMdEntryPx( 1000.12345 + i );
            tmp.setMdEntrySize( 100 + i * 10 );
            tmp.setMdEntryTime( 800000 + i );

            tmp.setNext( new MDEntryImpl() );
            tmp = tmp.getNext();
        }

        inc.setMDEntries( first );

        return inc;
    }

    private void runTest( int max, boolean nanoTiming, int numMDEntries, int initPMapSize, int chainSize ) {

        int chains     = max / chainSize;
        int extraAlloc = 10;

        presize( MDIncRefreshImpl.class, chains, chainSize, extraAlloc );
        presize( MDEntryImpl.class, chains, chainSize, extraAlloc );

        long[] time = new long[ max ];

        SuperPool<MDIncRefreshImpl> mdIncSP      = SuperpoolManager.instance().getSuperPool( MDIncRefreshImpl.class );
        MDIncRefreshRecycler        mdIncRecyler = new MDIncRefreshRecycler( chainSize, mdIncSP );

        _log.info( "Forcing gc START" );
        System.gc();
        _log.info( "Forcing gc COMPLETE" );

        final byte[] buf = new byte[ 512 ];

        FastFixDecodeBuilder decoder = new FastFixDecodeBuilder();
        FastFixBuilder       encoder = new FastFixBuilder( buf, 0 );

        ComponentFactory cf = new DictComponentFactory();

        MDIncRefresh_81_Reader reader = new MDIncRefresh_81_Reader( cf, name.getMethodName(), 81 );
        MDIncRefresh_81_Writer writer = new MDIncRefresh_81_Writer( cf, name.getMethodName(), 81 );

        MDIncRefreshImpl inc = makeUpdate( numMDEntries );

        _log.info( "START TEST ==================" );

        long start = Utils.nanoTime();

        long t;

        PresenceMapWriter pMapOut = new PresenceMapWriter( encoder, 0, initPMapSize );
        PresenceMapReader pMapIn  = new PresenceMapReader();

        MDIncRefreshImpl last;

        if ( nanoTiming ) {
            for ( int i = 0; i < max; i++ ) {
                encoder.clear();
                pMapOut.reset();

                writer.write( encoder, pMapOut, inc );
                pMapOut.end();

                t = Utils.nanoTime();

                decoder.start( buf, 0, buf.length );
                pMapIn.readMap( decoder );

                last = reader.read( decoder, pMapIn );

                time[ i ] = (int) (Utils.nanoTime() - t);

                mdIncRecyler.recycle( last );
            }
        } else {
            for ( int i = 0; i < max; i++ ) {
                encoder.clear();
                pMapOut.reset();

                writer.write( encoder, pMapOut, inc );
                pMapOut.end();

                decoder.start( buf, 0, buf.length );
                pMapIn.readMap( decoder );

                last = reader.read( decoder, pMapIn );

                mdIncRecyler.recycle( last );
            }
        }

        long end = Utils.nanoTime();

        System.out.println( "Tick Decode cnt=" + max + ", ave=" + ((end - start) / max) );

        if ( nanoTiming ) {
            Percentiles p = new Percentiles( time );

            System.out.println( "Tick Decode NanoSecond stats " + " count=" + max +
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

        _log.info( "Forcing gc START" );
        System.gc();
        _log.info( "Forcing gc COMPLETE" );
    }
}
