/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.thread.AllDisconnectedException;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadedReceiver extends Thread implements Receiver {

    private final Logger _rlog = LoggerFactory.create( ThreadedReceiver.class );

    private final RecoverableSession _session;
    private final    String  _id;
    private       AtomicBoolean      _stopping = new AtomicBoolean( false );
    private       ThreadPriority     _threadPriority;
    private volatile boolean _started = false;

    public ThreadedReceiver( RecoverableSession session, ThreadPriority threadPriority ) {
        this( (session.getComponentId() + "Receiver" + session.getIntId()), session, threadPriority, true );
    }

    public ThreadedReceiver( RecoverableSession session, ThreadPriority threadPriority, boolean isDaemon ) {
        this( (session.getComponentId() + "Receiver" + session.getIntId()), session, threadPriority, isDaemon );
    }

    public ThreadedReceiver( String id, RecoverableSession session, ThreadPriority threadPriority ) {
        this( id, session, threadPriority, true );
    }

    public ThreadedReceiver( String id, RecoverableSession session, ThreadPriority threadPriority, boolean isDaemon ) {
        super( id );
        _id             = id;
        _session        = session;
        _threadPriority = threadPriority;
        setDaemon( isDaemon );
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    // when get socket error on reader or writer then both threads need to  stop processing before open new socket 

    @Override
    public synchronized void start() {
        if ( !_started ) {
            super.start();
            _started = true;
        }
    }

    @Override
    public void run() {

        ThreadUtilsFactory.get().setPriority( this, _threadPriority );

        _rlog.info( "Session " + getName() + " RECEIVE LOOP STARTED" );

        try {
            while( !isStopping() ) {

                _session.internalConnect(); // connect within context of the receiver thread

                if ( !isStopping() ) {
                    if ( _session.getSessionState() == RecoverableSession.SessionState.Connected ) {
                        _session.processIncoming();
                    }

                    if ( _session.getSessionState() == RecoverableSession.SessionState.Disconnected ) {  // couldnt connect OR just disconnected dont retry straight away
                        ThreadUtilsFactory.getLive().waitFor( _session, SessionConstants.CONNECT_WAIT_DELAY_MS );
                    }
                }
            }
        } catch( AllDisconnectedException e ) {
            _rlog.info( "Session " + _session.getComponentId() + " ALL DISCONNECTED" );
        }

        _rlog.info( "Session " + _session.getComponentId() + " RECEIVE LOOP FINISHED" );
    }

    private boolean isStopping() {
        return _stopping.get();
    }

    @Override
    public void setStopping( boolean stopping ) {
        _stopping.set( true );
    }

    @Override
    public boolean isStarted() {
        return _started;
    }
}

