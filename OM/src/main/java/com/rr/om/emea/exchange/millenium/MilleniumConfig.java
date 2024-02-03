/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.millenium;

import com.rr.core.lang.ZString;
import com.rr.core.session.socket.SessionControllerConfig;

public interface MilleniumConfig extends SessionControllerConfig {

    ZString getNewPassword();

    ZString getPassword();

    ZString getUserName();

}
