/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.warmup;

public interface FixSimConstants {

    String DEFAULT_OM_UP_ID         = "SMC01";
    String DEFAULT_CLIENT_SIM_ID    = "CLT01";
    int    DEFAULT_OM_CLIENT_PORT   = 14802;
    String DEFAULT_OM_DOWN_ID       = "SME01";
    String DEFAULT_EXCHANGE_SIM_ID  = "EXE1";
    int    DEFAULT_OM_EXCHANGE_PORT = 14812;
    String DEFAULT_HUB_HOST         = "localhost";
    int    DEFAULT_HUB_PORT         = 14250;
    String DEFAULT_CLIENT_DATA_FILE = "./data/fixClientSimOrders.txt";
    String DEFAULT_HUB_BRIDGE_ID    = "HUB01";
    String DEFAULT_OM_HUB_ID        = "SMH01";
}
