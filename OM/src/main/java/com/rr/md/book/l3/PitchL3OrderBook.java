/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book.l3;

import com.rr.core.collections.LongHashMap;
import com.rr.core.collections.LongHashSet;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.stats.SizeConstants;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.Event;
import com.rr.core.model.Instrument;
import com.rr.core.model.book.*;
import com.rr.core.properties.AppProps;
import com.rr.core.utils.Utils;
import com.rr.core.utils.lock.OptimisticReadWriteLock;
import com.rr.core.utils.lock.StampedLockProxy;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.impl.*;
import com.rr.model.generated.internal.type.MMTMarketMechanism;
import com.rr.model.generated.internal.type.MMTTradingMode;
import com.rr.model.generated.internal.type.Side;
import com.rr.om.main.OMProps;

/**
 * full order book implementation ... uses book events to maintain a book
 * <p>
 * Mutation must only occur in one thread which must protect against another thread invoking snap
 * <p>
 * May be MANY books loaded against several threads which is why the pools need to be aligned to the thread not the book
 * <p>
 * Assumption is that 99% of updates will not be dups and as such code is optimised to assumption data is good
 * <p>
 * Book comprises of doubly linked list of FullBookLevelEntry, runnig from Best to Worst .... next() gives next worst, prev() gives better, worst entry fixed
 */

public final class PitchL3OrderBook extends AbstractBook implements EventMutatableBook {

    static final double DUMMY_BID = Long.MIN_VALUE;
    static final double DUMMY_ASK = Double.MAX_VALUE;
    private static final Logger _log = LoggerFactory.create( PitchL3OrderBook.class );
    private static final ErrorCode ERR_OVERFILL = new ErrorCode( "PL3100", "OVERFILL : posDupTrade ? Requires manual verification !" );
    private static final ErrorCode ERR_RECYCLED = new ErrorCode( "PL3200", "Book error, orderEntry already recycled ... check for dup event" );
    private static final int MIN_ORDERS            = 16;
    private static final int DEFAULT_EXEC_SET_SIZE = SizeConstants.DEFAULT_EXEC_ID_SET_SIZE_PER_BOOK;
    private final           PitchBookPoolMgr<OrderBookEntry> _poolMgr;
    private final           LongHashMap<OrderBookEntry>      _orderEntryMap;
    private final           FullBookLevelEntry               _worstLevelBuy  = new FullBookLevelEntry( DUMMY_BID );
    private final           FullBookLevelEntry               _worstLevelSell = new FullBookLevelEntry( DUMMY_ASK );
    private final transient StampedLockProxy                 _lock           = new StampedLockProxy();
    private final           LongHashSet                      _execSet;
    private                 FullBookLevelEntry               _bestLevelBuy   = _worstLevelBuy;
    private                 FullBookLevelEntry               _bestLevelSell  = _worstLevelSell;
    private                 int                              _buyLevels;
    private                 int                              _sellLevels;
    private                 double                           _totalValueTradedOnBook;
    private                 double                           _totalValueTradedOffBook;
    private                 long                             _totalVolTradedOnBook;
    private                 long                             _totalVolTradedOffBook;
    private                 int                              _numTradesOnBook;
    private                 int                              _numTradesOffBook;
    private                 int                              _maxActiveOrders;
    private                 ReusableString                   _logMsg         = new ReusableString();
    private                 BookChangeListener               _listener;
    private                 boolean                          _dirty          = true;
    private                 long                             _checkCount;

    public PitchL3OrderBook( Instrument instrument, int presizeOrders, PitchBookPoolMgr<OrderBookEntry> poolMgr ) {

        super( instrument );

        if ( presizeOrders < MIN_ORDERS ) presizeOrders = MIN_ORDERS;

        _poolMgr       = poolMgr;
        _orderEntryMap = new LongHashMap<>( presizeOrders, 0.75f, poolMgr.getLongEntryFactory(), poolMgr.getLongEntryRecycler() );
        _orderEntryMap.registerCleaner( ( c ) -> recycle( c ) );

        boolean execIdChk = AppProps.instance().getBooleanProperty( OMProps.PITCH_L3_DUP_EXEC_ID_CHK, false, false );

        _execSet = (execIdChk) ? new LongHashSet( DEFAULT_EXEC_SET_SIZE, 0.75f ) : null;
    }

