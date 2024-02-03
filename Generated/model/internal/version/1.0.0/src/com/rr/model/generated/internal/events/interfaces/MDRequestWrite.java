package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.type.SubsReqType;
import com.rr.model.generated.internal.events.interfaces.SymbolRepeatingGrp;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface MDRequestWrite extends BaseMDRequestWrite, MDRequest {

   // Getters and Setters
    void setMdReqId( byte[] buf, int offset, int len );
    ReusableString getMdReqIdForUpdate();

    void setSubsReqType( SubsReqType val );

    void setMarketDepth( int val );

    void setNumRelatedSym( int val );

    void setSymbolGrp( SymbolRepeatingGrp val );

}
