/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.utp;

import com.rr.core.session.socket.SeqNumSession;
import com.rr.om.session.state.SessionController;
import com.rr.om.session.state.SessionState;
import com.rr.om.session.state.SessionStateFactory;

public class UTPStateFactory implements SessionStateFactory {

    private final UTPSocketConfig _config;

    public UTPStateFactory( UTPSocketConfig config ) {
        _config = config;
    }

    @Override
    public SessionState createLoggedOutState( SeqNumSession session, SessionController<?> sessionController ) {
        return new UTPLoggedOutState( session, (UTPController) sessionController, _config );
    }

    @Override
    public SessionState createLoggedOnState( SeqNumSession session, SessionController<?> sessionController ) {
        return new UTPLoggedOnState( session, (UTPController) sessionController );
    }

    @Override
    public SessionState createSynchroniseState( SeqNumSession session, SessionController<?> sessionController ) {
        return new UTPSynchroniseState( session, (UTPController) sessionController );
    }

}
