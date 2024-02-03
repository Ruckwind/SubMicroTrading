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

public interface NewsWrite extends CommonHeaderWrite, News, com.rr.core.model.InstRefDataEvent<News> {

   // Getters and Setters
    void setDataSrc( DataSrc val );

    void setInstrument( Instrument val );

    void setDataSeqNum( long val );

    void setSubject( byte[] buf, int offset, int len );
    ReusableString getSubjectForUpdate();

    void setShortText( byte[] buf, int offset, int len );
    ReusableString getShortTextForUpdate();

    void setLongText( byte[] buf, int offset, int len );
    ReusableString getLongTextForUpdate();

}
