package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.OrdRejReason;
import com.rr.model.generated.internal.type.TradingStatus;
import com.rr.model.internal.type.ExecType;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.OrderCapacity;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface RejectedWrite extends CommonExecRptWrite, Rejected {

   // Getters and Setters
    void setOrigClOrdId( byte[] buf, int offset, int len );
    ReusableString getOrigClOrdIdForUpdate();

    void setOrdRejReason( OrdRejReason val );

    void setTradingStatus( TradingStatus val );

}
