/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.session.fixsocket;

import com.rr.core.lang.ZString;
import com.rr.core.session.socket.FixControllerConfig;

public interface FixConfig extends FixControllerConfig {

    ZString getEncryptMethod();

    ZString getPassword();

    ZString getRawData();

    ZString getSenderCompId();

    ZString getSenderLocationId();

    ZString getSenderSubId();

    ZString getTargetCompId();

    void setTargetCompId( ZString targetCompId );

    ZString getTargetSubId();

    ZString getUserName();
}
