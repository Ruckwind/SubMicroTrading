/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.thread;

import com.rr.core.component.CompRunState;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ErrorCode;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.properties.AppProps;
import com.rr.core.utils.ShutdownManager;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;

public abstract class AbstractControlThread extends Thread implements ControlThread {

    private static final ErrorCode MISSING_ELEM = new ErrorCode( "ACT100", "Misconfiguration! " );
    private static final ErrorCode INIT_EXCEPT  = new ErrorCode( "ACT200", "Worker Exception in threadedInit " );
    private static final ErrorCode ERR_CTL_LOOP = new ErrorCode( "ACT300", "Worker Exception in control thread " );
    protected final Object         _passiveLock      = new Object();
    private final   Logger         _log              = LoggerFactory.create( AbstractControlThread.class );
    private final   String         _name;
    private final   int            _lowPriLoopWaitMs = AppProps.instance().getIntProperty( "ACT_PASSIVE_WAIT_MS", false, Constants.LOW_PRI_LOOP_WAIT_MS );
    private                      ThreadPriority _priority;
    private transient volatile CompRunState   _compRunState = CompRunState.Initial;

    private         long           _passiveCnt       = 0;

    public AbstractControlThread( String name, ThreadPriority priority ) {

        super( name );

        setDaemon( true );

        _log.info( "AbstractControlThread: created " + getClass().getSimpleName() + " id=" + name );

        _priority = priority;
        _name     = name;
    }

    @Override public String getComponentId() { return _name; }

    @Override public Thread getThread() { return this; }

    @Override public boolean isStarted() { return getCompRunState().ordinal() >= CompRunState.Started.ordinal(); }

    @Override public void setStopping( boolean stopping ) {
        setCompRunState( CompRunState.Stopping );
        statusChange();
    }

    @Override public abstract void register( ExecutableElement ex );

    @Override public void setPriority( ThreadPriority priority ) { _priority = priority; }

    @Override public void statusChange() {
        synchronized( _passiveLock ) {
            _passiveLock.notifyAll();
        }
    }

    @Override public synchronized void start() {
        if ( getCompRunState() == CompRunState.Initial ) {
            super.start();
            _log.info( "Starting AbstractControlThread " + getName() );
            setCompRunState( CompRunState.Started );
        }
    }

    @Override public void run() {

        ThreadUtilsFactory.get().setPriority( this, _priority );

        _log.info( "CONTROL LOOP STARTED " + _name );

        try {
            runControlLoop();
        } catch( Throwable e ) {
            _log.error( ERR_CTL_LOOP, getComponentId() + ": " + e.getMessage(), e );
        }

        setCompRunState( CompRunState.Stopped );

        _log.info( "CONTROL LOOP FINISHED " + _name + ", passiveCnt=" + _passiveCnt );
    }

    public int getLowPriLoopWaitMs() { return _lowPriLoopWaitMs; }

    protected void abortMissingWorker() {
        _log.error( MISSING_ELEM, getClass().getSimpleName() + " missing worker, controlId=" + getName() );

        ShutdownManager.instance().shutdown( -1 );
    }

    protected void abortThreadedInit( Exception e ) {
        _log.error( INIT_EXCEPT, getClass().getSimpleName() + " " + getName() + " : " + e.getMessage(), e );

        ShutdownManager.instance().shutdown( -1 );
    }

    protected final void goPassive() {

        synchronized( _passiveLock ) {
            try {
                ++_passiveCnt;

                _passiveLock.wait( _lowPriLoopWaitMs );
            } catch( InterruptedException e ) {
                // dont care
            }
        }
    }

    protected abstract void runControlLoop();

    private synchronized void setCompRunState( final CompRunState compRunState ) { // sync to make sure only one state change at any time
        if ( CompRunState.procStateChange( id(), _compRunState, compRunState ) ) {
            _compRunState = compRunState;
        }
    }
}
