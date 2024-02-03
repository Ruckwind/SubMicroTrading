/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.validate;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.CommonTimeUtils;
import com.rr.core.lang.ReusableString;
import com.rr.core.model.*;
import com.rr.core.utils.ThreadUtilsFactory;
import com.rr.model.generated.fix.codec.Standard44DecoderOMS;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.model.generated.internal.type.HandlInst;
import com.rr.model.generated.internal.type.OrdRejReason;
import com.rr.model.generated.internal.type.Side;
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

public class TestEmeaDmaValidatorNOS extends BaseTestCase {

    protected Standard44DecoderOMS _decoder = FixTestUtils.getOMSDecoder44();

    @Before
    public void prep() {
        BaseOMTestCase.unloadExchanges();
    }

    @Test
    public void testAge() {
        EmeaDmaValidator validator = new EmeaDmaValidator( 1000 );

        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, 25.12, null );
        nos.setTransactTime( CommonTimeUtils.unixTimeToInternalTime( ClockFactory.get().currentTimeMillis() ) );

        ThreadUtilsFactory.get().sleep( 2000 );

        Order order = FixTestUtils.createOrder( nos );

        doTestFail( validator, nos, order, "Request is older than max allowed seconds 1" );
    }

    @Test
    public void testExchangeNotOpenAndIDValid() {

        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, 25.12, null );

        DummyExchange ex = new DummyExchange( ExchangeCode.DUMMY, null, false );
        ((InstrumentWrite) nos.getInstrument()).setExchange( ex );
        ((ExchangeInstrument) nos.getInstrument()).getExchangeSession().setOpen( Long.MAX_VALUE );

        Order order = FixTestUtils.createOrder( nos );

        doTestFail( nos, order, "Exchange not open " );
    }

    @Test
    public void testFixedTickScale() {

        doTestTick( new FixedTickSize( 0.25 ), 0.27, 0.75 );
        doTestTick( new FixedTickSize( 0.1 ), 8.27, 5.9 );
        doTestTick( new FixedTickSize( 0.00001 ), 0.000005, 0.00003 );
        doTestTick( new FixedTickSize( 2 ), 4.00001, 4.0 );
    }

    @Test
    public void testHandlInstruction() {

        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, 25.12, null );
        nos.setHandlInst( HandlInst.ManualBestExec );

        Order order = FixTestUtils.createOrder( nos );

        doTestFail( nos, order, "Unsupported attribute value ManualBestExec type HandlInst" );
    }

    @Test
    public void testInstrumentDisabled() {

        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, 25.12, null );
        ((InstrumentWrite) nos.getInstrument()).setEnabled( false );

        Order order = FixTestUtils.createOrder( nos );

        doTestFail( nos, order, "Instrument is disabled, ID=ICAD.XPAR" );
    }

    @Test
    public void testInstrumentRestricted() {

        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, 25.12, null );
        ((InstrumentWrite) nos.getInstrument()).setTradeRestriction( new TradeRestriction( nos.getInstrument(), TradeRestrictionFlag.Deprecated ) );

        Order order = FixTestUtils.createOrder( nos );

        doTestFail( nos, order, "Cant trade restricted stock, bookingType= orderCapacity=" );

        // @ TODO TEST RESTRICTED BUT CAN TRADE -  not yet implemented as bank specific
    }

    @Test
    public void testNegQty() {

        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, 25.12, null );
        nos.setOrderQty( -10 );

        Order order = FixTestUtils.createOrder( nos );

        doTestFail( nos, order, "Quantity must be greater than zero, qty=-10.0" );
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
    public void testTwoErrIncEmptyClOrdId() {

        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "", 100, 25.12, null );
        nos.setOrderQty( -10 );

        Order order = FixTestUtils.createOrder( nos );

        doTestFail( nos, order, "Missing clOrdId , Quantity must be greater than zero, qty=-10.0" );
    }

    @Test
    public void testZeroQty() {

        ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, 25.12, null );
        nos.setOrderQty( 0 );

        Order order = FixTestUtils.createOrder( nos );

        doTestFail( nos, order, "Quantity must be greater than zero, qty=0.0" );
    }

    private void doBandFailTest( Side side, double low, double high, double price, String expErr ) {

        ClientNewOrderSingleImpl nos  = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, price, null );
        TradingRangeImpl         band = (TradingRangeImpl) ((ExchangeInstrument) nos.getInstrument()).getValidTradingRange();

        band.setMaxBuy( 1, high, 0 );
        band.setMinSell( 1, low, 0 );
        Order order = FixTestUtils.createOrder( nos );
        nos.setSide( side );

        doTestFail( nos, order, expErr );
    }

    private void doBandPassTest( Side side, double low, double high, double price ) {

        EmeaDmaValidator validator = new EmeaDmaValidator( Integer.MAX_VALUE );

        ClientNewOrderSingleImpl nos  = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, price, null );
        TradingRangeImpl         band = (TradingRangeImpl) ((ExchangeInstrument) nos.getInstrument()).getValidTradingRange();

        nos.setSide( side );
        band.setMaxBuy( 1, high, 0 );
        band.setMinSell( 1, low, 0 );
        Order order = FixTestUtils.createOrder( nos );

        if ( !validator.validate( nos, order ) ) {
            assertTrue( false ); // ORDER SHOULD OF PASSED
        }
    }

    private void doTestFail( ClientNewOrderSingleImpl nos, Order order, String expErr ) {
        EmeaDmaValidator validator = new EmeaDmaValidator( Integer.MAX_VALUE );

        doTestFail( validator, nos, order, expErr );
    }

    private void doTestFail( EmeaDmaValidator validator, ClientNewOrderSingleImpl nos, Order order, String expErr ) {
        if ( validator.validate( nos, order ) ) {
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
            ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, examplePass, null );
            ((InstrumentWrite) nos.getInstrument()).setTickType( t );

            Order order = FixTestUtils.createOrder( nos );

            if ( !validator.validate( nos, order ) ) {
                assertTrue( false ); // ORDER SHOULD OF PASSED
            }
        }

        {
            // BAD PRICE
            ClientNewOrderSingleImpl nos = FixTestUtils.getClientNOS( _decoder, "TST0000001", 100, exampleFail, null );
            ((InstrumentWrite) nos.getInstrument()).setTickType( t );

            Order          order  = FixTestUtils.createOrder( nos );
            ReusableString expMsg = new ReusableString( "Failed tick validation " );
            t.writeError( exampleFail, expMsg );

            doTestFail( nos, order, expMsg.toString() );
        }
    }
}