    @Override public boolean apply( Event event ) {
        boolean changed = false;

        final long stamp = _lock.writeLock();
        try {
            while( event != null ) {
                final Event nxt = event.getNextQueueEntry();

                changed |= applyEvent( event );

                event = nxt;
            }
            if ( _dirty ) {
                checkBook();
            }

        } finally {
            _lock.unlockWrite( stamp );
        }

        return changed;
    }

    @Override public void dump( ReusableString dest ) {
        int maxLevels = getMaxLevels();

        dest.append( "Book " ).append( _instrument.id() ).append( "\n" );

        /**
         * qty adjustment is sync'd, but insertion of new levels in book is not
         * so skip any levels where qty is zero
         */
        FullBookLevelEntry nxtBuy  = _bestLevelBuy;
        FullBookLevelEntry nxtSell = _bestLevelSell;

        int lvl = 0;
        while( lvl < maxLevels ) {
            if ( nxtBuy == _worstLevelBuy ) break;
            if ( nxtSell == _worstLevelSell ) break;

            dest.append( "[L" ).append( lvl ).append( "]  " );
            dest.append( nxtBuy.getQty() ).append( " x " ).append( nxtBuy.getPrice() ).append( "  :  " );
            dest.append( nxtSell.getPrice() ).append( " x " ).append( nxtSell.getQty() ).append( "\n" );

            nxtBuy  = nxtBuy.getNext();
            nxtSell = nxtSell.getNext();

            ++lvl;
        }

        if ( lvl == maxLevels ) return;

        if ( nxtBuy != _worstLevelBuy ) {
            while( lvl < _buyLevels && nxtBuy != _worstLevelBuy ) {

                dest.append( "[L" ).append( lvl ).append( "]  " );
                dest.append( nxtBuy.getQty() ).append( " x " ).append( nxtBuy.getPrice() ).append( "  :  " );
                dest.append( "0.0 x 0\n" );

                nxtBuy = nxtBuy.getNext();

                ++lvl;
            }

            return;
        }

        while( lvl < _sellLevels && nxtSell != _worstLevelSell ) {

            dest.append( "[L" ).append( lvl ).append( "]  " );
            dest.append( "0 x 0.0  :  " );
            dest.append( nxtSell.getQty() ).append( " x " ).append( nxtSell.getPrice() ).append( "\n" );

            nxtSell = nxtSell.getNext();

            ++lvl;
        }

        return;
    }

    @Override public OptimisticReadWriteLock getLock() {
        return _lock;
    }

    @Override public double getRefPrice() {
        if ( _worstLevelBuy != _bestLevelBuy && _worstLevelSell != _bestLevelSell ) {
            return (_bestLevelBuy.getPrice() + _bestLevelSell.getPrice()) / 2.0;
        }

        return Constants.UNSET_DOUBLE;
    }

    @Override public boolean isValid() {

        return (!_dirty &&
                Utils.compare( _bestLevelBuy.getPrice(), DUMMY_BID ) != 0 &&
                Utils.compare( _bestLevelSell.getPrice(), DUMMY_ASK ) != 0);
    }

    @Override public void snapTo( ApiMutatableBook dest ) {
        long stamp = _lock.tryOptimisticRead();

        doSnap( dest );

        if ( _lock.validate( stamp ) ) {
            return;
        }

        stamp = _lock.readLock();

        try {
            doSnap( dest );
        } finally {
            _lock.unlockRead( stamp );
        }
    }

    @Override public int getActiveLevels() {
        return (_buyLevels > _sellLevels) ? _buyLevels : _sellLevels;
    }

    @Override public boolean getAskEntry( int lvl, BookLevelEntry dest ) {

        boolean ok = false;

        int idx = 0;

        FullBookLevelEntry best = _bestLevelSell;

        while( idx < lvl && best != null ) {
            best = best.getNext();
            ++idx;
        }

        if ( best != null ) {

            if ( Utils.hasVal( best.getPrice() ) && Utils.compare( DUMMY_ASK, best.getPrice() ) != 0 ) {

                dest.set( best.getNumOrders(), best.getQty(), best.getPrice() );

                ok = true;
            }
        }

        return ok;
    }

