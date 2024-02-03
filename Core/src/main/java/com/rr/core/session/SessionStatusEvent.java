package com.rr.core.session;

import com.rr.core.lang.*;
import com.rr.core.model.BaseEvent;

public final class SessionStatusEvent extends BaseEvent<SessionStatusEvent> {

    ReusableString     _connId       = new ReusableString( 20 );
    int                _sessionIntId = Constants.UNSET_INT;
    SessionStatus      _status       = SessionStatus.UNKNOWN;
    SessionStatusEvent _next;

    @Override public void dump( final ReusableString out ) {

        out.append( "SessionStatusEvent: from " ).append( (getEventHandler() != null) ? getEventHandler().getComponentId() : "null" )
           .append( ", srcId=" ).append( getSessionIntId() ).append( ", status=" ).append( _status );
    }

    @Override public ReusableType getReusableType() {
        return CoreReusableType.SessionStatusEvent;
    }

    @Override public void reset() {
        super.reset();
        _status       = SessionStatus.UNKNOWN;
        _sessionIntId = Constants.UNSET_INT;
        _connId.reset();
        _next = null;
    }

    public ReusableString getConnId() { return _connId; }

    public int getSessionIntId() {
        return _sessionIntId;
    }

    public SessionStatus getStatus() {
        return _status;
    }

    public SessionStatusEvent set( int sessionId, SessionStatus status, ZString connId ) {
        _sessionIntId = sessionId;
        _status       = status;
        _connId.copy( connId );
        setEventTimestamp( Constants.UNSET_LONG );
        return this;
    }
}

