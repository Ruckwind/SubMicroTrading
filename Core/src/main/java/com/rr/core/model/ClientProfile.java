/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.component.SMTComponentWithPostConstructHook;

public interface ClientProfile extends SMTComponentWithPostConstructHook {

    int  DEFAULT_LOW_THRESHOLD  = 60;
    int  DEFAULT_MED_THRESHOLD  = 80;
    int  DEFAULT_HIGH_THRESHOLD = 90;
    long DEFAULT_MAX_TOTAL_QTY  = Long.MAX_VALUE;
    long DEFAULT_MAX_TOTAL_VAL  = Long.MAX_VALUE;
    long DEFAULT_MAX_SINGLE_VAL = Long.MAX_VALUE;
    int  DEFAULT_MAX_SINGLE_QTY = Integer.MAX_VALUE;

    @Override String id();

    double getMaxSingleOrderQty();

    void setMaxSingleOrderQty( double maxSingleOrderQty );

    double getMaxSingleOrderValueUSD();

    void setMaxSingleOrderValueUSD( double maxSingleOrderValueUSD );

    double getMaxTotalOrderValueUSD();

    void setMaxTotalOrderValueUSD( double maxTotalValueUSD );

    double getMaxTotalQty();

    void setMaxTotalQty( double maxTotalQty );

    double getTotalOrderQty();

    double getTotalOrderValueUSD();

    double getTotalQty();

    void setThresholds( int lowThresholdPercent, int medThresholdPercent, int highThresholdPercent );
}
