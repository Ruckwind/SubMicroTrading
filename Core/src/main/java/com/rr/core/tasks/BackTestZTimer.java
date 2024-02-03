package com.rr.core.tasks;

import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.time.BackTestClock;
import com.rr.core.utils.SMTRuntimeException;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.ListIterator;
import java.util.function.Predicate;

/**
 * all registered times are unix time
 */
public final class BackTestZTimer implements ZTimer {

    private static final Logger _log = LoggerFactory.create( BackTestZTimer.class );

    private static final ErrorCode TM100 = new ErrorCode( "BTT100", "Exception in backtesttimer thread" );

    /**
     * queue of events in time order OLD to NEW
     */
    private final ArrayList<ZTimerTask> _entries = new ArrayList<>(); // will contribute to GC due to wrapper elems
    private final BackTestClock  _clock;
    private final ReusableString _msg   = new ReusableString();
    private       boolean        _debug = true;

    /**
     * @param clock the backtest clock to use with this timer, should have the currentTime preset to the appropriate backtest time
     */
    public BackTestZTimer( final BackTestClock clock ) {
        _clock = clock;
    }

    /**
     * fire events pending upto the specified time
     * note recurring events can fire multiple times
     *
     * will move the backtest clock forward to fire time
     *
     * @param uptoTimeMS
     * @return events fired
     */
    public long firePending( long uptoTimeMS ) {

        if ( _entries.size() == 0 ) return 0;

        ZTimerTask e = _entries.get(0);

        int fired = 0;

        while ( e.getNextFireTime() <= uptoTimeMS ) {

            _entries.remove( 0 );

            final long fireTime = e.getNextFireTime();

            _clock.setCurrentTimeMillis( fireTime ); // MOVE CLOCK FORWARD BEFORE INVOKE TASK

            if ( !e.isCancelled() ) {
                fireTask( e ); // FIRE TASK AND REINSERT INTO QUEUE OF TASKS IF REPEATING

                ++fired;
            }

            if ( _entries.size() > 0 ) {
                e = _entries.get( 0 );
            } else {
                break; // NOTHING LEFT TO PROCESS
            }
        }

        return  fired;
    }

    public void clear() {
        _entries.clear();
    }

    public void removeTasks( Predicate<String> tester ) {
        ListIterator<ZTimerTask> it = _entries.listIterator();

        while ( it.hasNext() ) {
            final ZTimerTask entry = it.next();

            if ( tester.test( entry.getName() ) ) {
                it.remove();
            }
        }
    }

    /**
     * @return time of next timer to fire, or 0 if none
     */
    public long getNextTimerFireTime() {

        if ( _entries.size()  == 0 ) return Constants.UNSET_LONG;

        return _entries.get( 0 ).getNextFireTime();
    }

    @Override public void schedule( final ZTimerTask task, final long unixTimeMS, final long repeatInterval ) {
        schedule( task, unixTimeMS, repeatInterval, _log );
    }

    @Override public void schedule( final ZTimerTask task, final long unixTimeMS, final long repeatInterval, final int maxRepeats ) {
        schedule( task, unixTimeMS, repeatInterval, maxRepeats, _log );
    }

    @Override public void schedule( final ZTimerTask task, final String firstTimeInDay, final String lastTimeInDay, final long fireIntervalMS, final EnumSet<DayOfWeek> daysEnabled ) {
        schedule( task, firstTimeInDay, lastTimeInDay, fireIntervalMS, daysEnabled, _log );

    }

    @Override public void schedule( final ZTimerTask task, long time, final long repeatInterval, Logger log ) {
        schedule( task, time, repeatInterval, (repeatInterval==0) ? 0 : Integer.MAX_VALUE, log );
    }

    /**
     * schedules a timerTask, if time is in the backtest clock past, its set to the next valid current time
     * @param task
     * @param time the time to fire the task (MS from epoch) ... if this is in the past will fire asap
     * @param repeatInterval 0 for one off firing, otherwise number of ms when to repeat fire event
     */
    @Override public void schedule( final ZTimerTask task, long time, final long repeatInterval, int maxRepeat, Logger log ) {

        if ( task.getTaskState() != ZTimerTask.TaskState.INITIAL ) throw new SMTRuntimeException( "Task has previously been scheduled" );

        task.init( time, repeatInterval, maxRepeat );

        addTaskEntry( task );
    }

