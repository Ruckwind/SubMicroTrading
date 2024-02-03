/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.collections.EventQueue;
import com.rr.core.collections.SimpleEventQueue;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.*;
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
 * a multi session threaded dispatcher for session outbound messages
 * <p>
 * does NOT own spinning control thread, that is shared and which round robins sessions
 * <p>
 * if a session cannot send a message eg socket blocked due to slow consumer then the NonBlockingSession returns immediately
 * that session will have to wait its turn to retry writing remaining data to the socket
 * <p>
 * Currently max sockets is 32 but this could easily be changed to 64 with minimal delay impact IF using OS bypass eg Solarflare OpenOnload
 */
public final class MultiSessionThreadedDispatcher implements MultiSessionDispatcher, ExecutableElement {

    static final         ZString   DISCONNECTED    = new ViewString( "MultiSessionThreadedDispatcher() Unable to dispatch as destination Session Disconnected" );
    private static final Logger _log = LoggerFactory.create( MultiSessionThreadedDispatcher.class );
    private static final ErrorCode MISSING_HANDLER = new ErrorCode( "MSD100", "No registered dispatcher for message to session " );
    private static final ZString   DROP_MSG        = new ViewString( "Dropping session message as not logged in, type=" );
    private final ReusableString _logMsg          = new ReusableString( 50 );
    private final Object _disconnectLock = new Object();
    private final ControlThread _ctl;
    private final String _id;
    private SessionWrapper[] _sessions = new SessionWrapper[ 0 ];
    private int _nextSession = 0;
    private       boolean        _allDisconnected = true;              // not volatile as mem barrier already occurs when get msg off queue
    private Event          _curMsg   = null;
    private SessionWrapper _curSessW = null;

    private volatile boolean _fullyFlushed = false;

    private AtomicBoolean _stopping = new AtomicBoolean( false );
    private RunState _runState = RunState.Created;

    public MultiSessionThreadedDispatcher( String id, ControlThread ctl ) {
        _ctl = ctl;
        _id  = id;

        ctl.register( this );
    }

    // array index assigned to session to save map lookup

