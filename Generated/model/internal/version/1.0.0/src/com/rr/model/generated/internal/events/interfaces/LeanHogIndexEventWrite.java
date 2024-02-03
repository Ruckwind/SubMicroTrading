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

public interface LeanHogIndexEventWrite extends CommonHeaderWrite, LeanHogIndexEvent, com.rr.core.model.InstRefDataEvent<LeanHogIndexEvent> {

   // Getters and Setters
    void setDataSrc( DataSrc val );

    void setInstrument( Instrument val );

    void setSubject( byte[] buf, int offset, int len );
    ReusableString getSubjectForUpdate();

    void setDataSeqNum( long val );

    void setIndexDate( int val );

    void setNegotHeadCount( double val );

    void setNegotAverageNetPrice( double val );

    void setNegotAverageCarcWt( double val );

    void setSpmfHeadCount( double val );

    void setSpmfAverageNetPrice( double val );

    void setSpmfAverageCarcWt( double val );

    void setNegotSpmfHeadCount( double val );

    void setNegotSpmfAverageNetPrice( double val );

    void setNegotSpmfAverageCarcWt( double val );

    void setDailyWeightedPrice( double val );

    void setIndexValue( double val );

}
