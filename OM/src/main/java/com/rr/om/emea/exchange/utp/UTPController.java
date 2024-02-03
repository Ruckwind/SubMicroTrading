/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.utp;

import com.rr.core.session.socket.SeqNumSession;
import com.rr.om.session.state.SessionSeqNumController;

public final class UTPController extends SessionSeqNumController {

    public UTPController( SeqNumSession session, UTPSocketConfig config ) {
        super( session, new UTPStateFactory( config ), new UTPSessionFactory( config ) );
    }
}