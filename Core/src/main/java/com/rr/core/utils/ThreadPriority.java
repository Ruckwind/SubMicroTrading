/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

public enum ThreadPriority implements SchedulingPriority {

    /**
     * SMT Process (upto 2 inbound and outbound sessions and 1 processor)
     */
    Processor,
    SessionInbound1,
    SessionOutbound1,

    SessionInbound2,
    SessionOutbound2,

    /**
     * placeholders for apps with more pipes
     */
    DataIn1,
    DataIn2,
    DataIn3,
    DataIn4,
    DataIn5,
    DataIn6,
    DataIn7,
    DataIn8,

    Controller1,
    Controller2,
    Controller3,
    Controller4,

    Algo1,
    Algo2,
    Algo3,
    Algo4,
    Algo5,
    Algo6,
    Algo7,
    Algo8,

    // General SMT 
    MultiSessConnector,
    MemMapAllocator,
    Main,

    Lowest,
    Scheduler,
    BackgroundLogger,
    SessionInboundOther,
    SessionOutboundOther,
    PriceTolerance,

    Other,
    HubSimulator,
    HubProcessor,
    ClientSimulatorMain,
    ClientSimulatorIn,
    ClientSimulatorOut,
    ExchangeSimulatorIn,
    ExchangeSimProcessor,
    ExchangeSimulatorOut;

    public final static int LOWEST  = 1;
    public final static int LOW     = 2;
    public final static int MEDIUM  = 3;
    public final static int HIGH    = 4;
    public final static int HIGHEST = 5;

    private int        _mask;
    private int        _priority;
    private CPU        _cpu;
    private CoreThread _coreThread;

    ThreadPriority() {
        _priority   = MEDIUM;
        _cpu        = CPU.ANY;
        _coreThread = CoreThread.ANY;
        _mask       = UNKNOWN_MASK;
    }

    @Override
    public int getPriority() {
        return _priority;
    }

    @Override
    public CPU getCPU() {
        return _cpu;
    }

    @Override
    public CoreThread getCoreThread() {
        return _coreThread;
    }

    @Override
    public void setPriority( int priority ) {
        _priority = priority;
    }    @Override
    public int getMask() {
        return _mask;
    }

    @Override
    public void setMask( int mask ) {
        _mask = mask;
    }


}
