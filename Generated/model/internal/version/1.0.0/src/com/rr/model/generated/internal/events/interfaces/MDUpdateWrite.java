package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.interfaces.TickUpdate;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface MDUpdateWrite extends BaseMDResponseWrite, MDUpdate {

   // Getters and Setters
    void setMdReqId( byte[] buf, int offset, int len );
    ReusableString getMdReqIdForUpdate();

    void setBook( Book val );

    void setNoMDEntries( int val );

    void setTickUpdates( TickUpdate val );

}
