package com.rr.model.generated.internal.events.interfaces;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.interfaces.MDEntry;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;

@SuppressWarnings( { "unused", "override"  })

public interface MDIncRefresh extends BaseMDResponseWrite, Event {

   // Getters and Setters
    long getReceived();

    int getNoMDEntries();

    MDEntry getMDEntries();

    @Override void dump( ReusableString out );

}
