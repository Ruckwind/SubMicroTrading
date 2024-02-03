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

public interface MDSnapshotFullRefreshWrite extends BaseMDResponseWrite, MDSnapshotFullRefresh {

   // Getters and Setters
    void setReceived( long val );

    void setLastMsgSeqNumProcessed( int val );

    void setTotNumReports( int val );

    void setRptSeq( int val );

    void setMdBookType( int val );

    void setSecurityIDSource( SecurityIDSource val );

    void setSecurityID( byte[] buf, int offset, int len );
    ReusableString getSecurityIDForUpdate();

    void setSecurityExchange( ExchangeCode val );

    void setMdSecurityTradingStatus( int val );

    void setNoMDEntries( int val );

    void setMDEntries( MDSnapEntry val );

}
