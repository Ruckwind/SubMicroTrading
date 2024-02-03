/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.dispatch;

import com.rr.core.collections.EventQueue;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.Constants;
import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.model.NullEvent;
import com.rr.core.thread.AllDisconnectedException;
import com.rr.core.thread.RunState;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;

/**
 * threaded dispatcher, to make into a blocking dispatcher which waits for queue entries and signal to wake up use a blocking queue
 */
public final class ThreadedDispatcher implements EventDispatcher {

    static final Logger _log = LoggerFactory.create( ThreadedDispatcher.class );

    private static final class Dispatcher extends Thread {

        private static final long      MAX_PAUSE           = Constants.LOW_PRI_LOOP_WAIT_MS;
        private static final ErrorCode ERR_DISPATCH_PROC   = new ErrorCode( "THD100", "Error processing dispatched message" );
        private static final ErrorCode ERR_MISSING_HANDLER = new ErrorCode( "THD200", "ThreadDispatcher is missing a handler" );
        ;

        private final    EventQueue     _queue;
        private final    ThreadPriority _threadPriority;
        private          EventHandler   _handler;
        private volatile boolean        _finished;

        public Dispatcher( String threadName, ThreadPriority threadPriority, EventQueue queue ) {
            super( threadName );

            _queue          = queue;
            _threadPriority = threadPriority;

            setDaemon( true );
        }

        @Override
        public void run() {

            if ( _handler == null ) _log.error( ERR_MISSING_HANDLER, ": " + getName() );

            ThreadUtilsFactory.get().setPriority( this, _threadPriority );

            _handler.threadedInit();

            while( !_finished ) {
                connectedHandler();
                disconnectedHandler();
            }

            _log.info( "ThreadedDispatcher " + getName() + " finished" );
        }

        public void setFinished() {
            _finished = true;
            if ( _queue.isEmpty() ) {
                _queue.add( new NullEvent() ); // wake up queue
            }
        }

        public void setHandler( EventHandler handler ) {

            if ( _handler != null && _handler != handler ) {
                _log.warn( "ThreadedDispatcher overwriting handler " + _handler.id() + " with " + handler.id() + "! .... this component cannot be shared check logic " + getName() );
            }

            _handler = handler;
        }

        private void connectedHandler() {
            if ( _handler.canHandle() ) {
                try {
                    Event msg;
                    while( !_finished && _handler.canHandle() ) {
                        msg = _queue.next();

                        if ( msg.getReusableType() != CoreReusableType.NullEvent ) {
                            _handler.handleNow( msg );
                        }
                    }

                } catch( AllDisconnectedException e ) {
                    _log.info( "ThreadedDispatcher " + getName() + " all workers completed, terminating thread" );
                    setFinished();
                } catch( Exception e ) {
                    _log.error( ERR_DISPATCH_PROC, "ThreadedDispatcher " + getName() + " exception ", e );
                }
            }
        }

        private void disconnectedHandler() {
            if ( !_handler.canHandle() && !_finished ) {
                synchronized( _queue ) {
                    try {
                        _queue.wait( MAX_PAUSE );
                    } catch( InterruptedException e ) {
                        // dont care
                    }
                }
            }
        }
    }

    private final EventQueue _queue;
    private final Dispatcher _threadDispatcher;
    private final String     _id;

    private transient volatile RunState _runState = RunState.Created;

    public ThreadedDispatcher( String id, EventQueue queue ) {
        this( id, queue, ThreadPriority.Other, null );
    }

    public ThreadedDispatcher( String id, EventQueue queue, ThreadPriority threadPriority ) {
        this( id, queue, threadPriority, null );
    }

    public ThreadedDispatcher( String id, EventQueue queue, ThreadPriority threadPriority, EventHandler handler ) {
        super();
        _id    = id;
        _queue = queue;

        String dispatchName = "DISPATCH_" + id;

        _log.info( "ThreadedDispatcher " + id + " using queue " + queue.getClass().getSimpleName() );

        _threadDispatcher = new Dispatcher( dispatchName, threadPriority, queue );

        if ( handler != null ) {
            _threadDispatcher.setHandler( handler );
        }
    }

    @Override
    public boolean canQueue() {
        return true;
    }

    @Override
    public void dispatch( Event msg ) {
        _queue.add( msg );
    }

    @Override
    public void dispatchForSync( Event msg ) {
        _queue.add( msg );
    }

    @Override public void forceStop() {
        _queue.forceStop();
    }

    @Override
    public void handleStatusChange( EventHandler handler, boolean isOk ) {
        // nothing
    }

    @Override
    public String info() {
        return "( " + _threadDispatcher.getName() + " : Q=" + _queue.size() + ")";
    }

    @Override
    public void setHandler( EventHandler handler ) {
        _threadDispatcher.setHandler( handler );
    }

    @Override
    public void setStopping() {
        _threadDispatcher.setFinished();
        setRunState( RunState.Stopping );
    }

    @Override
    public synchronized void start() {
        if ( _runState != RunState.Active ) {
            _threadDispatcher.setDaemon( true );
            _threadDispatcher.start();
            setRunState( RunState.Active );
        }
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState() {
        return _runState;
    }

    @Override
    public void init( SMTStartContext ctx, CreationPhase creationPhase ) {
        setRunState( RunState.Initialised );
    }

    @Override
    public void prepare() {
        // nothing
    }

    @Override public RunState setRunState( final RunState newState ) {
        return _runState = newState;
    }

    @Override
    public void startWork() {
        start();
    }

    @Override
    public void stopWork() {
        setStopping();
    }
}
