/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup.units;

import com.rr.core.warmup.JITWarmup;

public class WarmupJavaSpecific implements JITWarmup {

    public static int _stopOpt;

    private int _warmupCount;

    public WarmupJavaSpecific( int warmupCount ) {
        _warmupCount = warmupCount;
    }

    @Override
    public String getName() {
        return "JavaSpecific";
    }

    @Override
    public void warmup() {
        warmClass();
    }

    private void warmClass() {
        Class<WarmupJavaSpecific> c = WarmupJavaSpecific.class;

        java.lang.reflect.Method[] m;

        // 1250   b   java.lang.Class::clearCachesOnClassRedefinition (70 bytes)

        for ( int i = 0; i < _warmupCount; i++ ) {
            m = c.getDeclaredMethods();
            _stopOpt += m.length;
        }
    }
}
