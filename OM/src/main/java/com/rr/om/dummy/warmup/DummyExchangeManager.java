/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.dummy.warmup;

import com.rr.core.model.Exchange;
import com.rr.core.model.ExchangeCode;

import java.util.HashMap;
import java.util.Map;

import static com.rr.core.model.ExchangeCode.XLON;

public class DummyExchangeManager {

    private static DummyExchangeManager _instance = new DummyExchangeManager();

    private final Map<ExchangeCode, Exchange> _exchanges;

    public static DummyExchangeManager instance() { return _instance; }

    private DummyExchangeManager() {
        _exchanges = new HashMap<>();
    }

    public Exchange get( ExchangeCode code ) {

        Exchange e = _exchanges.get( code );

        if ( e == null ) {

            code = ExchangeCode.UNKNOWN;

            e = new DummyExchange( code, null, false );

            if ( code == XLON ) {
                ((DummyExchange) e).setExchangeSymbolLongId( true );
            }

            _exchanges.put( code, e );
        }

        return e;
    }

    public void reset() {
        _exchanges.clear();
    }
}
