/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.tasks;

import com.rr.core.lang.*;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * all registered callbacks must finish in good time so as not to affect other timer events
 */
public final class SchedulerImpl implements Scheduler {

    private static final Logger _console = ConsoleFactory.console( SchedulerImpl.class );
    private static final Logger _log     = LoggerFactory.create( SchedulerImpl.class );

    private static final class GroupListenerTask extends BaseTimerTask {

        private static final Logger _glog = LoggerFactory.create( GroupListenerTask.class );

        private static final ErrorCode TASK_FAILED = new ErrorCode( "GLT100", "Failed to run task, name=" );

        private final Set<Callback>  _callbackSet = new LinkedHashSet<>();
        private final ScheduledEvent _event;

        private boolean _scheduled;

        public GroupListenerTask( Callback listener, ScheduledEvent event ) {
            super( event.name() );

            if ( listener != null ) {
                synchronized( _callbackSet ) {
                    _callbackSet.add( listener );
                }
            }
            _event = event;
        }

        public GroupListenerTask( Callback listener, TimeZone tz, ScheduledEvent event ) {
            super( event.name(), tz );

            if ( listener != null ) {
                synchronized( _callbackSet ) {
                    _callbackSet.add( listener );
                }
            }
            _event = event;
        }

        @Override
        public void fire() {
            Collection<Callback> c = null;

            synchronized( _callbackSet ) {
                c = new ArrayList<>( _callbackSet );
            }

            for ( Callback listener : c ) {
                try {
                    listener.event( _event );
                } catch( Throwable e ) {
                    _glog.error( TASK_FAILED, listener.getName() + " " + e.getClass().getName() + " : " + e.getMessage(), e );
                }
            }
        }

        public void addListener( Callback listener ) {
            synchronized( _callbackSet ) {
                _callbackSet.add( listener );
            }
        }

        public boolean isScheduled() {
            return _scheduled;
        }

        public void setScheduled( boolean scheduled ) {
            _scheduled = scheduled;
        }
    }

    private static final class SingleCallbackTask extends BaseTimerTask {

        private static final Logger _sclog = LoggerFactory.create( SingleCallbackTask.class );

        private static final ErrorCode TASK_FAILED = new ErrorCode( "SLT100", "Failed to run task, name=" );

        private final Callback       _listener;
        private final ScheduledEvent _event;

        public SingleCallbackTask( Callback listener, ScheduledEvent event ) {
            super( event.name() + ":" + listener.getName() );

            _listener = listener;
            _event    = event;
        }

        public SingleCallbackTask( Callback listener, TimeZone tz, ScheduledEvent event ) {
            super( event.name() + ":" + listener.getName(), tz );

            _listener = listener;
            _event    = event;
        }

        @Override
        public void fire() {
            try {
                _listener.event( _event );
            } catch( Exception e ) {
                _sclog.error( TASK_FAILED, _listener.getName(), e );
            }
        }
    }

    private Map<ScheduledEvent, Map<Callback, SingleCallbackTask>> _eventNotifierMap      = new HashMap<>();
    private Map<ScheduledEvent, GroupListenerTask>                 _eventNotifierGroupMap = new HashMap<>();

    private ZTimer _timer;

    SchedulerImpl() {
        _timer = ZTimerFactory.get();
    }

    public SchedulerImpl( final ZTimer timer ) {
        _timer = timer;
    }

    @Override public synchronized void cancelIndividual( ScheduledEvent event, Callback listener ) {
        Map<Callback, SingleCallbackTask> eventMap = _eventNotifierMap.get( event );

        if ( eventMap == null ) {
            return;
        }

        SingleCallbackTask task = eventMap.get( listener );

        if ( task != null && !task.isCancelled() ) {
            _log.info( "Scheduler.cancelIndividual event " + event + " for listener " + listener.getName() );

            task.cancel();
        }
    }

    @Override public ZTimer getTimer() {
        return _timer;
    }

    @Override public void initDaily( final EventTaskHandler<ScheduledEvent> dailyRollFunc ) {
        TimeUtils local             = TimeUtilsFactory.createTimeUtils();
        Calendar  localNextMidnight = TimeUtilsFactory.safeTimeUtils().getCalendar( local.getLocalTimeZone() );
        localNextMidnight.set( Calendar.HOUR_OF_DAY, 0 );
        localNextMidnight.set( Calendar.MINUTE, 0 );
        localNextMidnight.set( Calendar.SECOND, 0 );
        localNextMidnight.set( Calendar.MILLISECOND, 500 );
        localNextMidnight.add( Calendar.DAY_OF_MONTH, 1 );

        // @note when timestamps are not stored as millis from start of UTC today then can remove this event
        TimeZone utc             = TimeZone.getTimeZone( "UTC" );
        Calendar utcNextMidnight = TimeUtilsFactory.safeTimeUtils().getCalendar( utc );
        utcNextMidnight.set( Calendar.HOUR_OF_DAY, 0 );
        utcNextMidnight.set( Calendar.MINUTE, 0 );
        utcNextMidnight.set( Calendar.SECOND, 0 );
        utcNextMidnight.set( Calendar.MILLISECOND, 1 );
        utcNextMidnight.add( Calendar.DAY_OF_MONTH, 1 );

        SchedulerFactory.get().registerGroupRepeating( CoreScheduledEvent.UTCDateRoll, utcNextMidnight, Constants.MS_IN_DAY );
        SchedulerFactory.get().registerGroupRepeating( CoreScheduledEvent.EndOfDay, localNextMidnight, Constants.MS_IN_DAY );

        if ( dailyRollFunc != null ) {
            SchedulerFactory.get().registerForGroupEvent( CoreScheduledEvent.EndOfDay, new BasicSchedulerCallback( "DailyRoll", dailyRollFunc ) );
        }
    }

