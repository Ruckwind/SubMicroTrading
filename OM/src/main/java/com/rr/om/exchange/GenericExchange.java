package com.rr.om.exchange;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.ExchangeSession;
import com.rr.om.BaseEnricher;
import com.rr.core.idgen.IDGenerator;

import java.util.Calendar;
import java.util.TimeZone;

public class GenericExchange extends BaseExchangeImpl {

    public GenericExchange( ExchangeCode micCode,
                            TimeZone timezone,
                            ExchangeSession exchangeSession,
                            IDGenerator idGen,
                            Calendar eodExpireEvents ) {

        super( new BaseEnricher(),
               new GeneralEventValidator(),
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
