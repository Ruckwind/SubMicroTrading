/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.ets;

import com.rr.core.lang.ZString;
import com.rr.core.session.socket.SessionControllerConfig;

public interface ETSConfig extends SessionControllerConfig {

    ZString getUserName();

}