    /**
     * @param session - a non blocking session ie an NIO one that wont block if socket cant read/write
     */
    @Override
    public synchronized void addSession( final NonBlockingSession session ) {

        for ( SessionWrapper that : _sessions ) {
            if ( that._session == session ) return;
        }

        SessionWrapper[] newSessions = new SessionWrapper[ _sessions.length + 1 ];

        int idx = 0;
        while( idx < _sessions.length ) {
            newSessions[ idx ] = _sessions[ idx ];
            ++idx;
        }

        newSessions[ idx ] = new SessionWrapper( session, session.getSendQueue(), session.getSendSyncQueue() );

        _sessions = newSessions;

        _fullyFlushed = false;
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override public RunState getRunState() {
        return _runState;
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
    public void init( SMTStartContext ctx, CreationPhase creationPhase ) {
        // nothing
    }

    @Override
    public void prepare() {
        // nothing
    }

    @Override
    public synchronized void start() {
        setRunState( RunState.Active );
        _ctl.start();
    }

    @Override
    public void setStopping() {

        setRunState( RunState.Stopping );

        // dont actually stop, but wake up to force flush

        final int numSessions = _sessions.length;

        for ( int i = 0; i < numSessions; i++ ) {
            final SessionWrapper s = _sessions[ i ];

            EventQueue q = s._queue;

            if ( q.isEmpty() ) {
                q.add( new NullEvent() ); // wake up queue
            }
        }

        _fullyFlushed = false;
    }

    @Override
    public void dispatch( final Event msg ) {
        if ( msg != null ) {
            final EventHandler       handler = msg.getEventHandler();
            final NonBlockingSession session = (NonBlockingSession) handler;

            if ( session != null ) {
                final EventQueue queue = session.getSendQueue();
                if ( queue != null ) {
                    queue.add( msg );
                } else {
                    // should NEVER happen
                    ReusableString s = TLC.instance().pop();
                    s.copy( handler.getComponentId() ).append( ": Missing Queue, unable to dispatch : " );
                    msg.dump( s );
                    _log.error( MISSING_HANDLER, s );
                    TLC.instance().pushback( s );
                }
            } else {
                // should NEVER happen
                ReusableString s = TLC.instance().pop();
                if ( handler != null ) {
                    s.copy( getComponentId() ).append( " handlerId=" ).append( handler.getComponentId() ).append( " : Missing session, unable to dispatch : " );
                } else {
                    s.copy( getComponentId() ).append( ": Missing session, and event missing handler, unable to dispatch : " );
                }
                msg.dump( s );
                _log.error( MISSING_HANDLER, s );
                TLC.instance().pushback( s );
            }
        }
    }

    @Override
    public void setHandler( EventHandler handler ) {
        throw new SMTRuntimeException( "MultiSessionThreadedDispatcher MISCONFIGURATION : only for use with multi session and addSession()" );
    }

    @Override
    public void handleStatusChange( EventHandler handler, boolean connected ) {
        final int numSessions = _sessions.length;

        boolean anyConnected = false;

        for ( int i = 0; i < numSessions; i++ ) {
            SessionWrapper sessW = _sessions[ i ];

            if ( sessW._session == handler ) {
                if ( connected != sessW._connected ) {
                    final NonBlockingSession sess = sessW._session;

                    _log.info( "MultiSession OutDispatcher " + getComponentId() + " : " + ((connected) ? "CONNECTED" : "DISCONNECTED") +
                               " with " + sess.getComponentId() + ", canHandle=" + sess.canHandle() + ", isLoggedIn=" + sess.isLoggedIn() );

                    sessW._connected = connected;
                }
            }

            if ( sessW._connected ) {
                anyConnected = true;
            }
        }

        _fullyFlushed = false;

        synchronized( _disconnectLock ) {       // force mem barrier
            _allDisconnected = !anyConnected;
        }

        _ctl.statusChange();
    }

    @Override
    public boolean canQueue() {
        return true;
    }

    @Override
    public String info() {
        return "MultiSessionThreadedDispatcher( " + _id + " )";
    }

    @Override
    public void dispatchForSync( Event msg ) {
        if ( msg != null ) {
            final EventHandler handler = msg.getEventHandler(); // cant be null
            NonBlockingSession session = (NonBlockingSession) handler;
            EventQueue         queue   = session.getSendSyncQueue();
            if ( queue != null ) {
                queue.add( msg );
            } else {
                // @TODO add ReusableString write( ReusableString buf ) to Message and logger details
                // should NEVER happen
                _log.error( MISSING_HANDLER, handler.getComponentId() );
            }
        }
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
        _log.info( "MultiSessionThreadedDispatcher " + getComponentId() + " threadedInit in thread " + Thread.currentThread().getName() );

        boolean allFinished = true;

        for ( int idx = 0; idx < _sessions.length; ++idx ) {
            _sessions[ idx ]._session.threadedInit();

            if ( !_sessions[ idx ]._session.isStopping() ) {
                allFinished = false;
            }
        }

        if ( allFinished ) {
            stop();
        }
    }

    @Override
    public void execute() throws Exception {

        _curSessW = _sessions[ _nextSession ];

        final EventQueue         queue    = _curSessW._queue;
        final EventQueue         preQueue = _curSessW._preQueue;
        final NonBlockingSession sess     = _curSessW._session;

        if ( ++_nextSession >= _sessions.length ) _nextSession = 0;

        if ( _curSessW._connected && sess.canHandle() ) {
            if ( sess.isLoggedIn() ) {
                if ( sess.isMsgPendingWrite() ) {
                    sess.retryCompleteWrite();
                } else if ( preQueue.isEmpty() ) {
                    _curMsg = queue.poll();                     // POLL = non blocking, causes MEM_READ barrier
                    if ( _curMsg != null && _curMsg.getReusableType() != CoreReusableType.NullEvent ) {
                        sess.handleNow( _curMsg );
                    }
                } else { // QUEUED MESSAGES FROM PREVIOUS FLUSH CALLS
                    _curMsg = preQueue.next();
                    if ( _curMsg.getReusableType() != CoreReusableType.NullEvent ) {
                        sess.handleNow( _curMsg );
                    }
                }
            } else { // SYNC mode
                final EventQueue syncQueue = _curSessW._syncQueue;
                if ( sess.isMsgPendingWrite() ) {
                    sess.retryCompleteWrite();
                } else if ( !syncQueue.isEmpty() ) {
                    _curMsg = syncQueue.next();
                    if ( _curMsg.getReusableType() != CoreReusableType.NullEvent ) {
                        sess.handleNow( _curMsg );
                    }
                }
            }
        } else {
            flush( _curSessW );
        }
    }

    @Override
    public void handleExecutionException( Exception ex ) {
        final NonBlockingSession sess = _curSessW._session;

        if ( _curMsg != null && sess != null ) {
            _log.warn( "SessionThreadedDispatcher " + getComponentId() + ", msgSeqNum=" + _curMsg.getMsgSeqNum() +
                       ", sess=" + sess.getComponentId() + " exception " + ex.getMessage() );
        }

        flush( _curSessW );

        // some problem, possibly disconnect, poke controller to wake up anything waiting on controller passive lock
        _ctl.statusChange(); // Mem barrier
    }

    @Override
    public boolean checkReady() {
        return (_allDisconnected == false);
    }

    @Override
    public void notReady() {
        disconnectedFlushAll();
    }

    @Override
    public void stop() {
        if ( _stopping.compareAndSet( false, true ) ) {
            setRunState( RunState.Stopping );

            _ctl.setStopping( true );

            final int numSessions = _sessions.length;

            for ( int i = 0; i < numSessions; i++ ) {
                final SessionWrapper s = _sessions[ i ];

                EventQueue q = s._queue;

                if ( q.isEmpty() ) {
                    q.add( new NullEvent() ); // wake up queue
                }
            }

            _fullyFlushed = false;
        }
    }

    private void disconnectedFlushAll() {
        if ( !_fullyFlushed ) {

            final int numSessions = _sessions.length;

            for ( int i = 0; i < numSessions; i++ ) {
                flush( _sessions[ i ] );
            }

            _fullyFlushed = true;
        }
    }

    // @NOTE keep flush private the preQ is not threadsafe
    private void flush( SessionWrapper sessW ) {
        // disconnected drop any session messages
        // optionally keep any other messages or reject back  upstream

        final RecoverableSession session = sessW._session;
        final EventQueue         queue   = sessW._queue;
        final EventQueue         preQ    = sessW._preQueue;
        final EventQueue         syncQ   = sessW._syncQueue;

        while( !syncQ.isEmpty() ) {
            syncQ.next(); // DISCARD
        }

        Event head = null;
        Event tail = null;

        while( !queue.isEmpty() ) {
            Event msg = queue.next();
            if ( msg.getReusableType() == CoreReusableType.NullEvent ) break;

            if ( session.discardOnDisconnect( msg ) == false ) {
                if ( session.handleDisconnectedNow( msg ) == false ) {
                    if ( session.rejectMessageUpstream( msg, DISCONNECTED ) ) {
                        // message recycled by successful reject processing
                    } else {
                        if ( tail == null ) {
                            head = msg;
                            tail = msg;
                        } else {
                            tail.attachQueue( msg );
                            tail = msg;
                        }
                    }
                }
            } else {
                _logMsg.copy( DROP_MSG ).append( msg.getReusableType().toString() );
                _log.info( _logMsg );
                session.outboundRecycle( msg );
            }
        }

        // move remaining messages to the preQ

        if ( head != null ) {
            Event tmp = head;

            while( tmp != null ) {
                Event next = tmp.getNextQueueEntry();
                tmp.detachQueue();
                preQ.add( tmp );
                tmp = next;
            }
        }
    }

    private class SessionWrapper {

        final EventQueue         _queue;
        final EventQueue         _syncQueue;
        final EventQueue         _preQueue;               // when disconnected flushed events that need to be kept go here
        final NonBlockingSession _session;
        boolean _connected = false;

        SessionWrapper( NonBlockingSession session, EventQueue queue, EventQueue syncQueue ) {
            _queue     = queue;
            _syncQueue = syncQueue;
            _session   = session;
            _preQueue  = new SimpleEventQueue();
        }
    }
}
