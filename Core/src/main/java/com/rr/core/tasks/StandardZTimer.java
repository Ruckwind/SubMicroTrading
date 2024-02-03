/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.tasks;

import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.tasks.ZTimerTask.TaskState;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * simple Timer specialisation to be able to control the thread affinity / priority of the timer thread
 * on standard linux the timer will have the O/S scheduler margin of error ... around 10ms.
 */
public final class StandardZTimer implements ZTimer {

    static final Logger _log = LoggerFactory.create( StandardZTimer.class );

    private static class TimerThread extends Thread {

        private static final ErrorCode TM100 = new ErrorCode( "ZTM100", "Exception in timer thread" );

        private static final long DELAY_ON_EMPTY_QUEUE_MS = 1000;

        private final TaskQueue      _queue;
        private final ThreadPriority _priority;
        private final ReusableString _msg = new ReusableString();

        boolean _running     = true;
        long    _nextDelayMS = 0;

        private boolean _debug = false;

        TimerThread( TaskQueue queue, ThreadPriority priority ) {
            _queue    = queue;
            _priority = priority;
        }

        @Override public void run() {
            ThreadUtilsFactory.get().setPriority( this, _priority );
            ZTimerTask task;

            while( _running ) {
                task = waitForNextEvent();

                if ( _running && task != null ) {     // still running and non cancelled task
                    if ( _nextDelayMS > 0 ) continue; // next task not ready to fire

                    if ( _debug ) dumpQueue();

                    fireTask( task );

                    postFire( task );
                }
            }
        }

        public void setDebug( boolean debug ) {
            _debug = debug;
        }

        private void dumpQueue() {
            _msg.copy( "ZTimer dump before fire\n" );
            _queue.dumpTable( _msg );
            _log.info( _msg );
        }

        private void fireTask( ZTimerTask task ) {
            if ( !task.isCancelled() ) {
                if ( _debug ) {
                    _msg.copy( "ZTimer firing " ).append( task.getName() );
                    _log.info( _msg );
                }

                try {
                    task.fire();
                } catch( Exception e ) {
                    _log.error( TM100, "Task " + task.getName(), e );
                }
            }
        }

        private ZTimerTask getNextNonCancelledTask() {
            ZTimerTask task = _queue.nextToFire(); // must be a task to run to get here

            while( task != null ) {

                synchronized( task ) {
                    if ( task.getTaskState() == TaskState.CANCELLED ) {
                        _queue.pop();
                    } else {
                        return task;
                    }
                }

                task = _queue.nextToFire(); // must be a task to run to get here
            }

            return null;
        }

        private long nextSleepPeriod( ZTimerTask task ) {
            if ( task == null ) return 0; // sleep until item added

            long delayMS;
            long currentTime;
            long executionTime;

            synchronized( task ) {
                currentTime   = ClockFactory.get().currentTimeMillis();
                executionTime = task.getNextFireTime();

                delayMS = executionTime - currentTime;
            }

            if ( delayMS == 0 ) delayMS = -1; // ready to run now

            return delayMS;
        }

        private void postFire( ZTimerTask task ) {
            synchronized( _queue ) {
                synchronized( task ) {
                    if ( task.isCancelled() ) return;

                    task.setTaskState( TaskState.EXECUTED );
                    if ( task.getTaskInterval() > 0 ) {
                        task.scheduleNext();

                        if ( task.isCancelled() ) {
                            if ( _debug ) {
                                _msg.copy( "ZTimer " ).append( task.getName() ).append( " cancelled / maxRepeatsHit" );
                                _log.info( _msg );
                            }
                        } else {

                            if ( _debug ) {
                                _msg.copy( "ZTimer " ).append( task.getName() ).append( " reset to " ).append( task.getNextFireTime() - ClockFactory.get().currentTimeMillis() ).append( "ms from now" );
                                _log.info( _msg );
                            }

                            _queue.add( task );
                        }
                    }
                }

                task         = getNextNonCancelledTask();
                _nextDelayMS = nextSleepPeriod( task );
            }
        }

