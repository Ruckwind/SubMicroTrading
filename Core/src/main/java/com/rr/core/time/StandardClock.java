package com.rr.core.time;

import com.rr.core.lang.Clock;
import com.rr.core.lang.CommonTimeUtils;
import com.rr.core.utils.Utils;

@SuppressWarnings( "unused" ) // used via reflection
public class StandardClock implements Clock {

    @Override public long currentInternalTime() {
        return CommonTimeUtils.unixTimeToInternalTime( currentTimeMillis() );
    }

    @Override public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override public long nanoTime() {
        return Utils.nanoTime();
    }

    @Override public long nanoTimeMonotonicRaw() {
        return Utils.nanoTimeMonotonicRaw();
    }
}
