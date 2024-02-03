/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.millenium;

import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.utils.Utils;
import com.rr.model.generated.codec.MilleniumLSEDecoder;
import com.rr.om.dummy.warmup.DummyInstrumentLocator;
import com.rr.om.warmup.FixTestUtils;

import java.util.TimeZone;

public class MilleniumTestUtils {

    public static MilleniumLSEDecoder getDecoder( String dateStr ) {
        MilleniumLSEDecoder decoder = new MilleniumLSEDecoder();
        decoder.setClientProfile( FixTestUtils.getTestClient() );
        decoder.setInstrumentLocator( new DummyInstrumentLocator() );
        TimeUtils calc = TimeUtilsFactory.createTimeUtils();
        if ( dateStr != null ) {
            calc.setTodayFromLocalStr( dateStr );
        }
        calc.setLocalTimezone( TimeZone.getTimeZone( "GMT" ) );
        decoder.setTimeUtils( calc );
        decoder.setReceived( Utils.nanoTime() );
        return decoder;
    }

}