        private long prepTask( ZTimerTask task ) {
            if ( task == null ) return 0;

            long delayMS;
            long currentTime;
            long executionTime;

            synchronized( task ) {
                currentTime   = ClockFactory.get().currentTimeMillis();
                executionTime = task.getNextFireTime();

                delayMS = executionTime - currentTime;

                if ( delayMS <= 0 ) { // ready to fire
                    _queue.pop();

                    if ( !task.isCancelled() ) task.setTaskState( TaskState.EXECUTING );
                }
            }

            return delayMS;
        }

        private ZTimerTask waitForNextEvent() {
            ZTimerTask task = null;

            while( task == null ) {
                synchronized( _queue ) {
                    long delay = _nextDelayMS;

                    if ( _queue.nextToFire() == null && delay == 0 ) {
                        delay = DELAY_ON_EMPTY_QUEUE_MS;
                    }

                    if ( delay > 0 ) {
                        try {
                            if ( _debug ) {
                                _log.info( "ZTimer sleeping for " + delay + "ms" );
                            }

                            _queue.wait( delay );
                        } catch( InterruptedException e ) {
                            // dont care
                        }
                    }

                    task = getNextNonCancelledTask();

                    if ( task != null ) {
                        _nextDelayMS = prepTask( task ); // pops from queue and sets to executing IF ready to fire, returns 0 if no tasks left
                    } else {
                        _nextDelayMS = DELAY_ON_EMPTY_QUEUE_MS;
                    }
                }
            }

            return task;
        }
    }

    private static class TaskQueue { // elements always in order of next scheduled time.

        private ArrayList<ZTimerTask> _queue = new ArrayList<>();

        public TaskQueue() { /* nothing */ }

        public void dumpTable( ReusableString msg ) {
            long now = ClockFactory.get().currentTimeMillis();

            for ( int i = 0; i < _queue.size(); i++ ) {
                msg.append( "i=" ).append( i ).append( " : " ).append( _queue.get( i ).getName() ).append( ", nextFireInMS=" ).append( _queue.get( i ).getNextFireTime() - now ).append( ", state " ).append( _queue.get( i ).getTaskState() )
                   .append( "\n" );
            }
        }

        void add( ZTimerTask task ) {
            long time  = task.getNextFireTime();
            int  qSize = _queue.size();
            int  i     = 0;

            for ( ; i < qSize; i++ ) {
                final ZTimerTask t = _queue.get( i );
                if ( time < t.getNextFireTime() ) {
                    break;
                }
            }

            _queue.add( i, task );
        }

        ZTimerTask nextToFire() {
            if ( _queue.size() == 0 ) return null;

            return _queue.get( 0 );
        }

        void pop() {
            _queue.remove( 0 );
        }
    }

    private final TaskQueue   _tasksQueue = new TaskQueue();
    private final TimerThread _workerThread;

