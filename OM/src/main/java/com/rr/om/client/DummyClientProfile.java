/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.client;

import com.rr.core.model.ClientProfile;

public class DummyClientProfile implements ClientProfile {

    private final String _dummyId = "dummyId";

    @Override
    public String getComponentId() {
        return "DummyClientProfile";
    }

    @Override public String id()                                                                                      { return _dummyId; }

    @Override
    public double getMaxSingleOrderQty() {
        return 0;
    }

    @Override public void setMaxSingleOrderQty( double maxSingleOrderQty )                                            { /* nothing */ }

    @Override
    public double getMaxSingleOrderValueUSD() {
        return 0;
    }

    @Override public void setMaxSingleOrderValueUSD( double maxSingleOrderValueUSD )                                  { /* nothing */ }

    @Override
    public double getMaxTotalOrderValueUSD() {
        return 0;
    }

    @Override public void setMaxTotalOrderValueUSD( double maxTotalValueUSD )                                         { /* nothing */ }

    @Override
    public double getMaxTotalQty() {
        return 0;
    }

    @Override public void setMaxTotalQty( double maxTotalQty )                                                        { /* nothing */ }

    @Override
    public double getTotalOrderQty() {
        return 0;
    }

    @Override
    public double getTotalOrderValueUSD() {
        return 0;
    }

    @Override
    public double getTotalQty() {
        return 0;
    }

    @Override public void setThresholds( int lowThresholdPercent, int medThresholdPercent, int highThresholdPercent ) { /* nothing */ }

    @Override public void postConstruction() { /* nothing */ }

}
