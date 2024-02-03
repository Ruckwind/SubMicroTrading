package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.LiquidityInd;
import com.rr.model.generated.internal.type.MultiLegReportingType;
import com.rr.model.internal.type.ExecType;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.OrderCapacity;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface TradeCorrectWrite extends TradeBaseWrite, TradeCorrect {

   // Getters and Setters
    void setExecRefID( byte[] buf, int offset, int len );
    ReusableString getExecRefIDForUpdate();

    void setOrigQty( double val );

    void setOrigPx( double val );

}
