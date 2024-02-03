/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

/**
 * Historical Instrument Store
 * <p>
 * 2 versions exist, a threadsafe one and non threadsafe
 * <p>
 * Concurrent is not an option and not required, ULL wont use HistoricalInstrumentStore
 */
public interface HistInstStore extends InstrumentStore, HistExchInstSecDefLocator {

}
