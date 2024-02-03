/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.exchange;

import com.rr.core.lang.*;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.model.*;
import com.rr.core.properties.AppProps;
import com.rr.core.properties.CoreProps;
import com.rr.core.tasks.*;
import com.rr.core.idgen.IDGenerator;

import java.util.Calendar;
import java.util.TimeZone;

public class BaseExchangeImpl implements Exchange {

    private static final Logger _log = ConsoleFactory.console( BaseExchangeImpl.class );

    private static int _nextExchangeId = 0;

    private final Enricher            _enricher;
    private final OMExchangeValidator _validator;

    private final ExchangeCode    _exchangeCode;
    private final boolean         _usePrimarySym;
    private final boolean         _supportMarketOrders;
    private final boolean         _supportMOCOrders;
    private final TimeZone        _tz;
    private final boolean         _isMTF;
    private final IDGenerator     _execIdGen;
    private final boolean         _requiresExecIdGen;
    private final ExchangeSession _exSess;
    private final boolean         _sendCancelToExchangeAtEOD;
    private final boolean         _supportsTradeCorrection;
    private final Calendar        _expireTimeForSendEODEvents;   // @TODO implement EOD send expire events

    private final int     _id = nextExchangeId();
    private final boolean _exchangeSymIsLongId;

    private int            _nextEventId;
    private Calendar       _resetTime;
    private ScheduledEvent _exchangeResetEvent;

    private static synchronized int nextExchangeId() { return _nextExchangeId++; }

    public BaseExchangeImpl( Enricher enricher,
                             OMExchangeValidator validator,
                             ExchangeCode exchangeCode,
                             TimeZone tz,
                             boolean usePrimarySym,
                             boolean supportMarketOrders,
                             boolean supportMOCOrders,
                             boolean isMTF,
                             IDGenerator execIdGen,
                             ExchangeSession exSess,
                             boolean sendCancelToExchangeAtEOD,
                             boolean supportsTradeCorrect,
                             boolean exchangeSymIsLongId,
                             Calendar expireTimeForSendEODEvents ) {

        super();

        _enricher                   = enricher;
        _validator                  = validator;
        _exchangeCode               = exchangeCode;
        _tz                         = tz;
        _usePrimarySym              = usePrimarySym;
        _supportMarketOrders        = supportMarketOrders;
        _supportMOCOrders           = supportMOCOrders;
        _isMTF                      = isMTF;
        _requiresExecIdGen          = (execIdGen != null);
        _execIdGen                  = execIdGen;
        _exSess                     = exSess;
        _sendCancelToExchangeAtEOD  = sendCancelToExchangeAtEOD;
        _supportsTradeCorrection    = supportsTradeCorrect;
        _expireTimeForSendEODEvents = expireTimeForSendEODEvents;
        _exchangeSymIsLongId        = exchangeSymIsLongId;

        rollTimer( tz );
    }

    @Override
    public final int getId() {
        return _id;
    }

    @Override
    public final ExchangeCode getExchangeCode() {
        return _exchangeCode;
    }

    @Override
    public final boolean isExchangeAnMTF() {
        return _isMTF;
    }

    @Override
    public boolean isPrimaryRICRequired() {
        return _usePrimarySym;
    }

    @Override
    public TimeZone getTimeZone() {
        return _tz;
    }

    @Override
    public final ExchangeSession getSession() {
        return _exSess;
    }

    @Override
    public final ExchangeState getExchangeState() {
        return _exSess.getExchangeStateToday( ClockFactory.get().currentTimeMillis() );
    }

    @Override
    public boolean isSendCancelToExchangeAtEOD() {
        return _sendCancelToExchangeAtEOD;
    }

    @Override
    public boolean isGeneratedExecIDRequired() {
        return _requiresExecIdGen;
    }

    @Override
    public final long getExpireTimeToSendEndOfDayEvents() {
        long t = Constants.UNSET_LONG;

        if ( _expireTimeForSendEODEvents != null ) t = _expireTimeForSendEODEvents.getTimeInMillis();

        else if ( _resetTime != null ) t = _resetTime.getTimeInMillis();

        else t = _exSess.getCloseTime();

        return t;
    }

    @Override
    public final Enricher getEnricher() {
        return _enricher;
    }

    @Override
    public final ExchangeValidator getExchangeEventValidator() {
        return _validator;
    }

