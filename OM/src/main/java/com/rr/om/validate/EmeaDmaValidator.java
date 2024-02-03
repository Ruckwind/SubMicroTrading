/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.validate;

import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.*;
import com.rr.model.generated.internal.events.interfaces.CancelReplaceRequest;
import com.rr.model.generated.internal.events.interfaces.NewOrderSingle;
import com.rr.model.generated.internal.events.interfaces.OrderRequest;
import com.rr.model.generated.internal.type.*;
import com.rr.om.Strings;
import com.rr.om.exchange.OMExchangeValidator;
import com.rr.om.order.Order;
import com.rr.om.order.OrderVersion;

public class EmeaDmaValidator implements EventValidator {

    private static final Logger _log = LoggerFactory.create( EmeaDmaValidator.class );

    private static final ZString MISSING_CLORDID          = new ViewString( "Missing clOrdId " );
    private static final ZString UNSUPPORTED              = new ViewString( "Unsupported attribute value " );
    private static final ZString UNABLE_TO_CHANGE_CCY     = new ViewString( "Unable to change the currency " );
    private static final ZString UNABLE_TO_CHANGE_ORDTYPE = new ViewString( "Unable to change the order type " );
    private static final ZString UNABLE_TO_CHANGE_SIDE    = new ViewString( "Unable to change the side " );
    private static final ZString UNABLE_TO_CHANGE_SYM     = new ViewString( "Unable to change the symbol " );
    private static final ZString CANNOT_AMEND_BELOW_CQTY  = new ViewString( "Cannot amend qty below cumQty, qty=" );
    private static final ZString MAJOR_FIELDS_UNCHANGED   = new ViewString( "At least one of Qty/Price/TIF must change on an amend" );
    private static final ZString REQ_TOO_OLD              = new ViewString( "Request is older than max allowed seconds " );
    private static final ZString INST_MISMATCH            = new ViewString( "Cant change the instrument on an order, received" );
    private static final ZString INVALID_PRICE            = new ViewString( "Failed tick validation " );
    private static final ZString MISSING_TICK             = new ViewString( "Missing tick type for instrument, ID=" );
    private static final ZString RESTRICTED               = new ViewString( "Cant trade restricted stock, bookingType=" );
    private static final ZString INSTRUMENT_DISABLED      = new ViewString( "Instrument is disabled, ID=" );

    private final ReusableString _err = new ReusableString( 256 );
    private final int            _maxAgeMS;

    public EmeaDmaValidator( int maxAge ) {
        _maxAgeMS = maxAge;
    }

    @Override
    public OrdRejReason getOrdRejectReason() {
        return OrdRejReason.UnsupOrdCharacteristic;
    }

    @Override
    public ViewString getRejectReason() {
        return _err;
    }

    @Override
    public CxlRejReason getReplaceRejectReason() {
        return CxlRejReason.Other;
    }

    @Override
    public boolean validate( CancelReplaceRequest newVersion, Order order ) {
        reset();

        final OrderVersion lastAcc  = order.getLastAckedVerion();
        final OrderRequest previous = (OrderRequest) lastAcc.getBaseOrderRequest();
        final double       cumQty   = order.getLastAckedVerion().getCumQty();

        final ExchangeInstrument inst = (ExchangeInstrument) newVersion.getInstrument();
        final Exchange           ex   = inst.getExchange();

        if ( !inst.isTestInstrument() ) { // dont validate test instruments at ALL
            final long   now       = ClockFactory.get().currentTimeMillis();
            final double newOrdQty = newVersion.getOrderQty();

            commonValidation( ex, newVersion, order, now );

            if ( newVersion.getCurrency() != previous.getCurrency() ) {               // CANT CHANGE CCY
                delim().append( UNABLE_TO_CHANGE_CCY ).append( Strings.FROM ).append( previous.getCurrency().toString() )
                       .append( Strings.TO ).append( newVersion.getCurrency().toString() );
            }

            if ( newVersion.getOrdType() != previous.getOrdType() ) {
                delim().append( UNABLE_TO_CHANGE_ORDTYPE ).append( Strings.FROM ).append( previous.getOrdType().toString() )
                       .append( Strings.TO ).append( newVersion.getOrdType().toString() );
            }

            if ( newVersion.getSide() != previous.getSide() ) {
                delim().append( UNABLE_TO_CHANGE_SIDE ).append( Strings.FROM ).append( previous.getSide().toString() )
                       .append( Strings.TO ).append( newVersion.getSide().toString() );
            }

            if ( !newVersion.getSymbol().equals( previous.getSymbol() ) ) {
                delim().append( UNABLE_TO_CHANGE_SYM ).append( Strings.FROM ).append( previous.getSymbol() )
                       .append( Strings.TO ).append( newVersion.getSymbol() );
            }

            if ( newVersion.getPrice() == previous.getPrice() &&
                 newOrdQty == previous.getOrderQty() &&
                 newVersion.getTimeInForce() == previous.getTimeInForce() ) {
                delim().append( MAJOR_FIELDS_UNCHANGED );
            }

            if ( newOrdQty < cumQty ) {
                delim().append( CANNOT_AMEND_BELOW_CQTY ).append( newOrdQty ).append( Strings.CUMQTY ).append( cumQty );
            }

            final OMExchangeValidator exchangeValidator = (OMExchangeValidator) ex.getExchangeEventValidator();
            if ( exchangeValidator != null ) {
                exchangeValidator.validate( newVersion, _err, now );
            }
        }

        return _err.length() == 0;
    }

