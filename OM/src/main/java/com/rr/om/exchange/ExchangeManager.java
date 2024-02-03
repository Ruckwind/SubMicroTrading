/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.exchange;

import com.rr.core.component.SMTComponent;
import com.rr.core.idgen.IDGenerator;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.lang.TimeUtilsFactory;
import com.rr.core.lang.ZString;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Exchange;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.ExchangeInstrument;
import com.rr.core.model.ExchangeSession;
import com.rr.core.tasks.ScheduledEvent;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.md.book.l3.L3BookFactoryLSE;
import com.rr.model.generated.codec.ItchLSEDecoder;
import com.rr.om.asia.bse.BSEExchange;
import com.rr.om.emea.exchange.chix.CHIXExchange;
import com.rr.om.emea.exchange.eti.eurex.ETIMessageValidatorHFT;
import com.rr.om.emea.exchange.eti.eurex.EurexETIExchange;
import com.rr.om.emea.exchange.millenium.lse.LSEExchange;
import com.rr.om.emea.exchange.utp.enx.ENXExchange;
import com.rr.om.emea.exchange.utp.liffe.LiffeExchange;
import com.rr.om.us.cme.CMEExchange;

import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @TODO move to component and remove singleton ... remove the hard coded per class exchange
 */

public class ExchangeManager implements SMTComponent {

    private static final Logger          _log      = LoggerFactory.create( ExchangeManager.class );
    private static final ExchangeManager _instance = new ExchangeManager( "ExchangeManagerSingleton" );

    private static final int NUM_SYMBOLS = 1024; // TODO get num symbols from config
    private final Map<ExchangeCode, Exchange> _exchangeCodeMap = new ConcurrentHashMap<>( 32, 0.75f, 2 );
    private final Map<ZString, Exchange>      _micMap          = new ConcurrentHashMap<>( 32, 0.75f, 2 );
    private       String                      _id;
    private IDGenerator _mktClOrdIdGen;  // some exchanges require specific IDGenerator

    public static ExchangeManager instance() { return _instance; }

    public ExchangeManager( String id ) {
        _id = id;
    }

    @Override
    public String getComponentId() {
        return _id;
    }

    public void clear() {
        _log.warn( "Clearing ExchangeManager map" );

        _micMap.clear();
        _exchangeCodeMap.clear();
    }

    public void forEach( final Consumer<? super Exchange> consumer ) {
        _exchangeCodeMap.values().forEach( consumer );
    }

    public Exchange get( final ExchangeInstrument instrument ) {
        return getByCode( instrument.getPrimaryExchangeCode() );
    }

    public Exchange getByCode( ExchangeCode code ) {
        if ( code == null ) return null;

        return _exchangeCodeMap.get( code );
    }

    public Exchange getByMIC( ZString mic ) {
        return _micMap.get( mic );
    }

