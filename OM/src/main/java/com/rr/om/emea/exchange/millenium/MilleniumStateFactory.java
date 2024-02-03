/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.millenium;

import com.rr.core.session.socket.SeqNumSession;
import com.rr.om.session.state.SessionController;
import com.rr.om.session.state.SessionState;
import com.rr.om.session.state.SessionStateFactory;

public class MilleniumStateFactory implements SessionStateFactory {

    private final MilleniumSocketConfig _config;

    public MilleniumStateFactory( MilleniumSocketConfig config ) {
        _config = config;
    }

    @Override
    public SessionState createLoggedOutState( SeqNumSession session, SessionController<?> sessionController ) {
        return new MilleniumLoggedOutState( session, (MilleniumController) sessionController, _config );
    }

    @Override
    public SessionState createLoggedOnState( SeqNumSession session, SessionController<?> sessionController ) {
        return new MilleniumLoggedOnState( session, (MilleniumController) sessionController );
    }

    @Override
    public SessionState createSynchroniseState( SeqNumSession session, SessionController<?> sessionController ) {
        return null; // recovery via recovery controller
    }
}
