/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.lang.ReusableString;
import com.rr.core.model.Instrument;

public abstract class BaseL1Book extends BaseFixedSizeBook {

    private final BookEntryImpl _bid = new BookEntryImpl();
    private final BookEntryImpl _ask = new BookEntryImpl();

    public BaseL1Book( Instrument instrument ) {
        super( instrument, 1 );
    }

    @Override public final void dump( final ReusableString dest ) {
        if ( _instrument != null ) {
            dest.append( "Book " ).append( _instrument.id() );
        } else {
            dest.append( id() );
        }
        dest.append( " [L0] " );

        dest.append( (_bid.isDirty()) ? "D " : "  " );
        dest.append( _bid.getQty() ).append( " x " ).append( _bid.getPrice() ).append( "  :  " );
        dest.append( _ask.getPrice() ).append( " x " ).append( _ask.getQty() );
        dest.append( (_ask.isDirty()) ? " D" : "  " );
    }

    @Override public double getRefPrice() { return (_bid.getPrice() + _ask.getPrice()) / 2.0; }

    @Override
    public final boolean isValid() {
        return (_bid.isValid() && _ask.isValid());
    }

    @Override
    public final void snapTo( final ApiMutatableBook dest ) {
        if ( _numLevels > 0 ) {
            dest.setLevel( 0, _bid.getNumOrders(), _bid.getQty(), _bid.getPrice(), _bid.isDirty(), _ask.getNumOrders(), _ask.getQty(), _ask.getPrice(), _ask.isDirty() );
        }
        dest.setNumLevels( _numLevels );
    }

    @Override
    public final int getActiveLevels() {
        return _numLevels;
    }

    @Override
    public final boolean getAskEntry( final int lvl, final BookLevelEntry dest ) {
        if ( lvl == 0 ) {
            dest.set( _ask.getQty(), _ask.getPrice() );
            return true;
        }
        return false;
    }

    @Override
    public final boolean getBidEntry( final int lvl, final BookLevelEntry dest ) {
        if ( lvl == 0 ) {
            dest.set( _bid.getQty(), _bid.getPrice() );
            return true;
        }
        return false;
    }

    @Override public Level getLevel() { return Level.L1; }

    @Override
    public final boolean getLevel( final int lvl, final DoubleSidedBookEntry dest ) {
        if ( lvl > _numLevels ) {
            return false;
        }
        dest.set( _bid, _ask );
        return true;
    }

    @Override
    public final void reset() {
        _ask.clear();
        _bid.clear();
    }

    @Override
    public final void setDirty( final boolean isDirty ) {
        _bid.setDirty( isDirty );
        _ask.setDirty( isDirty );
    }

    @Override public void setLevel( final int lvl, final int bidNumOrders, final double bidQty, final double bidPrice, final boolean bidIsDirty, final int askNumOrders, final double askQty, final double askPrice,
                                    final boolean askIsDirty ) {
        if ( lvl == 0 ) {
            ++_ticks;

            _bid.set( bidNumOrders, bidQty, bidPrice, bidIsDirty );
            _ask.set( askNumOrders, askQty, askPrice, askIsDirty );
        }
    }

    @Override
    public final void setLevel( final int lvl, final double buyQty, final double buyPrice, final boolean buyIsDirty, final double sellQty, final double sellPrice, final boolean sellIsDirty ) {
        if ( lvl == 0 ) {
            ++_ticks;

            _bid.set( buyQty, buyPrice, buyIsDirty );
            _ask.set( sellQty, sellPrice, sellIsDirty );
        }
    }

    @Override
    public final void setLevel( final int lvl, final BookEntryImpl bid, final BookEntryImpl ask ) {
        if ( lvl == 0 ) {
            ++_ticks;

            _bid.set( bid );
            _ask.set( ask );
        }
    }

    @Override
    public final void setNumLevels( final int lvl ) {
        _numLevels = (lvl < _maxLevels) ? lvl : _maxLevels;
        if ( _numLevels == 0 ) {
            _ask.clear();
            _bid.clear();
        }
    }

    @Override
    public final void setBid( final int lvl, final BookLevelEntry entry ) {
        if ( lvl == 0 ) {
            ++_ticks;
            _bid.set( entry.getQty(), entry.getPrice() );
        }
    }