    @Override public boolean getBidEntry( int lvl, BookLevelEntry dest ) {

        boolean ok = false;

        int idx = 0;

        FullBookLevelEntry best = _bestLevelBuy;

        while( idx < lvl && best != null ) {
            best = best.getNext();
            ++idx;
        }

        if ( best != null ) {

            if ( Utils.hasVal( best.getPrice() ) && Utils.compare( DUMMY_BID, best.getPrice() ) != 0 ) {

                dest.set( best.getNumOrders(), best.getQty(), best.getPrice() );

                ok = true;
            }
        }

        return ok;
    }

    @Override public Level getLevel()                            { return Level.L3; }

    @Override public boolean getLevel( int lvl, DoubleSidedBookEntry dest ) {

        boolean ok = false;

        int idx = 0;

        FullBookLevelEntry bestBid = _bestLevelBuy;
        FullBookLevelEntry bestAsk = _bestLevelSell;

        while( idx < lvl && bestBid != null && bestAsk != null ) {
            bestBid = bestBid.getNext();
            bestAsk = bestAsk.getNext();
            ++idx;
        }

        if ( bestBid != null && bestAsk != null ) {

            if ( Utils.hasVal( bestBid.getPrice() ) && Utils.compare( DUMMY_BID, bestBid.getPrice() ) != 0 &&
                 Utils.hasVal( bestAsk.getPrice() ) && Utils.compare( DUMMY_ASK, bestAsk.getPrice() ) != 0 ) {

                dest.set( bestBid.getNumOrders(), bestBid.getQty(), bestBid.getPrice(), false,
                          bestAsk.getNumOrders(), bestAsk.getQty(), bestAsk.getPrice(), false );

                ok = true;
            }
        }

        return ok;
    }

    @Override public int getMaxLevels() {
        return (_buyLevels > _sellLevels) ? _buyLevels : _sellLevels;
    }

    @Override
    public void reset() {
        final long stamp = _lock.writeLock();
        try {
            doClear();
            resetCounts();
        } finally {
            _lock.unlockWrite( stamp );
        }
    }

    @Override public void setDirty( final boolean isDirty ) { _dirty = isDirty; }

    public void eodStats() {
        int activeOrders = _orderEntryMap.size();

        L3BookStats.instance().add( this, activeOrders, _maxActiveOrders, _buyLevels, _sellLevels );
    }

    /**
     * @return
     * @WARN not locked ... only use as INDICATION .. eg EOD stats / tests
     */
    public int getActiveOrders() { return _orderEntryMap.size(); }

    public OrderBookEntry getOrderBookEntry( final long orderId ) {
        return _orderEntryMap.get( orderId );
    }

    public void setListener( final BookChangeListener listener ) { _listener = listener; }

    private boolean applyAdd( PitchBookAddOrderImpl event ) {

        if ( event.getSide().getIsBuySide() ) {
            return applyAddBuy( event );
        }
        return applyAddSell( event );
    }

    private boolean applyAddBuy( PitchBookAddOrderImpl event ) {

        final double price    = event.getPrice();
        final double orderQty = event.getOrderQty();

        OrderBookEntry newOBE = _poolMgr.getOrderBookEntryFactory().get();

        OrderBookEntry old = _orderEntryMap.put( event.getOrderId(), newOBE );

        if ( old != null ) {
            removeFromBook( old, old.getQty(), 0, false );
        }

        if ( orderQty <= Constants.TICK_WEIGHT ) {
            _orderEntryMap.remove( event.getOrderId() );
            recycle( newOBE );
            return false;
        }

        FullBookLevelEntry l = getLevelBuy( price );

        newOBE.setPrice( price );
        newOBE.setQty( orderQty );
        newOBE.setBuySide( true );

        l.addQty( orderQty );

        checkChains();

        if ( _listener != null ) _listener.set( Side.Buy, price, l.getQty() );

        recycle( old );

        _poolMgr.recycle( event );

        statsCheck();

        return true;
    }