    @Override public synchronized void registerForGroupEvent( ScheduledEvent event, Callback listener ) {

        GroupListenerTask task = _eventNotifierGroupMap.get( event );

        if ( task == null ) {
            task = new GroupListenerTask( listener, event );

            _eventNotifierGroupMap.put( event, task );
        } else {
            task.addListener( listener );
        }

        // use console as this function is used in startup and can logging here before TimeUtils setup is problem
        _console.info( "Scheduler.registerForGroupEvent " + event + ", listener " + listener.getName() + ", isEventScheduled=" + task.isScheduled() );
    }

    @Override public synchronized void registerGroupRepeating( ScheduledEvent event, Calendar fireNext, long repeatPeriodMS ) {
        GroupListenerTask task = _eventNotifierGroupMap.get( event );

        DateFormat dfSYS    = new SimpleDateFormat( "HH:mm:ss (z)" );
        TimeZone   timeZone = fireNext.getTimeZone();
        dfSYS.setTimeZone( timeZone );

        if ( task == null ) {
            task = new GroupListenerTask( null, timeZone, event );

            _eventNotifierGroupMap.put( event, task );
        } else {
            _console.info( "Event was registered with scheduler, setting new time " + event );
        }

        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss (z)" );
        df.setCalendar( fireNext );
        df.setTimeZone( timeZone );

        DateFormat dfUTC = new SimpleDateFormat( "HH:mm:ss (z)" );
        dfUTC.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

        _console.info( "Scheduler.registerGroupRepeating " + event +
                       ", nextFire=" + df.format( fireNext.getTime() ) +
                       ", nextFireUTC=" + dfUTC.format( fireNext.getTime() ) +
                       ", nextFireSYS=" + dfSYS.format( fireNext.getTime() ) +
                       ", repeatPeriodMS=" + repeatPeriodMS );

        if ( task.isScheduled() ) {
            task.cancel();
        } else {
            _log.info( "registerGroupRepeating override state for " + event.name() + " from " + task.getTaskState().name() + " to INITIAL" );

            task.setTaskState( ZTimerTask.TaskState.INITIAL );
        }

        _timer.schedule( task, fireNext.getTime().getTime(), repeatPeriodMS );
    }

    @Override public synchronized void registerIndividualRepeating( ScheduledEvent event, Callback listener, long millisFromNow, long repeatPeriodMS ) {
        Map<Callback, SingleCallbackTask> eventMap = _eventNotifierMap.computeIfAbsent( event, k -> new LinkedHashMap<>() );

        SingleCallbackTask task = eventMap.get( listener );

        if ( task != null ) {
            _log.info( "Scheduler.registerIndividualRepeating cancel previous event " + event + " for listener " + listener.getName() );

            task.cancel();
        }

        _log.info( "Scheduler.registerIndividualRepeating " + event + ", listener " + listener.getName() +
                   ", nextFireMS=" + millisFromNow + ", repeatPeriodMS=" + repeatPeriodMS );

        task = new SingleCallbackTask( listener, event );

        eventMap.put( listener, task );

        _timer.schedule( task, ClockFactory.get().currentTimeMillis() + millisFromNow, repeatPeriodMS );
    }

    @Override public synchronized void registerIndividualRepeating( ScheduledEvent event, Callback listener, ZLocalDateTime localTime, long repeatPeriodMS ) {
        Map<Callback, SingleCallbackTask> eventMap = _eventNotifierMap.computeIfAbsent( event, k -> new LinkedHashMap<>() );

        SingleCallbackTask task = eventMap.get( listener );

        if ( task != null ) {
            _log.info( "Scheduler.registerIndividualRepeating cancel previous event " + event + " for listener " + listener.getName() );

            task.cancel();
        }

        TimeUtils tu = TimeUtilsFactory.createTimeUtils();

        long nextFireTimeMS = tu.localTimeStampToUnixTime( localTime );

        _log.info( "Scheduler.registerIndividualRepeating " + event + ", listener " + listener.getName() +
                   ", tz=" + localTime.getTz().getID() +
                   ", at " + localTime.getLocalTimeStamp() +
                   ", nextFireMS=" + nextFireTimeMS + ", repeatPeriodMS=" + repeatPeriodMS );

        task = new SingleCallbackTask( listener, localTime.getTz(), event );

        eventMap.put( listener, task );

        _timer.schedule( task, nextFireTimeMS, repeatPeriodMS );
    }
}
