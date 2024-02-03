/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableType;
import com.rr.core.lang.stats.SizeConstants;

public class LogEventSmall extends BaseLogEvent<LogEventSmall> {

    private static final int EXPECTED_SIZE = SizeConstants.DEFAULT_LOG_EVENT_SMALL;

    public LogEventSmall() {
        super();
    }

    public LogEventSmall( String str ) {
        super( str );
    }

    @Override
    protected final int getExpectedMaxEventSize() {
        return EXPECTED_SIZE;
    }

    @Override
    public final ReusableType getReusableType() {
        return CoreReusableType.LogEventSmall;
    }
}