    private boolean applyAddSell( PitchBookAddOrderImpl event ) {

        final double price    = event.getPrice();
        final double orderQty = event.getOrderQty();

        OrderBookEntry newOBE = _poolMgr.getOrderBookEntryFactory().get();

        OrderBookEntry old = _orderEntryMap.put( event.getOrderId(), newOBE );

        if ( old != null ) {
            removeFromBook( old, old.getQty(), 0, false );
        }

        if ( orderQty <= Constants.TICK_WEIGHT ) {
            _orderEntryMap.remove( event.getOrderId() );
            recycle( newOBE );
            return false;
        }

        FullBookLevelEntry l = getLevelSell( price );

        newOBE.setPrice( price );
        newOBE.setQty( orderQty );
        newOBE.setBuySide( false );
        l.addQty( orderQty );

        checkChains();

        if ( _listener != null ) _listener.set( Side.Sell, price, l.getQty() );

        recycle( old );

        _poolMgr.recycle( event );

        return true;
    }

    private boolean applyClear() {
        boolean hasData = _orderEntryMap.size() > 0;

        doClear();

        return hasData;
    }

    private boolean applyDelete( PitchBookCancelOrderImpl event ) {

        final long orderId = event.getOrderId();

        final OrderBookEntry entry = getOrderBookEntry( orderId );

        if ( entry == null ) {
            if ( _log.isEnabledFor( com.rr.core.logger.Level.trace ) ) {
                _logMsg.setValue( "Delete for entry not in book (probably inst on addOrder not in secdef)" );
                event.dump( _logMsg );
                _log.log( com.rr.core.logger.Level.trace, _logMsg );
                _poolMgr.recycle( event );
            }
            return false;
        }

        double cancelQty = (Utils.isNull( event.getCancelQty() ) ? entry.getQty() : event.getCancelQty());
        double leavesQty = entry.getQty() - cancelQty;

        removeOrder( orderId, entry, cancelQty, leavesQty, false );  // order book entry now recycled IF leavesQty <= 0

        _poolMgr.recycle( event );

        return true;
    }

    private boolean applyEvent( Event event ) {
        setDataSeqNum( event.getMsgSeqNum() );

        switch( event.getReusableType().getSubId() ) {
        case EventIds.ID_PITCHBOOKADDORDER:
            ++_ticks;
            return applyAdd( (PitchBookAddOrderImpl) event );
        case EventIds.ID_PITCHBOOKCANCELORDER:
            ++_ticks;
            return applyDelete( (PitchBookCancelOrderImpl) event );
        case EventIds.ID_PITCHSYMBOLCLEAR:
            return applyClear();
        case EventIds.ID_PITCHBOOKORDEREXECUTED:
            return orderExecuted( (PitchBookOrderExecutedImpl) event );
        case EventIds.ID_PITCHPRICESTATISTIC:
            return applyStatistic( (PitchPriceStatisticImpl) event );
        case EventIds.ID_PITCHOFFBOOKTRADE:
            return offBookTrade( (PitchOffBookTradeImpl) event );
        }

        return false;
    }

    private boolean applyStatistic( final PitchPriceStatisticImpl event ) {
        return true;
    }

    private void bad( final String s ) {
        _log.warn( s );
    }

    private void checkBook() { // IF BBO VALID MARK NOT DIRTY
        if ( Utils.compare( _bestLevelBuy.getPrice(), DUMMY_BID ) != 0 &&
             Utils.compare( _bestLevelSell.getPrice(), DUMMY_ASK ) != 0 ) {

            _dirty = false;
        }
    }

    private void checkChains() {
        checkFromTail( _worstLevelSell );
        checkFromTail( _worstLevelBuy );

        checkFromHead( _bestLevelSell );
        checkFromHead( _bestLevelBuy );
    }

