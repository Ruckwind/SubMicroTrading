/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book.l2;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.*;
import com.rr.core.model.book.*;
import com.rr.core.recycler.EventRecycler;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.core.utils.lock.OptimisticReadWriteLock;
import com.rr.md.book.MutableFixBook;
import com.rr.model.generated.internal.events.impl.MDSnapEntryImpl;
import com.rr.model.generated.internal.events.interfaces.MDEntry;
import com.rr.model.generated.internal.events.interfaces.MDSnapshotFullRefresh;

/**
 * Wrapper which applies fix events to mutable book
 * <p>
 * Has a queue of pending incremental events which will be used during gap recovery
 * <p>
 * Implements Message so it can be used with existing MessageDispatchers
 * <p>
 * Must only be written to on single thread, if using threadsafe underlying book then can snap on multiple reader threads
 * <p>
 * Fix Book level starts at 1 not 0
 * <p>
 * All locking of the book against reading and writing must happen at a higher level to avoid costly extra synchronisation
 *
 * @TODO refactor Message into Dispatchable interface
 */
public abstract class BaseL2FixBook implements MutableFixBook {

    protected static final Logger _log = LoggerFactory.create( BaseL2FixBook.class );

    protected static final int LOG_THRESHOLD = 100;

    protected final ApiMutatableBook _book;

    // use of tmpEntry means cannot have multiple threads updating same book
    protected final BookEntryImpl _tmpEntry = new BookEntryImpl();
    protected final ReusableString _errMsg = new ReusableString();
    protected int    _totalTradeVol;
    protected double _totalTraded;
    protected int    _lastTradeQty;
    protected double _lastTradePrice;
    private volatile Event _nextMessage = null;

    private long _tickCount;

    public BaseL2FixBook( ApiMutatableBook book ) {
        _book = book;
    }

    @Override public boolean applyIncrementalEntry( final int eventSeqNum, final MDEntry entry ) {

        ++_tickCount;

        setSeqNums( eventSeqNum, entry.getRepeatSeq() );

        switch( entry.getMdEntryType() ) {
        case Bid:
            applyBid( entry );
            return true;
        case Offer:
            applyAsk( entry );
            return true;
        case Trade:
            applyTrade( entry );
            return true;
        case EmptyBook:
            reset();
            break;
        case AuctionClearingPrice:
        case ClosingPrice:
        case CompositeUnderlyingPrice:
        case EarlyPrices:
        case FixingPrice:
        case Imbalance:
        case IndexValue:
        case MarginRate:
        case MidPrice:
        case OpenInterest:
        case OpeningPrice:
        case PriorSettlePrice:
        case SessionHighBid:
        case SessionLowOffer:
        case SettleHighPrice:
        case SettleLowPrice:
        case SettlementPrice:
        case SimulatedBuy:
        case SimulatedSellPrice:
        case TradeVolume:
        case TradingSessionHighPrice:
        case TradingSessionLowPrice:
        case TradingSessionVWAPPrice:
        case Unknown:
        default:
            break;
        }

        return false;
    }

    @Override public boolean applySnapshot( MDSnapshotFullRefresh msg, EventRecycler entryRecycler ) {

        boolean ok;

        ++_tickCount;

        setSeqNums( msg.getLastMsgSeqNumProcessed(), msg.getRptSeq() );
        MDSnapEntryImpl next = (MDSnapEntryImpl) msg.getMDEntries();

        while( next != null ) {
            applySnapEntry( next );

            next = next.getNext();
        }

        _book.setEventTimestamp( msg.getReceived() );

        ok = replayQueued( msg.getLastMsgSeqNumProcessed(), msg.getRptSeq(), entryRecycler );

        return ok;
    }

    @Override public void reset() {
        _book.reset();
        _book.setEventTimestamp( 0 );
    }

    @Override public int getTotalTradeVol() {
        return _totalTradeVol;
    }

    @Override public double getTotalTraded() {
        return _totalTraded;
    }

    @Override public int getLastTradeQty() {
        return _lastTradeQty;
    }

    @Override public double getLastTradePrice() {
        return _lastTradePrice;
    }

    @Override public final void attachQueue( Event nxt ) {
        _nextMessage = nxt;
    }

    @Override public final void detachQueue() {
        _nextMessage = null;
    }

    @Override public final Event getNextQueueEntry() {
        return _nextMessage;
    }

    @Override public ListenerMktDataContextWrapper<ApiMutatableBook, BookContext> getContextWrapper() {
        return _book.getContextWrapper();
    }

