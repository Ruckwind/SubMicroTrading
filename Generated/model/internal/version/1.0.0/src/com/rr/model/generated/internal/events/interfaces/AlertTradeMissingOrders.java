package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.internal.type.ExecType;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface AlertTradeMissingOrders extends Alert, Event {

   // Getters and Setters
    ViewString getOrderId();

    ExecType getExecType();

    OrdStatus getOrdStatus();

    double getLastQty();

    double getLastPx();

    ViewString getLastMkt();

    @Override void dump( ReusableString out );

}
