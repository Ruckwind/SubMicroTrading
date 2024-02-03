/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableType;
import com.rr.core.lang.stats.SizeConstants;

public class LogEventHuge extends BaseLogEvent<LogEventHuge> {

    private static final int EXPECTED_SIZE = SizeConstants.DEFAULT_LOG_EVENT_HUGE;

    @Override protected final int getExpectedMaxEventSize() {
        return EXPECTED_SIZE;
    }

    @Override public final ReusableType getReusableType() {
        return CoreReusableType.LogEventHuge;
    }
}