    @Override public void schedule( ZTimerTask task, String firstTimeInDay, String lastTimeInDay, long fireIntervalMS, EnumSet<DayOfWeek> daysEnabled, Logger log ) {
        if ( task.getTaskState() != ZTimerTask.TaskState.INITIAL ) throw new SMTRuntimeException( "Task has previously been scheduled" );

        task.init( firstTimeInDay, lastTimeInDay, fireIntervalMS, daysEnabled );

        addTaskEntry( task );
    }

    @Override public void schedule( ZTimerTask task, String firstTimeInDay, String lastTimeInDay, long fireIntervalMS, EnumSet<DayOfWeek> daysEnabled, long randMS, Logger log ) {
        if ( task.getTaskState() != ZTimerTask.TaskState.INITIAL ) throw new SMTRuntimeException( "Task has previously been scheduled" );

        task.init( firstTimeInDay, lastTimeInDay, fireIntervalMS, randMS, daysEnabled );

        addTaskEntry( task );
    }

    @Override public void setDebug( final boolean debug ) {
        _debug = debug;
    }

    public int size() {
        return _entries.size();
    }

    public void remove( final ZTimerTask task ) {
        for( int i=0 ; i < _entries.size() ; i++ ) {
            ZTimerTask e = _entries.get(i);

            if ( e == task ) {
                _entries.remove( i );
            }
        }
    }

    private void fireTask( ZTimerTask task ) {

        if ( ! task.isCancelled() ) {
            if ( _debug ) {
                _msg.copy( "ZTimer firing " ).append( task.getName() );
                _log.info( _msg );
            }

            task.setTaskState( ZTimerTask.TaskState.EXECUTING );

            long fireTime = task.getNextFireTime();

            try {
                task.fire();
            } catch( Exception e ) {
                _log.error( TM100, "Task " + task.getName(), e );
            }

            if ( task.isCancelled() ) return;

            task.setTaskState( ZTimerTask.TaskState.EXECUTED );

            if ( task.getTaskInterval() > 0 ) {
                task.scheduleNext();

                if ( task.isCancelled() ) {
                    if ( _debug ) {
                        _msg.copy( "ZTimer " ).append( task.getName() ).append( " cancelled, repeatsLeft=" ).append( task.getRepeatsLeft() );
                        _log.info( _msg );
                    }
                    return;
                }

                if ( _debug ) {
                    long diffMS = (task.getNextFireTime() - ClockFactory.get().currentTimeMillis());
                    int hours = (int)(diffMS / (60 * 60 * 1000));
                    int mins  = (int)((diffMS - (hours * 60 * 60 *1000)) / (60 * 1000));
                    _msg.copy( "ZTimer " ).append( task.getName() ).append( " reset to " ).append( hours ).append( " hours and " ).append( mins ).append( " mins from now at " );
                    TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( _msg, task.getNextFireTime() );
                    _log.info( _msg );
                }

                addTaskEntry( task ); // insert back
            }
        }
    }

    private void addTaskEntry( final ZTimerTask taskEntry ) {
        if ( _entries.size() == 0 ) {
            _entries.add( taskEntry );
        } else {
            for( int i=0 ; i < _entries.size() ; i++ ) {
                ZTimerTask e = _entries.get( i);

                if ( e.getNextFireTime() > taskEntry.getNextFireTime() ) {
                    _entries.add( i, taskEntry );
                    return;
                }
            }
            _entries.add( taskEntry );
        }
    }

    public BackTestClock getClock() {
        return _clock;
    }

    public ArrayList<ZTimerTask> getEntries() { return _entries; }

    public void log( long uptoTimeMS ) {
        if ( _entries.size() == 0 ) return;

        for ( int idx = 0 ; idx < _entries.size() ; ++idx ) {
            ZTimerTask e = _entries.get( idx );

            if ( e.getNextFireTime() <= uptoTimeMS ) {
                _log.info( "BackTestZTimer " + Thread.currentThread().getName() + " pending task " + e.getName() + " at " + TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( e.getNextFireTime() ) );
            }
        }
    }
}
