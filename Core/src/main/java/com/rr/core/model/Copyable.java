/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.Snapable;

public interface Copyable<T> extends Snapable<T>, ShallowCopy<T>, DeepCopy<T> {
    /* tag interface */
}
