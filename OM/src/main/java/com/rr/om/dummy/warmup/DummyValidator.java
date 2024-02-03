/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.dummy.warmup;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.model.generated.internal.events.interfaces.CancelReplaceRequest;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.om.Strings;
import com.rr.om.exchange.BaseExchangeValidator;

public class DummyValidator extends BaseExchangeValidator {

    private static final ZString INVALID_ORD_QTY = new ViewString( "Quantity must be greater than zero, " );

    @Override
    public void validate( NewOrderSingle msg, ReusableString err, long now ) {
        validateMinQty( msg.getOrderQty(), err );
        validateOpen( msg.getInstrument(), now, err );
    }

    @Override
    public void validate( CancelReplaceRequest msg, ReusableString err, long now ) {
        validateMinQty( msg.getOrderQty(), err );
        validateOpen( msg.getInstrument(), now, err );
    }

    private void validateMinQty( double orderQty, ReusableString err ) {
        if ( orderQty <= 0 ) delim( err ).append( INVALID_ORD_QTY ).append( Strings.QUANTITY ).append( orderQty );
    }
}
