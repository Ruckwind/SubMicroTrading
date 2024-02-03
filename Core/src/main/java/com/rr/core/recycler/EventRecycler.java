/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.recycler;

import com.rr.core.lang.HasReusableType;

public interface EventRecycler {

    void recycle( HasReusableType msg );
}
