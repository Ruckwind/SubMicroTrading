/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.utp;

import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.utils.Utils;
import com.rr.model.generated.codec.UTPEuronextCashDecoder;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.warmup.FixTestUtils;

import java.util.TimeZone;

public class UTPTestUtils {

    public static UTPEuronextCashDecoder getDecoder( String dateStr ) {
        UTPEuronextCashDecoder decoder = new UTPEuronextCashDecoder();
        decoder.setClientProfile( FixTestUtils.getTestClient() );
        decoder.setInstrumentLocator( new DummyInstrumentLocator() );
        TimeUtils calc = TimeUtilsFactory.createTimeUtils();
        if ( dateStr != null ) {
            calc.setTodayFromLocalStr( dateStr );
        }
        calc.setLocalTimezone( TimeZone.getTimeZone( "CET" ) );
        decoder.setTimeUtils( calc );
        decoder.setReceived( Utils.nanoTime() );
        return decoder;
    }

}
