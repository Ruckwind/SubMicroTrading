/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common;

public enum FieldDataType {
    int32,
    uInt32,
    int64,
    uInt64,
    byteVector,
    decimal,
    string,
    length,
    group,
    sequence,
    template
}
