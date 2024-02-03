/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.utp;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.session.RecoverableSession;
import com.rr.core.session.socket.SeqNumSession;
import com.rr.core.session.socket.SessionStateException;
import com.rr.om.session.state.SessionState;

public class UTPSynchroniseState implements SessionState {

    private static final Logger  _log           = LoggerFactory.create( UTPSynchroniseState.class );
    private static final ZString BAD_SESS_STATE = new ViewString( "Invalid session state, UTP doesnt support synchronise state" );

    private final RecoverableSession _session;

    private final ReusableString _logMsg = new ReusableString( 100 );
    private final ReusableString _logMsgBase;

    public UTPSynchroniseState( SeqNumSession session, UTPController sessionController ) {
        _session    = session;
        _logMsgBase = new ReusableString( "[UTPSynchroniseState-" + _session.getComponentId() + "] " );
    }

    @Override
    public void handle( Event msg ) throws SessionStateException {
        // UTP doesnt have a sync session, shouldnt be possible to get here

        _logMsg.copy( _logMsgBase ).append( BAD_SESS_STATE ).append( msg.getReusableType().toString() );

        _session.inboundRecycle( msg );

        throw new SessionStateException( _logMsg.toString() );
    }

    @Override
    public void connected() {
        _log.warn( "Unexpected connected event when in synchronise mode" );
    }
}
