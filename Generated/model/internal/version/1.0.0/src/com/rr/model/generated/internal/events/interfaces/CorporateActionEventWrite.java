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

public interface CorporateActionEventWrite extends CommonHeaderWrite, CorporateActionEvent, com.rr.core.model.InstRefDataEvent<CorporateActionEvent> {

   // Getters and Setters
    void setDataSrc( DataSrc val );

    void setInstrument( Instrument val );

    void setDataSeqNum( long val );

    void setSubject( byte[] buf, int offset, int len );
    ReusableString getSubjectForUpdate();

    void setIdSource( SecurityIDSource val );

    void setSecurityId( byte[] buf, int offset, int len );
    ReusableString getSecurityIdForUpdate();

    void setSecurityExchange( ExchangeCode val );

    void setType( CorporateActionClassification val );

    void setAnnounceTimestamp( long val );

    void setQualifyTimestamp( long val );

    void setRecordTimestamp( long val );

    void setActionTimestamp( long val );

    void setCcy( Currency val );

    void setAdjustType( AdjustmentType val );

    void setPriceAdjustVal( double val );

}
