/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.state;

import com.rr.core.admin.AdminCommand;

public interface SessionManagerAdminMBean extends AdminCommand {

    String listAllSessions();

    String listClientSessions();

    String listExchangeSessions();

    String loginSession( String sessionName );

    String logoutSession( String sessionName );

    String pauseSession( String sessionName );

    String resumeSession( String sessionName );

    String safeResetSeqNums( String sessionName );

    String setSessionSeqNums( String sessionName, int nextInSeqNum, int nextOutSeqNum, boolean passiveReset );

}
