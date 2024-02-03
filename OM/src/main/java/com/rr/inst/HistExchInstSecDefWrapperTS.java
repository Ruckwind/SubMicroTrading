package com.rr.inst;

import com.rr.core.model.HistoricalExchangeInstrument;
import com.rr.model.generated.internal.events.interfaces.SecurityDefinition;

/**
 * Historical Exchange Instrument Security Definition Wrapper Time Series
 */
public interface HistExchInstSecDefWrapperTS extends HistExchInst, HistoricalExchangeInstrument<ExchInstSecDefWrapperTSEntry>, InstrumentSecurityDefWrapper {

    boolean isNewVersionRequired( SecurityDefinition newDef );
}
