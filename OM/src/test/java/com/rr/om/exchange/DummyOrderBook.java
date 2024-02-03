/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.exchange;

import com.rr.core.lang.ZString;
import com.rr.core.model.ExchangeBook;
import com.rr.model.generated.internal.events.interfaces.TradeNew;
import com.rr.model.generated.internal.type.OrdType;
import com.rr.model.generated.internal.type.Side;

// @NOTE only generates one sided fill
// @TODO implement proper book and generate fills for both sides as appropriate
public class DummyOrderBook implements ExchangeBook {

    public DummyOrderBook() {
        //
    }

    public TradeNew add( final ZString mktOrdId, final double orderQty, final double price, final OrdType ordType, final Side side ) {
        return null;
    }

    public TradeNew amend( ZString marketOrderId, int newQty, int origQty, int fillQty, double newPrice, double origPrice, OrdType ordType, Side side ) {
        return null;
    }

    public void remove( ZString marketOrderId, int openQty, double price, OrdType ordType, Side side ) {
        //
    }
}
