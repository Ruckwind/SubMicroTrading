/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.warmup;

public interface JITWarmup {

    String getName();

    void warmup() throws Exception;
}
