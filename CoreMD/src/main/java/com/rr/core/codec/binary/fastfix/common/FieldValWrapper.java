/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common;

import com.rr.core.lang.ReusableString;

public interface FieldValWrapper {

    boolean hasValue();

    void log( ReusableString dest );

    void reset();
}
