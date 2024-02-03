package com.rr.core.time;

import com.rr.core.lang.Clock;
import com.rr.core.lang.CommonTimeUtils;
import com.rr.core.lang.TimeUtils;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;

import static com.rr.core.utils.Utils.getRequestedStartTime;

/**
 * for backtesting we want one clock instance per thread as a strategy backtest container wrapper will run the strat in single thread
 * and will set the clock before processing each event
 */

@SuppressWarnings( "unused" ) // used via reflection
public class BackTestClock implements Clock {

    private static ThreadLocal<ClockState> _state = ThreadLocal.withInitial( ClockState::new );

    private static final class ClockState {

        private static int _nextClockId = 1;

        private final int _clockId;

        private Logger _log = LoggerFactory.create( BackTestClock.class );
        private long _currentTimeMillis;

        private static synchronized int getNextId() {
            return _nextClockId++;
        }

        ClockState() {
            _clockId = getNextId();

            _currentTimeMillis = getRequestedStartTime();
        }

        long getCurrentTimeMillis() {
            return _currentTimeMillis;
        }

        void setTime( long newTime ) {

            if ( _log.isEnabledFor( Level.trace ) ) {
                _log.log( Level.trace, Thread.currentThread().getName() + " clock #" + _clockId + " : move from " + TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( _currentTimeMillis ) +
                                       " to " + TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( newTime ) );
            }

            _currentTimeMillis = newTime;
        }
    }

    public static void reset() {
        _state.remove();
    }

    @Override public long currentTimeMillis() {
        return _state.get().getCurrentTimeMillis();
    }

    @Override public long nanoTime() {
        return _state.get().getCurrentTimeMillis() * 1000000;
    }

    @Override public long nanoTimeMonotonicRaw() {
        return _state.get().getCurrentTimeMillis() * 1000000;
    }

    @Override public long currentInternalTime() {
        return CommonTimeUtils.unixTimeToInternalTime( currentTimeMillis() );
    }

    public String toString() {
        return TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( TimeUtilsFactory.safeTimeUtils().getLocalTimeZone(), _state.get().getCurrentTimeMillis() );
    }

    public void add( long deltaMillis ) {
        _state.get().setTime( _state.get().getCurrentTimeMillis() + deltaMillis );
    }

    public int getId() { return _state.get()._clockId; }

    /**
     * Test utlity function
     *
     * @param timeStr time in FIX UTC date time format eg 20100510-12:01:01.100
     */
    public void setCurrentTimeMillis( final String timeStr ) {
        final TimeUtils.DateParseResults dateResult = new TimeUtils.DateParseResults();

        TimeUtilsFactory.safeTimeUtils().parseUTCStringToInternalTime( timeStr.getBytes(), 0, dateResult );

        long time = TimeUtilsFactory.safeTimeUtils().internalTimeToUnixTime( dateResult._internalTime );

        setCurrentTimeMillis( time );
    }

    public void setCurrentTimeMillis( long time ) {
        _state.get().setTime( time );
    }
}
