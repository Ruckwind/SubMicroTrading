/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.fulldict.entry;

import com.rr.core.lang.ReusableString;

public interface DictEntry {

    boolean hasValue();

    void log( ReusableString dest );

    void reset();
}
