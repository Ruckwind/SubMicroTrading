/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common;

public enum FieldOperator {
    NOOP,
    CONSTANT,
    COPY,
    DEFAULT,
    DELTA,
    INCREMENT
}

