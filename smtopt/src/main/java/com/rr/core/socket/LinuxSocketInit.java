/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.socket;

public class LinuxSocketInit {

    static {
        LinuxSocketImpl.init();
    }

    public static void init() {
        // already init
    }
}
