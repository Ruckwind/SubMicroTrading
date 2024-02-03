/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.validate;

import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.model.generated.internal.events.impl.ClientCancelReplaceRequestImpl;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.type.*;
import com.rr.om.BaseOMTestCase;
import com.rr.om.dummy.warmup.DummyExchange;
import com.rr.om.dummy.warmup.TradingRangeImpl;
import com.rr.om.model.instrument.InstrumentWrite;
import com.rr.om.order.Order;
import com.rr.om.validate.EmeaDmaValidator;
import com.rr.om.warmup.FixTestUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestEmeaDmaValidatorAmend extends BaseTestCase {

    private ClientProfile                  _testClient;
    private Standard44DecoderOMS           _decoder;
    private ReusableString                 _buffer;
    private int                            _qty;
    private double                         _price;
    private ClientNewOrderSingleImpl       _nos;
    private ZString                        _repClOrdId;
    private ClientCancelReplaceRequestImpl _rep;
    private EmeaDmaValidator               _validator;

    @Before
    public void setUp() {
        BaseOMTestCase.unloadExchanges();

        _testClient = FixTestUtils.getTestClient();
        _decoder    = FixTestUtils.getOMSDecoder44( _testClient );
        _buffer     = new ReusableString();
        _qty        = 100;
        _price      = 25.12;
        _nos        = FixTestUtils.getClientNOS( _decoder, "TST0000001", _qty, _price, null );
        _repClOrdId = new ViewString( "TST0000002" );
        _validator  = new EmeaDmaValidator( Integer.MAX_VALUE );
        _rep        = FixTestUtils.getClientCancelReplaceRequest( _buffer, _decoder, _repClOrdId, _nos.getClOrdId(), _qty, _price, null );

        _rep.setTransactTime( CommonTimeUtils.unixTimeToInternalTime( ClockFactory.get().currentTimeMillis() ) );
        _rep.setInstrument( _nos.getInstrument() );
        _rep.setCurrency( _nos.getCurrency() );
        _rep.setSide( _nos.getSide() );
        _rep.setOrderQty( _nos.getOrderQty() - 10 );
        _rep.setTimeInForce( TimeInForce.ImmediateOrCancel );
        _rep.setPrice( _nos.getPrice() + 0.1 );
    }

    @Test
    public void testAge() {
        EmeaDmaValidator validator = new EmeaDmaValidator( 1000 );

        _rep.setTransactTime( CommonTimeUtils.unixTimeToInternalTime( ClockFactory.get().currentTimeMillis() ) );

        ThreadUtilsFactory.get().sleep( 2000 );

        Order order = FixTestUtils.createOrder( _nos, _rep );

        doTestFail( validator, order, "Request is older than max allowed seconds 1" );
    }

    @Test
    public void testCantChangeCurrency() {

        _nos.setCurrency( Currency.USD );
        _rep.setCurrency( Currency.GBP );

        Order order = FixTestUtils.createOrder( _nos, _rep );

        doTestFail( order, "Unable to change the currency  from USD to GBP" );
    }

    @Test
    public void testCantChangeOrdType() {

        _nos.setOrdType( OrdType.Limit );
        _rep.setOrdType( OrdType.Market );

        Order order = FixTestUtils.createOrder( _nos, _rep );

        doTestFail( order, "Unable to change the order type  from Limit to Market" );
    }

    @Test
    public void testCantChangeSide() {

        _nos.setSide( Side.Buy );
        _rep.setSide( Side.Sell );

        Order order = FixTestUtils.createOrder( _nos, _rep );

        doTestFail( order, "Unable to change the side  from Buy to Sell" );
    }

    @Test
    public void testChangedTIFOk() {
        _rep.setOrderQty( _nos.getOrderQty() );
        _rep.setTimeInForce( TimeInForce.FillOrKill );
        _rep.setPrice( _nos.getPrice() );

        Order order = FixTestUtils.createOrder( _nos, _rep );

        if ( !_validator.validate( _rep, order ) ) {
            assertTrue( false ); // ORDER SHOULD OF PASSED
        }
    }

    @Test
    public void testEmptyClOrdId() {

        _rep.getClOrdId().reset();

        Order order = FixTestUtils.createOrder( _nos, _rep );

        doTestFail( order, "Missing clOrdId " );
    }

    @Test
    public void testExchangeNotOpenAndRECValid() {

        DummyExchange ex = new DummyExchange( ExchangeCode.DUMMY, null, false );
        ((InstrumentWrite) _rep.getInstrument()).setExchange( ex );
        ((ExchangeInstrument) _rep.getInstrument()).getExchangeSession().setOpen( Long.MAX_VALUE );

        Order order = FixTestUtils.createOrder( _nos, _rep );

        doTestFail( order, "Exchange not open " );
    }

    @Test
    public void testFixedTickScale() {

        doTestTick( new FixedTickSize( 0.25 ), 0.27, 0.75 );
        doTestTick( new FixedTickSize( 0.1 ), 8.27, 5.9 );
        doTestTick( new FixedTickSize( 0.00001 ), 0.000005, 0.00003 );
    }

    @Test
    public void testHandlInstruction() {

        _rep.setHandlInst( HandlInst.ManualBestExec );

        Order order = FixTestUtils.createOrder( _nos, _rep );

        doTestFail( order, "Unsupported attribute value ManualBestExec type HandlInst" );
    }

    @Test
    public void testInstrumentDisabled() {

        ((InstrumentWrite) _rep.getInstrument()).setEnabled( false );

        Order order = FixTestUtils.createOrder( _nos, _rep );

        doTestFail( order, "Instrument is disabled, ID=ICAD.XPAR" );
    }

    @Test
    public void testInstrumentRestricted() {

        ((InstrumentWrite) _rep.getInstrument()).setTradeRestriction( new TradeRestriction( _rep.getInstrument(), TradeRestrictionFlag.Deprecated ) );

        Order order = FixTestUtils.createOrder( _nos, _rep );

        doTestFail( order, "Cant trade restricted stock, bookingType= orderCapacity=" );

        // @ TODO TEST RESTRICTED BUT CAN TRADE -  not yet implemented as bank specific
    }

    @Test
    public void testNegQty() {

        _rep.setOrderQty( -10 );

        Order order = FixTestUtils.createOrder( _nos, _rep );

        doTestFail( order, "Cannot amend qty below cumQty, qty=-10.0 cumQty 0.0, Quantity must be greater than zero, qty=-10.0" );
    }

    @Test
    public void testNoFieldsChange() {

        _rep.setOrderQty( _nos.getOrderQty() );
        _rep.setTimeInForce( _nos.getTimeInForce() );
        _rep.setPrice( _nos.getPrice() );

        Order order = FixTestUtils.createOrder( _nos, _rep );

        doTestFail( order, "At least one of Qty/Price/TIF must change on an amend" );
    }

    @Test
    public void testPriceBand() {

        doBandPassTest( Side.Buy, 24.5, 25.12, 25.12 );
        doBandPassTest( Side.Buy, 24.5, 25.12, 25.119 );
        doBandFailTest( Side.Buy, 24.5, 25.12, 25.121, "Invalid BUY price of 25.121, maxBuy=25.12, tickID=1 flags=0" );
        doBandFailTest( Side.Buy, 24.5, 25.12, -1, "Invalid BUY price of -1.0, maxBuy=25.12, tickID=1 flags=0" );

        doBandPassTest( Side.Sell, 24.5, 25.12, 25.12 );
        doBandPassTest( Side.Sell, 24.5, 25.12, 24.5 );
        doBandFailTest( Side.Sell, 24.5, 25.12, 24.49999, "Invalid SELL price of 24.49999, minSell=24.5, tickID=1 flags=0" );
        doBandFailTest( Side.Sell, 24.5, 25.12, -1, "Invalid SELL price of -1.0, minSell=24.5, tickID=1 flags=0" );
    }

    @Test
    public void testPriceChangedOk() {
        _rep.setOrderQty( _nos.getOrderQty() );
        _rep.setTimeInForce( _nos.getTimeInForce() );
        _rep.setPrice( _nos.getPrice() + 0.01 );

        Order order = FixTestUtils.createOrder( _nos, _rep );

        if ( !_validator.validate( _rep, order ) ) {
            assertTrue( false ); // ORDER SHOULD OF PASSED
        }
    }

    @Test
    public void testQtyChangedOk() {
        _rep.setOrderQty( _nos.getOrderQty() - 10 );
        _rep.setTimeInForce( _nos.getTimeInForce() );
        _rep.setPrice( _nos.getPrice() );

        Order order = FixTestUtils.createOrder( _nos, _rep );

        if ( !_validator.validate( _rep, order ) ) {
            assertTrue( false ); // ORDER SHOULD OF PASSED
        }
    }

    @Test
    public void testZeroQty() {

        _rep.setOrderQty( 0 );

        Order order = FixTestUtils.createOrder( _nos, _rep );

        doTestFail( order, "Quantity must be greater than zero, qty=0.0" );
    }

    private void doBandFailTest( Side side, double low, double high, double price, String expErr ) {

        _rep.setPrice( price );
        TradingRangeImpl band = (TradingRangeImpl) ((ExchangeInstrument) _rep.getInstrument()).getValidTradingRange();

        band.setMaxBuy( 1, high, 0 );
        band.setMinSell( 1, low, 0 );
        Order order = FixTestUtils.createOrder( _nos, _rep );
        _rep.setSide( side );

        doTestFail( order, expErr );
    }

    private void doBandPassTest( Side side, double low, double high, double price ) {

        EmeaDmaValidator validator = new EmeaDmaValidator( Integer.MAX_VALUE );

        _rep.setPrice( price );
        TradingRangeImpl band = (TradingRangeImpl) ((ExchangeInstrument) _rep.getInstrument()).getValidTradingRange();

        _nos.setSide( side );
        _rep.setSide( side );
        band.setMaxBuy( 1, high, 0 );
        band.setMinSell( 1, low, 0 );
        Order order = FixTestUtils.createOrder( _nos, _rep );

        if ( !validator.validate( _rep, order ) ) {
            assertTrue( false ); // ORDER SHOULD OF PASSED
        }
    }

    private void doTestFail( Order order, String expErr ) {
        EmeaDmaValidator validator = new EmeaDmaValidator( Integer.MAX_VALUE );

        doTestFail( validator, order, expErr );
    }

    private void doTestFail( EmeaDmaValidator validator, Order order, String expErr ) {
        if ( validator.validate( _rep, order ) ) {
            assertTrue( false ); // SHOULD FAIL
        } else {
            String       rejReason    = validator.getRejectReason().toString();
            OrdRejReason ordRejReason = validator.getOrdRejectReason();

            assertEquals( expErr, rejReason );
            assertSame( OrdRejReason.UnsupOrdCharacteristic, ordRejReason );
        }
    }

    private void doTestTick( FixedTickSize t, double exampleFail, double examplePass ) {

        EmeaDmaValidator validator = new EmeaDmaValidator( Integer.MAX_VALUE );

        {
            // GOOD PRICE
            _rep.setPrice( examplePass );
            ((InstrumentWrite) _rep.getInstrument()).setTickType( t );

            Order order = FixTestUtils.createOrder( _nos, _rep );

            if ( !validator.validate( _rep, order ) ) {
                assertTrue( false ); // ORDER SHOULD OF PASSED
            }
        }

        {
            // BAD PRICE
            _rep.setPrice( exampleFail );
            ((InstrumentWrite) _rep.getInstrument()).setTickType( t );

            Order          order  = FixTestUtils.createOrder( _nos, _rep );
            ReusableString expMsg = new ReusableString( "Failed tick validation " );
            t.writeError( exampleFail, expMsg );

            doTestFail( order, expMsg.toString() );
        }
    }
}
