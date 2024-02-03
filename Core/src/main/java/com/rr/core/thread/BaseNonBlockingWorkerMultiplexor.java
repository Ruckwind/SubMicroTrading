/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.thread;

import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.logger.ExceptionTrace;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.SMTRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseNonBlockingWorkerMultiplexor implements NonBlockingMultiplexor {

    private static final Logger _log = LoggerFactory.create( BaseNonBlockingWorkerMultiplexor.class );
    private final String  _id;
    private final Object  _disconnectLock  = new Object();
    private final ControlThread _ctl;
    private final String        _pipeIdList  = null; // comma delimitted string list of pipeline ids
    private final AtomicBoolean _stopping = new AtomicBoolean( false );
    private WorkerWrapper[] _workers = new WorkerWrapper[ 0 ];
    private       int     _nextWorker      = 0;
    private       boolean _allDisconnected = true;              // not volatile as mem barrier already occurs when get msg off queue
    private       WorkerWrapper _curWorker   = null;
    private       List<String>  _pipeLineIds = null;
    private       RunState      _runState    = RunState.Created;

    public BaseNonBlockingWorkerMultiplexor( String id, ControlThread ctl ) {
        _ctl = ctl;
        _id  = id;

        ctl.register( this );
    }

    // array index assigned to worker to save map lookup

    /**
     * @param worker - a non blocking worker
     */
    @Override
    public synchronized void addWorker( final NonBlockingWorker worker ) {

        for ( WorkerWrapper w : _workers ) {
            if ( worker == w._worker ) return; // already a member
        }

        WorkerWrapper[] newWorkers = new WorkerWrapper[ _workers.length + 1 ];

        int idx = 0;
        while( idx < _workers.length ) {
            newWorkers[ idx ] = _workers[ idx ];
            ++idx;
        }

        final WorkerWrapper wrapper = new WorkerWrapper( worker );
        newWorkers[ idx ] = wrapper;

        _workers = newWorkers;

        worker.registerListener( state -> {
            wrapper._state = state;
            handleStatusChange( worker, state );
        } );
    }

    @Override
    public boolean checkReady() {
        return (_allDisconnected == false);
    }

    @Override
    public void execute() throws Exception {

        _curWorker = _workers[ _nextWorker ];

        final NonBlockingWorker worker = _curWorker._worker;

        if ( ++_nextWorker >= _workers.length ) _nextWorker = 0;

        if ( _curWorker.isActive() && worker.hasOutstandingWork() ) {
            worker.doWorkUnit();
        }
    }

    @Override
    public void handleExecutionException( Exception ex ) {

        if ( ex instanceof AllDisconnectedException ) {
            _log.info( "NonBlockingWorkerMultiplexor " + getComponentId() + " ALL WORKERS NOW PASSIVE, GIVE ControlThread CHANCE TO GO PASSIVE" );
        } else {

            final NonBlockingWorker sess = _curWorker._worker;

            if ( sess != null ) {
                ReusableString m = TLC.instance().pop();

                m.copy( "NonBlockingWorkerMultiplexor " ).append( getComponentId() ).append( ", sess=" ).append( sess.getComponentId() ).append( " exception " );

                m.append( ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage() );

                ExceptionTrace.getStackTrace( m, ex );

                _log.warn( m );

                TLC.instance().pushback( m );
            }

            // some problem, possibly disconnect, poke controller to wake up anything waiting on controller passive lock

            _ctl.statusChange(); // Mem barrier
        }
    }

    @Override
    public String info() {
        return "MultiWorkerThreadedDispatcher( " + _id + " )";
    }

    @Override
    public void notReady() {
        int           nextSession = 0;
        WorkerWrapper sess;

        while( nextSession < _workers.length ) {
            sess = _workers[ nextSession++ ];

            if ( sess.isActive() ) {
                _allDisconnected = false;
            }
        }
    }

    @Override
    public void stop() {
        if ( _stopping.compareAndSet( false, true ) ) {
            setRunState( RunState.Stopping );

            _ctl.setStopping( true );

            final int numSessions = _workers.length;

            for ( int i = 0; i < numSessions; i++ ) {
                final WorkerWrapper s = _workers[ i ];

                NonBlockingWorker q = s._worker;

                q.stop();
            }
        }
    }

    @Override
    public void threadedInit() {
        _log.info( "BaseNonBlockingWorkerMultiplexor " + getComponentId() + " threadedInit in thread " + Thread.currentThread().getName() );

        boolean allFinished = true;

        for ( int idx = 0; idx < _workers.length; ++idx ) {
            _workers[ idx ]._worker.threadedInit();

            if ( _workers[ idx ]._worker.hasOutstandingWork() ) {
                allFinished = false;
            }
        }

        if ( allFinished ) {
            stop();
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
        setPipeIdList( _pipeIdList );
    }

    @Override
    public void prepare() {
        _ctl.start();
    }

    @Override public RunState setRunState( final RunState newState ) {
        RunState old = _runState;

        if ( old != newState ) {
            _log.info( getComponentId() + " change state from " + old + " to " + newState );
            _runState = newState;
        }

        return old;
    }

    @Override
    public void startWork() {
        setRunState( RunState.Active );
    }

    @Override
    public void stopWork() {
        stop();
    }

    /**
     * if pipelineIds is null try and generate the pipeLine list as can be invoked by loader before init is run
     *
     * @return the list of pipelineIds
     */
    public synchronized List<String> getPipeLineIds() {
        if ( _pipeLineIds == null ) {
            setPipeIdList( _pipeIdList );
        }
        return _pipeLineIds;
    }

    public boolean hasPipeLineId( String pipeLineId ) {
        return _pipeLineIds.contains( pipeLineId );
    }

    public void setPipeIdList( String pipeIdList ) {
        List<String> pipeLineIds = new ArrayList<>();

        if ( pipeIdList != null ) {
            String[] parts = pipeIdList.split( "," );

            for ( String part : parts ) {
                part = part.trim();

                if ( part.length() > 0 ) {
                    pipeLineIds.add( part );
                }
            }
        }

        _pipeLineIds = pipeLineIds;
    }

    void handleStatusChange( NonBlockingWorker handler, RunState newState ) {
        final int numWorkers = _workers.length;

        boolean allDisconnected = true;

        int deadCount = 0;

        for ( int i = 0; i < numWorkers; i++ ) {
            WorkerWrapper sessW = _workers[ i ];

            if ( sessW._worker == handler ) {
                if ( newState != sessW._state ) {
                    final NonBlockingWorker sess = sessW._worker;

                    _log.info( "NonBlockingWorkerMultiplexor " + getComponentId() + " : " +
                               " with " + sess.getComponentId() + ((sess.hasOutstandingWork()) ? " ACTIVE" : " PASSIVE") );

                    sessW._state = newState;
                }
            }

            if ( sessW.isActive() ) {
                allDisconnected = false;
            } else if ( sessW.isDead() ) {
                ++deadCount;
            }
        }

        if ( deadCount > 0 ) {
            removeDead( deadCount );
        }

        synchronized( _disconnectLock ) {       // force mem barrier
            _allDisconnected = allDisconnected;

            if ( _allDisconnected ) {
                stop();
            }
        }

        _ctl.statusChange();
    }

    private void removeDead( int deadCnt ) {
        int numWorkers = _workers.length - deadCnt;

        if ( numWorkers < 0 ) {
            _log.info( getComponentId() + " removeDead oldCnt=" + _workers.length + " newCnt=" + numWorkers + " so setting to 0" );
        } else {
            _log.info( getComponentId() + " removeDead oldCnt=" + _workers.length + " newCnt=" + numWorkers );
        }

        WorkerWrapper[] newWrappers = new WorkerWrapper[ numWorkers ];

        int newIdx     = 0;
        int verifyDead = 0;

        int i = 0;

        for ( ; i < numWorkers; i++ ) {
            WorkerWrapper sessW = _workers[ i ];

            if ( sessW.isDead() ) {
                _log.info( getComponentId() + " dropping dead component " + sessW._worker.getComponentId() );
                ++verifyDead;
            } else {
                newWrappers[ newIdx++ ] = sessW;
            }
        }

        for ( ; i < _workers.length; i++ ) {
            WorkerWrapper sessW = _workers[ i ];

            if ( sessW.isDead() ) {
                _log.info( getComponentId() + " dropping dead component " + sessW._worker.getComponentId() );
                ++verifyDead;
            }
        }

        if ( verifyDead != deadCnt ) throw new SMTRuntimeException( getComponentId() + " error mismatch removing dead workers, deadCnt=" + deadCnt + ", verifyCnt=" + verifyDead );

        /**
         * As _nextWorker and _workers is not updated within a sync block
         * there is an edge that could cause execute() to throw index exception
         * however there will be immediate mem barrier so next work unit will be ok
         * ie dont worry about it
         */
        if ( _nextWorker >= newWrappers.length ) _nextWorker = 0;
        _workers = newWrappers;
    }

    private class WorkerWrapper {

        final NonBlockingWorker _worker;
        RunState _state = null;

        WorkerWrapper( NonBlockingWorker worker ) {
            _worker = worker;
        }

        public boolean isActive() { return _state == RunState.Active; }

        public boolean isDead()   { return _state == RunState.Dead; }
    }
}
