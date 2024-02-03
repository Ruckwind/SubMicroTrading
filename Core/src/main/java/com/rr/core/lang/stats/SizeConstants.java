/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.lang.stats;

public class SizeConstants {

    // on startup the constants are initialised from the StatsMgr ... obviously need be careful of startup order

    // only constants which need to be in the persisted stats file need an entry in the stats mgr
    // otherwise just hard code here
    public static final int MIN_MEMCPY_LENGTH          = StatsMgr.instance().find( SizeType.DEFAULT_MIN_MEMCPY_LENGTH );
    public static final int DEFAULT_MAX_MSG_BUFFER     = StatsMgr.instance().find( SizeType.DEFAULT_MAX_MSG_BUFFER );
    public static final int DEFAULT_MAX_SESSION_BUFFER = StatsMgr.instance().find( SizeType.DEFAULT_MAX_SESSION_BUFFER );

    public static final int DEFAULT_STRING_LENGTH            = StatsMgr.instance().find( SizeType.DEFAULT_STRING_LENGTH );
    public static final int DEFAULT_ACCOUNT_LENGTH           = StatsMgr.instance().find( SizeType.DEFAULT_ACCOUNT_LENGTH );
    public static final int DEFAULT_CLIENTID_LENGTH          = StatsMgr.instance().find( SizeType.DEFAULT_CLIENTID_LENGTH );
    public static final int DEFAULT_CLORDID_LENGTH           = StatsMgr.instance().find( SizeType.DEFAULT_CLORDID_LENGTH );
    public static final int DEFAULT_EXDESTINATION_LENGTH     = StatsMgr.instance().find( SizeType.DEFAULT_EXDESTINATION_LENGTH );
    public static final int DEFAULT_EXECID_LENGTH            = StatsMgr.instance().find( SizeType.DEFAULT_EXECID_LENGTH );
    public static final int DEFAULT_EXECID_MAP_SIZE          = StatsMgr.instance().find( SizeType.DEFAULT_EXECID_MAP_SIZE );
    public static final int DEFAULT_ORDER_MAP_SIZE           = StatsMgr.instance().find( SizeType.DEFAULT_ORDER_MAP_SIZE );
    public static final int DEFAULT_MARKETORDERID_LENGTH     = StatsMgr.instance().find( SizeType.DEFAULT_MARKETORDERID_LENGTH );
    public static final int DEFAULT_SECURITYID_LENGTH        = StatsMgr.instance().find( SizeType.DEFAULT_SECURITYID_LENGTH );
    public static final int DEFAULT_SENDERCOMPID_LENGTH      = StatsMgr.instance().find( SizeType.DEFAULT_SENDERCOMPID_LENGTH );
    public static final int DEFAULT_SENDERSUBID_LENGTH       = StatsMgr.instance().find( SizeType.DEFAULT_SENDERSUBID_LENGTH );
    public static final int DEFAULT_ONBEHALFOFID_LENGTH      = StatsMgr.instance().find( SizeType.DEFAULT_ONBEHALFOFID_LENGTH );
    public static final int DEFAULT_EXECBROKER_LENGTH        = StatsMgr.instance().find( SizeType.DEFAULT_EXECBROKER_LENGTH );
    public static final int DEFAULT_SYMBOL_LENGTH            = StatsMgr.instance().find( SizeType.DEFAULT_SYMBOL_LENGTH );
    public static final int DEFAULT_RIC_LENGTH               = StatsMgr.instance().find( SizeType.DEFAULT_RIC_LENGTH );
    public static final int DEFAULT_TARGETCOMPID_LENGTH      = StatsMgr.instance().find( SizeType.DEFAULT_TARGETCOMPID_LENGTH );
    public static final int DEFAULT_TARGETSUBID_LENGTH       = StatsMgr.instance().find( SizeType.DEFAULT_TARGETSUBID_LENGTH );
    public static final int DEFAULT_SENDERLOCID_LENGTH       = StatsMgr.instance().find( SizeType.DEFAULT_SENDERLOCID_LENGTH );
    public static final int DEFAULT_TEXT_LENGTH              = StatsMgr.instance().find( SizeType.DEFAULT_TEXT_LENGTH );
    public static final int DEFAULT_BENCHMARK_LENGTH         = StatsMgr.instance().find( SizeType.DEFAULT_BENCHMARK_LENGTH );
    public static final int DEFAULT_USERNAME                 = StatsMgr.instance().find( SizeType.DEFAULT_USERNAME );
    public static final int DEFAULT_PASSWORD                 = StatsMgr.instance().find( SizeType.DEFAULT_PASSWORD );
    public static final int DEFAULT_LASTMKT_LENGTH           = StatsMgr.instance().find( SizeType.DEFAULT_LASTMKT_LENGTH );
    public static final int DEFAULT_SECURITYEXCH_LENGTH      = StatsMgr.instance().find( SizeType.DEFAULT_SECURITYEXCH_LENGTH );
    public static final int DEFAULT_SUBPARTYGRPID_LENGTH     = StatsMgr.instance().find( SizeType.DEFAULT_SUBPARTYGRPID_LENGTH );
    public static final int DEFAULT_MATURITYMONTHYEAR_LENGTH = StatsMgr.instance().find( SizeType.DEFAULT_MATURITYMONTHYEAR_LENGTH );
    public static final int DEFAULT_MATURITYDAY_LENGTH       = StatsMgr.instance().find( SizeType.DEFAULT_MATURITYDAY_LENGTH );
    public static final int DEFAULT_VIEW_NOS_BUFFER          = StatsMgr.instance().find( SizeType.DEFAULT_VIEW_NOS_BUFFER );
    public static final int DEFAULT_COMPANYNAME_LENGTH       = StatsMgr.instance().find( SizeType.DEFAULT_COMPANYNAME_LENGTH );

    public static final int DEFAULT_LOG_EVENT_SMALL = StatsMgr.instance().find( SizeType.DEFAULT_LOG_EVENT_SMALL );
    public static final int DEFAULT_LOG_EVENT_LARGE = StatsMgr.instance().find( SizeType.DEFAULT_LOG_EVENT_LARGE );
    public static final int DEFAULT_LOG_EVENT_HUGE  = StatsMgr.instance().find( SizeType.DEFAULT_LOG_EVENT_HUGE );

    public static final int DEFAULT_LOG_MAX_QUEUE_SIZE = StatsMgr.instance().find( SizeType.DEFAULT_LOG_MAX_QUEUE_SIZE );
    public static final int DEFAULT_CHAIN_SIZE         = StatsMgr.instance().find( SizeType.DEFAULT_CHAIN_SIZE );
    public static final int DEFAULT_MAX_LOG_CHAINS     = StatsMgr.instance().find( SizeType.DEFAULT_MAX_LOG_CHAINS );
    public static final int DEFAULT_PERF_BLOCK_SIZE    = StatsMgr.instance().find( SizeType.DEFAULT_PERF_BLOCK_SIZE );

    public static final int DEFAULT_BOOK_LVLS      = StatsMgr.instance().find( SizeType.DEFAULT_BOOK_LVLS );
    public static final int DEFAULT_LIQ_DELTA_LVLS = StatsMgr.instance().find( SizeType.DEFAULT_PERF_BLOCK_SIZE );

    public static final int DEFAULT_EXEC_ID_SET_SIZE_PER_BOOK = StatsMgr.instance().find( SizeType.DEFAULT_EXEC_ID_SET_SIZE_PER_BOOK );
}
