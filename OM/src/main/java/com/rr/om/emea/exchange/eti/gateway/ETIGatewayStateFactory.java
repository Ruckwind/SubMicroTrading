/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.eti.gateway;

import com.rr.core.session.socket.SeqNumSession;
import com.rr.om.emea.exchange.eti.trading.ETISocketConfig;
import com.rr.om.session.state.SessionController;
import com.rr.om.session.state.SessionState;
import com.rr.om.session.state.SessionStateFactory;

public class ETIGatewayStateFactory implements SessionStateFactory {

    private final ETISocketConfig _config;

    public ETIGatewayStateFactory( ETISocketConfig config ) {
        _config = config;
    }

    @Override
    public SessionState createLoggedOutState( SeqNumSession session, SessionController<?> sessionController ) {
        return new ETIGatewayLoggedOutState( session, (ETIGatewayController) sessionController, _config );
    }

    @Override
    public SessionState createLoggedOnState( SeqNumSession session, SessionController<?> sessionController ) {
        return null;
    }

    @Override
    public SessionState createSynchroniseState( SeqNumSession session, SessionController<?> sessionController ) {
        return null;
    }
}
