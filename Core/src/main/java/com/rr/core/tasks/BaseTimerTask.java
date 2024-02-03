/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.tasks;

import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.model.MsgFlag;
import com.rr.core.utils.SMTRuntimeException;

import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Random;
import java.util.TimeZone;

public abstract class BaseTimerTask implements ZTimerTask {

    private static final Logger _log = LoggerFactory.create( BaseTimerTask.class );

    private static final String ANONYMOUS = "ANON";

    private static final long TIMER_SEED = 5139872107L;

    private final String   _name;
    private final TimeZone _timeZone;

    private TaskState _taskState = TaskState.INITIAL;

    private long _nextFireTime;
    private long _lastRandomOffsetMS;
    private long _taskInterval = 0;
    private int  _repeatsLeft  = Integer.MAX_VALUE;
    private long _randMS       = 0;

    private          Calendar           _startDayCal;
    private          Calendar           _endDayCal;
    private          long               _unixEndOfTodayMS;
    private          EnumSet<DayOfWeek> _daysEnabled;
    private volatile long               _reqOffsetMS = 0;

    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private transient          Random       _random         = new Random();

    public BaseTimerTask() {
        this( ANONYMOUS );
    }

    public BaseTimerTask( String name ) {
        this( name, TimeZone.getDefault() );
    }

    public BaseTimerTask( String name, TimeZone tz ) {
        _name     = name;
        _timeZone = tz;
    }

    @Override public synchronized void cancel() {
        _taskState = TaskState.CANCELLED;
    }

    /**
     * fire the task within the timers thread of control
     */
    @Override public abstract void fire();

    @Override public String getName() {
        return _name;
    }

    @Override public final long getNextFireTime() {
        return _nextFireTime;
    }

    @Override public final void setNextFireTime( long nextFireTime ) { _nextFireTime = nextFireTime; }

    @Override public int getRepeatsLeft()                            { return _repeatsLeft; }

    @Override public void setRepeatsLeft( final int repeatsLeft )    { _repeatsLeft = repeatsLeft; }

    @Override public long getReqOffsetMS()                           { return _reqOffsetMS; }

    @Override public void setReqOffsetMS( final long reqOffsetMS )   { _reqOffsetMS = reqOffsetMS; }

    @Override public final long getTaskInterval() {
        return _taskInterval;
    }

    @Override public final void setTaskInterval( long taskInterval ) { _taskInterval = taskInterval; }

    @Override public final TaskState getTaskState() {
        return _taskState;
    }

    @Override public final void setTaskState( TaskState taskState ) {
        _taskState = taskState;
    }

    @Override public TimeZone getTimeZone() { return _timeZone; }

    // initialiser used by the ZTimer
    @Override public void init( long nextFireTime, long repeatInterval, int maxRepeat ) {
        setTaskState( TaskState.SCHEDULED );
        setTaskInterval( repeatInterval );
        setRepeatsLeft( maxRepeat );
        setNextFireTime( nextFireTime );

        long now = ClockFactory.get().currentTimeMillis();

        if ( _nextFireTime < now ) {
            if ( repeatInterval == 0 ) {
                String past   = TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( _timeZone, _nextFireTime );
                String nowStr = TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( _timeZone, now );

                setNextFireTime( now );

                _log.info( getName() + " initial fireTime was in past [" + past + "], now scheduled for now which is [" + nowStr + "]" );

            } else {
                String past = TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( _timeZone, _nextFireTime );

                while( _nextFireTime < now && getTaskState() != TaskState.NO_MORE_RETRIES ) {
                    scheduleNext();
                }

                String future = TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( _timeZone, _nextFireTime );
                String nowStr = TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( _timeZone, now );

                _log.info( getName() + " initial fireTime was in past [" + past + "], now scheduled for [" + future + "], now is [" + nowStr + "]" );
            }
        } else {
            String future = TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( _timeZone, _nextFireTime );
            String nowStr = TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( _timeZone, now );

            _log.info( getName() + " fireTime scheduled for [" + future + "], now is [" + nowStr + "]" );
        }
    }

    @Override public void init( String firstTimeInDay, String lastTimeInDay, long fireIntervalMS, EnumSet<DayOfWeek> daysEnabled ) {
        init( firstTimeInDay, lastTimeInDay, fireIntervalMS, 0, daysEnabled );
    }

