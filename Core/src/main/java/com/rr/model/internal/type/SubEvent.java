/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.model.internal.type;

import com.rr.core.lang.HasReusableType;
import com.rr.core.lang.ReusableString;

public interface SubEvent extends HasReusableType {

    void dump( ReusableString out );

    void reset();
}
