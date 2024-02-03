/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.properties;

import java.util.HashSet;
import java.util.Set;

/**
 * core properties, application properties and runtime properties .. ie those that change per env
 * <p>
 * note when adding add constant and entry to propertyTags
 */
public class CoreProps implements PropertyTags {

    // application properties
    public static final String APP_NAME          = "app.name";
    public static final String APP_DEBUG         = "app.debug";
    public static final String APP_TIMEZONE      = "app.timeZone";
    public static final String ID_PREFIX         = "app.genIdPrefix";
    public static final String NUM_PREFIX        = "app.genNumIdPrefix";
    public static final String APP_TAGS          = "app.propertyTags";
    public static final String LOG_UTC           = "log.appendUTC";
    public static final String APP_LOG_OVERRIDES = "log.lvl/";

    // properties which change with runtime env
    public static final String RUN_ENV                       = "ENV";
    public static final String ENABLE_WARMUP                 = "run.enableWarmup";
    public static final String PERSIST_DIR                   = "run.persistDir";
    public static final String TMP_DIR                       = "run.tmpDir";
    public static final String CPU_MASK_FILE                 = "run.cpuMaskFile";
    public static final String LOG_FILE_NAME                 = "run.logFileName";
    public static final String LOG_LEVEL                     = "run.logLevel";
    public static final String LOG_Q_BOUNDED                 = "run.logQueueBounded";
    public static final String LOG_BUF_SIZE                  = "run.logOutBufSize";
    public static final String LOG_ROOT                      = "run.logRoot";
    public static final String ERR_DOWNGRADE_FILE            = "run.errDowngradeFile";
    public static final String FORCE_SINGLE_LOG              = "run.forceSingleLog";
    public static final String SLEEP_ON_EMPTY_LOGQ           = "run.sleepOnEmptyLogQ";
    public static final String MIN_LOG_FLUSH_SECS            = "run.minLogFlushSecs";
    public static final String MAX_LOG_SIZE                  = "run.maxLogSize";
    public static final String MAX_LOGQ_SIZE                 = "run.maxLogQueueSize";
    public static final String STARTUP_DELAY                 = "run.start.delay";
    public static final String SOCKET_FACTORY                = "run.socketFactoryClass";
    public static final String MAIN_THREAD_PRI               = "run.mainPriority";
    public static final String STATS_CFG_FILE                = "run.statsCfgFile";
    public static final String TIME_UTILS_CLASS              = "run.timeUtilsClass";
    public static final String CLOCK_CLASS                   = "run.clockClass";
    public static final String SCHEDULER_CLASS               = "run.schedulerClass";
    public static final String THREAD_UTILS_CLASS            = "run.threadUtilsClass";
    public static final String EMAIL_USER                    = "run.emailUser";
    public static final String EMAIL_PASSWORD                = "run.emailPwd";
    public static final String SHARED_THREAD_POOL_CORE_RATIO = "run.coreRatioToSharedThreadPool";
    public static final String MAX_CORES                     = "run.maxCores";
    public static final String RUN_START_TIME                = "run.runStartTimestamp";
    public static final String RUN_END_TIME                  = "run.runEndTimestamp";
    public static final String DISABLE_ORDER_AGE_CHECKS      = "run.disableOrderAgeChecks";

    // other
    public static final String ADMIN_HTML_PORT = "ADMIN_HTML_PORT";
    public static final String ADMIN_RMI_PORT  = "ADMIN_RMI_PORT";
    public static final String SMS_FROM_PHONE  = "run.smsFromPhone";
    public static final String SMS_ACCOUNT_SID = "run.smsAccountSID";
    public static final String SMS_AUTH_TOKEN  = "run.smsAuthToken";
    public static final String SMS_TO_PHONES   = "run.smsToPhones";

    public static final String BT_AUTO_FF_ON_RESTART       = "bt.onStartLoadLastSnapshot";
    public static final String BT_END_SNAPSHOT             = "bt.onEndTakeSnapshot";
    public static final String DISABLE_RISK_ROUTING        = "run.disableRiskRouting";
    public static final String TIMER_WARN_THRESHOLD_MS     = "run.timerWarnThresholdMS";
    public static final String EXIT_ON_ERROR               = "run.exitOnError";
    public static final String ENABLE_EXCHANGE_RESET_EVENT = "run.enableExchangeResetEvent";
    public static final String PROD_ENABLED                = "prodEnabled";
    public static final String BACKTEST_ROOT               = "BACKTEST_ROOT";
    public static final String USER_HOME                   = "HOME";
    public static final String SPAWN_ITERATION             = "SPAWN_ITERATION";
    public static final String UTC_TODAY                   = "UTC_TODAY";         // today
    public static final String UTC_LAST_WEEK_DAY           = "UTC_LAST_WEEK_DAY"; // the last non holiday weekday starting today
    public static final String UTC_YESTERDAY_WEEK_DAY      = "UTC_YESTERDAY_WEEK_DAY"; // the last non holiday weekday starting yesterday
    private static final Set<String> _set = new HashSet<>();
    private static CoreProps _instance = new CoreProps();