    private void checkFromHead( final FullBookLevelEntry worstLevel ) {

        ++_checkCount;

        boolean            isBuy = worstLevel == _bestLevelBuy;
        FullBookLevelEntry tmp   = worstLevel;

        int idx = 0;

        while( tmp.getNext() != null ) {
            FullBookLevelEntry nxt = tmp.getNext();

            if ( tmp.getQty() < Constants.TICK_WEIGHT && tmp != _worstLevelBuy && tmp != _worstLevelSell ) {
                bad( id() + " null qty (HA) at idx=" + idx + ", checkCount=" + _checkCount + ", price=" + tmp.getPrice() );
            }

            if ( nxt.getPrev() != tmp ) {
                bad( id() + " chain broken (HB) at idx=" + idx + ", checkCount=" + _checkCount );
            }

            tmp = nxt;
        }

        if ( tmp.getQty() < Constants.TICK_WEIGHT && tmp != _worstLevelBuy && tmp != _worstLevelSell ) {
            bad( id() + " null qty (HC) at idx=" + idx + ", checkCount=" + _checkCount + ", price=" + tmp.getPrice() );
        }

        if ( isBuy ) {
            if ( tmp != _worstLevelBuy ) {
                bad( id() + " chain broken (HD) at idx=" + idx + ", checkCount=" + _checkCount );

                if ( _worstLevelBuy.getPrev() != tmp ) {
                    bad( id() + " chain broken (HE) at idx=" + idx + ", checkCount=" + _checkCount );
                }

                if ( _worstLevelBuy.getNext() != null ) {
                    bad( id() + " chain broken (HF) at idx=" + idx + ", checkCount=" + _checkCount );
                }
            }
        } else if ( tmp != _worstLevelSell ) {
            bad( id() + " chain broken (HG) at idx=" + idx + ", checkCount=" + _checkCount );

            if ( _worstLevelSell.getPrev() != tmp ) {
                bad( id() + " chain broken (HH) at idx=" + idx + ", checkCount=" + _checkCount );
            }

            if ( _worstLevelSell.getNext() != null ) {
                bad( id() + " chain broken (HI) at idx=" + idx + ", checkCount=" + _checkCount );
            }
        }
    }

    private void checkFromTail( final FullBookLevelEntry worstLevel ) {

        ++_checkCount;

        boolean            isBuy = worstLevel == _worstLevelBuy;
        FullBookLevelEntry tmp   = worstLevel;

        int idx = 0;

        while( tmp.getPrev() != null ) {
            FullBookLevelEntry nxt = tmp.getPrev();

            if ( tmp.getQty() < Constants.TICK_WEIGHT && tmp != _worstLevelBuy && tmp != _worstLevelSell ) {
                bad( id() + " null qty (A) at idx=" + idx + ", checkCount=" + _checkCount + ", price=" + tmp.getPrice() );
            }

            if ( nxt.getNext() != tmp ) {
                bad( id() + " chain broken (B) at idx=" + idx + ", checkCount=" + _checkCount );
            }

            tmp = nxt;
        }

        if ( tmp.getQty() < Constants.TICK_WEIGHT && tmp != _worstLevelBuy && tmp != _worstLevelSell ) {
            bad( id() + " null qty (C) at idx=" + idx + ", checkCount=" + _checkCount + ", price=" + tmp.getPrice() );
        }

        if ( isBuy ) {
            if ( tmp != _bestLevelBuy ) {
                bad( id() + " chain broken (D) at idx=" + idx + ", checkCount=" + _checkCount );

                if ( _bestLevelBuy.getPrev() != tmp ) {
                    bad( id() + " chain broken (E) at idx=" + idx + ", checkCount=" + _checkCount );
                }

                if ( _bestLevelBuy.getNext() != null ) {
                    bad( id() + " chain broken (F) at idx=" + idx + ", checkCount=" + _checkCount );
                }
            }
        } else if ( tmp != _bestLevelSell ) {
            bad( id() + " chain broken (G) at idx=" + idx + ", checkCount=" + _checkCount );

            if ( _bestLevelSell.getPrev() != tmp ) {
                bad( id() + " chain broken (H) at idx=" + idx + ", checkCount=" + _checkCount );
            }

            if ( _bestLevelSell.getNext() != null ) {
                bad( id() + " chain broken (I) at idx=" + idx + ", checkCount=" + _checkCount );
            }
        }
    }

    private boolean checkPriceChange( OrderBookEntry entry, double newPrice ) {
        if ( Utils.hasVal( newPrice ) ) {
            double delta = Math.abs( newPrice - entry.getPrice() );

            return delta > Constants.WEIGHT;
        }

        return false;
    }

