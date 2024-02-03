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

public interface MDRequest extends BaseMDRequestWrite, Event {

   // Getters and Setters
    ViewString getMdReqId();

    SubsReqType getSubsReqType();

    int getMarketDepth();

    int getNumRelatedSym();

    SymbolRepeatingGrp getSymbolGrp();

    @Override void dump( ReusableString out );

}
