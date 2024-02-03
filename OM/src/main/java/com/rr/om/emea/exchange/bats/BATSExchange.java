/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.bats;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.ExchangeSession;
import com.rr.om.exchange.BaseExchangeImpl;

import java.util.Calendar;
import java.util.TimeZone;

public class BATSExchange extends BaseExchangeImpl {

    public BATSExchange( ExchangeCode micCode, TimeZone timezone, ExchangeSession exchangeSession, Calendar eodExpireEvents ) {
        super( new BATSEnricher(),
               new BATSMessageValidator(),
               micCode,
               timezone,
               false,
               true,
               true,
               true,
               null,
               exchangeSession,
               false,
               false,
               false,
               eodExpireEvents );
    }

    @Override
    public void generateMarketClOrdId( ReusableString dest, ZString clientClOrdId ) {
        dest.setValue( clientClOrdId );
    }
}
