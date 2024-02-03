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

public interface BookClear extends BaseITCHWrite, Event {

   // Getters and Setters
    int getNanosecond();

    long getOrderId();

    Book getBook();

    @Override void dump( ReusableString out );

}
