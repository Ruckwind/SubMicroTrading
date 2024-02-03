/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.exchange;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.ExchangeInstrument;
import com.rr.core.model.Instrument;
import com.rr.om.Strings;

public abstract class BaseExchangeValidator implements OMExchangeValidator {

    private static final ZString UNSUPPORTED       = new ViewString( "Unsupported attribute value " );
    private static final ZString EXCHANGE_NOT_OPEN = new ViewString( "Exchange not open " );

    protected final void addError( final ReusableString err, final ZString msg ) {
        delim( err ).append( msg );
    }

    protected final void addErrorUnsupported( final Enum<?> val, final ReusableString err ) {

        if ( err.length() > 0 ) {
            err.append( Strings.DELIM );
        }

        err.append( UNSUPPORTED ).append( val.toString() ).append( Strings.TYPE ).append( val.getClass().getSimpleName() );
    }

    protected final ReusableString delim( final ReusableString err ) {
        if ( err.length() > 0 ) {
            err.append( Strings.DELIM );
        }

        return err;
    }

    protected final void validateOpen( final Instrument instrument, final long now, final ReusableString err ) {
        if ( !((ExchangeInstrument) instrument).getExchangeSession().isOpenToday( now ) ) addError( err, EXCHANGE_NOT_OPEN );
    }

}
