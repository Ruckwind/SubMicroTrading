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

public interface MsgSeqNumGapWrite extends MsgSeqNumGap {

   // Getters and Setters
    void setChannelId( int val );

    @Override void setMsgSeqNum( int val );

    void setPrevSeqNum( int val );

    void setPossDupFlag( boolean val );

    void setEventTimestamp( long val );

}
