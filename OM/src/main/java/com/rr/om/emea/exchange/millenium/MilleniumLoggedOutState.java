/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.millenium;

import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.session.RecoverableSession;
import com.rr.core.session.socket.SeqNumSession;
import com.rr.core.session.socket.SessionStateException;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.interfaces.MilleniumLogonReply;
import com.rr.model.generated.internal.events.interfaces.MilleniumLogout;
import com.rr.om.session.state.SessionState;

public class MilleniumLoggedOutState implements SessionState {

    private static final Logger _log = LoggerFactory.create( MilleniumLoggedOutState.class );

    private static final ZString   NOT_LOGGED_IN  = new ViewString( "Recieved unexpected message during logon process" );
    private static final ErrorCode LOGON_REJECTED = new ErrorCode( "MLO100", "Logon Rejected" );
    private static final ErrorCode LOGOUT         = new ErrorCode( "MLO200", "" );

    private final RecoverableSession    _session;
    private final MilleniumController   _controller;
    private final MilleniumSocketConfig _config;

    private final ReusableString _logMsg = new ReusableString( 100 );
    private final ReusableString _logMsgBase;

    public MilleniumLoggedOutState( SeqNumSession session, MilleniumController sessionController, MilleniumSocketConfig config ) {
        _session    = session;
        _controller = sessionController;
        _logMsgBase = new ReusableString( "[LoggedOut-" + _session.getComponentId() + "] " );
        _config     = config;
    }

    @Override
    public void connected() {
        if ( !_controller.isServer() ) {
            // socket is connected and as this session is not the server need initiate logon
            _controller.sendLogonNow();
        }
    }

    @Override
    public void handle( Event msg ) throws SessionStateException {

        if ( msg.getReusableType().getSubId() == EventIds.ID_MILLENIUMLOGON ) {

            if ( _controller.isServer() ) {
                _controller.sendLogonReplyNow( null, 0 );
                _controller.startHeartbeatTimer( _config.getHeartBeatIntSecs() );
                _controller.changeState( _controller.getStateLoggedIn() );
            }

        } else if ( msg.getReusableType().getSubId() == EventIds.ID_MILLENIUMLOGONREPLY ) {

            final MilleniumLogonReply rep = (MilleniumLogonReply) msg;

            if ( rep.getRejectCode() != 0 ) {
                _log.error( LOGON_REJECTED, "rejectCode=" + rep.getRejectCode() + ", expiryDayCount=" + rep.getPwdExpiryDayCount() );
                _session.disconnect( false );
            } else {
                _controller.initiateRecovery();
                _controller.startHeartbeatTimer( _config.getHeartBeatIntSecs() );

                // note allow trading even if not fully recovered

                _controller.changeState( _controller.getStateLoggedIn() );
            }

        } else if ( msg.getReusableType().getSubId() == EventIds.ID_MILLENIUMLOGOUT ) {
            final MilleniumLogout rep = (MilleniumLogout) msg;
            _log.error( LOGOUT, "reason=" + rep.getReason() );
            _session.disconnect( false );

        } else {
            _logMsg.copy( _logMsgBase ).append( NOT_LOGGED_IN ).append( msg.getReusableType().toString() );

            _session.inboundRecycle( msg );

            throw new SessionStateException( _logMsg.toString() );
        }
    }
}
