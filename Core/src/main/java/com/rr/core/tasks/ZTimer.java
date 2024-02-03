/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.tasks;

import com.rr.core.lang.Constants;
import com.rr.core.logger.Logger;

import java.time.DayOfWeek;
import java.util.EnumSet;

/**
 * simple Timer specialisation to be able to control the thread affinity / priority of the timer thread
 * on standard linux the timer will have the O/S scheduler margin of error ... around 10ms.
 * <p>
 * if a timer task is for a local timezone other than default then it must pass that into the ZTimerTask constructor
 */
public interface ZTimer {

    /**
     * schedule task to run
     * <p>
     * ZTimerTask  is taken from TimerTask but without temp object creation
     * <p>
     * <p>
     * Remember the ZTimerTask fire method will be invoked
     *
     * @param task
     * @param unixTimeMS     the time to fire the task (unix time, ie MS from epoch) ... if this is in the past will fire asap
     * @param repeatInterval 0 for one off firing, otherwise number of ms when to repeat fire event
     */
    void schedule( ZTimerTask task, long unixTimeMS, long repeatInterval );

    void schedule( ZTimerTask task, long unixTimeMS, long repeatInterval, int maxRepeats );

    void schedule( ZTimerTask task, String firstTimeInDay, String lastTimeInDay, long fireIntervalMS, EnumSet<DayOfWeek> daysEnabled );

    void schedule( ZTimerTask task, long unixTimeMS, long repeatInterval, Logger log );

    void schedule( ZTimerTask task, long unixTimeMS, long repeatInterval, int maxRepeats, Logger log );

    void schedule( ZTimerTask task, String firstTimeInDay, String lastTimeInDay, long fireIntervalMS, EnumSet<DayOfWeek> daysEnabled, Logger log );

    void schedule( ZTimerTask task, String firstTimeInDay, String lastTimeInDay, long fireIntervalMS, EnumSet<DayOfWeek> daysEnabled, long randMS, Logger log );

    default void scheduleDaily( ZTimerTask task, String localTimeToFire, EnumSet<DayOfWeek> daysEnabled ) {
        schedule( task, localTimeToFire, localTimeToFire, Constants.MS_IN_DAY, daysEnabled );
    }

    void setDebug( boolean debug );
}
