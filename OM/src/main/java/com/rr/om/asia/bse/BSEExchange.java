/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.asia.bse;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.ExchangeSession;
import com.rr.om.exchange.BaseExchangeImpl;
import com.rr.core.idgen.IDGenerator;

import java.util.Calendar;
import java.util.TimeZone;

public class BSEExchange extends BaseExchangeImpl {

    public static final ZString BSE_REC = new ViewString( "BO" );

    public BSEExchange( ExchangeCode micCode,
                        TimeZone timezone,
                        ExchangeSession exchangeSession,
                        IDGenerator idGen,
                        Calendar eodExpireEvents ) {

        super( new BSEEnricher(),
               new BSEMessageValidator(),
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
               false,
               eodExpireEvents );
    }

    @Override
    public void generateMarketClOrdId( ReusableString dest, ZString clientClOrdId ) {
        dest.setValue( clientClOrdId );
    }
}
