/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session.socket;

import com.rr.core.model.FixVersion;

public interface FixControllerConfig extends SessionControllerConfig {

    FixVersion getFixVersion();

    /**
     * safest behaviour is disconnect on gap, otherwise send a gap fill request
     */
    boolean isDisconnectOnSeqGap();

    boolean isUseNewFix44GapFillProtocol();
}
