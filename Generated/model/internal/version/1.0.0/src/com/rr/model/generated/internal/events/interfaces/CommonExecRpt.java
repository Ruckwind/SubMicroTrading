package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.internal.type.ExecType;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.model.generated.internal.type.Side;
import com.rr.model.generated.internal.type.OrderCapacity;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface CommonExecRpt extends CommonExchangeHeader, Event {

   // Getters and Setters
    ViewString getExecId();

    ViewString getClOrdId();

    ViewString getSecurityId();

    ViewString getSymbol();

    Currency getCurrency();

    SecurityIDSource getSecurityIDSource();

    ViewString getOrderId();

    ExecType getExecType();

    OrdStatus getOrdStatus();

    long getTransactTime();

    double getLeavesQty();

    double getCumQty();

    double getAvgPx();

    double getOrderQty();

    double getPrice();

    Side getSide();

    ViewString getText();

    OrderCapacity getMktCapacity();

    ViewString getParentClOrdId();

    ViewString getStratId();

    ViewString getOrigStratId();

    @Override void dump( ReusableString out );

}
