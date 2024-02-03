/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.codec.RuntimeDecodingException;
import com.rr.core.codec.RuntimeEncodingException;
import com.rr.core.component.CreationPhase;
import com.rr.core.component.SMTStartContext;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.logger.ExceptionTrace;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.thread.ControlThread;
import com.rr.core.thread.ExecutableElement;
import com.rr.core.thread.PipeLineable;
import com.rr.core.thread.RunState;
import com.rr.core.utils.ThreadPriority;
import com.rr.core.utils.ThreadUtilsFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * single receiver thread used to service all registered sessions
 *
 * @TODO add reconnection throttling and out of hours handling
 */
public class MultiSessionThreadedReceiver implements MultiSessionReceiver, ExecutableElement, PipeLineable {

    static final Logger _rlog = LoggerFactory.create( MultiSessionThreadedReceiver.class );
    private static final ErrorCode ERR_INT_CONN = new ErrorCode( "MSR100", "MultiSessionThreadedReceiver exception on internalConnect " );
    final ControlThread                            _ctlr;
    final Map<RecoverableSession, SocketConnector> _connectors = new ConcurrentHashMap<>( 64 );
    private final String               _id;
    boolean _allDisconnected = true; // note not volatile
    private       AtomicBoolean        _stopping    = new AtomicBoolean( false );
    private       List<String>         _pipeLineIds = new ArrayList<>();
    private       NonBlockingSession[] _sessions    = new NonBlockingSession[ 0 ];
    private       int                  _nextSession;
    private       NonBlockingSession   _curSess     = null;
    private       RunState             _runState;

    public MultiSessionThreadedReceiver( String receiverId, ControlThread ctlr ) {
        this( receiverId, ctlr, null );
    }

    public MultiSessionThreadedReceiver( String receiverId, ControlThread ctlr, String pipeLineIds ) {
        _id   = receiverId;
        _ctlr = ctlr;

        _ctlr.register( this );

        setPipeIdList( pipeLineIds );
    }

    @Override
    public synchronized void addSession( final NonBlockingSession session ) {
        List<NonBlockingSession> newSessions = new ArrayList<>( _sessions.length + 1 );

        for ( NonBlockingSession that : _sessions ) {
            if ( that == session ) return;
        }

        int idx = 0;
        while( idx < _sessions.length ) {
            final NonBlockingSession curSess = _sessions[ idx ];
            if ( curSess.isChildSession() && curSess.isStopping() && !curSess.isConnected() ) {
                _rlog.info( "MultiSessionThreadedReceiver : " + getComponentId() + " dropping child session that is disconnected and stopping, id=" + curSess.getComponentId() );
            } else {
                newSessions.add( curSess );
            }
            ++idx;
        }

        newSessions.add( session );

        _sessions = newSessions.toArray( new NonBlockingSession[ newSessions.size() ] );

        /**
         * manual disconnect calls will not trigger a disconnectedException
         * for test code to work, trap disconnect events and kick off reconnect if open hours
         */
        session.registerConnectionListener( new ConnectionListener() {

            @Override
            public void connected( RecoverableSession connectedSession ) { updateDisconnectedStatus(); }

            @SuppressWarnings( "synthetic-access" )
            @Override
            public void disconnected( RecoverableSession disconnectedSession ) {
                nonBlockingConnect( (NonBlockingSession) disconnectedSession );
            }
        } );
    }