    /**
     * @param firstTimeInDay start time in HH:mm:ss format
     * @param lastTimeInDay  latest fire time of day in HH:mm:ss format, after this next timer fire will be next day
     * @param fireIntervalMS interval between firing
     * @param randMS         random POSITIVE element to be ADDED to fire time (max of fireInterval/3)
     * @param daysEnabled    valid days to fire on, if empty all days enabled
     */
    @Override public void init( String firstTimeInDay, String lastTimeInDay, long fireIntervalMS, long randMS, EnumSet<DayOfWeek> daysEnabled ) {

        if ( randMS > 0 && fireIntervalMS > 0 ) {
            double maxRand = fireIntervalMS * 0.67;
            if ( randMS > maxRand ) {
                throw new SMTRuntimeException( "Unable to set timer " + _name + " as randMS " + randMS + " too close to fireInterVal " + fireIntervalMS + ", max=" + maxRand );
            }
        }

        _daysEnabled = daysEnabled;
        _startDayCal = getCalendar( firstTimeInDay );
        _endDayCal   = getCalendar( lastTimeInDay );
        _randMS      = randMS;

        _lastRandomOffsetMS = getRandomOffset();
        _nextFireTime       = _startDayCal.getTimeInMillis();
        _unixEndOfTodayMS   = _endDayCal.getTimeInMillis();

        setTaskState( TaskState.SCHEDULED );
        setTaskInterval( fireIntervalMS );
        setRepeatsLeft( Integer.MAX_VALUE );

        if ( _unixEndOfTodayMS < _nextFireTime ) {
            nextValidDay( _endDayCal, 1 );
            _unixEndOfTodayMS = _endDayCal.getTimeInMillis();
        }

        long now = ClockFactory.get().currentTimeMillis();

        _nextFireTime += _lastRandomOffsetMS;

        long fireFromNowMins = (_nextFireTime - now) / (60 * 1000);

        if ( _nextFireTime < now ) {
            String past = TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( _timeZone, _nextFireTime );

            while( _nextFireTime < now && getTaskState() != TaskState.NO_MORE_RETRIES ) {
                scheduleNext();
            }

            String future = TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( _timeZone, _nextFireTime );
            String nowStr = TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( _timeZone, now );

            _log.info( getName() + " initial fireTime was in past [" + past + "], now scheduled for [" + future + "], now is [" + nowStr + "]" );
        } else {

            String future = TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( _timeZone, _nextFireTime );
            String nowStr = TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( _timeZone, now );

            _log.info( getName() + " initial fireTime scheduled for [" + future + "], now is [" + nowStr + "], maxRandomSecs=" + (_randMS / 1000) + ", randomSecs=" + (_lastRandomOffsetMS / 1000) );
        }
    }

    @Override public boolean isCancelled() {
        return _taskState == TaskState.CANCELLED || _taskState == TaskState.NO_MORE_RETRIES;
    }

    @Override public void primeRandomGenerator() { /* nothing */ }

    @Override public final void scheduleNext() {

        _nextFireTime = _nextFireTime - _lastRandomOffsetMS; // normalise the last fire time

        if ( _repeatsLeft > 0 ) {
            --_repeatsLeft;
        } else {
            setTaskState( TaskState.NO_MORE_RETRIES );
            return;
        }

        if ( _unixEndOfTodayMS > 0 ) {
            scheduleNextWithIntraDayBoundary();
            return;
        }

        int days = (int) (_taskInterval / Constants.MS_IN_DAY);

        if ( days >= 1 ) {
            Calendar c = Calendar.getInstance( _timeZone );
            c.setTimeInMillis( _nextFireTime );

            boolean wasDST = _timeZone.inDaylightTime( c.getTime() );

            int msInDay     = (int) (_taskInterval - (days * Constants.MS_IN_DAY));
            int hours       = msInDay / MS_IN_HOUR;
            int remainingMS = msInDay - (hours * MS_IN_HOUR);

            c.add( Calendar.DAY_OF_MONTH, days );
            c.add( Calendar.HOUR, hours );
            c.add( Calendar.MILLISECOND, remainingMS );

            boolean nowDST = _timeZone.inDaylightTime( c.getTime() );

            if ( !wasDST && nowDST ) {
                _log.info( getName() + " was not in DST now is" );
            } else if ( wasDST && !nowDST ) {
                _log.info( getName() + " was in DST now not" );
            }

            _nextFireTime = c.getTimeInMillis();
        } else {
            _nextFireTime = _nextFireTime + _taskInterval;
        }

        _lastRandomOffsetMS = getRandomOffset();
        _nextFireTime += _lastRandomOffsetMS;
    }

    @Override public void setRandomGenerator( final Random r )              { _random = r; }

    @Override public final void attachQueue( Event nxt ) {
        _nextMessage = nxt;
    }

    @Override public final void detachQueue() {
        _nextMessage = null;
    }

    @Override public void dump( final ReusableString out ) { out.append( "BaseTimerTask " ).append( _name ); }

    @Override public final EventHandler getEventHandler() {
        return _messageHandler;
    }

    @Override public final void setEventHandler( EventHandler handler ) {
        _messageHandler = handler;
    }

    @Override public long getEventTimestamp()                               { return _nextFireTime; }

    @Override public void setEventTimestamp( final long internalTime )      { /* nothing */ }

