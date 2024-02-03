package com.rr.core.tasks;

import com.rr.core.lang.Env;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;

import java.util.Calendar;

public class BackTestScheduler implements Scheduler {

    private static ThreadLocal<Scheduler>      _threadLocalScheduler = ThreadLocal.withInitial( BackTestScheduler::createScheduler );

    private static Scheduler createScheduler() {
        if ( Env.BACKTEST != AppProps.instance().getProperty( CoreProps.RUN_ENV, Env.class ) ) {
            throw new RuntimeException( "BackTestScheduler must be used with RUN_ENV set to BACKTEST" );
        }

        return new SchedulerImpl( BackTestZTimerFactory.get() );
    }

    public static void reset() {
        _threadLocalScheduler.remove();
    }

    @Override public ZTimer getTimer() {
        return _threadLocalScheduler.get().getTimer();
    }

    @Override public void registerGroupRepeating( final ScheduledEvent event, final Calendar fireNext, final long repeatPeriodMS ) {
        _threadLocalScheduler.get().registerGroupRepeating( event, fireNext, repeatPeriodMS );
    }

    @Override public void registerForGroupEvent( final ScheduledEvent event, final Callback listener ) {
        _threadLocalScheduler.get().registerForGroupEvent( event, listener );
    }

    @Override public void registerIndividualRepeating( final ScheduledEvent event, final Callback listener, final long millisFromNow, final long repeatPeriodMS ) {
        _threadLocalScheduler.get().registerIndividualRepeating( event, listener, millisFromNow, repeatPeriodMS );
    }

    @Override public void registerIndividualRepeating( final ScheduledEvent event, final Callback listener, final ZLocalDateTime localTime, final long repeatPeriodMS ) {
        _threadLocalScheduler.get().registerIndividualRepeating( event, listener, localTime, repeatPeriodMS );
    }

    @Override public void cancelIndividual( final ScheduledEvent event, final Callback listener ) {
        _threadLocalScheduler.get().cancelIndividual( event, listener );
    }

    @Override public void initDaily( final EventTaskHandler<ScheduledEvent> dailyRollFunc ) {
        _threadLocalScheduler.get().initDaily( dailyRollFunc );
    }
}
