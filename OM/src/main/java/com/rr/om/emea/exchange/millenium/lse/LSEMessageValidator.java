/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.emea.exchange.millenium.lse;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.model.generated.internal.events.interfaces.CancelReplaceRequest;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.model.generated.internal.type.OrdType;
import com.rr.model.generated.internal.type.TimeInForce;
import com.rr.model.generated.internal.type.TypeIds;
import com.rr.om.Strings;
import com.rr.om.exchange.BaseExchangeValidator;

/**
 * may be used on multiple threads so dont maintain state in mem vars
 */

// TODO push common into base class

public class LSEMessageValidator extends BaseExchangeValidator {

    private static final ZString INVALID_ORD_QTY = new ViewString( "Order qty must be greater than minQty of " );

    private final int _minQty = 10; // min qty on LSE

    @Override
    public final void validate( final NewOrderSingle msg, final ReusableString err, final long now ) {
        validateOpen( msg.getInstrument(), now, err );
        validateTIF( msg.getTimeInForce(), err );
        validateOrderType( msg.getOrdType(), err );
        validateMinQty( msg.getOrderQty(), err );
    }

    @Override
    public void validate( CancelReplaceRequest msg, ReusableString err, long now ) {
        validateOpen( msg.getInstrument(), now, err );
        validateTIF( msg.getTimeInForce(), err );
        validateOrderType( msg.getOrdType(), err );
        validateMinQty( msg.getOrderQty(), err );
    }

    private void validateMinQty( double orderQty, ReusableString err ) {
        if ( orderQty < _minQty ) delim( err ).append( INVALID_ORD_QTY ).append( _minQty ).append( ' ' ).append( Strings.QUANTITY ).append( orderQty );
    }

    private void validateOrderType( OrdType ordType, ReusableString err ) {
        switch( ordType.getID() ) {
        case TypeIds.ORDTYPE_LIMIT:
        case TypeIds.ORDTYPE_STOP:
        case TypeIds.ORDTYPE_STOPLIMIT:
            break;
        case TypeIds.ORDTYPE_MARKET:
        case TypeIds.ORDTYPE_UNKNOWN:
            addErrorUnsupported( ordType, err );
        }
    }

    private void validateTIF( TimeInForce tif, ReusableString err ) {
        if ( tif == null )
            return;
        switch( tif.getID() ) {
        case TypeIds.TIMEINFORCE_DAY:
        case TypeIds.TIMEINFORCE_IMMEDIATEORCANCEL:
        case TypeIds.TIMEINFORCE_FILLORKILL:
            break;
        case TypeIds.TIMEINFORCE_GOODTILLCANCEL:
        case TypeIds.TIMEINFORCE_ATTHEOPENING:
        case TypeIds.TIMEINFORCE_GOODTILLCROSSING:
        case TypeIds.TIMEINFORCE_GOODTILLDATE:
        case TypeIds.TIMEINFORCE_ATTHECLOSE:
        case TypeIds.TIMEINFORCE_UNKNOWN:
            addErrorUnsupported( tif, err );
        }
    }
}