    @Override
    public ExchangeSession getExchangeSession( ZString marketSegment ) {
        return _exSess.getExchangeSession( marketSegment );
    }

    @Override
    public void generateMarketClOrdId( ReusableString dest, ZString clientClOrdId ) {
        dest.setValue( clientClOrdId );
    }

    @Override
    public void makeExecIdUnique( ReusableString execIdForUpdate, ZString execId, Instrument inst ) {
        if ( _execIdGen == null ) {
            execIdForUpdate.copy( execId );
        } else {
            _execIdGen.genID( execIdForUpdate );
        }
    }

    @Override
    public boolean isTradeCorrectionSupported() {
        return _supportsTradeCorrection;
    }

    @Override public boolean isExchangeSymbolLongId() { return _exchangeSymIsLongId; }

    @Override
    public ReusableString toString( ReusableString s ) {
        s.append( "MIC=" ).append( _exchangeCode.getMIC() ).append( ", usePrimarySym=" ).append( _usePrimarySym );
        s.append( ", isMTF=" ).append( _isMTF ).append( ", timezone=" ).append( _tz.getDisplayName() );
        s.append( ", isHalfDay=" ).append( getSession().isHalfDay() ).append( ", execIdGen=" );
        s.append( _requiresExecIdGen ).append( "\n    " );

        s.append( "enricher=" ).append( _enricher.getClass().getSimpleName() );
        s.append( ", validator=" ).append( _validator.getClass().getSimpleName() ).append( "\n    " );

        s.append( "mktOrderSup=" ).append( _supportMarketOrders ).append( ", mocSup=" ).append( _supportMOCOrders );
        s.append( ", tradeCorrSupp=" ).append( _supportsTradeCorrection );
        s.append( ", exSymLong=" ).append( _exchangeSymIsLongId );
        s.append( ", sendEODCancel=" ).append( _sendCancelToExchangeAtEOD ).append( ", expireSendTime=" );

        if ( _expireTimeForSendEODEvents != null ) {
            TimeUtilsFactory.safeTimeUtils().unixTimeToShortLocal( s, _expireTimeForSendEODEvents.getTimeInMillis() );
        } else {
            s.append( "N/A" );
        }

        if ( _resetTime != null ) {
            s.append( ", dailyReset=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToShortLocal( s, _resetTime.getTimeInMillis() );
        }

        s.append( "\n    exchangeSession=[" );
        _exSess.dump( s ).append( "]" );

        return s;
    }

    @Override public void setResetTime( final Calendar resetTime, final ScheduledEvent exchangeReset ) {
        _resetTime          = resetTime;
        _exchangeResetEvent = exchangeReset;

        if ( AppProps.instance().getBooleanProperty( CoreProps.ENABLE_EXCHANGE_RESET_EVENT, false, false ) ) {

            String future = TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( getTimeZone(), resetTime.getTimeInMillis() );

            _log.log( Level.debug, "ExchangeManager resetExpireTimer for " + _exchangeCode.name() + " to " + future );

            SchedulerFactory.get().registerGroupRepeating( exchangeReset, resetTime, Constants.MS_IN_DAY );
        }
    }

    @Override public Calendar getResetTime()                { return _resetTime; }

    @Override public ScheduledEvent getExchangeResetEvent() { return _exchangeResetEvent; }

    public ExchangeSession getExSess() {
        return _exSess;
    }

    public OMExchangeValidator getValidator() {
        return _validator;
    }

    public boolean isExecIdGen() {
        return _requiresExecIdGen;
    }

    public boolean isSupportMOCOrders() {
        return _supportMOCOrders;
    }

    public boolean isSupportMarketOrders() {
        return _supportMarketOrders;
    }

    public boolean isSupportsTradeCorrection() {
        return _supportsTradeCorrection;
    }

    protected synchronized int nextEventId()                { return _nextEventId++; }

    protected void setToday()                               { _exSess.setToday(); }

    private void rollTimer( final TimeZone tz ) {
        String         cbName    = "ExchangeDateRoll_" + _exchangeCode.getMIC();
        ZLocalDateTime localTime = new ZLocalDateTime( tz, TimeUtils.TIME_FMT_MS, "00:00:00.001" );
        SchedulerFactory.get().registerIndividualRepeating( CoreScheduledEvent.StartOfDay, new BasicSchedulerCallback( cbName, ( e ) -> setToday() ), localTime, Constants.MS_IN_DAY );
    }
}