    public StandardZTimer( String name, ThreadPriority priority ) {
        _workerThread = new TimerThread( _tasksQueue, priority );

        _workerThread.setName( name );
        _workerThread.setDaemon( true );
        _workerThread.start();
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

    /**
     * schedule task to run
     *
     * @param task
     * @param time           the time to fire the task (MS from epoch) ... if this is in the past will fire asap
     * @param repeatInterval 0 for 1 off firing, otherwise number of ms when to repeat fire event
     */
    @Override public void schedule( ZTimerTask task, long time, long repeatInterval, Logger log ) {
        schedule( task, time, repeatInterval, (repeatInterval == 0) ? 0 : Integer.MAX_VALUE, log );
    }

    /**
     * schedule task to run
     *
     * @param task
     * @param time           the time to fire the task (MS from epoch) ... if this is in the past will fire asap
     * @param repeatInterval 0 for 1 off firing, otherwise number of ms when to repeat fire event
     */
    @Override public void schedule( ZTimerTask task, long time, long repeatInterval, int maxRetries, Logger log ) {
        if ( time <= 0 ) throw new SMTRuntimeException( "Bad time" );
        if ( repeatInterval < 0 ) throw new SMTRuntimeException( "Bad interval" );

        synchronized( _tasksQueue ) {
            if ( !_workerThread._running ) throw new SMTRuntimeException( "ZTimer closed" );

            synchronized( task ) {
                if ( task.getTaskState() != TaskState.INITIAL ) throw new SMTRuntimeException( "Task has previously been scheduled" );

                task.init( time, repeatInterval, maxRetries );
            }

            logTask( task, log );

            _tasksQueue.add( task );
            _tasksQueue.notify();       // wake up background thread as its no doubt asleep
        }
    }

    @Override public void schedule( ZTimerTask task, String firstTimeInDay, String lastTimeInDay, long fireIntervalMS, EnumSet<DayOfWeek> daysEnabled, Logger log ) {

        if ( fireIntervalMS < 0 ) throw new SMTRuntimeException( "Bad interval " + fireIntervalMS );

        synchronized( _tasksQueue ) {
            if ( !_workerThread._running ) throw new SMTRuntimeException( "ZTimer closed" );

            synchronized( task ) {
                if ( task.getTaskState() != TaskState.INITIAL ) throw new SMTRuntimeException( "Task has previously been scheduled" );

                task.init( firstTimeInDay, lastTimeInDay, fireIntervalMS, daysEnabled );
            }

            logTask( task, log );

            _tasksQueue.add( task );
            _tasksQueue.notify();       // wake up background thread as its no doubt asleep
        }
    }

    @Override public void schedule( ZTimerTask task, String firstTimeInDay, String lastTimeInDay, long fireIntervalMS, EnumSet<DayOfWeek> daysEnabled, long randMS, Logger log ) {

        if ( fireIntervalMS < 0 ) throw new SMTRuntimeException( "Bad interval " + fireIntervalMS );

        synchronized( _tasksQueue ) {
            if ( !_workerThread._running ) throw new SMTRuntimeException( "ZTimer closed" );

            synchronized( task ) {
                if ( task.getTaskState() != TaskState.INITIAL ) throw new SMTRuntimeException( "Task has previously been scheduled" );

                task.init( firstTimeInDay, lastTimeInDay, fireIntervalMS, randMS, daysEnabled );
            }

            logTask( task, log );

            _tasksQueue.add( task );
            _tasksQueue.notify();       // wake up background thread as its no doubt asleep
        }
    }

    @Override public void setDebug( boolean debug ) {
        _workerThread.setDebug( debug );
    }

    private void logTask( final ZTimerTask task, Logger log ) {

        long now    = ClockFactory.get().currentTimeMillis();
        long diffMS = (task.getNextFireTime() - now);
        long days   = diffMS / Constants.MS_IN_DAY;

        diffMS = diffMS - (days * Constants.MS_IN_DAY);

        long hours = diffMS / Constants.MS_IN_HOUR;

        diffMS = diffMS - (hours * Constants.MS_IN_HOUR);

        long mins = diffMS / Constants.MS_IN_MINUTE;

        diffMS = diffMS - (mins * Constants.MS_IN_MINUTE);

        long secs = diffMS / 1000;

        ReusableString msg = TLC.instance().pop();
        msg.copy( "ZTimer Adding task " ).append( task.getName() )
           .append( " for " ).append( days )
           .append( " days, " ).append( hours )
           .append( " hours and " ).append( mins )
           .append( " mins and " ).append( secs )
           .append( " from now at " );
        ;

        TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( msg, task.getTimeZone(), task.getNextFireTime(), true );

        log.info( msg );
        TLC.instance().pushback( msg );
    }

}
