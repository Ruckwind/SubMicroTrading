/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.model.event;

import com.rr.core.model.Currency;
import com.rr.core.model.Event;
import com.rr.core.model.MsgFlag;
import com.rr.model.generated.internal.events.interfaces.OrderRequest;

public final class EventUtils {

    public static double convertForMajorMinor( OrderRequest src, double price ) {

        final Currency clientCurrency  = src.getCurrency();
        final Currency tradingCurrency = src.getInstrument().getCurrency();

        if ( tradingCurrency != clientCurrency ) {

            price = clientCurrency.majorMinorConvert( tradingCurrency, price );
        }

        return price;
    }

    public static void propogateFlags( final Event src, final Event dest ) {
        if ( src == null ) return;

        for ( MsgFlag f : MsgFlag.values() ) {
            dest.setFlag( f, src.isFlagSet( f ) );
        }
    }

    public static void propogateFlags( final int src, final Event dest ) {
        if ( dest == null ) return;

        for ( MsgFlag f : MsgFlag.values() ) {
            dest.setFlag( f, MsgFlag.isOn( src, f ) );
        }
    }
}
