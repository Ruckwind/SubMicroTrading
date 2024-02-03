/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ZString;

/**
 * Reference Data Event Interface - represent data event related to single instrument
 */

public interface InstRefDataEvent<T> extends InstRefData<Instrument> {

    default T getData() { return (T) this; }

    ZString getSubject();
}
