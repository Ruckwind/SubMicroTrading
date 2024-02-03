/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.sess.soupbin;

import com.rr.core.session.socket.SeqNumSession;
import com.rr.om.session.state.SessionController;
import com.rr.om.session.state.SessionState;
import com.rr.om.session.state.SessionStateFactory;

public class SoupBinStateFactory implements SessionStateFactory {

    private final SoupBinSocketConfig _config;

    public SoupBinStateFactory( SoupBinSocketConfig config ) {
        _config = config;
    }

    @Override
    public SessionState createLoggedOutState( SeqNumSession session, SessionController<?> sessionController ) {

//        return new LoggedOutFixState( session, (FixController)sessionController, _config );
        return null;
    }

    @Override
    public SessionState createLoggedOnState( SeqNumSession session, SessionController<?> sessionController ) {

//        return new LoggedOnFixState( session, (FixController)sessionController, _config );
        return null;
    }

    @Override
    public SessionState createSynchroniseState( SeqNumSession session, SessionController<?> sessionController ) {

//        return new SynchroniseFixState( session, (FixController)sessionController );

        return null;
    }
}