    /**
     * dont do validation that the exchange validator is doing
     */
    @Override
    public boolean validate( NewOrderSingle msg, Order order ) {
        reset();

        final ExchangeInstrument inst = (ExchangeInstrument) msg.getInstrument();
        final Exchange           ex   = inst.getExchange();

        if ( !inst.isTestInstrument() ) { // dont validate test instruments at ALL
            final long now = ClockFactory.get().currentTimeMillis();

            commonValidation( ex, msg, order, now );

            final OMExchangeValidator exchangeValidator = (OMExchangeValidator) ex.getExchangeEventValidator();

            // qty validation done in exchangevalidator

            if ( exchangeValidator != null ) exchangeValidator.validate( msg, _err, now );
        }

        return _err.length() == 0;
    }

    private void addError( ZString msg ) {
        delim().append( msg );
    }

    private void addErrorUnsupported( Enum<?> val ) {

        delim().append( UNSUPPORTED ).append( val.toString() ).append( Strings.TYPE ).append( val.getClass().getSimpleName() );
    }

    private boolean canTradeRestricted( ClientProfile client, BookingType bookingType, OrderCapacity orderCapacity ) {
        // TODO  THIS will be client specific

        return false;
    }

    private void commonValidation( Exchange ex, OrderRequest req, Order order, long now ) {
        if ( req.getClOrdId().length() == 0 ) addError( MISSING_CLORDID );

        validateHandlingInstruction( req.getHandlInst() );
        validateAge( req.getTransactTime(), now );

        ExchangeInstrument newInst = (ExchangeInstrument) req.getInstrument();
        ExchangeInstrument oldInst = (ExchangeInstrument) order.getLastAckedVerion().getBaseOrderRequest().getInstrument();

        if ( newInst != oldInst ) {
            delim().append( INST_MISMATCH ).append( newInst.getExchangeSymbol() ).append( Strings.EXPECTED ).append( oldInst.getExchangeSymbol() );
        }

        final ViewString   exDest = req.getExDest();
        final ExchangeCode secEx  = req.getSecurityExchange();
        final double       price  = order.getPendingVersion().getMarketPrice();

        validateTicksize( newInst, price );

        if ( (newInst.getTradeRestriction() != null && newInst.getTradeRestriction().inRange( ClockFactory.get().currentTimeMillis() )) &&
             !canTradeRestricted( req.getClient(), req.getBookingType(), req.getOrderCapacity() ) ) {
            delim().append( RESTRICTED ).append( req.getBookingType() ).append( Strings.ORDCAP )
                   .append( req.getOrderCapacity() );
        }

        if ( !newInst.isEnabled() ) {
            delim().append( INSTRUMENT_DISABLED ).append( newInst.id() );
        }

        TradingRange band = newInst.getValidTradingRange();

        band.valid( price, req.getSide().getIsBuySide(), _err );
    }

    private ReusableString delim() {
        if ( _err.length() > 0 ) {
            _err.append( Strings.DELIM );
        }

        return _err;
    }

    private void reset() {
        _err.reset();
    }

    private void validateAge( long transactTime, long now ) {
        if ( (CommonTimeUtils.unixTimeToInternalTime( now ) - transactTime) > _maxAgeMS ) {
            delim().append( REQ_TOO_OLD ).append( _maxAgeMS / 1000 );
        }
    }

    private void validateHandlingInstruction( HandlInst handlInst ) {
        if ( handlInst == HandlInst.ManualBestExec ) {
            addErrorUnsupported( handlInst );
        }
    }

    private void validateTicksize( ExchangeInstrument instrument, double price ) {
        TickType ts = instrument.getTickType();

        if ( ts.canVerifyPrice() ) {
            if ( !ts.isValid( price ) ) {

                delim().append( INVALID_PRICE );

                ts.writeError( price, _err );
            }
        } else {
            ReusableString msg = TLC.instance().pop();
            msg.append( MISSING_TICK ).append( instrument.id() );
            _log.warn( msg );
            TLC.instance().pushback( msg );
        }
    }
}
