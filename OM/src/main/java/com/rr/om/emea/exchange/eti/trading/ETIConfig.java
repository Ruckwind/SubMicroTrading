/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.eti.trading;

import com.rr.core.lang.ZString;
import com.rr.core.session.socket.SessionControllerConfig;
import com.rr.model.generated.internal.type.ETIEnv;
import com.rr.model.generated.internal.type.ETISessionMode;

public interface ETIConfig extends SessionControllerConfig {

    String VERSION = "0.9";

    int DEFAULT_THROTTLE_MSGS      = 1024;       // rounded down to closest power of 2
    int DEFAULT_THROTTLE_PERIOD_MS = 1000;

    ZString getAppSystemName();

    ETISessionMode getETISessionMode();

    ZString getETIVersion();

    /**
     * methods for emulating exchange
     */

    int getEmulationTestHost();

    int getEmulationTestPort();

    ETIEnv getEnv();

    long getLocationId();

    // session logon
    int getPartyIDSessionID();

    ZString getPassword();

    ZString getSessionLogonPassword();

    ZString getTraderPassword();

    // for connection gateway login
    int getUserId();

    boolean isForceTradingServerLocalhost();

}
