/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket;

import com.rr.core.admin.AdminCommand;

public interface FixSessionMBean extends AdminCommand {

    void setNextExpectedSeqNums( int nextInSeqNum, int nextOutSeqNum );
}
