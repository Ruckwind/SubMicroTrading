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

public interface SoupDebugPacketWrite extends BaseSoupWrite, SoupDebugPacket {

   // Getters and Setters
    void setText( byte[] buf, int offset, int len );
    ReusableString getTextForUpdate();

}
