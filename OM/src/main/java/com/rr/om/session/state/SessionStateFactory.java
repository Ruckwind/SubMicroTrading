/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.state;

import com.rr.core.session.socket.SeqNumSession;

public interface SessionStateFactory {

    SessionState createLoggedOnState( SeqNumSession session, SessionController<?> sessionController );

    SessionState createLoggedOutState( SeqNumSession session, SessionController<?> sessionController );

    SessionState createSynchroniseState( SeqNumSession session, SessionController<?> sessionController );

}
