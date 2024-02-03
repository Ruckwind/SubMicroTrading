package com.rr.core.tasks;

import com.rr.core.lang.Env;
import com.rr.core.utils.ThreadPriority;

/**
 * ZTimer t = ZTimerFactory.get()
 * <p>
 * Note the ZTimer will be a shared StandardZTimer (as its a heavyweight object) or a backtest threadLocal instance
 */
public class ZTimerFactory {

    private static boolean _useBacktestClock = false;

    private static ZTimer _timerInstance = createTimer();

    private static ZTimer createTimer() {
        if ( Env.isBacktest() ) {

            return BackTestZTimerFactory.get();
        }

        return new StandardZTimer( "Scheduler", ThreadPriority.Scheduler );
    }

    public static ZTimer get() {
        if ( Env.isBacktest() ) {

            return BackTestZTimerFactory.get();
        }

        return _timerInstance;
    }
}
