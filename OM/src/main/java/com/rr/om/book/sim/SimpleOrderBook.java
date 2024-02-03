/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.book.sim;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ViewString;
import com.rr.core.lang.ZString;
import com.rr.core.model.ExchangeBook;
import com.rr.core.pool.SuperpoolManager;
import com.rr.model.generated.internal.events.factory.TradeNewFactory;
import com.rr.model.generated.internal.events.impl.TradeNewImpl;
import com.rr.model.generated.internal.events.interfaces.TradeNew;
import com.rr.model.generated.internal.type.OrdStatus;
import com.rr.model.generated.internal.type.OrdType;
import com.rr.model.generated.internal.type.Side;
import com.rr.model.internal.type.ExecType;
import com.rr.om.model.id.AnnualBase32IDGenerator;
import com.rr.core.idgen.IDGenerator;

// @NOTE only generates one sided fill
// @TODO implement proper book and generate fills for both sides as appropriate

public class SimpleOrderBook implements ExchangeBook {

    private static final ZString FILL = new ViewString( "TRADE" );
    private static TradeNewFactory _tradeNewFactory;

    private static class Level {

        private final double _price;
        private       double _buyQty  = 0;
        private       double _sellQty = 0;
        private       Level  _next    = null;
        private       Level  _prev    = null;

        Level( double price )             { _price = price; }

        void addBuyQty( double buyQty )   { _buyQty += buyQty; }

        void addSellQty( double sellQty ) { _sellQty += sellQty; }

        double getBuyQty()                { return _buyQty; }

        void setBuyQty( double buyQty )   { _buyQty = buyQty; }

        Level getNext()                   { return _next; }

        void setNext( Level next )        { _next = next; }

        Level getPrev()                   { return _prev; }

        void setPrev( Level prev )        { _prev = prev; }

        double getPrice()                 { return _price; }

        double getSellQty()               { return _sellQty; }

        void setSellQty( double sellQty ) { _sellQty = sellQty; }
    }
    private final Level       _low;
    private final Level       _high;
    private       double      _lastTrade;
    private       double      _lastTradePrice;
    private       IDGenerator _nextIdGen;

    public static synchronized void init() {

        if ( _tradeNewFactory == null ) {
            SuperpoolManager sp = SuperpoolManager.instance();

            _tradeNewFactory = sp.getFactory( TradeNewFactory.class, TradeNewImpl.class );
        }
    }

    public SimpleOrderBook() {

        init();

        _low  = new Level( 0 );
        _high = new Level( Double.MAX_VALUE );

        _low.setNext( _high );
        _high.setPrev( _low );

        _nextIdGen = new AnnualBase32IDGenerator( new ViewString( "T" ), 7 );
    }

    public synchronized TradeNew add( final ZString mktOrdId, final double orderQty, final double price, final OrdType ordType, final Side side ) {

        if ( price <= 0 || orderQty <= 0 ) return null;

        double trade;
        double needed        = orderQty;
        double tradePriceSum = 0;

        if ( side.getIsBuySide() ) {

            Level curL = _low;

            while( curL != null && needed > 0 && (price - curL.getPrice()) > -0.0000005 ) {
                double curAvail = curL.getSellQty();
                if ( curAvail > 0 ) {
                    double diff = curAvail - needed;
                    if ( diff < 0 ) {
                        curL.setSellQty( 0 );
                        tradePriceSum += (curAvail * curL.getPrice());
                        needed -= curAvail;
                    } else { // full fill
                        curL.setSellQty( diff );
                        tradePriceSum += (needed * curL.getPrice());
                        needed = 0;
                    }
                }
                curL = curL.getNext();
            }

            if ( needed > 0 ) {
                Level l = getLevel( price );
                l.addBuyQty( needed );
            }

        } else {
            Level curL = _high;

            while( curL != null && needed > 0 && (curL.getPrice() - price) > -0.0000005 ) {
                double curAvail = curL.getBuyQty();
                if ( curAvail > Constants.TICK_WEIGHT ) {
                    double diff = curAvail - needed;
                    if ( diff < Constants.TICK_WEIGHT ) {
                        curL.setBuyQty( 0 );
                        tradePriceSum += (curAvail * curL.getPrice());
                        needed -= curAvail;
                    } else { // full fill
                        curL.setBuyQty( diff );
                        tradePriceSum += (needed * curL.getPrice());
                        needed = 0;
                    }
                }
                curL = curL.getPrev();
            }

            if ( needed > Constants.TICK_WEIGHT ) {
                Level l = getLevel( price );
                l.addSellQty( needed );
            }
        }

        trade = orderQty - needed;

        if ( trade > Constants.TICK_WEIGHT ) {
            _lastTrade      = trade;
            _lastTradePrice = price;
            return (makeTrade( orderQty, _lastTrade, _lastTradePrice, tradePriceSum / trade, mktOrdId, side ));
        }

        return null;
    }

    public synchronized TradeNew amend( ZString marketOrderId, double newQty, double origQty, double fillQty, double newPrice, double origPrice, OrdType ordType, Side side ) {
        remove( marketOrderId, origQty - fillQty, origPrice, ordType, side );
        return add( marketOrderId, newQty - fillQty, newPrice, ordType, side );
    }

    public synchronized void remove( ZString marketOrderId, double openQty, double price, OrdType ordType, Side side ) {

        if ( price <= 0 || openQty <= 0 ) return;

        Level l = getLevel( price );

        if ( side.getIsBuySide() ) {
            double qty = l.getBuyQty();
            qty -= openQty;
            l.setBuyQty( (qty >= Constants.TICK_WEIGHT) ? qty : 0 );
        } else {
            double qty = l.getSellQty();
            qty -= openQty;
            l.setSellQty( (qty >= Constants.TICK_WEIGHT) ? qty : 0 );
        }
    }

    private Level getLevel( double price ) {
        Level l = _low;

        while( price > l.getPrice() ) {
            l = l.getNext();
        }

        // @NOTE should really ensure its in a tick bucket
        if ( Math.abs( price - l.getPrice() ) > Constants.WEIGHT ) {

            Level newL = new Level( price );
            newL.setPrev( l.getPrev() );
            newL.setNext( l );
            l.getPrev().setNext( newL );
            l.setPrev( newL );

            l = newL;
        }

        return l;
    }

    private TradeNew makeTrade( double orderQty, double lastTrade, double price, double avgPrice, ZString mktOrdId, Side side ) {
        TradeNewImpl trade = _tradeNewFactory.get();
        trade.setAvgPx( avgPrice );
        trade.setPrice( price );
        trade.setOrderQty( orderQty );
        trade.setLastQty( lastTrade );
        trade.setLastPx( avgPrice );
        trade.setSide( side );
        trade.getOrderIdForUpdate().copy( mktOrdId );

        trade.setOrdStatus( (lastTrade == orderQty) ? OrdStatus.Filled : OrdStatus.PartiallyFilled );
        trade.setExecType( ExecType.Trade );

        ReusableString execId = trade.getExecIdForUpdate();

        _nextIdGen.genID( execId );

        return trade;
    }
}
