/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.session;

import com.rr.core.lang.ErrorCode;

public interface SessionConstants {

    int CONNECT_WAIT_DELAY_MS = 5000;              // wait for 5secs then check if reconnected
    int DEFAULT_MAX_DELAY_MS  = 1000 * 60 * 15;    // max reconnect delay 15min

    int DEFAULT_MAX_CONNECT_ATTEMPTS = 10;
    int MAX_CONNECT_ATTEMPTS         = 60 * 24 * 7;       // once a min for week

    ErrorCode ERR_MAX_ATT_EXCEEDED = new ErrorCode( "SEC100", "Exceeded max connect attempts, pausing session which will " +
                                                              "require manual restart : " );
    ErrorCode ERR_OPEN_SOCK        = new ErrorCode( "SEC200", "Error opening socket: " );
}
