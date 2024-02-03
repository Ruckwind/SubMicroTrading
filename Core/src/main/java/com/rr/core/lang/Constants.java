/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang;

public interface Constants {

    boolean DISABLE_RECYCLING = true;
    boolean RECYCLE_DEBUG     = false;

    int DEFAULT_STRING_LENGTH = 16;

    boolean DEFAULT_BACKTEST_RESTORE_ON_RESTART = false;

    String FIX_TIMESTAMP_DATE_FORMAT   = "yyyyMMdd-";
    String FIX_TIMESTAMP_MILLIS_FORMAT = "yyyyMMdd-HH:mm:ss.SSS";
    String FIX_TIMESTAMP_SEC_FORMAT    = "yyyyMMdd-HH:mm:ss";
    String FUT_CONTRACT_MONTH_CODE     = "FGHJKMNQUVXZ";

    String GLOBAL_EVENT_MGR = "globalEventMgr";
    String PERF_RESET       = "PERF_RESET";
    String ADMIN_EVENT      = "ADMIN";
    String AQUADB           = "aqua";
    String IQ_DATA_DB       = "iq_data";
    String IMPORTED         = "IMPORT";

    String HEARTBEAT_ID_START = "GRP_";

    int FIX_TIMESTAMP_MILLIS_FORMAT_LEN = FIX_TIMESTAMP_MILLIS_FORMAT.length();
    int FIX_TIMESTAMP_SEC_FORMAT_LEN    = FIX_TIMESTAMP_SEC_FORMAT.length();

    /**
     * strictly speaking should have UNSET values for UINT, ULONG
     * however dont have a real use case for that need and extra complexity
     * WOULD NEED CAREFUL INTRODUCTION
     */
    byte[] Z_INFINITY = "infinity".getBytes();

    String Z_POS_INFINITY = "+" + new String( Z_INFINITY );
    String Z_NEG_INFINITY = "-" + new String( Z_INFINITY );

    long   UNSET_LONG        = Long.MIN_VALUE;
    int    UNSET_INT         = Integer.MIN_VALUE;
    short  UNSET_SHORT       = Short.MIN_VALUE;
    char   UNSET_CHAR        = '?';
    byte   UNSET_BYTE        = (byte) 0xFF;
    float  UNSET_FLOAT       = Float.NaN;              // must use Utils.isNull and Utils.hasVal
    double UNSET_DOUBLE      = Double.NaN;             // must use Utils.isNull and Utils.hasVal
    long   MIN_PRICE_AS_LONG = Long.MIN_VALUE + 1;
    int    MIN_PRICE_AS_INT  = Integer.MIN_VALUE + 1;

    int FIX_TAG_ACK_STATS = 11611;

    int SECS_IN_HOUR = 60 * 60;

    int MINUTES_IN_DAY = 1440;

    int SECS_IN_DAY = 24 * SECS_IN_HOUR;

    long MS_IN_DAY = 24 * 60 * 60 * 1000;

    long MS_IN_WEEK = 24 * 60 * 60 * 1000 * 7;

    int MS_IN_HOUR = 60 * 60 * 1000;

    int MS_IN_MINUTE = 60 * 1000;

    int MS_IN_TEN_MINUTE = 60 * 1000;

    int MAX_BUF_LEN = 4096;

    int MAX_SESSIONS = 32;

    int LOW_PRI_LOOP_WAIT_MS = 10;

    int DATE_STR_LEN = 9; // YYYYMMDD-

    int INST_ID_LEN = 15;

    int    PRICE_DP_S         = 6; // must be multiple of 2 ... if change MUST change mask below
    double PRICE_DP_S_DFACTOR = 1000000D;
    long   PRICE_DP_S_LFACTOR = 1000000L;

    int    PRICE_DP_L         = 8; // must be multiple of 2 ... if change MUST change mask below
    double PRICE_DP_L_DFACTOR = 100000000D; // used to convert integer representation of fraction into double
    long   PRICE_DP_L_LFACTOR = 100000000L; // long factor for mult

    int    PRICE_DP_H         = 12;
    double PRICE_DP_H_DFACTOR = 1000000000000D; // used to convert integer representation of fraction into double
    long   PRICE_DP_H_LFACTOR = 1000000000000L; // long factor for mult

    // PRICE / DOUBLE RELATED CONSTANTS  LONG (8dp) and SHORT(2dp)

    long PRICE_DP_THRESHOLD_MASK_8DP = 0x7FFFFFFFFF000000L; // if double exceeds this value will use short form

    long PRICE_DP_THRESHOLD_MASK_NODP = 0x7FFFFFE000000000L; // if double exceeds this value dont use DP

    double TICK_WEIGHT = 0.00000001;

    // dont change the weight without re-enabling and running the testEncodePrice
    double WEIGHT  = 0.00000000001;
    double EPSILON = 1e-15;
}
