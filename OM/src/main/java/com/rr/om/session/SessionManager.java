/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session;

import com.rr.core.admin.AdminAgent;
import com.rr.core.component.SMTComponent;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Exchange;
import com.rr.core.session.RecoverableSession;
import com.rr.core.session.SessionDirection;
import com.rr.om.session.state.SessionManagerAdmin;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SessionManager implements SMTComponent {

    private static final Logger _log = LoggerFactory.create( SessionManager.class );

    private final Set<RecoverableSession> _upStream   = new LinkedHashSet<>();
    private final Set<RecoverableSession> _downStream = new LinkedHashSet<>();
    private final Set<RecoverableSession> _otherSess  = new LinkedHashSet<>();
    private final Map<RecoverableSession, Exchange> _sessToExchange = new HashMap<>();
    private       String                  _id;
    private       RecoverableSession      _hub        = null;

    public SessionManager( String id ) {
        _id = id;
        SessionManagerAdmin sma = new SessionManagerAdmin( this );
        AdminAgent.register( sma );
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    public void add( RecoverableSession sess, boolean isDownstrem ) {
        if ( isDownstrem ) {
            _log.info( "SessionManager.add() DOWNSTREAM " + sess.info() );
            _downStream.add( sess );
            sess.getConfig().setDirection( SessionDirection.Downstream );
        } else {
            _log.info( "SessionManager.add() UPSTREAM " + sess.info() );
            _upStream.add( sess );
            sess.getConfig().setDirection( SessionDirection.Upstream );
        }
    }

    public void add( RecoverableSession sess ) {
        _log.info( "SessionManager.add() OTHER  " + sess.info() );
        _otherSess.add( sess );
    }

    public void associateExchange( RecoverableSession sess, Exchange e ) {
        _sessToExchange.put( sess, e );
    }

    public RecoverableSession[] getDownStreamSessions() {
        return _downStream.toArray( new RecoverableSession[ 0 ] );
    }

    public Exchange getExchange( RecoverableSession sess ) {
        return _sessToExchange.get( sess );
    }

    public RecoverableSession getHub() {
        return _hub;
    }

    public void setHub( RecoverableSession hub ) {
        _log.info( "SessionManager.setHub() HUB " + hub.info() );
        _hub = hub;
    }

    public RecoverableSession[] getOtherSessions() {
        return _otherSess.toArray( new RecoverableSession[ 0 ] );
    }

    public RecoverableSession getSession( String sessionName ) {
        if ( sessionName == null ) return null;

        for ( RecoverableSession s : _upStream ) {
            if ( s.getComponentId().equalsIgnoreCase( sessionName ) ) {
                return s;
            }
        }

        for ( RecoverableSession s : _downStream ) {
            if ( s.getComponentId().equalsIgnoreCase( sessionName ) ) {
                return s;
            }
        }

        if ( _hub != null && _hub.getComponentId().equalsIgnoreCase( sessionName ) ) {
            return _hub;
        }

        return null;
    }

    public RecoverableSession[] getUpStreamSessions() {
        return _upStream.toArray( new RecoverableSession[ 0 ] );
    }
}
