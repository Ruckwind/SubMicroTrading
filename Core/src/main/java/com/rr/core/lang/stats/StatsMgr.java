/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang.stats;

public class StatsMgr {

    private static Stats _instance = new DummyStats();

    public static Stats instance()             { return _instance; }

    public static void setStats( Stats stats ) { _instance = stats; }
}
