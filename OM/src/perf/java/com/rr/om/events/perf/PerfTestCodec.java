/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Reusable;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Logger;
import com.rr.core.model.Event;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.utils.Percentiles;
import com.rr.core.utils.Utils;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.model.generated.fix.codec.Standard44Encoder;
import com.rr.model.generated.internal.events.impl.ClientNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderAckImpl;
import com.rr.model.generated.internal.events.impl.MarketNewOrderSingleImpl;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Test;

public class PerfTestCodec extends BaseTestCase {

    private static final Logger _log = ConsoleFactory.console( PerfTestCodec.class );

    private int count = 10000;

    private static <T extends Reusable<T>> void presize( Class<T> aclass, int chains, int chainSize, int extraAlloc ) {

        SuperPool<T> sp = SuperpoolManager.instance().getSuperPool( aclass );

        sp.init( chains, chainSize, extraAlloc );
    }

    public PerfTestCodec() {
        // nothing
    }

    @Test
    public void testEncoding() {
        doTest( 5, count, true );
        doTest( 5, count, false );
    }

    private void doTest( int iter, int max, boolean nanoTiming ) {
        for ( int i = 0; i < iter; ++i ) {
            runTest( max, nanoTiming );
        }
    }

    private void runTest( int max, boolean nanoTiming ) {

        int chainSize  = 1000;
        int chains     = max / chainSize;
        int extraAlloc = 10;

        presize( ClientNewOrderSingleImpl.class, chains, chainSize, extraAlloc );
        presize( MarketNewOrderSingleImpl.class, chains, chainSize, extraAlloc );
        presize( ClientNewOrderAckImpl.class, chains, chainSize, extraAlloc );
        presize( MarketNewOrderAckImpl.class, chains, chainSize, extraAlloc );

        long[] time = new long[ max ];

        _log.info( "Forcing gc START" );
        System.gc();
        _log.info( "Forcing gc COMPLETE" );

        byte[] cnos = FixTestUtils.semiColonToFixDelim( "8=FIX.4.2;9=209;35=D;49=CLIENTXY;56=XXWWZZQQ1;34=85299;52=" + FixTestUtils.getDateStr() +
                                                        "-08:29:08.618;15=GBP;60=" + FixTestUtils.getDateStr() + "-08:29:08.618;1=CLNT_JSGT33;48=ICAD.XPAR;" +
                                                        "21=1;22=R;38=133;54=1;40=2;55=ICAD.XPAR;100=CHIX;11=100621100514021;58=SWP;59=0;" +
                                                        "44=73.4500;10=129;" );

        // "8=FIX.4.2;9=210;35=D;49=ME01;50=ST;56=CHIX;34=84737;52=20100621-08:29:08.620;11=HC02ILOFT;55=ICADp;15=GBP;21=1;38=133;40=2;44=73.45;54=1;58=SWP;59=0;100=CHIX;47=P;528=P;526=HR_100621100514021;128=CHIX;129=CHIX;207=CIX;11611=42;10=244;"

        byte[] mack = FixTestUtils.semiColonToFixDelim( "8=FIX.4.2;9=225;35=8;49=CHIX;56=ME01;34=85795;52=" + FixTestUtils.getDateStr() + "-08:29:08.622;" +
                                                        "57=ST;20=0;60=" + FixTestUtils.getDateStr() + "-08:29:08.622;37=4441418;150=0;39=0;40=2;54=1;38=133;" +
                                                        "55=ICADp;44=73.45;47=P;59=0;109=ME01;14=0;6=0.00;17=A4441418;32=0;31=0.00;151=133;" +
                                                        "11=HC02ILOFT;10=101;" );

        // "8=FIX.4.2;9=287;35=8;49=XXWWZZQQ1;56=CLIENTXY;34=86027;52=20100621-08:29:08.623;150=0;20=0;11=100621100514021;55=ICAD.XPAR;15=GBP;44=73.45;6=0;32=0;31=0;1=CLNT_JSGT33;14=0;17=A4441418;30=CHI;38=133;39=0;40=2;54=1;59=0;151=133;37=HC02ILOFT;120=GBP;22=R;48=ICAD.XPAR;60=20100621-08:29:08.623;11611=42,2403,55;10=103;"

        final byte[] buf = new byte[ 512 ];

        Standard44DecoderOMS decoder = FixTestUtils.getOMSDecoder44();
        Standard44Encoder    encoder = new Standard44Encoder( (byte) '4', (byte) '4', buf );
        TimeUtils            calc    = TimeUtilsFactory.createTimeUtils();

        encoder.setNanoStats( nanoTiming );
        decoder.setNanoStats( nanoTiming );

        calc.setTodayFromLocalStr( FixTestUtils.getDateStr() );
        decoder.setTimeUtils( calc );
        decoder.setReceived( Utils.nanoTime() );

        Event m1 = decoder.decode( cnos, 0, cnos.length );
        Event m2 = decoder.decode( mack, 0, mack.length );

        ClientNewOrderSingleImpl clientNos = (ClientNewOrderSingleImpl) m1;
        MarketNewOrderSingleImpl mktNos    = FixTestUtils.getMarketNOS( clientNos );
        MarketNewOrderAckImpl    mktAck    = (MarketNewOrderAckImpl) m2;
        ClientNewOrderAckImpl    clientAck = FixTestUtils.getClientAck( mktAck, clientNos );

        _log.info( "START TEST ==================" );

        long start = Utils.nanoTime();

        long t;

        if ( nanoTiming ) {
            for ( int i = 0; i < max; i++ ) {
                t = Utils.nanoTime();

                decoder.decode( cnos, 0, cnos.length );   // 2usec
                encoder.encode( mktNos );                   // 4usecs
                decoder.decode( mack, 0, mack.length );     // 2uwsec
                encoder.encode( clientAck );                 // 4 usecs

                time[ i ] = (int) (Utils.nanoTime() - t);
            }
        } else {
            for ( int i = 0; i < max; i++ ) {
                decoder.decode( cnos, 0, cnos.length );   // 2usec
                encoder.encode( mktNos );                   // 4usecs
                decoder.decode( mack, 0, mack.length );     // 2uwsec
                encoder.encode( clientAck );                 // 4 usecs
            }
        }

        long end = Utils.nanoTime();

        System.out.println( "NOS cnt=" + max + ", ave=" + ((end - start) / max) );

        if ( nanoTiming ) {
            Percentiles p = new Percentiles( time );

            System.out.println( "NanoSecond stats " + " count=" + max +
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