    public enum Tags implements PropertyTags.Tag {

        allowMultipleChildren,
        appendUTC,
        appProps,
        asyncDownloadRefData,
        chainSize,
        className,
        clockClass,
        cloudLogAuthJSON,
        cloudLoggingEnabled,
        clusterName,
        cnt,
        codecBufferSize,
        componentManager,
        compressLog,
        concurrent,
        config,
        connectionFactory,
        consumerGrp,
        coreRatioToSharedThreadPool,
        cpuMaskFile,
        data,
        database,
        debug,
        decoder,
        defaultMIC,
        defaultProperties,
        delay,
        delayMS,
        disableDecodeChecksum,
        disableHub,
        disableOrderAgeChecks,
        disableRiskRouting,
        download,
        driver,
        emailBatchSizeSecs,
        emailErrors,
        emailPwd,
        emailRecipients,
        emailUser,
        enableExchangeResetEvent,
        enableReceiverSpinLock,
        enableSendSpinLock,
        enableWarmup,
        encoder,
        endDate,
        env,
        errDowngradeFile,
        exchangeReplyRouter,
        exchangeXML,
        exitOnError,
        expMatcher,
        file,
        files,
        filesIn,
        filesOut,
        fileName,
        fileNames,
        fileRoot,
        filter,
        forceConsole,
        forceSingleLog,
        forceSlowMode,
        genIdPrefix,
        genNumIdPrefix,
        globalSubs,
        handler,
        home,
        host,
        hostname,
        htmlPort,
        ignoreCols,
        individualLogs,
        inRouter,
        instSimDataStore,
        inStream,
        instSubsMgr,
        inTopic,
        isLocalPersistWhenDisconnected,
        isDurable,
        keyType,
        loader,
        localTimeZoneStr,
        lockToSocketOne,
        logEvents,
        logFileName,
        logLevel,
        logOutBufSize,
        logQueueBounded,
        logPojoEvents,
        logRoot,
        logSimulatorEvents,
        logWriter,
        logWriterB,
        mainPriority,
        maxConnectAttempts,
        maxCores,
        maxLogSize,
        maxLogQueueSize,
        maxWaitBeforeReconMS,
        mergedSecDef,
        mergedSecDefIn,
        mergedSecDefOut,
        MIC,
        minLogFlushSecs,
        mktDataMaxAgeToTradeMS,
        name,
        next,
        numCorePerCPU,
        numUpstreamSources,
        onStartLoadLastSnapshot,
        orderRouterId,
        outStream,
        outTopic,
        password,
        pathRoots,
        patternMatch,
        persistDir,
        persistFlushPeriodMS,
        persistDatPageSize,
        persistDatPreSize,
        persistIdxPreSize,
        pipeLine,
        pnlFactoryType,
        port,
        prodEnabled,
        propertyTags,
        publisher,
        pwdfile,
        readSpinThrottle,
        readSpinDelayMS,
        reconnectOnLogout,
        recycler,
        refDate,
        rmiPort,
        runEndTimestamp,
        runStartTimestamp,
        schedulerClass,
        sessionTrace,
        snapshotCaretaker,
        snapshotMembers,
        socketFactoryClass,
        sourceId,
        sleepOnEmptyLogQ,
        smsFromPhone,
        smsAccountSID,
        smsAuthToken,
        smsToPhones,
        srcDir,
        startDate,
        statsBlockSize,
        statsCfgFile,
        stream,
        subsMgr,
        throttleBatch,
        tickManager,
        tickScaleFile,
        timeStamp,
        timeZone,
        timeUtilsClass,
        timerWarnThresholdMS,
        threadPriority,
        threadUtilsClass,
        throwOnMissingInst,
        trace,
        tmpDir,
        tzStr,
        upstreamDispatcher,
        url,
        urls,
        useNativeLinux,
        useLinuxNonLockingNIOSockets,
        useUniversalTickScales,
        valType,
        value,
        warnPercent
    }

    static {
        for ( Tags p : Tags.values() ) {
            _set.add( p.toString().toLowerCase() );
        }
    }

    public static CoreProps instance() { return _instance; }

    protected CoreProps() {
        //
    }

    @Override
    public String getSetName() {
        return "CoreProps";
    }

    @Override
    public boolean isValidTag( String tag ) {
        if ( tag == null ) return false;

        return _set.contains( tag.toLowerCase() );
    }

    @Override
    public Tag lookup( String tag ) {
        return Tags.valueOf( tag );
    }
}
