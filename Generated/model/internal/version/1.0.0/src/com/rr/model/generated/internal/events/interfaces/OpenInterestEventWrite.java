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

public interface OpenInterestEventWrite extends CommonHeaderWrite, OpenInterestEvent, com.rr.core.model.InstRefDataEvent<OpenInterestEvent> {

   // Getters and Setters
    void setDataSrc( DataSrc val );

    void setInstrument( Instrument val );

    void setSubject( byte[] buf, int offset, int len );
    ReusableString getSubjectForUpdate();

    void setDataSeqNum( long val );

    void setOpenInterest( double val );

    void setNetOpenInterest( double val );

    void setPrevOpenInterest( double val );

    void setOpenInterestDateTime( long val );

    void setPrevOpenInterestDateTime( long val );

}
