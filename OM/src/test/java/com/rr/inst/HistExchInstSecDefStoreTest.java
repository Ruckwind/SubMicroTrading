package com.rr.inst;

import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.model.Currency;
import com.rr.core.model.ExchangeCode;
import com.rr.core.model.SecurityIDSource;
import com.rr.core.model.SecurityType;
import com.rr.model.generated.internal.events.impl.SecurityAltIDImpl;
import com.rr.model.generated.internal.events.impl.SecurityDefinitionImpl;
import com.rr.om.BaseOMTestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class HistExchInstSecDefStoreTest extends BaseOMTestCase {

    static {
        loadExchanges();
    }

    private HistExchInstSecDefStore _store  = new HistExchInstSecDefStore( "tst", 100 );
    private int                     _nextId = 100000001;

    @Test public void addThreeIsinNoClashWithSimpleLookup() {

        SecurityDefinitionImpl secDef1 = makeSecDef( "VOD", "VODc", SecurityIDSource.ISIN, "GB000000101", Currency.GBP, ExchangeCode.CHIX );
        SecurityDefinitionImpl secDef2 = makeSecDef( "BT", "BTc", SecurityIDSource.ISIN, "GB000000102", Currency.GBP, ExchangeCode.CHIX );
        SecurityDefinitionImpl secDef3 = makeSecDef( "O2", "O2l", SecurityIDSource.ISIN, "GB000000103", Currency.GBP, ExchangeCode.XLON );

        _store.add( secDef1 );
        _store.add( secDef2 );
        _store.add( secDef3 );

        HistExchInstSecDefWrapperTS inst1 = _store.getExchInstTS( new ViewString( "GB000000101" ), SecurityIDSource.ISIN );
        HistExchInstSecDefWrapperTS inst2 = _store.getExchInstTS( new ViewString( "GB000000102" ), SecurityIDSource.ISIN );
        HistExchInstSecDefWrapperTS inst3 = _store.getExchInstTS( new ViewString( "GB000000103" ), SecurityIDSource.ISIN );

        assertSame( secDef1, inst1.getSecDef() );
        assertSame( secDef2, inst2.getSecDef() );
        assertSame( secDef3, inst3.getSecDef() );

    }

    @Test public void dupISIN() {

        SecurityDefinitionImpl secDef1 = makeSecDef( "VOD", "VODc", SecurityIDSource.ISIN, "GB000000101", Currency.GBP, ExchangeCode.CHIX );
        SecurityDefinitionImpl secDef2 = makeSecDef( "BT", "BTc", SecurityIDSource.ISIN, "GB000000101", Currency.GBP, ExchangeCode.CHIX );

        secDef1.setUniqueInstId( 1 );
        secDef1.setUniqueInstId( 2 );

        _store.add( secDef1 );
        _store.add( secDef2 );

        try {
            HistExchInstSecDefWrapperTS inst1 = _store.getExchInstTS( new ViewString( "GB000000101" ), SecurityIDSource.ISIN );

            fail( "should throw Ambiguous" );

        } catch( AmbiguousKeyRuntimeException e ) {
        }

        try {
            HistExchInstSecDefWrapperTS inst2 = _store.getExchInstTS( new ViewString( "GB000000101" ), SecurityIDSource.ISIN );

            fail( "should throw Ambiguous" );

        } catch( AmbiguousKeyRuntimeException e ) {
        }
    }

    @Test public void isinSameDifferentExchanges() {

        SecurityDefinitionImpl secDef1 = makeSecDef( "VOD", "VODc", SecurityIDSource.ISIN, "GB000000101", Currency.GBP, ExchangeCode.CHIX );
        SecurityDefinitionImpl secDef2 = makeSecDef( "BT", "BTc", SecurityIDSource.ISIN, "GB000000102", Currency.GBP, ExchangeCode.CHIX );
        SecurityDefinitionImpl secDef3 = makeSecDef( "O2", "O2l", SecurityIDSource.ISIN, "GB000000101", Currency.GBP, ExchangeCode.XLON );

        _store.add( secDef1 );
        _store.add( secDef2 );
        _store.add( secDef3 );

        HistExchInstSecDefWrapperTS inst2 = _store.getExchInstTS( new ViewString( "GB000000102" ), SecurityIDSource.ISIN );

        HistExchInstSecDefWrapperTS inst1 = _store.getExchInstTS( new ViewString( "GB000000101" ), SecurityIDSource.ISIN, ExchangeCode.CHIX );
        HistExchInstSecDefWrapperTS inst3 = _store.getExchInstTS( new ViewString( "GB000000101" ), SecurityIDSource.ISIN, ExchangeCode.XLON );

        try {
            HistExchInstSecDefWrapperTS inst4 = _store.getExchInstTS( new ViewString( "GB000000101" ), SecurityIDSource.ISIN );

            fail( "should of thrown AmbiguousKeyRuntimeException" );

        } catch( AmbiguousKeyRuntimeException e ) {
            /* expected */
        }

        assertSame( secDef1, inst1.getSecDef() );
        assertSame( secDef2, inst2.getSecDef() );
        assertSame( secDef3, inst3.getSecDef() );
    }

    @Test public void isinSameExchangeDiffCcy() {

        SecurityDefinitionImpl secDef1 = makeSecDef( "VOD", "VODc", SecurityIDSource.ISIN, "GB000000101", Currency.GBP, ExchangeCode.CHIX );
        SecurityDefinitionImpl secDef2 = makeSecDef( "VOD", "VOD2c", SecurityIDSource.ISIN, "GB000000101", Currency.USD, ExchangeCode.CHIX );
        SecurityDefinitionImpl secDef3 = makeSecDef( "O2", "O2l", SecurityIDSource.ISIN, "GB000000101", Currency.GBP, ExchangeCode.XLON );

        _store.add( secDef1 );
        _store.add( secDef2 );
        _store.add( secDef3 );

        HistExchInstSecDefWrapperTS inst1 = _store.getExchInstTS( new ViewString( "GB000000101" ), SecurityIDSource.ISIN, ExchangeCode.CHIX, Currency.GBP );
        HistExchInstSecDefWrapperTS inst2 = _store.getExchInstTS( new ViewString( "GB000000101" ), SecurityIDSource.ISIN, ExchangeCode.CHIX, Currency.USD );
        HistExchInstSecDefWrapperTS inst3 = _store.getExchInstTS( new ViewString( "GB000000101" ), SecurityIDSource.ISIN, ExchangeCode.XLON, Currency.GBP );

        try {
            HistExchInstSecDefWrapperTS inst4 = _store.getExchInstTS( new ViewString( "GB000000101" ), SecurityIDSource.ISIN, ExchangeCode.XLON );

            fail( "should of thrown AmbiguousKeyRuntimeException" );

        } catch( AmbiguousKeyRuntimeException e ) {
            /* expected */
        }

        try {
            HistExchInstSecDefWrapperTS inst4 = _store.getExchInstTS( new ViewString( "GB000000101" ), SecurityIDSource.ISIN, ExchangeCode.CHIX );

            fail( "should of thrown AmbiguousKeyRuntimeException" );

        } catch( AmbiguousKeyRuntimeException e ) {
            /* expected */
        }

        try {
            HistExchInstSecDefWrapperTS inst4 = _store.getExchInstTS( new ViewString( "GB000000101" ), SecurityIDSource.ISIN );

            fail( "should of thrown AmbiguousKeyRuntimeException" );

        } catch( AmbiguousKeyRuntimeException e ) {
            /* expected */
        }

        assertSame( secDef1, inst1.getSecDef() );
        assertSame( secDef2, inst2.getSecDef() );
        assertSame( secDef3, inst3.getSecDef() );

    }

    @Test public void isinSwitch() {

        SecurityDefinitionImpl secDef1a = makeSecDef( "VOD", "VODc", SecurityIDSource.ISIN, "GB000000101", Currency.GBP, ExchangeCode.CHIX );
        SecurityDefinitionImpl secDef2a = makeSecDef( "BT", "BTc", SecurityIDSource.ISIN, "GB000000102", Currency.GBP, ExchangeCode.CHIX );
        SecurityDefinitionImpl secDef1b = new SecurityDefinitionImpl();
        SecurityDefinitionImpl secDef2b = new SecurityDefinitionImpl();

        secDef1a.setUniqueInstId( 1 );
        secDef2a.setUniqueInstId( 2 );

        secDef1b.deepCopyFrom( secDef1a );
        secDef2b.deepCopyFrom( secDef2a );

        long isinVODTime1 = secDef1a.getEventTimestamp();
        long isinVODTime2 = secDef1a.getEventTimestamp() + 100;

        long isinBTTime1 = secDef2a.getEventTimestamp();
        long isinBTTime2 = secDef2a.getEventTimestamp() + 125;

        secDef1b.setEventTimestamp( isinVODTime2 );
        secDef2b.setEventTimestamp( isinBTTime2 );

        _store.add( secDef1a );
        _store.add( secDef2a );

        setKey( secDef1b, SecurityIDSource.ISIN, "GB000000103" );
        setKey( secDef2b, SecurityIDSource.ISIN, "GB000000101" );

        _store.add( secDef1b );
        _store.add( secDef2b );

        HistExchInstSecDefWrapperTS instA = _store.getExchInstTS( new ViewString( "GB000000101" ), SecurityIDSource.ISIN );
        HistExchInstSecDefWrapperTS instB = _store.getExchInstTS( new ViewString( "GB000000102" ), SecurityIDSource.ISIN );
        HistExchInstSecDefWrapperTS instC = _store.getExchInstTS( new ViewString( "GB000000103" ), SecurityIDSource.ISIN );

        assertSame( secDef2b, instA.getSecDef() );
        assertSame( secDef2a, instB.getAt( isinBTTime1 ).getSecDef() ); // 102 is prev   version of BT  in BT  series
        assertSame( secDef2a, instB.latest().getPrev().getSecDef() );    // 102 is prev   version of BT  in BT  series
        assertSame( secDef1b, instC.getSecDef() );                       // 103 is latest version of VOD in VOD series
        assertSame( secDef1a, instC.latest().getPrev().getSecDef() );    // 101 is prev   version of VOD in VOD series

        ExchInstSecDefWrapperTSEntry instVODV1 = _store.getExchInstAt( new ViewString( "GB000000101" ), SecurityIDSource.ISIN, isinVODTime1 );
        ExchInstSecDefWrapperTSEntry instVODV2 = _store.getExchInstAt( new ViewString( "GB000000103" ), SecurityIDSource.ISIN, isinVODTime2 );

        ExchInstSecDefWrapperTSEntry instBTV1 = _store.getExchInstAt( new ViewString( "GB000000102" ), SecurityIDSource.ISIN, isinBTTime1 );
        ExchInstSecDefWrapperTSEntry instBTV2 = _store.getExchInstAt( new ViewString( "GB000000101" ), SecurityIDSource.ISIN, isinBTTime2 );

        assertSame( secDef1a, instVODV1.getSecDef() );
        assertSame( secDef1b, instVODV2.getSecDef() );

        assertSame( secDef2a, instBTV1.getSecDef() );
        assertSame( secDef2b, instBTV2.getSecDef() );
    }

    @Test public void lookupFutByExchangeSym() {

        SecurityDefinitionImpl secDef1 = makeFutSecDef( "SPM5", SecurityIDSource.ExchangeSymbol, "SPM2005", Currency.GBP, ExchangeCode.CHIX );

        _store.add( secDef1 );

        HistExchInstSecDefWrapperTS inst1 = _store.getExchInstTS( new ViewString( "SPM5" ), SecurityIDSource.ExchangeSymbol );

        assertNull( inst1 );
    }

    @Test public void missingEntryNull() {
        HistExchInstSecDefWrapperTS inst2 = _store.getExchInstTS( new ViewString( "GB000000102" ), SecurityIDSource.ISIN );
        assertNull( inst2 );
    }

    private SecurityDefinitionImpl makeFutSecDef( String symbol, SecurityIDSource idSrc, String secId, Currency ccy, ExchangeCode exchangeCode ) {
        SecurityDefinitionImpl secDef = new SecurityDefinitionImpl();

        secDef.setSecurityType( SecurityType.Future );
        secDef.setSecurityIDSource( idSrc );
        secDef.getSecurityIDForUpdate().copy( secId );
        secDef.setSecurityExchange( exchangeCode );
        secDef.setCurrency( ccy );
        secDef.getSymbolForUpdate().copy( symbol );

        secDef.setNoSecurityAltID( 0 );

        secDef.setEventTimestamp( ClockFactory.get().currentTimeMillis() );

        return secDef;
    }

    private SecurityDefinitionImpl makeSecDef( String symbol, String exchangeSymbol, SecurityIDSource idSrc, String secId, Currency ccy, ExchangeCode exchangeCode ) {
        SecurityDefinitionImpl secDef = new SecurityDefinitionImpl();

        int tradingItemId = nextIntId();

        secDef.setSecurityType( SecurityType.Equity );
        secDef.setSecurityIDSource( SecurityIDSource.ExchangeSymbol );
        secDef.getSecurityIDForUpdate().copy( "" + tradingItemId + "." + exchangeCode.getMIC() );
        secDef.setSecurityExchange( exchangeCode );
        secDef.setCurrency( ccy );
        secDef.getSymbolForUpdate().copy( symbol );

        secDef.setNoSecurityAltID( 2 );

        SecurityAltIDImpl altId1 = new SecurityAltIDImpl();
        altId1.setSecurityAltIDSource( idSrc );
        altId1.getSecurityAltIDForUpdate().copy( secId );

        SecurityAltIDImpl altId2 = new SecurityAltIDImpl();
        altId2.setSecurityAltIDSource( SecurityIDSource.ExchangeSymbol );
        altId2.getSecurityAltIDForUpdate().copy( exchangeSymbol );

        altId1.setNext( altId2 );

        secDef.setSecurityAltIDs( altId1 );

        secDef.setEventTimestamp( ClockFactory.get().currentTimeMillis() );

        return secDef;
    }

    private int nextIntId() {
        return ++_nextId;
    }

    private void setKey( final SecurityDefinitionImpl secDef, final SecurityIDSource idSrc, final String secId ) {
        ReusableString rs = (ReusableString) InstUtils.getKey( secDef, idSrc );
        rs.copy( secId );
    }

}