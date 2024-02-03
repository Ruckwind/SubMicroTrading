/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.us.cme;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.ExchangeSession;
import com.rr.om.exchange.BaseExchangeImpl;
import com.rr.core.idgen.IDGenerator;

import java.util.Calendar;
import java.util.TimeZone;

public class CMEExchange extends BaseExchangeImpl {

    public CMEExchange( ExchangeCode micCode,
                        TimeZone timezone,
                        ExchangeSession exchangeSession,
                        IDGenerator idGen,
                        Calendar eodExpireEvents ) {

        super( new CMEEnricher(),
               new CMEMessageValidator(),
               micCode,
               timezone,
               false,
               true,
               true,
               false,
               idGen,
               exchangeSession,
               false,
               true,
               true,
               eodExpireEvents );
    }

    @Override
    public void generateMarketClOrdId( ReusableString dest, ZString clientClOrdId ) {
        dest.setValue( clientClOrdId );
    }
}
