package com.rr.sess.soupbin;

import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.om.session.state.StatefulSessionFactory;

public class SoupBinSessionFactory implements StatefulSessionFactory {

    @Override public Event getHeartbeat( final ZString testReqID ) {
        return null;
    }

    @Override public Event getSessionLogOn( final int heartBtInt, final int nextOutSeqNum, final int nextExpInSeqNum ) {
        return null;
    }

    @Override public Event createGapFillMessage( final int gapBeginSeqNo, final int curMsgSeqNo ) {
        return null;
    }

    @Override public Event createForceSeqNumResetMessage( final int nextMsgSeqNoOut ) {
        return null;
    }

    @Override public Event getLogOut( final ZString logMsg, final int code, final Event logon, final int nextOutSeqNum, final int nextExpectedInSeqNum ) {
        return null;
    }

    @Override public Event getResendRequest( final int fromSeqNum, final int toSeqNum ) {
        return null;
    }
}
