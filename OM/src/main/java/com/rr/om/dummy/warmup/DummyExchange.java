/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.dummy.warmup;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.*;
import com.rr.core.tasks.ScheduledEvent;
import com.rr.om.client.OMEnricher;
import com.rr.om.exchange.OMExchangeValidator;
import com.rr.core.idgen.DailySimpleIDGenerator;
import com.rr.core.idgen.IDGenerator;

import java.util.Calendar;
import java.util.TimeZone;

public class DummyExchange implements Exchange {

    public static final Exchange DUMMY = new DummyExchange( ExchangeCode.DUMMY, new DailySimpleIDGenerator( new ViewString( "DUM" ) ), false );
    private final ExchangeCode    _code;
    private final boolean         _isMTF;
    private final IDGenerator     _numericIDGenerator;
    private final ExchangeSession _exSess;
    private OMEnricher          _enricher       = new DummyEnricher();
    private OMExchangeValidator _dummyValidator = new DummyValidator();
    private ExchangeState       _state          = ExchangeState.Continuous;
    private ExchangeSession     _session        = new DummyExchangeSession();
    private boolean             _execIdUnqiue   = true;
    private boolean _exchangeSymbolLongId = false;

    public DummyExchange( ExchangeCode c, IDGenerator idGen, boolean isMTF ) {
        _code               = c;
        _numericIDGenerator = idGen;
        _isMTF              = isMTF;
        _exSess             = new DummyExchangeSession();
    }

    @Override
    public void generateMarketClOrdId( ReusableString dest, ZString clientClOrdId ) {
        if ( _numericIDGenerator != null ) {

            _numericIDGenerator.genID( dest );

        } else {
            dest.copy( clientClOrdId );
        }
    }

    @Override
    public Enricher getEnricher() {
        return _enricher;
    }

    @Override
    public ExchangeCode getExchangeCode() {
        return _code;
    }

    @Override
    public ExchangeValidator getExchangeEventValidator() {
        return _dummyValidator;
    }

    @Override public ScheduledEvent getExchangeResetEvent() {
        return null;
    }

    @Override
    public ExchangeSession getExchangeSession( ZString marketSegment ) {
        return _exSess;
    }

    @Override
    public ExchangeState getExchangeState() {
        return _state;
    }

    @Override
    public long getExpireTimeToSendEndOfDayEvents() {
        return 0;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override public Calendar getResetTime() {
        return null;
    }

    @Override
    public ExchangeSession getSession() {
        return _session;
    }

    @Override
    public TimeZone getTimeZone() {
        return null;
    }

    @Override
    public boolean isExchangeAnMTF() {
        return _isMTF;
    }

    @Override public boolean isExchangeSymbolLongId() {
        return _exchangeSymbolLongId;
    }

    @Override
    public boolean isGeneratedExecIDRequired() {
        return _execIdUnqiue == false;
    }

    @Override
    public boolean isPrimaryRICRequired() {
        return false;
    }

    @Override
    public boolean isSendCancelToExchangeAtEOD() {
        return false;
    }

    @Override
    public boolean isTradeCorrectionSupported() {
        return true;
    }

    @Override
    public void makeExecIdUnique( ReusableString execIdForUpdate, ZString execId, Instrument inst ) {

        if ( _execIdUnqiue ) {
            execIdForUpdate.copy( execId );
        } else {
            execIdForUpdate.copy( ((ExchangeInstrument) inst).getExchange().getExchangeCode().getMIC() );
            execIdForUpdate.append( inst.getCurrency().getVal() ).append( execId );
        }
    }

    @Override public void setResetTime( final Calendar resetTime, final ScheduledEvent exchangeReset ) {
        /* nothing */
    }

    @Override
    public ReusableString toString( ReusableString buf ) {
        buf.append( "DummyExchange" );
        return buf;
    }

    public void setExchangeSymbolLongId( final boolean exchangeSymbolLongId ) {
        _exchangeSymbolLongId = exchangeSymbolLongId;
    }

    public boolean isExecIdUnqiue() {
        return _execIdUnqiue;
    }

    public boolean isNumericClOrdIdRequired() {
        return _numericIDGenerator != null;
    }

    public boolean setExecIdUnqiue( boolean execIdUnqiue ) {
        boolean old = _execIdUnqiue;

        _execIdUnqiue = execIdUnqiue;

        return old;
    }
}
