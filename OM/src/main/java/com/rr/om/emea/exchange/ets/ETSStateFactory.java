/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.ets;

import com.rr.core.session.socket.SeqNumSession;
import com.rr.om.session.state.SessionController;
import com.rr.om.session.state.SessionState;
import com.rr.om.session.state.SessionStateFactory;

public class ETSStateFactory implements SessionStateFactory {

    private final ETSSocketConfig _config;

    public ETSStateFactory( ETSSocketConfig config ) {
        _config = config;
    }

    @Override
    public SessionState createLoggedOnState( SeqNumSession session, SessionController<?> sessionController ) {
        return new ETSLoggedOnState( session, (ETSController) sessionController );
    }

    @Override
    public SessionState createLoggedOutState( SeqNumSession session, SessionController<?> sessionController ) {
        return new ETSLoggedOutState( session, (ETSController) sessionController, _config );
    }

    @Override
    public SessionState createSynchroniseState( SeqNumSession session, SessionController<?> sessionController ) {
        return new ETSSynchroniseState( session, (ETSController) sessionController );
    }

}
