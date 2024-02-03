/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

public interface PriceToleranceLimits {

    double getDynamicForClosing();

    // dynamic limits
    double getDynamicForContinuous();

    double getDynamicForIntraday();

    double getStaticForClosing();

    double getStaticForContinuous();

    double getStaticForIntraday();

    // static limits
    double getStaticForOpen();
}
