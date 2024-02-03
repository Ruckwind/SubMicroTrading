package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface RefPriceEventWrite extends CommonHeaderWrite, RefPriceEvent, com.rr.core.model.InstRefDataEvent<RefPriceEvent> {

   // Getters and Setters
    void setDataSrc( DataSrc val );

    void setInstrument( Instrument val );

    void setDataSeqNum( long val );

    void setSubject( byte[] buf, int offset, int len );
    ReusableString getSubjectForUpdate();

    void setCode( byte[] buf, int offset, int len );
    ReusableString getCodeForUpdate();

    void setVal( double val );

}
