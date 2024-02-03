/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.eti.trading;

import com.rr.codec.emea.exchange.eti.ETIDecodeContext;
import com.rr.core.session.SeperateGatewaySession;
import com.rr.core.session.socket.SeqNumSession;

public interface ETISession extends SeqNumSession, SeperateGatewaySession {

    ETIDecodeContext getDecodeContext();

}
