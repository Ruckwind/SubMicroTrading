package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.interfaces.MDSnapEntry;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface MDSnapshotFullRefresh extends BaseMDResponseWrite, Event {

   // Getters and Setters
    long getReceived();

    int getLastMsgSeqNumProcessed();

    int getTotNumReports();

    int getRptSeq();

    int getMdBookType();

    SecurityIDSource getSecurityIDSource();

    ViewString getSecurityID();

    ExchangeCode getSecurityExchange();

    int getMdSecurityTradingStatus();

    int getNoMDEntries();

    MDSnapEntry getMDEntries();

    @Override void dump( ReusableString out );

}
