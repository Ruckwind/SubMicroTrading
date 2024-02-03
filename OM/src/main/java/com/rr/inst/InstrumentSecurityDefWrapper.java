/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.lang.ZString;
import com.rr.core.model.ExchangeInstrument;
import com.rr.core.model.InstrumentWrite;
import com.rr.core.model.TickType;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.model.generated.internal.events.impl.SecurityStatusImpl;
import com.rr.model.generated.internal.events.interfaces.SecDefLeg;
import com.rr.model.generated.internal.type.SecurityTradingStatus;

public interface InstrumentSecurityDefWrapper extends ExchangeInstrument, InstrumentWrite {

    @Override ZString getISIN();

    ZString getFIGI();

    SecurityStatusImpl getLastStatus();

    void setLastStatus( final SecurityStatusImpl lastStatus );

    SecurityDefinitionImpl getSecDef();

    SecurityTradingStatus getSecurityTradingStatus();

    void setSecurityTradingStatus( SecurityTradingStatus securityTradingStatus );

    void setPlaceHolderDefinition( final SecDefLeg def );

    void setSecurityDefinition( final SecurityDefinitionImpl def );

    void setTickType( final TickType ts );
}
