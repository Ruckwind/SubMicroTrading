/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.ets;

import com.rr.core.session.socket.SeqNumSession;
import com.rr.om.session.state.SessionSeqNumController;

public final class ETSController extends SessionSeqNumController {

    public ETSController( SeqNumSession session, ETSSocketConfig config ) {
        super( session, new ETSStateFactory( config ), new ETSSessionFactory( config ) );
    }
}