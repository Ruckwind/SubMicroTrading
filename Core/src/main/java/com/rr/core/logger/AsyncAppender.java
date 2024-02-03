/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.logger;

import com.rr.core.collections.ArrayBlockingEventQueue;
import com.rr.core.collections.BlockingSyncQueue;
import com.rr.core.collections.EventQueue;
import com.rr.core.lang.*;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.ShutdownManager;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;

import java.nio.ByteBuffer;

import static com.rr.core.logger.Logger.DEFAULT_FLUSH_INT_MS;

public final class AsyncAppender implements Appender {

    private static final long      FORCE_FLUSH_DELAY_MS      = 6000;
    private static final int       PAUSE_CHECK_INTERVAL      = 1000;
    private static final long      ASYNC_BACK_MAX_WAIT       = 500;
    private static final int       DEFAULT_DELAY_ON_EMPTY_MS = 10;
    private static final ErrorCode ERR_DELEGATE              = new ErrorCode( "AAR100", "Error in log event delegate handler" );

    static class PausedLock implements ZLock {
        // use specific class so can spot contention in profiler
    }
    private final transient com.rr.core.logger.Logger _console = ConsoleFactory.console( AsyncAppender.class, Level.info );
    private final transient Thread                    _thread;
    private final transient String                    _id;
    private final transient    PausedLock _pauseLock = new PausedLock();
    private final transient    EventQueue _queue;
    private transient Appender _delegate;
    private volatile transient boolean    _paused    = false;
    private volatile transient boolean _systemStopping = false;
    private transient          long    _eventCount     = 0;
    private transient          int     _minFlushIntervalMS;
    private transient          int     _sleepOnEmpty   = DEFAULT_DELAY_ON_EMPTY_MS;
    private transient          boolean _logDepth       = false;
    private transient boolean    _syncMode = false;
    private transient ByteBuffer _errBuf   = ByteBuffer.allocate( 2048 );

    public AsyncAppender( Appender delegate, String id ) {
        this( delegate, id, false, 131072, DEFAULT_FLUSH_INT_MS );
    }

    public AsyncAppender( Appender delegate, String id, boolean registerShutdownHook, int maxQ, int minFlushIntervalMS ) {

        _id = id;

        _delegate = delegate;

        _minFlushIntervalMS = minFlushIntervalMS;

        boolean useFixedQueue = AppProps.instance().getBooleanProperty( CoreProps.LOG_Q_BOUNDED, false, true );

        boolean forceOneLogFile = AppProps.instance().getBooleanProperty( CoreProps.FORCE_SINGLE_LOG, false, false );

        if ( !forceOneLogFile ) {
            int sleepOnEmptyQ = AppProps.instance().getIntProperty( CoreProps.SLEEP_ON_EMPTY_LOGQ, false, 200 );

            _sleepOnEmpty = sleepOnEmptyQ;
        }

        _queue = (useFixedQueue) ? new ArrayBlockingEventQueue( "AsyncLogQ", maxQ, false ) : new BlockingSyncQueue( "AsyncLogQ" );

        if ( minFlushIntervalMS >= 0 ) {
            if ( registerShutdownHook ) {
                ShutdownManager.instance().registerLogger( () -> {
                    ThreadUtilsFactory.getLive().sleep( 250 ); // give other threads chance to logger shutdown
                    setSystemStopping( true );
                    close();
                } );
            }

            _thread = new Thread( this::dispatchLoop, id );

            _thread.setDaemon( false );

            _thread.start();
        } else {
            _thread   = null;
            _syncMode = true;

            synchronized( this ) {
                _delegate.open();
            }
        }
    }

    @Override public void chain( final Appender dest ) {
        throw new SMTRuntimeException( "AsyncAppender delegate must be passed in constructor so its final" );
    }

    @Override public synchronized void close() {
        if ( _syncMode ) {

            synchronized( this ) {
                _delegate.flush();
                _delegate.close();
            }

        } else {
            setPaused( true );

            if ( _thread != null ) {

                LogEventSmall event = new LogEventSmall( "AsyncAppender.close" );
                _queue.add( event );

                boolean done = false;

                do {
                    boolean isEmpty = _queue.isEmpty();

                    synchronized( _pauseLock ) {
                        if ( !isEmpty ) {
                            try {
                                _pauseLock.wait( ASYNC_BACK_MAX_WAIT );

                                if ( !_thread.isAlive() ) {
                                    done = true;
                                }
                            } catch( InterruptedException e ) { /* dont care */ }
                        } else {
                            done = true;
                        }
                    }
                } while( !done );

                // at this point the background Loop is no longer processing events so its safe to instruct flush on delegate

                _delegate.flush();
            }

            LogEvent event;

            while( !_queue.isEmpty() ) {
                event = (LogEvent) _queue.poll();

                if ( event != null ) {
                    _console.info( event.getMessage() );
                } else {
                    break; // shouldnt happen
                }
            }
        }
    }

