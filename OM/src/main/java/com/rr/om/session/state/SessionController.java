/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.state;

import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.NullEvent;
import com.rr.core.pool.SuperPool;
import com.rr.core.pool.SuperpoolManager;
import com.rr.core.session.RecoverableSession;
import com.rr.core.session.socket.SeqNumSession;
import com.rr.core.session.socket.SessionControllerConfig;
import com.rr.core.session.socket.SessionStateException;
import com.rr.core.tasks.CoreScheduledEvent;
import com.rr.core.tasks.ScheduledEvent;
import com.rr.core.tasks.Scheduler;
import com.rr.core.tasks.SchedulerFactory;
import com.rr.model.generated.internal.events.factory.HeartbeatFactory;
import com.rr.model.generated.internal.events.impl.HeartbeatImpl;

public abstract class SessionController<T_SESSION_FACTORY extends StatefulSessionFactory> {

    protected static final ErrorCode ERR_MISSED_HB = new ErrorCode( "SCT100", "Missed heartbeats" );
    protected final SeqNumSession           _session;
    protected final SessionControllerConfig _controllerConfig;
    protected final T_SESSION_FACTORY       _sessionFactory;
    private final Logger _log = LoggerFactory.create( SessionController.class );
    private final HeartbeatActor        _heartbeatActor;
    private final MissingHeartbeatActor _missHBActor;
    private SessionState _loggedOutState;
    private SessionState _loggedInState;
    private SessionState _synchroniseState;

    private SessionState _state;

    private          long _heartbeatBreakTimeMS = Long.MAX_VALUE;
    private          long _lastRecvMsgMS        = 0;
    private volatile long _lastRecvHeartBeatMS  = Long.MAX_VALUE; // volatile as read from a timer thread

    public SessionController( SeqNumSession session, SessionStateFactory stateFactory, T_SESSION_FACTORY msgFactory ) {
        _controllerConfig = session.getStateConfig();
        _session          = session;

        _sessionFactory = msgFactory;

        _loggedOutState   = stateFactory.createLoggedOutState( session, this );
        _loggedInState    = stateFactory.createLoggedOnState( session, this );
        _synchroniseState = stateFactory.createSynchroniseState( session, this );

        _heartbeatActor = new HeartbeatActor( "HB_" + session.getComponentId() );
        _missHBActor    = new MissingHeartbeatActor( "MHB_" + session.getComponentId() );

        _state = _loggedOutState;
    }

    public synchronized void changeState( SessionState toState ) {

        if ( toState != _state ) {

            _log.info( "SessionController change state from " + _state.getClass().getSimpleName() + " to " + toState.getClass().getSimpleName() +
                       " for " + _session.getComponentId() );

            if ( toState == _loggedOutState ) {
                onLogout();
            }

            _state = toState;

            _log.info( _session.getComponentId() + " " + info() );

            // WAKE UP QUEUES

            _session.handle( new NullEvent( _session ) );
            _session.handleForSync( new NullEvent( _session ) );
        }
    }

    public void connected() {
        _state.connected();
    }

    public void enqueueHeartbeat( ZString testReqID ) {
        Event hb = _sessionFactory.getHeartbeat( testReqID );
        hb.setEventHandler( _session );
        _session.handle( hb );
    }

    public SessionControllerConfig getControllerConfig() {
        return _controllerConfig;
    }

    public SessionState getStateLoggedIn() {
        return _loggedInState;
    }

    public SessionState getStateLoggedOut() {
        return _loggedOutState;
    }

    public SessionState getStateSynchronise() {
        return _synchroniseState;
    }

    public void handle( Event msg ) throws SessionStateException {
        _lastRecvMsgMS = ClockFactory.get().currentTimeMillis();
        _state.handle( msg );
    }

    public String info() {
        return ", state=" + _state.getClass().getSimpleName();
    }

    public boolean isLoggedIn() {
        return _state == _loggedInState;
    }

    public boolean isLoggedOut() {
        return _state == _loggedOutState;
    }

    public boolean isReconnectOnLogout() {
        if ( isServer() ) return false;

        return _controllerConfig.isReconnectOnLogout();
    }

    public boolean isServer() {
        return _session.getStateConfig().isServer();
    }

    public String logInboundRecoveryFinished() {
        return "";
    }

    public String logOutboundRecoveryFinished() {
        return "";
    }

    public abstract void outboundError();

    public void persistPosDupMsg( int newSeqNum ) {
        _session.persistLastInboundMesssage();
    }

    public abstract void recoverContext( Event msg, boolean inBound );

