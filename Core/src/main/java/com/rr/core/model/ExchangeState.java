/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

public enum ExchangeState {

    PreOpen,
    OpeningAuction,
    Continuous,
    IntradayAuction,
    ClosingAuction,
    PostClose,
    Closed,
    Unknown
}
