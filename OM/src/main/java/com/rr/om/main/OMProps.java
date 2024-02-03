/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.main;

import com.rr.core.properties.CoreProps;
import com.rr.core.properties.PropertyTags;

import java.util.HashSet;
import java.util.Set;

/**
 * only the final tag of a property is validated, eg property xxx.yyy.ZZZ, only ZZZ must be a member of Tags
 * this is because the set of complete property names is dynamic and can vary across instances
 */

public class OMProps extends CoreProps {

    public static final String EXPECTED_ORDERS    = "run.expectedOrders";
    public static final String WARMUP_COUNT       = "run.warmUpCount";
    public static final String WARMUP_PORT_OFFSET = "run.warmUpPortOffset";
    public static final String EXCHANGE_XML       = "run.exchangeXML";
    public static final String SEND_SPINLOCKS     = "run.enableSendSpinLock";
    public static final String PROC_ROUTER        = "proc.router";

    public static final String DEFAULT_CPU_WARM_MISS_COUNT = "fastfix.default.cpuWarmMissCount";
    public static final String PITCH_L3_DUP_EXEC_ID_CHK    = "run.pitch.l3.execIdCheck";
    private static final Set<String> _set = new HashSet<>();
    private static OMProps _instance = new OMProps();

    public enum Tags implements PropertyTags.Tag {
        // app properties
        numCorePerCPU,
        lockToSocketOne,
        useNativeLinux,
        useLinuxNonLockingNIOSockets,
        mainPriority,
        threadPriority,
        exchangeXML,
        securityExchange,
        securityIdSrc,
        barDirRoot,
        exchangeCode,

        expectedOrders,
        warmUpCount,
        warmUpPortOffset,
        forceRemovePersistence,
        enabled,

        configFile,
        env,

        // processor validator
        maxAgeMS,
        expTrades,
        forceCancelUnknownExexId,

        // processor order map
        mapType,
        loadFactor,
        segments,

        // general session properties
        trace,
        userName, secUserName,
        userId,
        defaultToFullDecoder,
        disableNanoStats,
        logEvents,
        logPojoEvents,
        logKeepWarmEvents,
        logStats,
        enableReceiverSpinLock,
        enableSendSpinLock,
        router,
        dispatcher,
        queue,
        queuePresize,
        soDelayMS,
        throttleSender,
        throttlerClass,
        sessionDirection,
        useDummySession,

        // socket properties
        server,
        localPort,
        useNIO,
        nic,
        hostname, secHostname,
        port, secPort,
        altPort,                    // some sessions require two ports, one for trading and one for connection/recovery
        logDelayedWriteNanos,
        disableLoopback,
        maxMsgsPerSecond,
        ttl,
        qos,

        // fix session properties
        type,
        multifix,
        codecId,
        inThreadPriority,
        outThreadPriority,
        persistThreadPriority,
        senderCompId,
        senderSubId,
        senderLocationId,
        targetCompId,
        targetSubId,
        persistDatPageSize,
        persistIdxPreSize,
        persistDatPreSize,
        isRecoverFromLoginSeqNumTooLow,
        heartBeatIntSecs,
        dummyPersister,
        rawData,
        encryptMethod,
        isGapFillAllowed,
        disconnectOnSeqGap,
        maxResendRequestSize,
        useNewFix44GapFillProtocol,

        // binary session properties
        sessionTrace,
        isCancelOnDisconnect,

        // multifix
        controlthread,
        in,
        out,
        inboundRouter,

        // client profile limits
        defaultClientProfile,
        useDummyProfile,
        clientProfiles,
        clientName,
        lowThresholdPercent,
        medThresholdPercent,
        highThresholdPercent,
        maxTotalQty,
        maxTotalOrderValueUSD,
        maxSingleOrderValueUSD,
        maxSingleOrderQty,
        maxOrderQty,

        // SIM params
        postConnectWaitSecs,
        batchSize,
        sendEvents,
        batchDelayMicros,
        eventTemplateFile,
        logSimulatorEvents,
        logOrderInTS,

        // MDS
        presubFile,
        execIdCheck,

        // instrument store
        threadsafe,
        file,

        // Other
        MIC,        // reuters exchange code
        securityIdSrcA,
        securityIdSrcB,

        // Exchange specific
        partyIDSessionID,
        password, secPassword,
        traderPassword,
        sessionLogonPassword,
        locationId,
        forceTradingServerLocalhost,
        uniqueClientCode,

        // fast fix
        multicast,
        multifixList,
        subChannelMask,
        multicastGroups,
        tickToTradeRatio,
        bookLevels,
        enqueueIncTicksOnGap,
        subscriptionFile,
        channelList,
        overrideSubscribeSet,

        // T1 & Book
        allowIntradaySecurityUpdates,
        disableDirtyAllBooksOnPacketGap,
        ignoreDirtyOnGap,
        maxEnqueueIncUpdatesOnGap,
        enqueueIncUpdatesOnGap,
        bookListener,
        logIntermediateFix,
        mktDataSrcMgr,

        //component references
        ref,
        orderRouter,
        sessionManager,
        inboundHandler,
        hubSession,
        sessionConfig,
        gwyConfig,
        clientProfileManager,
        exchangeManager,
        instLocator,
        instrumentLocator,
        instrumentStore,
        inboundDispatcher,
        outboundDispatcher,
        warmupControl,
        templateFile
    }

    static {
        for ( Tags p : Tags.values() ) {
            _set.add( p.toString().toLowerCase() );
        }
    }

    public static OMProps instance() { return _instance; }

    protected OMProps() {
        // protected
    }

    @Override
    public String getSetName() {
        return "OMProps";
    }

    @Override
    public boolean isValidTag( String tag ) {
        if ( tag == null ) return false;

        if ( _set.contains( tag.toLowerCase() ) ) {
            return true;
        }

        return super.isValidTag( tag );
    }

    @Override
    public Tag lookup( String tag ) {
        Tag val = null;

        try {
            val = Tags.valueOf( tag );
        } catch( Exception e ) {
            // ignore
        }

        if ( val == null ) {
            val = super.lookup( tag );
        }

        return val;
    }
}
