/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.utp;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.session.RecoverableSession;
import com.rr.core.session.socket.SeqNumSession;
import com.rr.core.session.socket.SessionStateException;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.interfaces.UTPLogon;
import com.rr.om.session.state.SessionState;

public class UTPLoggedOutState implements SessionState {

    private static final ZString NOT_LOGGED_IN = new ViewString( "Recieved unexpected message during logon process" );

    private final RecoverableSession _session;
    private final UTPController      _controller;
    private final UTPSocketConfig    _config;

    private final ReusableString _logMsg = new ReusableString( 100 );
    private final ReusableString _logMsgBase;

    public UTPLoggedOutState( SeqNumSession session, UTPController sessionController, UTPSocketConfig config ) {
        _session    = session;
        _controller = sessionController;
        _logMsgBase = new ReusableString( "[LoggedOut-" + _session.getComponentId() + "] " );
        _config     = config;
    }

    @Override
    public void connected() {
        if ( !_controller.isServer() ) {
            // socket is connected and as this session is not the server need initiate logon
            sendLogon();
        }
    }

    @Override
    public void handle( Event msg ) throws SessionStateException {
        if ( msg.getReusableType().getSubId() == EventIds.ID_UTPLOGON ) {

            final UTPLogon req                = (UTPLogon) msg;
            int            lastSeenSentSeqNum = req.getMsgSeqNum();

            if ( lastSeenSentSeqNum > 0 ) --lastSeenSentSeqNum;
            if ( _controller.isServer() ) sendLogon();

            // check to see if other side missing msgs from us, if so need to send them
            if ( lastSeenSentSeqNum < lastSeenSentSeqNum ) {
                _controller.sendMissingMsgsToClientNow( lastSeenSentSeqNum + 1 );
            }

            // DONT START HB TIMER BEFORE RESYNC FINISHED
            _controller.startHeartbeatTimer( _config.getHeartBeatIntSecs() );
            _controller.changeState( _controller.getStateLoggedIn() );

        } else {
            _logMsg.copy( _logMsgBase ).append( NOT_LOGGED_IN ).append( msg.getReusableType().toString() );

            _session.inboundRecycle( msg );

            throw new SessionStateException( _logMsg.toString() );
        }
    }

    private void sendLogon() {
        int lastReceivedMsg = _controller.getNextExpectedInSeqNo();
        if ( lastReceivedMsg > 0 ) --lastReceivedMsg;
        _controller.sendLogonNow( _config.getHeartBeatIntSecs(), lastReceivedMsg );
    }
}
