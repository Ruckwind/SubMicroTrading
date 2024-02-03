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

public interface CommonClientHeaderWrite extends CommonHeaderWrite, CommonClientHeader {

   // Getters and Setters
    void setSenderCompId( byte[] buf, int offset, int len );
    ReusableString getSenderCompIdForUpdate();

    void setOnBehalfOfId( byte[] buf, int offset, int len );
    ReusableString getOnBehalfOfIdForUpdate();

}
