package com.rr.core.lang;

import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.time.StandardClock;
import com.rr.core.utils.ReflectUtils;

/**
 * Clock c = ClockFactory.get()
 * <p>
 * Note the Clock could be a threadsafe shared clock ... eg the StandardClock  or
 * the BTClock which has a single instance per Thread
 */
public class ClockFactory {

    private static final String DEFAULT_TIME_UTILS_STANDARD_CLASS = "com.rr.core.time.StandardClock";
    private static final String DEFAULT_TIME_UTILS_BACKTEST_CLASS = "com.rr.core.time.BackTestClock";

    private static Clock _clockInstance = createClock();
    private static Clock _standardClock = new StandardClock();

    public static Clock createClock() {
        String dClass = (Env.isBacktest()) ? DEFAULT_TIME_UTILS_BACKTEST_CLASS : DEFAULT_TIME_UTILS_STANDARD_CLASS;

        String ClockClass = AppProps.instance().getProperty( CoreProps.CLOCK_CLASS, false, dClass );

        Class<Clock> cl = ReflectUtils.getClass( ClockClass );

        try {
            return cl.newInstance();
        } catch( Exception e ) {
            throw new RuntimeException( "Unable to instantiate " + cl.getName() + " : " + e.getMessage(), e );
        }
    }

    public static Clock get() { return _clockInstance; }

    /**
     * back testing / test utility method
     *
     * @param c new clock
     * @return previous clock
     * @WARNING dont forget in test tearDown to restore the original clock or you will break unit tests that require time to pass without prompting
     */
    public static Clock set( Clock c ) {
        Clock orig = _clockInstance;
        _clockInstance = c;
        return orig;
    }

    public static void reset() {
        _clockInstance = createClock();
    }

    public static Clock getLiveClock() { return _standardClock; }

    public static void init() {
        if ( Env.isBacktest() ) {

        }
    }
}