    @Override public ListenerMktDataContextWrapper<ApiMutatableBook, BookContext> setContextWrapper( final ListenerMktDataContextWrapper<ApiMutatableBook, BookContext> context ) {
        return _book.setContextWrapper( context );
    }

    @Override public long getEventTimestamp()                 { return _book.getEventTimestamp(); }

    @Override public void setEventTimestamp( long timestamp ) { _book.setEventTimestamp( timestamp ); }

    @Override public ReusableType getReusableType()               { return null; }

    @Override public String id()      { return _book.id(); }

    @Override public Instrument getInstrument() {
        return _book.getInstrument();
    }

    @Override public Level getLevel() { return Level.L2; }

    @Override public OptimisticReadWriteLock getLock() {
        return _book.getLock();
    }

    @Override public int getMaxLevels() {
        return _book.getMaxLevels();
    }

    @Override public int getActiveLevels() {
        return _book.getActiveLevels();
    }

    @Override public boolean getLevel( int lvl, DoubleSidedBookEntry dest ) {
        return _book.getLevel( lvl, dest );
    }

    @Override public boolean getBidEntry( int lvl, BookLevelEntry dest ) {
        return _book.getBidEntry( lvl, dest );
    }

    @Override public boolean getAskEntry( int lvl, BookLevelEntry dest ) {
        return _book.getAskEntry( lvl, dest );
    }

    @Override public void snapTo( ApiMutatableBook dest ) {
        _book.snapTo( dest );
        dest.setEventTimestamp( getEventTimestamp() );
        dest.setDataSeqNum( getDataSeqNum() );
    }

    @Override public void dump( ReusableString dest ) {
        dest.append( "L2FixBook bookSeqNum=" ).append( getDataSeqNum() ).append( " " );
        _book.dump( dest );
    }

    @Override public boolean isValid() {
        return _book.isValid();
    }

    @Override public long getTickCount() {
        return _tickCount;
    }

    @Override public void setDirty( boolean isDirty ) {
        _book.setDirty( isDirty );
        _book.setEventTimestamp( 0 );
    }    @Override public void setEventHandler( EventHandler handler ) { /* nothing */ }

    @Override public void setInstrument( final Instrument instrument ) { if ( instrument != _book.getInstrument() ) throw new SMTRuntimeException( getClass().getSimpleName() + " doesnt support changing instrument" ); }    @Override public EventHandler getEventHandler()               { return null; }

    @Override public String toString() {
        return _book.toString();
    }    @Override public void setFlag( MsgFlag flag, boolean isOn )   { /* nothing */ }

    protected abstract void applyAsk( final MDEntry entry );    @Override public boolean isFlagSet( MsgFlag flag )            { return false; }

    protected abstract void applyBid( final MDEntry entry );    @Override public int getFlags()                               { return 0; }

    protected abstract void applyTrade( MDEntry entry );

    protected abstract boolean replayQueued( int lastSeqNumProcessed, int snapSeq, EventRecycler entryRecycler );

    protected abstract void setSeqNums( int msgSeqNum, int rptSeq );

    private void applySnapEntry( final MDSnapEntryImpl entry ) {

        final int lvl = entry.getMdPriceLevel() - 1;

        switch( entry.getMdEntryType() ) {
        case Bid:
            _tmpEntry.set( entry.getMdEntrySize(), entry.getMdEntryPx() );
            _book.setBid( lvl, _tmpEntry );
            break;
        case Offer:
            _tmpEntry.set( entry.getMdEntrySize(), entry.getMdEntryPx() );
            _book.setAsk( lvl, _tmpEntry );
            break;
        case Trade:
            _lastTradeQty = entry.getMdEntrySize();
            _lastTradePrice = entry.getMdEntryPx();

            _totalTradeVol += _lastTradeQty;
            _totalTraded += (_lastTradeQty * _lastTradePrice);
            break;
        case EmptyBook:
            reset();
            break;
        case AuctionClearingPrice:
        case ClosingPrice:
        case CompositeUnderlyingPrice:
        case EarlyPrices:
        case FixingPrice:
        case Imbalance:
        case IndexValue:
        case MarginRate:
        case MidPrice:
        case OpenInterest:
        case OpeningPrice:
        case PriorSettlePrice:
        case SessionHighBid:
        case SessionLowOffer:
        case SettleHighPrice:
        case SettleLowPrice:
        case SettlementPrice:
        case SimulatedBuy:
        case SimulatedSellPrice:
        case TradeVolume:
        case TradingSessionHighPrice:
        case TradingSessionLowPrice:
        case TradingSessionVWAPPrice:
        case Unknown:
        default:
            break;
        }
    }











}
