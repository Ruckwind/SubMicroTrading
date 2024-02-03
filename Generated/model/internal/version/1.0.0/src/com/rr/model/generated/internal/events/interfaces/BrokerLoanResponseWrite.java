package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.LoanRateType;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface BrokerLoanResponseWrite extends CommonHeaderWrite, BrokerLoanResponse, com.rr.core.model.CoreBrokerLoanResponse {

   // Getters and Setters
    void setDataSrc( DataSrc val );

    void setInstrument( Instrument val );

    void setDataSeqNum( long val );

    void setSubject( byte[] buf, int offset, int len );
    ReusableString getSubjectForUpdate();

    void setIdSource( SecurityIDSource val );

    void setSecurityId( byte[] buf, int offset, int len );
    ReusableString getSecurityIdForUpdate();

    void setReference( byte[] buf, int offset, int len );
    ReusableString getReferenceForUpdate();

    void setIsDisabled( boolean val );

    void setSecurityExchange( ExchangeCode val );

    void setApproveQty( double val );

    void setAmount( double val );

    void setType( LoanRateType val );

    void setBroker( PartyID val );

}
