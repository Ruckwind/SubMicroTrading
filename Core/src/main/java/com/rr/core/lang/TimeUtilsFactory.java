package com.rr.core.lang;

import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.time.StandardTimeUtils;
import com.rr.core.utils.ReflectUtils;

public class TimeUtilsFactory {

    private static final String DEFAULT_TIME_UTILS_CLASS = "com.rr.core.time.StandardTimeUtils";

    private static TimeUtilsFactory _factoryInstance = new TimeUtilsFactory();

    private final Class<TimeUtils> _timeClass;

    private TimeUtils _safeTimeUtils;

    public static TimeUtilsFactory instance() { return _factoryInstance; }

    // helper methods to keep usage simpler
    public static TimeUtils safeTimeUtils() { return instance()._safeTimeUtils; }

    public static TimeUtils createTimeUtils() { return instance().create(); }

    public static void reset() {
        _factoryInstance = new TimeUtilsFactory();
    }

    private TimeUtilsFactory() {
        String timeUtilsClass = AppProps.instance().getProperty( CoreProps.TIME_UTILS_CLASS, false, DEFAULT_TIME_UTILS_CLASS );

        Class<TimeUtils> cl = ReflectUtils.getClass( timeUtilsClass );

        _timeClass = cl;

        TimeUtils letter = create();

        // BackTestTimeUtils is already threadsafe
        _safeTimeUtils = (letter instanceof StandardTimeUtils) ? new ThreadSafeTimeUtils() : letter;
    }

    public TimeUtils create() {
        try {
            return _timeClass.newInstance();
        } catch( Exception e ) {
            throw new RuntimeException( "Unable to instantiate " + _timeClass.getName() + " : " + e.getMessage(), e );
        }
    }

    public void setSafeTimeUtils( final TimeUtils safeTimeUtils ) {
        _safeTimeUtils = safeTimeUtils;
    }
}
