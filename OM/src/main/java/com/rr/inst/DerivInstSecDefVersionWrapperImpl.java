/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.inst;

import com.rr.core.lang.Constants;
import com.rr.core.model.CommonInstrument;
import com.rr.core.model.Exchange;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;

public class DerivInstSecDefVersionWrapperImpl extends DerivInstSecDefWrapperImpl implements DerivInstSecDefVersionWrapper {

    private final HistExchInstSecDefWrapperTS  _series;
    private       ExchInstSecDefWrapperTSEntry _next;
    private       ExchInstSecDefWrapperTSEntry _prev;
    private       long                         _startTimeStamp = Constants.UNSET_LONG;
    private       long                         _endTimeStamp   = Constants.UNSET_LONG;

    public DerivInstSecDefVersionWrapperImpl( Exchange exchange, SecurityDefinitionImpl secDef, CommonInstrument commonInstrument, int legCnt, HistExchInstSecDefWrapperTS series ) {
        super( exchange, secDef, commonInstrument, legCnt );
        _series = series;
    }

    @Override public HistExchInstSecDefWrapperTS getSeries() { return _series; }

    @Override public long getStartTimestamp()                            { return _startTimeStamp; }

    @Override public long getEndTimestamp()                              { return _endTimeStamp; }

    @Override public void setEndTimestamp( long endTimeStamp )           { _endTimeStamp = endTimeStamp; }

    @Override public ExchInstSecDefWrapperTSEntry getNext()  { return _next; }

    @Override public ExchInstSecDefWrapperTSEntry getPrev()  { return _prev; }

    @Override public void setStartTimestamp( final long startTimestamp ) { _startTimeStamp = startTimestamp; }

    @Override public ExchInstSecDefWrapperTSEntry setNext( final ExchInstSecDefWrapperTSEntry next ) {
        ExchInstSecDefWrapperTSEntry old = _next;
        _next = next;
        return old;
    }

    @Override public ExchInstSecDefWrapperTSEntry setPrev( final ExchInstSecDefWrapperTSEntry prev ) {
        ExchInstSecDefWrapperTSEntry old = _prev;
        _prev = prev;
        return old;
    }
}
