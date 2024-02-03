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

public interface MDUpdate extends BaseMDResponseWrite, Event {

   // Getters and Setters
    ViewString getMdReqId();

    Book getBook();

    int getNoMDEntries();

    TickUpdate getTickUpdates();

    @Override void dump( ReusableString out );

}