    private boolean checkQtyChange( OrderBookEntry entry, int newQty ) {
        if ( newQty != Constants.UNSET_INT ) {
            double delta = newQty - entry.getQty();

            return Math.abs( delta ) > Constants.TICK_WEIGHT;
        }

        return false;
    }

    private void doClear() {
        _dirty = true;

        _orderEntryMap.clear(); // recycles ALL order entries

        if ( _listener != null ) _listener.clear();

        if ( _execSet != null ) _execSet.clear();

        FullBookLevelEntry e = _bestLevelBuy;

        while( e != _worstLevelBuy ) {

            FullBookLevelEntry nxt = e.getNext();
            e.setNext( null );

            _poolMgr.getBookLevelRecycler().recycle( e );

            e = nxt;
        }

        e = _bestLevelSell;

        while( e != _worstLevelSell ) {
            FullBookLevelEntry nxt = e.getNext();
            e.setNext( null );

            _poolMgr.getBookLevelRecycler().recycle( e );

            e = nxt;
        }

        _ticks      = 0;
        _buyLevels  = 0;
        _sellLevels = 0;

        setBestLevelBuy( _worstLevelBuy );
        setBestLevelSell( _worstLevelSell );

        _worstLevelBuy.setPrev( null );
        _worstLevelSell.setPrev( null );

        checkChains();
    }

    private void doSnap( ApiMutatableBook dest ) {
        int maxLevels = dest.getMaxLevels();

        /**
         * qty adjustment is sync'd, but insertion of new levels in book is not
         * so skip any levels where qty is zero
         */
        FullBookLevelEntry nxtBuy  = _bestLevelBuy;
        FullBookLevelEntry nxtSell = _bestLevelSell;

        int lvl = 0;

        while( lvl < maxLevels ) {
            if ( nxtBuy == _worstLevelBuy ) break;
            if ( nxtSell == _worstLevelSell ) break;

            dest.setLevel( lvl, nxtBuy.getQty(), nxtBuy.getPrice(), false, nxtSell.getQty(), nxtSell.getPrice(), false );

            nxtBuy  = nxtBuy.getNext();
            nxtSell = nxtSell.getNext();

            ++lvl;
        }

        if ( lvl == maxLevels ) return;

        if ( nxtBuy != _worstLevelBuy ) {
            while( lvl < maxLevels && nxtBuy != _worstLevelBuy ) {

                dest.setLevel( lvl, nxtBuy.getQty(), nxtBuy.getPrice(), false, 0, Constants.UNSET_DOUBLE, false );

                nxtBuy = nxtBuy.getNext();

                ++lvl;
            }
        } else {
            while( lvl < maxLevels && nxtSell != _worstLevelSell ) {

                dest.setLevel( lvl, 0, Constants.UNSET_DOUBLE, false, nxtSell.getQty(), nxtSell.getPrice(), false );

                nxtSell = nxtSell.getNext();

                ++lvl;
            }
        }

        dest.setNumLevels( lvl );
        dest.setDirty( _dirty );
    }

    private FullBookLevelEntry getLevel( final OrderBookEntry entry ) {
        boolean isBuy = entry.isBuySide();

        double price = entry.getPrice();

        if ( isBuy ) {
            FullBookLevelEntry l = _bestLevelBuy;

            while( price < l.getPrice() ) {
                final FullBookLevelEntry nxt = l.getNext();

                if ( nxt == null ) break;

                l = nxt;
            }

            if ( Math.abs( price - l.getPrice() ) < Constants.WEIGHT ) {
                return l;
            }

            return null;

        }

        FullBookLevelEntry l = _bestLevelSell;

        while( price > l.getPrice() ) {
            final FullBookLevelEntry nxt = l.getNext();

            if ( nxt == null ) break;

            l = nxt;
        }

        if ( Math.abs( price - l.getPrice() ) < Constants.WEIGHT ) {
            return l;
        }

        return null;
    }