    @SuppressWarnings( "EqualsBetweenInconvertibleTypes" )
    public void register( ExchangeCode code,
                          TimeZone timezone,
                          Calendar eodExpireEventSend,
                          ExchangeSession session,
                          Calendar resetTime ) {

        Exchange exchange = null;

        switch( code ) {
        case XAMS:
            exchange = new ENXExchange( code, timezone, session, _mktClOrdIdGen, eodExpireEventSend );
            break;
        case XBOM:
            exchange = new BSEExchange( code, timezone, session, _mktClOrdIdGen, eodExpireEventSend );
            break;
        case CHIX:
            exchange = new CHIXExchange( code, timezone, session, eodExpireEventSend );
            break;
        case XCME:
            exchange = new CMEExchange( code, timezone, session, _mktClOrdIdGen, eodExpireEventSend );
            break;
        case XEUR:
            // TODO need clean way of specifying use HFT or LFT message validator
            exchange = new EurexETIExchange( code, timezone, session, _mktClOrdIdGen, eodExpireEventSend, new ETIMessageValidatorHFT() );
            break;
        case XLIF:
            exchange = new LiffeExchange( code, timezone, session, _mktClOrdIdGen, eodExpireEventSend );
            break;
        case XLIS:
            exchange = new ENXExchange( code, timezone, session, _mktClOrdIdGen, eodExpireEventSend );
            break;
        case XLON:
            ItchLSEDecoder.setBookFactory( L3BookFactoryLSE.class, NUM_SYMBOLS );
            // @TODO set L3 book factory with actual inst locator
            // L3BookFactoryLSE.setInstrumentLocator( new DummyInstrumentLocator() );
            exchange = new LSEExchange( code, timezone, session, eodExpireEventSend );
            break;
        case XMAT:
            exchange = new ENXExchange( code, timezone, session, _mktClOrdIdGen, eodExpireEventSend );
            break;
        case XPAR:
            exchange = new ENXExchange( code, timezone, session, _mktClOrdIdGen, eodExpireEventSend );
            break;
        case XSWX:
            exchange = new GenericExchange( code, timezone, session, _mktClOrdIdGen, eodExpireEventSend );
            break;
        case XSTO:
            exchange = new GenericExchange( code, timezone, session, _mktClOrdIdGen, eodExpireEventSend );
            break;
        case XETR:
            exchange = new GenericExchange( code, timezone, session, _mktClOrdIdGen, eodExpireEventSend );
            break;

        case IFEN:
        case IFED:
        case IFLL:
        case IFLX:
        case IFUS:
        case NZFX:
        case XASX:
        case XCBF:
        case XCBT:
        case XOSE:
        case XCEC:
        case XMOD:
        case XNYM:
        case XTKT:
        case DUMX:
        case IFEU:
        case KBCB:
        case XBRU:
        case XCBO:
        case XCSE:
        case XEMD:
        case XFRA:
        case XHEL:
        case XKBT:
        case XKLS:
        case XMGE:
        case XNYE:
        case XNYL:
        case XNYS:
        case XOSL:
        case TST:
        case DUMMY:
        case UNKNOWN:
        default:
            exchange = new GenericExchange( code, timezone, session, _mktClOrdIdGen, eodExpireEventSend );
            break;
        }

        if ( exchange == null ) {
            throw new SMTRuntimeException( "ExchangeManager attempt to add unsupported exchange mic=" + code );
        }

        ScheduledEvent exchangeReset = new ExchangeResetEvent( exchange );
        exchange.setResetTime( resetTime, exchangeReset );

        register( exchange );
    }

    public void register( IDGenerator mktClOrdIdGen ) {
        _mktClOrdIdGen = mktClOrdIdGen;
    }

    public void resetExpireTimers() {
        _exchangeCodeMap.values().forEach( ( e ) -> {
            Calendar c         = TimeUtilsFactory.safeTimeUtils().getCalendar( e.getTimeZone() );
            Calendar resetCall = e.getResetTime();
            resetCall.set( Calendar.YEAR, c.get( Calendar.YEAR ) );
            resetCall.set( Calendar.DAY_OF_YEAR, c.get( Calendar.DAY_OF_YEAR ) );

            e.setResetTime( resetCall, e.getExchangeResetEvent() );

        } );
    }

    public void setId( String id ) {
        _id = id;
    }

    private void register( Exchange exchange ) {
        if ( _exchangeCodeMap.containsKey( exchange.getExchangeCode() ) ) {
            throw new SMTRuntimeException( "ExchangeManager attempt to add duplicate exchange mic=" + exchange.getExchangeCode().getMIC() );
        }

        _exchangeCodeMap.put( exchange.getExchangeCode(), exchange );
        _micMap.put( exchange.getExchangeCode().getMIC(), exchange );

        if ( _log.isEnabledFor( Level.debug ) ) {
            ReusableString msg = TLC.instance().pop();
            msg.append( "Exchange Registered :\n[" );
            exchange.toString( msg );
            _log.log( Level.debug, msg );
            TLC.instance().pushback( msg );
        }
    }
}