    @Override public int getFlags()                        { return 0; }

    @Override public int getMsgSeqNum()                    { return 0; }

    @Override public void setMsgSeqNum( final int seqNum ) { /* nothing */ }

    @Override public final Event getNextQueueEntry() {
        return _nextMessage;
    }

    @Override public ReusableType getReusableType()                         { return CoreReusableType.NotReusable; }

    @Override public boolean isFlagSet( final MsgFlag flag ) { return false; }

    @Override public void setFlag( final MsgFlag flag, final boolean isOn ) { /* nothing */ }

    private int TP( String timeStr, int startIdx, int maxVal ) {
        int c1 = timeStr.charAt( startIdx ) - '0';
        int c2 = timeStr.charAt( startIdx + 1 ) - '0';

        int t = c1 * 10 + c2;

        if ( t > maxVal ) throw new SMTRuntimeException( "Invalid time component in timer task " + getName() + " [" + timeStr + "] at idx " + startIdx );

        return t;
    }

    private Calendar getCalendar( final String timeStr ) {

        if ( timeStr == null || timeStr.length() != 5 && timeStr.length() != 8 && timeStr.length() != 12 ) {
            throw new SMTRuntimeException( "Inavlid task time of [" + timeStr + "] but its not in  hh:mm:ss.SSS  format" );
        }

        int hour = TP( timeStr, 0, 24 );
        int min  = TP( timeStr, 3, 60 );
        int sec  = 0;

        if ( timeStr.length() > 5 ) {
            sec = TP( timeStr, 6, 60 );
        }

        int ms = 0;

        if ( timeStr.length() == 12 ) {
            int c1 = timeStr.charAt( 9 ) - '0';
            int c2 = timeStr.charAt( 10 ) - '0';
            int c3 = timeStr.charAt( 11 ) - '0';

            ms = c1 * 100 + c2 * 10 + c3;
        }

        Calendar c = TimeUtilsFactory.safeTimeUtils().getCalendar( _timeZone );

        c.set( Calendar.HOUR_OF_DAY, hour );
        c.set( Calendar.MINUTE, min );
        c.set( Calendar.SECOND, sec );
        c.set( Calendar.MILLISECOND, ms );

        return nextValidDay( c, 0 );
    }

    private Random getRandom()     { return _random; }

    private long getRandomOffset() { return (_randMS > 0) ? Math.abs( getRandom().nextLong() ) % _randMS : 0; }

    private boolean isDayEnabled( final int dayOfWeek ) {
        switch( dayOfWeek ) {
        case Calendar.SUNDAY:
            return _daysEnabled.contains( DayOfWeek.SUNDAY );
        case Calendar.MONDAY:
            return _daysEnabled.contains( DayOfWeek.MONDAY );
        case Calendar.TUESDAY:
            return _daysEnabled.contains( DayOfWeek.TUESDAY );
        case Calendar.WEDNESDAY:
            return _daysEnabled.contains( DayOfWeek.WEDNESDAY );
        case Calendar.THURSDAY:
            return _daysEnabled.contains( DayOfWeek.THURSDAY );
        case Calendar.FRIDAY:
            return _daysEnabled.contains( DayOfWeek.FRIDAY );
        case Calendar.SATURDAY:
            return _daysEnabled.contains( DayOfWeek.SATURDAY );
        default:
            return false;
        }
    }

    private Calendar nextValidDay( final Calendar c, int initialDaysOffset ) {
        if ( initialDaysOffset > 0 ) {
            c.add( Calendar.DAY_OF_YEAR, initialDaysOffset );
        }

        if ( _daysEnabled.size() > 0 ) {
            while( !isDayEnabled( c.get( Calendar.DAY_OF_WEEK ) ) ) {
                c.add( Calendar.DAY_OF_YEAR, 1 );
            }
        }

        return c;
    }

    private void scheduleNextWithIntraDayBoundary() {

        _nextFireTime += _taskInterval + _reqOffsetMS;

        _lastRandomOffsetMS = getRandomOffset();
        _nextFireTime += _lastRandomOffsetMS;

        if ( _nextFireTime > _unixEndOfTodayMS ) {
            boolean wasDST = _timeZone.inDaylightTime( _endDayCal.getTime() );

            nextValidDay( _startDayCal, 1 );
            _nextFireTime = _startDayCal.getTimeInMillis();
            nextValidDay( _endDayCal, 1 );
            _unixEndOfTodayMS = _endDayCal.getTimeInMillis();

            boolean nowDST = _timeZone.inDaylightTime( _endDayCal.getTime() );

            if ( !wasDST && nowDST ) {
                _log.info( getName() + " was not in DST now is" );
            } else if ( wasDST && !nowDST ) {
                _log.info( getName() + " was in DST now not" );
            }

            _nextFireTime += _lastRandomOffsetMS;
        }
    }

}