    private FullBookLevelEntry getLevelBuy( double price ) {
        FullBookLevelEntry l = _bestLevelBuy;

        while( price < l.getPrice() ) {
            final FullBookLevelEntry nxt = l.getNext();

            if ( nxt == null ) break;

            l = nxt;
        }

        if ( l == _worstLevelBuy || Math.abs( price - l.getPrice() ) > Constants.WEIGHT ) {

            final FullBookLevelEntry newL = _poolMgr.getBookLevelEntryFactory().get();
            final FullBookLevelEntry prev = l.getPrev();

            newL.setPrice( price );
            newL.setPrev( prev );
            newL.setNext( l );

            l.setPrev( newL );

            if ( prev == null ) {
                setBestLevelBuy( newL );
            } else {
                prev.setNext( newL );
            }

            l = newL;

            ++_buyLevels;
        }

        return l;
    }

    private FullBookLevelEntry getLevelSell( double price ) {
        FullBookLevelEntry l = _bestLevelSell;

        while( price > l.getPrice() ) {
            final FullBookLevelEntry nxt = l.getNext();

            if ( nxt == null ) break;

            l = nxt;
        }

        if ( l == _worstLevelSell || Math.abs( price - l.getPrice() ) > Constants.WEIGHT ) {

            final FullBookLevelEntry newL = _poolMgr.getBookLevelEntryFactory().get();
            final FullBookLevelEntry prev = l.getPrev();

            newL.setPrice( price );
            newL.setPrev( prev );
            newL.setNext( l );

            l.setPrev( newL );

            if ( prev == null ) {
                setBestLevelSell( newL );
            } else {
                prev.setNext( newL );
            }

            l = newL;

            ++_sellLevels;
        }

        return l;
    }

    private boolean isOnBook( final PitchBookOrderExecutedImpl event ) {
        boolean isOnBook = true;

        final MMTMarketMechanism mktMech = event.getMktMech();

        if ( mktMech != null ) {
            switch( mktMech ) {
            case CentralLimitOrderBook:
            case PeriodicAuction:
            case Unknown:
                break;
            case QuoteDrivenMarket:
            case DarkOrderBook:
            case OffBook:
            case RequestForQuotes:
            case AnyOtherIncludingHybrid:
                isOnBook = false;
                break;
            }
        }

        final MMTTradingMode tradingMode = event.getTradingMode();

        if ( tradingMode != null ) {
            switch( tradingMode ) {
            case ContinuousTrading:
            case Unknown:
                break;
            case UndefinedAuction:
            case AtMarketCloseTrading:
            case OutOfMainSession:
            case TradeReportingOnExchange:
            case TradeReportingOffExchange:
            case TradeReportingSystematicInternalizer:
            case ScheduledOpeningAuction:
            case ScheduledClosingAuction:
            case ScheduledIntradayAuction:
            case UnscheduledAuction:
                isOnBook = false;
                break;
            }
        }

        return isOnBook;
    }

    private boolean offBookTrade( final PitchOffBookTradeImpl event ) {
        final int    lastQty = event.getLastQty();
        final double value   = lastQty * event.getLastPx();

        _totalValueTradedOffBook += value;
        _totalVolTradedOffBook += lastQty;
        ++_numTradesOffBook;

        return true;
    }

    private boolean orderExecuted( final PitchBookOrderExecutedImpl event ) {

        final long orderId = event.getOrderId();

        final OrderBookEntry entry = getOrderBookEntry( orderId );

        if ( entry == null ) {
            _logMsg.setValue( "Modification for entry not in book " );
            event.dump( _logMsg );
            _log.warn( _logMsg );
            _poolMgr.recycle( event );
            return false;
        }

        FullBookLevelEntry priceLevel = getLevel( entry );

        if ( priceLevel == null ) {
            _logMsg.setValue( "price level doesnt exist anymore " );
            event.dump( _logMsg );
            _log.error( ERR_RECYCLED, _logMsg );
            return false;
        }

        if ( _execSet != null && !_execSet.add( event.getExecId() ) ) {
            _logMsg.setValue( "Duplicate execId, dropping trade " );
            event.dump( _logMsg );
            _log.warn( _logMsg );
            _poolMgr.recycle( event );
            return false;
        }

        double lastQty   = event.getLastQty();
        double leavesQty = entry.getQty() - lastQty;

        if ( leavesQty < -Constants.TICK_WEIGHT ) {
            _logMsg.copy( "Instrument " ).append( _instrument.id() ).append( "Reset tradeQty to qtyLeftInOrder=" ).append( entry.getQty() )
                   .append( ", event=" ).chain( () -> event.dump( _logMsg ) );

            _log.error( ERR_OVERFILL, _logMsg );

            lastQty   = entry.getQty();
            leavesQty = 0;
        }

        final double  lastPrice  = entry.getPrice();
        final double  value      = lastQty * lastPrice;
        final boolean isBuyOrder = entry.isBuySide();

        removeOrder( orderId, entry, lastQty, leavesQty, true ); // order book entry now recycled if leavesQty 0

        boolean isOnBook = isOnBook( event );

        _poolMgr.recycle( event );

        if ( isOnBook ) {
            _totalValueTradedOnBook += value;
            _totalVolTradedOnBook += lastQty;
            ++_numTradesOnBook;

            if ( _listener != null ) {
                final Side aggSide = (isBuyOrder) ? Side.Sell : Side.Buy;

                _listener.trade( aggSide, lastPrice, lastQty );
            }

        } else {
            _totalValueTradedOffBook += value;
            _totalVolTradedOffBook += lastQty;
            ++_numTradesOffBook;
        }

        return true;
    }