    @Override public void flush() {
        if ( _syncMode ) {

            synchronized( this ) {
                _delegate.flush();
            }

        } else {
            // wake up so process everything in queue
            LogEventSmall event = new LogEventSmall( "AsyncAppender.close" );
            _queue.add( event );
            if ( _minFlushIntervalMS == 0 ) {
                synchronized( _pauseLock ) {
                    try {
                        _pauseLock.notifyAll();
                    } catch( Exception e ) {
                        // ignore
                    }
                }
            }
        }
    }

    @Override public void handle( LogEvent event ) {

        if ( _syncMode ) {

            synchronized( this ) {
                _delegate.handle( event );
                _delegate.flush();
            }

        } else {
            boolean wasEmpty = _queue.isEmpty();

            _queue.add( event );

            if ( _minFlushIntervalMS == 0 && wasEmpty ) {
                synchronized( _pauseLock ) {
                    try {
                        _pauseLock.notifyAll();
                    } catch( Exception e ) {
                        // ignore
                    }
                }
            }
        }
    }

    @Override public void init( Level level ) {
        // nothing
    }

    @Override public boolean isEnabledFor( Level level ) {
        return _delegate.isEnabledFor( level );
    }

    @Override public void open() {
        setPaused( false );
    }

    @Override public String toString() { return "AsyncAppender{ " + "_id=" + _id + ", q=" + _queue.size() + " }"; }

    public void forceClose() {
        _systemStopping = true;
        synchronized( _pauseLock ) {
            _pauseLock.notifyAll();
        }
    }

    public int getMinFlushIntervalMS() {
        return _minFlushIntervalMS;
    }

    public void wakeup() {
        setPaused( false );
    }

    void dispatchLoop() {
        ThreadUtilsFactory.get().setPriority( _thread, ThreadPriority.BackgroundLogger );

        _delegate.open();

        LogEvent event = new LogEventSmall( "AsyncAppender.LoggerThread - " + Thread.currentThread().getName() );
        _delegate.handle( event );

        long lastEvent = ClockFactory.getLiveClock().currentTimeMillis(); // REAL TIME
        long lastFlush = lastEvent;

        while( !isSystemStopping() ) {
            while( !isPaused() && !isSystemStopping() ) {

                event = (LogEvent) _queue.poll();

                long now   = ClockFactory.getLiveClock().currentTimeMillis();
                long delay = now - lastEvent;

                if ( event != null ) {
                    try {
                        _delegate.handle( event );
                    } catch( Exception e ) {
                        _errBuf.clear();
                        event.encode( _errBuf );
                        int    size = _errBuf.position();
                        String line = new String( _errBuf.array(), 0, size );
                        _console.error( ERR_DELEGATE, e.getClass().getSimpleName() + " " + e.getMessage() + " EVENT=" + line, e );
                    }

                    ++_eventCount;
                    if ( _logDepth && (_eventCount & 0x3FF) == 0 ) {
                        ReusableString s = TLC.instance().pop();
                        s.setValue( "AsyncLogDepth : cnt=" );
                        s.append( _eventCount ).append( ", depth=" ).append( _queue.size() );
                        _console.info( s );

                        TLC.instance().pushback( s );
                    }

                    lastEvent = now;

                    if ( _minFlushIntervalMS == 0 || (_minFlushIntervalMS > 0 && (now - lastFlush) > _minFlushIntervalMS) ) {

                        _delegate.flush();
                        lastFlush = now;
                    }
                } else {

                    if ( delay > FORCE_FLUSH_DELAY_MS ) {
                        _delegate.flush();
                        lastEvent = now;
                        lastFlush = now;
                    }

                    synchronized( _pauseLock ) {
                        try {
                            _pauseLock.wait( _sleepOnEmpty );
                        } catch( InterruptedException e ) {
                            // ignore
                        }
                    }
                }
            }

            event = new LogEventSmall( "AsyncAppender background thread paused, flushing events left in queue" );
            _delegate.handle( event );

            do {
                event = (LogEvent) _queue.poll();

                if ( event != null ) {
                    _delegate.handle( event );
                }

            } while( event != null );

            event = new LogEventSmall( "AsyncAppender flushed" );
            _delegate.handle( event );
            _delegate.flush();

            synchronized( _pauseLock ) {
                _pauseLock.notifyAll();
            }

            while( !isSystemStopping() ) {
                ThreadUtilsFactory.getLive().sleep( PAUSE_CHECK_INTERVAL );

                synchronized( _pauseLock ) {
                    if ( !isPaused() ) {
                        break;
                    }
                }
            }

            if ( !isSystemStopping() ) {
                event = new LogEventSmall( "AsyncAppender background thread unpaused" );
                _delegate.handle( event );
            }
        }

        event = new LogEventSmall( "AsyncAppender closing" );
        _delegate.handle( event );

        _delegate.close();

        _console.info( Thread.currentThread().getName() + " LOG DISPATCH LOOP TERMINATING" );
    }

    private boolean isPaused() {
        return _paused;
    }

    public void setPaused( boolean isPaused ) {
        synchronized( _pauseLock ) {
            _paused = isPaused;
            _pauseLock.notifyAll();
        }
    }

    private boolean isSystemStopping() {
        return _systemStopping;
    }

    void setSystemStopping( boolean isStopping ) {
        _systemStopping = isStopping;
        synchronized( _pauseLock ) {
            _pauseLock.notifyAll();
        }
    }
}
