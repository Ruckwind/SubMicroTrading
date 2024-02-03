package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.SettlementPriceType;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface SettlementPriceEventWrite extends CommonHeaderWrite, SettlementPriceEvent, com.rr.core.model.InstRefDataEvent<SettlementPriceEvent> {

   // Getters and Setters
    void setDataSrc( DataSrc val );

    void setInstrument( Instrument val );

    void setSubject( byte[] buf, int offset, int len );
    ReusableString getSubjectForUpdate();

    void setDataSeqNum( long val );

    void setSettlementPrice( double val );

    void setSettlementPriceType( SettlementPriceType val );

    void setSettleDateTime( long val );

}