    private void recycle( OrderBookEntry old ) {
        if ( old != null ) {
            _poolMgr.getOrderBookEntryRecycler().recycle( old );
        }
    }

    private void removeBookEntry( FullBookLevelEntry ble, boolean isBuySide ) {

        final FullBookLevelEntry nxt = ble.getNext();
        final FullBookLevelEntry prv = ble.getPrev();

        if ( prv != null ) {
            if ( prv.getNext() == ble ) {
                prv.setNext( nxt );
            } else {
                bad( "removeBookEntry A" );
            }
        } else {
            // must be the head of chain

            if ( nxt == null ) {
                ble.reset();

                ble.setPrice( (isBuySide) ? DUMMY_BID : DUMMY_ASK );

                return; // cant delete the tail node (which is also the only node in chain)
            }

            if ( isBuySide ) {
                if ( _bestLevelBuy == ble ) {
                    setBestLevelBuy( nxt );
                }
            } else {
                if ( _bestLevelSell == ble ) {
                    setBestLevelSell( nxt );
                }
            }
        }

        if ( nxt != null ) {
            if ( nxt.getPrev() == ble ) {
                nxt.setPrev( prv );
            } else {
                bad( "removeBookEntry B" );
            }
        }

        if ( isBuySide ) {
            --_buyLevels;
        } else {
            --_sellLevels;
        }

        ble.setNext( null );

        checkChains();

        _poolMgr.getBookLevelRecycler().recycle( ble );
    }

    private void removeFromBook( OrderBookEntry old, final double lastQty, final double leavesQty, final boolean isTrade ) {
        FullBookLevelEntry ble = getLevel( old );

        if ( ble != null ) {
            ble.removeQty( lastQty );

            if ( _listener != null && !isTrade ) _listener.set( old.isBuySide() ? Side.Buy : Side.Sell, old.getPrice(), ble.getQty() );

            if ( ble.getQty() <= Constants.TICK_WEIGHT ) {
                removeBookEntry( ble, old.isBuySide() );
            }

            old.setQty( leavesQty );
        }
    }

    private void removeOrder( final long orderId, final OrderBookEntry entry, final double removeQty, final double leavesQty, boolean isTrade ) {
        removeFromBook( entry, removeQty, leavesQty, isTrade );
        if ( leavesQty <= Constants.TICK_WEIGHT ) {
            _orderEntryMap.remove( orderId );
            recycle( entry );
        }
    }

    private void resetCounts() {
        _totalValueTradedOnBook  = 0.0;
        _totalValueTradedOffBook = 0.0;
        _totalVolTradedOnBook    = 0;
        _totalVolTradedOffBook   = 0;
        _numTradesOnBook         = 0;
        _numTradesOffBook        = 0;
    }

    private void setBestLevelBuy( final FullBookLevelEntry newL ) {
        _bestLevelBuy = newL;
    }

    private void setBestLevelSell( final FullBookLevelEntry newL ) {
        _bestLevelSell = newL;
    }

    private void statsCheck() {
        int size = _orderEntryMap.size();

        if ( size > _maxActiveOrders ) _maxActiveOrders = size;
    }
}
