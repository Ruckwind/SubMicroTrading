package com.rr.sess.soupbin;

import com.rr.core.session.socket.SeqNumSession;
import com.rr.om.session.state.SessionSeqNumController;

public class SoupBinSessionController extends SessionSeqNumController {

    public SoupBinSessionController( SeqNumSession session, SoupBinSocketConfig config ) {
//        super( session, new FixStateFactory( config ), new FixSessionFactory( config ) );
        super( null, null, null );
    }

}
