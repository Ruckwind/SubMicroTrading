/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ClockFactory;
import com.rr.om.model.fix.FixTags;
import org.junit.Test;

/**
 * test the performance difference between using ReusableStrings in a NOS and ViewStrings
 *
 * @author Richard Rose
 */
// TODO check impact of replacing all the small (<=8 byte) with SmallString
public class PerfTestStrings extends BaseTestCase {

    public long perfTestReusableString( int iterations ) {

        TstReusableStringNOS nos = new TstReusableStringNOS();

        String msg = " 8=FIX.4.4; 9=152; 35=D; 34=12243; 49=PROPA; 52=20100510-12:01:01.100; " +
                     "56=ME; 59=0; 22=R; 48=BT.XLON; 40=2; 54=1; 55=BT.XLON; 11=XX2100; 21=1; " +
                     "60=20100510-12:01:01; 38=50; 44=10.23; 10=345;";

        byte[] buf = msg.getBytes();

        int accountStart          = findStart( msg, FixTags.Account );
        int accountLen            = findLen( msg, accountStart );
        int clOrdIdStart          = findStart( msg, FixTags.ClOrdID );
        int clOrdIdLen            = findLen( msg, clOrdIdStart );
        int securityIDStart       = findStart( msg, FixTags.SecurityID );
        int securityIDLen         = findLen( msg, securityIDStart );
        int senderCompIdStart     = findStart( msg, FixTags.SenderCompID );
        int senderCompIdLen       = findLen( msg, senderCompIdStart );
        int senderSubIdStart      = findStart( msg, FixTags.SenderSubID );
        int senderSubIdLen        = findLen( msg, senderSubIdStart );
        int onBehalfOfIdStart     = findStart( msg, FixTags.OnBehalfOfCompID );
        int onBehalfOfIdLen       = findLen( msg, onBehalfOfIdStart );
        int symbolStart           = findStart( msg, FixTags.Symbol );
        int symbolLen             = findLen( msg, symbolStart );
        int targetCompIdStart     = findStart( msg, FixTags.TargetCompID );
        int targetCompIdLen       = findLen( msg, targetCompIdStart );
        int targetSubIdStart      = findStart( msg, FixTags.TargetSubID );
        int targetSubIdLen        = findLen( msg, targetSubIdStart );
        int textStart             = findStart( msg, FixTags.Text );
        int textLen               = findLen( msg, textStart );
        int exDestinationStart    = findStart( msg, FixTags.ExDestination );
        int exDestinationLen      = findLen( msg, exDestinationStart );
        int securityExchangeStart = findStart( msg, FixTags.SecurityExch );
        int securityExchangeLen   = findLen( msg, securityExchangeStart );

        long startTime = ClockFactory.get().currentTimeMillis();

        for ( int i = 0; i < iterations; i++ ) {
            if ( accountStart > 0 ) nos._account.setValue( buf, accountStart, accountLen );
            if ( clOrdIdStart > 0 ) nos._clOrdID.setValue( buf, clOrdIdStart, clOrdIdLen );
            if ( securityIDStart > 0 ) nos._securityID.setValue( buf, securityIDStart, securityIDLen );
            if ( senderCompIdStart > 0 ) nos._senderCompID.setValue( buf, senderCompIdStart, senderCompIdLen );
            if ( senderSubIdStart > 0 ) nos._senderSubID.setValue( buf, senderSubIdStart, senderSubIdLen );
            if ( onBehalfOfIdStart > 0 ) nos._onBehalfOfID.setValue( buf, onBehalfOfIdStart, onBehalfOfIdLen );
            if ( symbolStart > 0 ) nos._symbol.setValue( buf, symbolStart, symbolLen );
            if ( targetCompIdStart > 0 ) nos._targetCompID.setValue( buf, targetCompIdStart, targetCompIdLen );
            if ( targetSubIdStart > 0 ) nos._targetSubID.setValue( buf, targetSubIdStart, targetSubIdLen );
            if ( textStart > 0 ) nos._text.setValue( buf, textStart, textLen );
            if ( exDestinationStart > 0 ) nos._exDestination.setValue( buf, exDestinationStart, exDestinationLen );
            if ( securityExchangeStart > 0 ) nos._securityExch.setValue( buf, securityExchangeStart, securityExchangeLen );
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        long duration = endTime - startTime;

        return duration;
    }

    public long perfTestViewString( int iterations ) {

        TstViewStringNOS nos = new TstViewStringNOS();

        String msg = " 8=FIX.4.4; 9=152; 35=D; 34=12243; 49=PROPA; 52=20100510-12:01:01.100; " +
                     "56=ME; 59=0; 22=R; 48=BT.XLON; 40=2; 54=1; 55=BT.XLON; 11=XX2100; 21=1; " +
                     "60=20100510-12:01:01; 38=50; 44=10.23; 10=345;";

        byte[] buf = msg.getBytes();

        int accountStart          = findStart( msg, FixTags.Account );
        int accountLen            = findLen( msg, accountStart );
        int clOrdIdStart          = findStart( msg, FixTags.ClOrdID );
        int clOrdIdLen            = findLen( msg, clOrdIdStart );
        int securityIDStart       = findStart( msg, FixTags.SecurityID );
        int securityIDLen         = findLen( msg, securityIDStart );
        int senderCompIdStart     = findStart( msg, FixTags.SenderCompID );
        int senderCompIdLen       = findLen( msg, senderCompIdStart );
        int senderSubIdStart      = findStart( msg, FixTags.SenderSubID );
        int senderSubIdLen        = findLen( msg, senderSubIdStart );
        int onBehalfOfIdStart     = findStart( msg, FixTags.OnBehalfOfCompID );
        int onBehalfOfIdLen       = findLen( msg, onBehalfOfIdStart );
        int symbolStart           = findStart( msg, FixTags.Symbol );
        int symbolLen             = findLen( msg, symbolStart );
        int targetCompIdStart     = findStart( msg, FixTags.TargetCompID );
        int targetCompIdLen       = findLen( msg, targetCompIdStart );
        int targetSubIdStart      = findStart( msg, FixTags.TargetSubID );
        int targetSubIdLen        = findLen( msg, targetSubIdStart );
        int textStart             = findStart( msg, FixTags.Text );
        int textLen               = findLen( msg, textStart );
        int exDestinationStart    = findStart( msg, FixTags.ExDestination );
        int exDestinationLen      = findLen( msg, exDestinationStart );
        int securityExchangeStart = findStart( msg, FixTags.SecurityExch );
        int securityExchangeLen   = findLen( msg, securityExchangeStart );

        long startTime = ClockFactory.get().currentTimeMillis();

        for ( int i = 0; i < iterations; i++ ) {
            nos.setValue( buf, 0, buf.length );
            if ( accountStart > 0 ) nos._account.setValue( accountStart, accountLen );
            if ( clOrdIdStart > 0 ) nos._clOrdID.setValue( clOrdIdStart, clOrdIdLen );
            if ( securityIDStart > 0 ) nos._securityID.setValue( securityIDStart, securityIDLen );
            if ( senderCompIdStart > 0 ) nos._senderCompID.setValue( senderCompIdStart, senderCompIdLen );
            if ( senderSubIdStart > 0 ) nos._senderSubID.setValue( senderSubIdStart, senderSubIdLen );
            if ( onBehalfOfIdStart > 0 ) nos._onBehalfOfID.setValue( onBehalfOfIdStart, onBehalfOfIdLen );
            if ( symbolStart > 0 ) nos._symbol.setValue( symbolStart, symbolLen );
            if ( targetCompIdStart > 0 ) nos._targetCompID.setValue( targetCompIdStart, targetCompIdLen );
            if ( targetSubIdStart > 0 ) nos._targetSubID.setValue( targetSubIdStart, targetSubIdLen );
            if ( textStart > 0 ) nos._text.setValue( textStart, textLen );
            if ( exDestinationStart > 0 ) nos._exDestination.setValue( exDestinationStart, exDestinationLen );
            if ( securityExchangeStart > 0 ) nos._securityExch.setValue( securityExchangeStart, securityExchangeLen );
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        long duration = endTime - startTime;

        return duration;
    }

    @Test
    public void testStringPerf() {

        int runs       = 5;
        int iterations = 1000000;

        for ( int idx = 0; idx < runs; idx++ ) {

            long reusableDuration   = perfTestReusableString( iterations );
            long viewStringDuration = perfTestViewString( iterations );

            System.out.println( "Run " + idx + " reusableStringDuration=" + reusableDuration + ", viewStringDuration=" + viewStringDuration );
        }
    }

    private int findLen( String msg, int valStart ) {

        if ( valStart == -1 ) return 0;

        int len = msg.indexOf( ';', valStart ) - valStart;

        return len;
    }

    private int findStart( String msg, int tag ) {

        String key = " " + tag + "=";

        int idx = msg.indexOf( key );

        if ( idx < 0 ) {
            return -1;
        }

        int valStart = idx + key.length();

        return valStart;
    }

}