    public void reset() {
        _log.info( "SessionController.reset " + _session.getComponentId() );

        _lastRecvHeartBeatMS  = Long.MAX_VALUE;
        _heartbeatBreakTimeMS = Long.MAX_VALUE;
    }

    public void setHeartbeatReceived() {
        _lastRecvHeartBeatMS = ClockFactory.get().currentTimeMillis();
    }

    public void startHeartbeatTimer( int heartBtIntSecs ) {

        _heartbeatBreakTimeMS = heartBtIntSecs * 3000; // upto 3 missed HB fine

        _lastRecvHeartBeatMS = ClockFactory.get().currentTimeMillis();

        long nextFireMS = (heartBtIntSecs * 1000);

        SchedulerFactory.get().registerIndividualRepeating( CoreScheduledEvent.Heartbeat, _heartbeatActor, nextFireMS, nextFireMS );
        SchedulerFactory.get().registerIndividualRepeating( CoreScheduledEvent.Heartbeat, _missHBActor, nextFireMS + 100, nextFireMS + 100 );
    }

    public void stop() {
        // forced stop, close any extra resources
    }

    protected Event formLogoutMessage() {
        Event lo = _sessionFactory.getLogOut( ERR_MISSED_HB.getError(), 0, null, 0, 0 );
        return lo;
    }

    protected final RecoverableSession getSession() {
        return _session;
    }

    protected void onLogout() {
        // for specialisation hooks
    }

    protected final void send( Event msg, boolean now ) {
        if ( now ) {
            _session.handleNow( msg );
        } else {
            msg.setEventHandler( _session );
            if ( isLoggedIn() ) {
                _session.handle( msg );
            } else {
                _session.handleForSync( msg );
            }
        }
    }

    void checkMissingHeartbeat() {
        final long nowMS          = ClockFactory.get().currentTimeMillis();
        final long ageHeartbeatMS = nowMS - _lastRecvHeartBeatMS;
        final long ageLastMsgMS   = nowMS - _lastRecvMsgMS;

        // only disconnect if missed HBs and not getting active messages
        if ( ageHeartbeatMS > _heartbeatBreakTimeMS && ageLastMsgMS > _heartbeatBreakTimeMS ) {
            if ( _controllerConfig.isDisconnectOnMissedHB() ) {
                _log.error( ERR_MISSED_HB, _session.getComponentId() );

                Event lo = formLogoutMessage();
                lo.setEventHandler( _session );

                _session.handle( lo );

                _session.disconnect( true );

            } else {
                _log.error( ERR_MISSED_HB, _session.getComponentId() );
            }
        }
    }

    String getState() {
        return _state.getClass().getSimpleName();
    }

    private final class HeartbeatActor implements Scheduler.Callback {

        private final SuperPool<HeartbeatImpl> _heartbeatPool    = SuperpoolManager.instance().getSuperPool( HeartbeatImpl.class );
        private final HeartbeatFactory         _heartbeatFactory = new HeartbeatFactory( _heartbeatPool );

        private final ZString        _name;
        private final ReusableString _msg = new ReusableString( 50 );

        public HeartbeatActor( String name ) {
            _name = new ViewString( name );
        }

        @Override
        public void event( final ScheduledEvent event ) {

            if ( isLoggedOut() ) {

                SchedulerFactory.get().cancelIndividual( CoreScheduledEvent.Heartbeat, this );

                return;
            }

            // logged in or syncing

            final HeartbeatImpl hb = _heartbeatFactory.get();
            hb.setEventHandler( _session );

            _msg.copy( _name ).append( " fired " ).append( ", loggedIn=" ).append( isLoggedIn() );
            _log.info( _msg );

            if ( isLoggedIn() ) {
                _session.handle( hb );
            } else {
                _session.handleForSync( hb );
            }

            _msg.copy( _name ).append( " fired HB dispatched " ).append( ", loggedIn=" ).append( isLoggedIn() );
            _log.info( _msg );
        }

        @Override
        public ZString getName() {
            return _name;
        }
    }

    private final class MissingHeartbeatActor implements Scheduler.Callback {

        private final ZString _name;

        public MissingHeartbeatActor( String name ) {
            _name = new ViewString( name );
        }

        @Override
        public void event( final ScheduledEvent event ) {

            if ( isLoggedOut() ) {

                SchedulerFactory.get().cancelIndividual( CoreScheduledEvent.Heartbeat, this );

                return;
            }
            // logged in or syncing
            checkMissingHeartbeat();
        }

        @Override
        public ZString getName() {
            return _name;
        }
    }
}