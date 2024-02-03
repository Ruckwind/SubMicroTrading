package com.rr.om.exchange;

import com.rr.core.model.Exchange;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.Identifiable;
import com.rr.core.tasks.ScheduledEvent;

public class ExchangeResetEvent implements ScheduledEvent, Identifiable {

    private final Exchange _exchange;
    private final String   _id;

    public ExchangeResetEvent( final Exchange exchange ) {
        _exchange = exchange;
        _id       = "ExchangeResetEvent" + exchange.getExchangeCode().name();
    }

    @Override public String id()              { return _id; }

    @Override public String name()            { return _id; }

    @Override public String toString()        { return _id; }

    public ExchangeCode getSecurityExchange() { return _exchange.getExchangeCode(); }
}
