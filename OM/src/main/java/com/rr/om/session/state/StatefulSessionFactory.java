/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.state;

import com.rr.core.lang.ZString;
import com.rr.core.model.Event;

public interface StatefulSessionFactory {

    Event createForceSeqNumResetMessage( int nextMsgSeqNoOut );

    Event createGapFillMessage( int gapBeginSeqNo, int curMsgSeqNo );

    Event getHeartbeat( ZString testReqID );

    Event getLogOut( ZString logMsg, int code, Event logon, int nextOutSeqNum, int nextExpectedInSeqNum );

    Event getResendRequest( int fromSeqNum, int toSeqNum );

    Event getSessionLogOn( int heartBtInt, int nextOutSeqNum, int nextExpInSeqNum );
}
