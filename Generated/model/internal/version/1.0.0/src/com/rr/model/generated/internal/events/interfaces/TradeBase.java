package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.LiquidityInd;
import com.rr.model.generated.internal.type.MultiLegReportingType;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface TradeBase extends CommonExecRpt, Event {

   // Getters and Setters
    double getLastQty();

    double getLastPx();

    LiquidityInd getLiquidityInd();

    MultiLegReportingType getMultiLegReportingType();

    ViewString getLastMkt();

    ViewString getSecurityDesc();

    @Override void dump( ReusableString out );

}
