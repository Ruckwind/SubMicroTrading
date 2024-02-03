package com.rr.core.tasks;

import com.rr.core.lang.ClockFactory;
import com.rr.core.time.BackTestClock;

public class BackTestZTimerFactory {

    private static ThreadLocal<BackTestZTimer> _threadLocalTimer     = ThreadLocal.withInitial( () -> new BackTestZTimer( (BackTestClock) ClockFactory.get() ) );

    public static BackTestZTimer get()  { return _threadLocalTimer.get(); }

    public static void reset() {
        _threadLocalTimer.remove();
    }
}
