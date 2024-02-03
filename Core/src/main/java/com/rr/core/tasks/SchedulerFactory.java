package com.rr.core.tasks;

import com.rr.core.lang.Env;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.utils.ReflectUtils;

/**
 * Clock c = ClockFactory.get()
 * <p>
 * Note the Clock could be a threadsafe shared clock ... eg the StandardClock  or
 * the BTClock which has a single instance per Thread
 */
public class SchedulerFactory {

    private static final String DEFAULT_SCHEDULER_CLASS = "com.rr.core.tasks.SchedulerImpl";

    private static Scheduler _schedulerInstance = createScheduler();

    private static Scheduler createScheduler() {
        if ( Env.isBacktest() ) {
            return new BackTestScheduler();
        } else {
            String schedClass = AppProps.instance().getProperty( CoreProps.SCHEDULER_CLASS, false, DEFAULT_SCHEDULER_CLASS );

            Class<Scheduler> cl = ReflectUtils.getClass( schedClass );

            try {
                return cl.newInstance();
            } catch( Exception e ) {
                throw new RuntimeException( "Unable to instantiate scheduler " + cl.getName() + " : " + e.getMessage(), e );
            }
        }
    }

    public static Scheduler get() { return _schedulerInstance; }

    /**
     * for backtest usage, use with care, ensure cancel any scheduled tasks on the existing scheduler before invoking reset
     */
    public static void reset() {
        _schedulerInstance = createScheduler();
    }
}
