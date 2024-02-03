/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.tasks;

import com.rr.core.model.Event;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.Random;
import java.util.TimeZone;

public interface ZTimerTask extends Event {

    int MS_IN_HOUR = 60 * 60 * 1000;

    enum TaskState {
        INITIAL, SCHEDULED, EXECUTING, EXECUTED, CANCELLED, NO_MORE_RETRIES
    }

    void cancel();

    /**
     * fire the task within the timers thread of control
     */
    void fire();

    String getName();

    long getNextFireTime();

    void setNextFireTime( long nextFireTime );

    int getRepeatsLeft();

    void setRepeatsLeft( final int repeatsLeft );

    long getReqOffsetMS();

    void setReqOffsetMS( long reqOffsetMS );

    long getTaskInterval();

    void setTaskInterval( long taskInterval );

    TaskState getTaskState();

    void setTaskState( TaskState taskState );

    TimeZone getTimeZone();

    /**
     * the original init timer method
     *
     * @param nextFireTime   next fire time in unixMS
     * @param repeatInterval duration in ms between fire events
     * @param maxRepeat      maximum number of fire repeats
     */
    void init( long nextFireTime, long repeatInterval, int maxRepeat );

    void init( String firstTimeInDay, String lastTimeInDay, long fireIntervalMS, EnumSet<DayOfWeek> daysEnabled );

    /**
     * initialise a timer task which runs between a start and end time either everyday or a set of specified days (eg week days)
     *
     * @param firstTimeInDay
     * @param lastTimeInDay
     * @param fireIntervalMS
     * @param randMS         - number of random milliseconds to add from each fire time
     * @param daysEnabled
     */
    void init( String firstTimeInDay, String lastTimeInDay, long fireIntervalMS, long randMS, EnumSet<DayOfWeek> daysEnabled );

    boolean isCancelled();

    /**
     * prime the random generator used to sync random generator with today so backtest and prod are in line
     * default is do nothing
     */
    default void primeRandomGenerator() { }

    void scheduleNext();

    /**
     * override default shared random generator
     * default is do nothing, requires specific implementation logic
     *
     * @param sf
     */
    default void setRandomGenerator( Random sf ) { }

}