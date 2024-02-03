/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.model.instrument;

import com.rr.core.model.*;

@SuppressWarnings( "deprecation" )
public interface InstrumentWrite extends Instrument, com.rr.core.model.InstrumentWrite {

    void setEnabled( boolean isEnabled );

    void setBookLevels( int bookLevels );

    void setCommonInstrument( CommonInstrument ex );

    void setCurrency( Currency ccy );

    void setExchange( Exchange ex );

    void setTickType( TickType tick );
}
