/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.collections.EventQueue;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.CoreReusableType;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.EventHandler;
import com.rr.core.model.NullEvent;
import com.rr.core.thread.ControlThread;
import com.rr.core.thread.ExecutableElement;
import com.rr.core.thread.RunState;
import com.rr.core.utils.SMTRuntimeException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * simple wrapper for ExecutableElement to a single message handler
 * <p>
 * does NOT own spinning control thread, that is shared and which round robins sessions
 */
public final class SimpleExecutableElement implements EventDispatcher, ExecutableElement {

    private static final Logger _log = LoggerFactory.create( SimpleExecutableElement.class );

    private final ControlThread _ctl;

    private final     EventQueue   _queue;
    private final String _id;
    private           EventHandler _handler;
    private transient RunState     _runState = RunState.Unknown;
    private Event _curMsg = null;
    private AtomicBoolean _stopping = new AtomicBoolean( false );

    // array index assigned to session to save map lookup

    public SimpleExecutableElement( String id, ControlThread ctl, EventQueue queue ) {
        _ctl   = ctl;
        _id    = id;
        _queue = queue;

        ctl.register( this );
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState()                          { return _runState; }

    @Override public RunState setRunState( final RunState newState ) { return _runState = newState; }

    @Override
    public void init( SMTStartContext ctx, CreationPhase creationPhase ) {
        // nothing
    }

    @Override
    public void prepare() {
        // nothing
    }

    @Override
    public synchronized void start() {
        _ctl.start();
    }

    @Override
    public void setStopping() {
        if ( _queue.isEmpty() ) {
            _queue.add( new NullEvent() ); // wake up queue
        }
    }

    @Override
    public void dispatch( final Event msg ) {
        if ( msg != null ) {
            _queue.add( msg );
        }
    }

    @Override
    public void setHandler( EventHandler handler ) {
        if ( _handler != null ) throw new SMTRuntimeException( "SingleExecutableElement MISCONFIGURATION : handler is already set" );

        _handler = handler;
    }

    @Override
    public void handleStatusChange( EventHandler handler, boolean connected ) {
        _ctl.statusChange();
    }

    @Override
    public boolean canQueue() {
        return true;
    }

    @Override
    public String info() {
        return "SimpleExecutableElement( " + _id + " )";
    }

    @Override
    public void dispatchForSync( Event msg ) {
        throw new SMTRuntimeException( "SingleExecutableElement : dispatchForSync not allowed" );
    }

    @Override
    public void startWork() {
        start();
    }

    @Override
    public void stopWork() {
        stop();
    }

    @Override
    public void threadedInit() {
        _log.info( "SimpleExecutableElement " + getComponentId() + " threadedInit in thread " + Thread.currentThread().getName() );

        _handler.threadedInit();
    }

    @Override
    public void execute() {
        if ( _handler.canHandle() ) {
            _curMsg = _queue.poll();
            if ( _curMsg != null && _curMsg.getReusableType() != CoreReusableType.NullEvent ) {
                _handler.handleNow( _curMsg );
            }
        }
    }

    @Override
    public void handleExecutionException( Exception ex ) {

        if ( _curMsg != null ) {
            _log.warn( "SimpleExecutableElement " + getComponentId() + ", msgSeqNum=" + _curMsg.getMsgSeqNum() +
                       ", sess=" + _handler.getComponentId() + " exception " + ex.getMessage() );
        }

        // some problem, possibly disconnect, poke controller to wake up anything waiting on controller passive lock
        _ctl.statusChange(); // Mem barrier
    }

    @Override
    public boolean checkReady() {
        return (_handler.canHandle());
    }

    @Override
    public void notReady() {
        // NADA
    }

    @Override
    public void stop() {
        if ( _stopping.compareAndSet( false, true ) ) {
            _ctl.setStopping( true );

            if ( _queue.isEmpty() ) {
                _queue.add( new NullEvent() ); // wake up queue
            }
        }
    }
}