    @Override
    public final void setBidQty( final int lvl, final double qty ) {
        if ( lvl == 0 ) {
            ++_ticks;
            _bid.setQty( qty );
        }
    }

    @Override
    public final void setBidPrice( final int lvl, final double px ) {
        if ( lvl == 0 ) {
            ++_ticks;
            _bid.setPrice( px );
        }
    }

    @Override public final void setBidNumOrders( final int lvl, final int numOrders ) {
        _bid.setNumOrders( numOrders );
    }

    @Override
    public final void insertBid( final int lvl, final BookLevelEntry entry ) {
        if ( lvl == 0 ) {
            ++_ticks;
            _bid.set( entry.getQty(), entry.getPrice() );
        }
    }

    @Override
    public final void deleteBid( final int lvl ) {
        if ( lvl == 0 ) {
            ++_ticks;
            _bid.clear();
        }
    }

    @Override public final void setBidDirty( final boolean isDirty ) {
        _bid.setDirty( isDirty );
    }

    @Override
    public final void setAsk( final int lvl, final BookLevelEntry entry ) {
        if ( lvl == 0 ) {
            ++_ticks;
            _ask.set( entry.getQty(), entry.getPrice() );
        }
    }

    @Override
    public final void setAskQty( final int lvl, final double qty ) {
        if ( lvl == 0 ) {
            ++_ticks;
            _ask.setQty( qty );
        }
    }

    @Override
    public final void setAskPrice( final int lvl, final double px ) {
        if ( lvl == 0 ) {
            ++_ticks;
            _ask.setPrice( px );
        }
    }

    @Override public final void setAskNumOrders( final int lvl, final int numOrders ) {
        _ask.setNumOrders( numOrders );
    }

    @Override
    public final void insertAsk( final int lvl, final BookLevelEntry entry ) {
        if ( lvl == 0 ) {
            ++_ticks;
            _ask.set( entry.getQty(), entry.getPrice() );
        }
    }

    @Override
    public final void deleteAsk( final int lvl ) {
        if ( lvl == 0 ) {
            ++_ticks;
            _ask.clear();
        }
    }

    @Override public final void setAskDirty( final boolean isDirty ) {
        _ask.setDirty( isDirty );
    }

    @Override public void deleteFrom( final int lvl ) {
        if ( lvl == 0 ) {
            _bid.clear();
            _ask.clear();
        }
    }

    @Override
    public final void deleteThruBid( final int lvl ) {
        if ( lvl == 0 ) {
            _bid.clear();
        }
    }

    @Override
    public final void deleteFromBid( final int lvl ) {
        if ( lvl == 0 ) {
            _bid.clear();
        }
    }

    @Override
    public final void deleteThruAsk( final int lvl ) {
        if ( lvl == 0 ) {
            _ask.clear();
        }
    }

    @Override
    public final void deleteFromAsk( final int lvl ) {
        if ( lvl == 0 ) {
            _ask.clear();
        }
    }

    @Override public void setMaxLevels( final int numLevels ) {

    }

    @Override public void setLevel( final int idx, final DoubleSidedBookEntryImpl ds ) {
        if ( idx == 0 ) {
            final BookEntryImpl bid = _bid;
            final BookEntryImpl ask = _ask;

            ++_ticks;

            bid.set( ds.getNumBuyOrders(), ds.getBidQty(), ds.getBidPx(), ds.isBidIsDirty() );
            ask.set( ds.getNumSellOrders(), ds.getAskQty(), ds.getAskPx(), ds.isAskIsDirty() );
        }
    }

    public int getBestAskNumOrders()           { return _ask._numOrders; }

    public void setBestAskNumOrders( int val ) { _ask._numOrders = val; }

    public double getBestAskPx()  { return _ask._price; }

    public void setBestAskPx( double val )     { _ask._price = val; }

    public double getBestAskQty() {
        return _ask._qty;
    }

    public void setBestAskQty( double val ) {
        _ask._qty = val;
    }

    public int getBestBidNumOrders()           { return _bid._numOrders; }

    public void setBestBidNumOrders( int val ) { _bid._numOrders = val; }

    public double getBestBidPx()  { return _bid._price; }

    public void setBestBidPx( double val )     { _bid._price = val; }

    public double getBestBidQty() { return _bid._qty; }

    public void setBestBidQty( double val )    { _bid._qty = val; }
}
