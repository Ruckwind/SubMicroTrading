package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.CorporateActionClassification;
import com.rr.model.generated.internal.type.AdjustmentType;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface CorporateActionEvent extends CommonHeaderWrite, Event, com.rr.core.model.InstRefDataEvent<CorporateActionEvent> {

   // Getters and Setters
    DataSrc getDataSrc();

    @Override Instrument getInstrument();

    /**
     *mandatory GLOBAL unique sequence number for event ... doesnt change on update
     */
    long getDataSeqNum();

    ViewString getSubject();

    SecurityIDSource getIdSource();

    ViewString getSecurityId();

    ExchangeCode getSecurityExchange();

    CorporateActionClassification getType();

    /**
     *declaration / announcement of event
     */
    long getAnnounceTimestamp();

    /**
     *qualifying timestamp threshold eg exDivDate
     */
    long getQualifyTimestamp();

    /**
     *date the company factors the action into its shares
     */
    long getRecordTimestamp();

    /**
     *effective date when action is applied eg payDate/splitDate
     */
    long getActionTimestamp();

    Currency getCcy();

    AdjustmentType getAdjustType();

    /**
     *value to adjust by, defined by adjustType
     */
    double getPriceAdjustVal();

    @Override void dump( ReusableString out );

}
