/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket;

import com.rr.core.session.socket.SeqNumSession;
import com.rr.om.session.state.SessionSeqNumController;

public final class FixController extends SessionSeqNumController {

    public FixController( SeqNumSession session, FixConfig config ) {
        super( session, new FixStateFactory( config ), new FixSessionFactory( config ) );
    }
}