/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.tasks;

import com.rr.core.lang.ZString;

import java.util.Calendar;

/**
 * all registered callbacks must finish in good time so as not to affect other timer events
 * <p>
 * implementations must be threadsafe
 */
public interface Scheduler {

    interface Callback {

        /**
         * @param event the registered event
         */
        void event( ScheduledEvent event );

        ZString getName();
    }

    void cancelIndividual( ScheduledEvent event, Callback listener );

    ZTimer getTimer();

    void initDaily( EventTaskHandler<ScheduledEvent> dailyRollFunc );

    void registerForGroupEvent( ScheduledEvent event, Callback listener );

    void registerGroupRepeating( ScheduledEvent event, Calendar fireNext, long repeatPeriodMS );

    /**
     * register a timer event STARTING FROM NOW !!!  + nextFireMS
     *
     * @param event
     * @param listener
     * @param millisFromNow
     * @param repeatPeriodMS
     */
    void registerIndividualRepeating( ScheduledEvent event, Callback listener, long millisFromNow, long repeatPeriodMS );

    /**
     * register a timer event to start at local time specified with a repeat period in MS
     * <p>
     * if the repeat period is >= 1 day then fire adjustments will be made with a local calendar
     *
     * @param event
     * @param listener
     * @param localTime
     * @param repeatPeriodMS
     */
    void registerIndividualRepeating( ScheduledEvent event, Callback listener, ZLocalDateTime localTime, long repeatPeriodMS );
}
