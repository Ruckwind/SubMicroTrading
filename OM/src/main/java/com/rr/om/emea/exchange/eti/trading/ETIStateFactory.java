/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.eti.trading;

import com.rr.core.session.socket.SeqNumSession;
import com.rr.om.session.state.SessionController;
import com.rr.om.session.state.SessionState;
import com.rr.om.session.state.SessionStateFactory;

public class ETIStateFactory implements SessionStateFactory {

    private final ETISocketConfig _config;

    public ETIStateFactory( ETISocketConfig config ) {
        _config = config;
    }

    @Override
    public SessionState createLoggedOutState( SeqNumSession session, SessionController<?> sessionController ) {
        return new ETILoggedOutState( session, (ETIController) sessionController, _config );
    }

    @Override
    public SessionState createLoggedOnState( SeqNumSession session, SessionController<?> sessionController ) {
        return new ETILoggedOnState( (ETISession) session, (ETIController) sessionController );
    }

    @Override
    public SessionState createSynchroniseState( SeqNumSession session, SessionController<?> sessionController ) {
        return new ETISyncState( (ETISession) session, (ETIController) sessionController );
    }
}