    @Override
    public int getNumSessions() {
        return _sessions.length;
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    @Override
    public List<String> getPipeLineIds() {
        return _pipeLineIds;
    }

    @Override
    public boolean hasPipeLineId( String pipeLineId ) {
        return _pipeLineIds.contains( pipeLineId );
    }

    @Override public RunState getRunState() {
        return _runState;
    }

    @Override public RunState setRunState( final RunState newState ) {
        RunState old = _runState;

        if ( old != newState ) {
            _rlog.info( getComponentId() + " change state from " + old + " to " + newState );
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
        _ctlr.start();
    }

    @Override
    public synchronized void setStopping( boolean stopping ) {
        // request to stop ignored as its propogated from session which doesnt own the control thread
    }

    @Override
    public synchronized boolean isStarted() {
        return _ctlr.isStarted();
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
        _rlog.info( "MultiSessionThreadedReceiver " + getComponentId() + " threadedInit in thread " + Thread.currentThread().getName() + ", numSessions=" + _sessions.length );

        boolean allFinished = true;

        for ( int i = 0; i < _sessions.length; ++i ) {
            nonBlockingConnect( _sessions[ i ] );

            if ( !_sessions[ i ].isStopping() ) {
                allFinished = false;
            }
        }

        if ( allFinished ) {
            stop();
        }
    }

    @Override
    public void execute() throws Exception {

        _curSess = _sessions[ _nextSession ];

        if ( ++_nextSession >= _sessions.length ) _nextSession = 0;

        if ( _curSess.getSessionState() == RecoverableSession.SessionState.Connected ) {
            _curSess.processNextInbound();
        }
    }

    @Override
    public void handleExecutionException( Exception ex ) {

        if ( _rlog.isEnabledFor( Level.debug ) ) {
            ReusableString t = TLC.strPop();
            t.copy( getComponentId() ).append( " handleExecutionException : " ).append( ex.getMessage() );

            if ( _curSess != null ) {
                t.append( ", curSess=" ).append( _curSess.id() );
            }

            ExceptionTrace.getStackTrace( t, ex );
            _rlog.info( t );
            TLC.strPush( t );
        }

        if ( ex instanceof SessionException ) {
            if ( _curSess != null ) {
                _curSess.logInboundError( ex );
                _curSess.disconnect( true );
            }
        } else if ( ex instanceof DisconnectedException ) {
            if ( _curSess != null ) {
                if ( !_ctlr.isStopping() ) _curSess.logDisconnected( ex );
                _curSess.disconnect( true );
            }
        } else if ( ex instanceof IOException ) {
            if ( _curSess != null ) {
                if ( !_ctlr.isStopping() ) _curSess.logInboundError( ex );
                _curSess.disconnect( true );
            }
        } else if ( ex instanceof RuntimeDecodingException ) {
            if ( _curSess != null ) {
                _curSess.logInboundDecodingError( (RuntimeDecodingException) ex );
            }
        } else if ( ex instanceof RuntimeEncodingException ) {
            if ( _curSess != null ) {
                _curSess.logOutboundEncodingError( (RuntimeEncodingException) ex );
            }
        } else {
            if ( _curSess != null ) {
                _curSess.logInboundError( ex );          // not a socket error dont drop socket
            }
        }

        updateDisconnectedStatus();

        nonBlockingConnect( _curSess );
    }

    @Override
    public boolean checkReady() {
        return (_allDisconnected == false);
    }

    @Override
    public void notReady() {
        int                nextSession = 0;
        NonBlockingSession sess;

        while( nextSession < _sessions.length ) {
            sess = _sessions[ nextSession++ ];

            if ( sess.getSessionState() == RecoverableSession.SessionState.Connected ) {
                _allDisconnected = false;
            } else {
                if ( _connectors.get( sess ) == null ) {
                    nonBlockingConnect( sess );
                }
            }
        }
    }

    @Override
    public void stop() {
        if ( _stopping.compareAndSet( false, true ) ) {
            setRunState( RunState.Stopping );

            _rlog.info( "MultiSessionThreadedReceiver " + getComponentId() + " stopping" );

            _ctlr.setStopping( true );

            Collection<SocketConnector> connSet    = _connectors.values();
            SocketConnector[]           connectors = connSet.toArray( new SocketConnector[ 0 ] );

            for ( SocketConnector conn : connectors ) {
                conn.getSession().stop();

                try {
                    conn.interrupt();
                } catch( Exception e ) {
                    // dont care
                }
            }
        }
    }

    @Override
    public String info() {
        return "MultiSessionThreadedReceiver( " + _id + " )";
    }

    public ControlThread getControlThread() {
        return _ctlr;
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

    private void nonBlockingConnect( NonBlockingSession session ) {
        if ( session != null ) {
            _rlog.info( "nonBlockingConnect session : " + session.getComponentId() + " state is " + session.getSessionState() );
            if ( session.getSessionState() == RecoverableSession.SessionState.Disconnected ) {
                if ( session.getConfig().isOpenUTC( ClockFactory.get().currentTimeMillis() ) && !session.isStopping() && !session.isPaused() ) {
                    synchronized( _connectors ) {
                        if ( _connectors.get( session ) == null ) {
                            _rlog.info( "nonBlockingConnect connect thread started for : " + session.getComponentId() );
                            SocketConnector conn = new SocketConnector( session );
                            _connectors.put( session, conn );
                            conn.start();
                        } else {
                            _rlog.info( "nonBlockingConnect connect request ignored as already queued : " + session.getComponentId() );
                        }
                    }
                }
            }
        }
    }

    private void updateDisconnectedStatus() {
        int                nextSession = 0;
        NonBlockingSession sess;

        while( nextSession < _sessions.length ) {
            sess = _sessions[ nextSession ];

            if ( sess.getSessionState() == RecoverableSession.SessionState.Connected ) {
                _allDisconnected = false;
                return;
            }

            ++nextSession;
        }

        _allDisconnected = true;
    }

    private final class SocketConnector extends Thread {

        private final NonBlockingSession _session;

        SocketConnector( final NonBlockingSession session ) {
            super( "SocketConnector-" + session.getComponentId() );
            setDaemon( true );
            _session = session;
        }

        @Override
        public void run() {
            ThreadUtilsFactory.get().setPriority( this, ThreadPriority.Other );
            _rlog.info( "MultiSession ConnectorThread STARTED : " + _session.getComponentId() );

            try {
                long start = ClockFactory.get().currentTimeMillis();

                _session.internalConnect();

                long duration = ClockFactory.get().currentTimeMillis() - start;

                _rlog.info( "MultiSession ConnectorThread END " + _session.getComponentId() + ", status=" + _session.getSessionState().toString() +
                            ", duration=" + duration );

                if ( _session.isConnected() ) _allDisconnected = false;

                _ctlr.statusChange(); // Mem barrier

            } catch( Exception e ) { // shouldnt be possible
                _rlog.error( ERR_INT_CONN, " MultiSession ConnectorThread ERROR : " + _session.getComponentId() + " " + e.getMessage(), e );
            } finally {
                synchronized( _connectors ) {
                    _connectors.remove( _session );
                }
            }
        }

        public NonBlockingSession getSession() {
            return _session;
        }
    }
}

