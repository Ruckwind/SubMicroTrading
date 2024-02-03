/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.utp.liffe;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.ExchangeSession;
import com.rr.om.exchange.BaseExchangeImpl;
import com.rr.core.idgen.IDGenerator;

import java.util.Calendar;
import java.util.TimeZone;

public class LiffeExchange extends BaseExchangeImpl {

    private IDGenerator _idGen;

    public LiffeExchange( ExchangeCode micCode,
                          TimeZone timezone,
                          ExchangeSession sess,
                          IDGenerator idGen,           // used for mktClOrdId and clientExecId
                          Calendar eodExpireEvents ) {

        super( new LiffeEnricher(),
               new LiffeMessageValidator(),
               micCode,
               timezone,
               false,
               true,
               true,
               false,
               idGen,
               sess,
               false,
               true,
               false,
               eodExpireEvents );

        if ( idGen == null ) throw new RuntimeException( "LiffeExchange requires an IDGenerator for numeric marketClOrdId" );

        _idGen = idGen;
    }

    @Override
    public void generateMarketClOrdId( ReusableString dest, ZString clientClOrdId ) {
        _idGen.genID( dest );
    }
}
