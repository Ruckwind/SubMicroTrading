/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.millenium.recovery;

import com.rr.core.session.socket.SeqNumSession;
import com.rr.om.emea.exchange.millenium.MilleniumController;
import com.rr.om.emea.exchange.millenium.MilleniumLoggedOnState;
import com.rr.om.emea.exchange.millenium.MilleniumSocketConfig;
import com.rr.om.session.state.SessionController;
import com.rr.om.session.state.SessionState;
import com.rr.om.session.state.SessionStateFactory;

public class MilleniumRecoveryStateFactory implements SessionStateFactory {

    private final MilleniumSocketConfig _config;

    public MilleniumRecoveryStateFactory( MilleniumSocketConfig config ) {
        _config = config;
    }

    @Override
    public SessionState createLoggedOnState( SeqNumSession session, SessionController<?> sessionController ) {
        return new MilleniumLoggedOnState( session, (MilleniumController) sessionController );
    }

    @Override
    public SessionState createLoggedOutState( SeqNumSession session, SessionController<?> sessionController ) {
        return new MilleniumRecoveryLoggedOutState( session, (MilleniumRecoveryController) sessionController, _config );
    }

    @Override
    public SessionState createSynchroniseState( SeqNumSession session, SessionController<?> sessionController ) {
        return new MilleniumRecoverySynchroniseState( session, (MilleniumRecoveryController) sessionController );
    }

}
