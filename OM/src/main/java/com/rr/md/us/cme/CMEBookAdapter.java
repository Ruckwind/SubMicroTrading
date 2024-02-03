/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme;

import com.rr.core.lang.Constants;
import com.rr.core.model.ExchangeInstrument;
import com.rr.core.model.book.ApiMutatableBook;
import com.rr.core.model.book.UnsafeL1Book;
import com.rr.core.recycler.EventRecycler;
import com.rr.core.utils.Utils;
import com.rr.md.book.l2.BaseL2FixBook;
import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.model.generated.internal.events.interfaces.MDEntry;
import com.rr.om.dummy.warmup.DummyInstrument;

/**
 * CME original FastFix market data event book wrapper
 * <p>
 * Wrapper which adapts CME fix events to mutable book
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
public final class CMEBookAdapter extends BaseL2FixBook {

    public static final CMEBookAdapter DUMMY = makeDummy( DummyInstrument.DUMMY );           // DUMMY instance to avoid null checks and repeated calls to book lookups for unknown insts

    private long _bookSeqNum;
    private int  _lastEventSeqNum;

    private MDEntryImpl _rootEnqueue;
    private MDEntryImpl _lastEnqueued;

    private static CMEBookAdapter makeDummy( ExchangeInstrument inst ) {
        CMEBookAdapter book = new CMEBookAdapter( new UnsafeL1Book( inst ) );

        book.setMsgSeqNum( Integer.MAX_VALUE - 1 );
        book.setEventTimestamp( 0 );
        book.setDataSeqNum( 0 );

        return book;
    }

    public CMEBookAdapter( ApiMutatableBook book ) {
        super( book );
    }

    @Override protected final void applyAsk( final MDEntry entry ) {
        final int lvl = entry.getMdPriceLevel() - 1;

        switch( entry.getMdUpdateAction() ) {
        case New: {
            _tmpEntry.set( entry.getMdEntrySize(), entry.getMdEntryPx() );
            _book.insertAsk( lvl, _tmpEntry );
        }
        break;
        case Change: {
            final double px = entry.getMdEntryPx();
            if ( Utils.hasVal( px ) ) {
                _tmpEntry.set( entry.getMdEntrySize(), px );
                _book.setAsk( lvl, _tmpEntry );
            } else {
                _book.setAskQty( lvl, entry.getMdEntrySize() );
            }
        }
        break;
        case Delete: {
            _book.deleteAsk( lvl );
        }
        break;
        case DeleteThru:
            _book.deleteThruAsk( lvl );
            break;
        case DeleteFrom:
            _book.deleteFromAsk( lvl );
            break;
        case Overlay: {
            final int qty = entry.getMdEntrySize();
            if ( qty != Constants.UNSET_INT ) {
                _tmpEntry.set( qty, entry.getMdEntryPx() );
                _book.setAsk( lvl, _tmpEntry );
            } else {
                _book.setAskPrice( lvl, entry.getMdEntryPx() );
            }
        }
        break;
        case Unknown:
            break;
        }
    }

    @Override protected final void applyBid( final MDEntry entry ) {
        final int lvl = entry.getMdPriceLevel() - 1;

        switch( entry.getMdUpdateAction() ) {
        case New: {
            _tmpEntry.set( entry.getMdEntrySize(), entry.getMdEntryPx() );
            _book.insertBid( lvl, _tmpEntry );
        }
        break;
        case Change: { // should be used ONLY for qty change, but CME use for price setting as well
            final double px = entry.getMdEntryPx();
            if ( Utils.hasVal( px ) ) {
                _tmpEntry.set( entry.getMdEntrySize(), px );
                _book.setBid( lvl, _tmpEntry );
            } else {
                _book.setBidQty( lvl, entry.getMdEntrySize() );
            }
        }
        break;
        case Delete: {
            _book.deleteBid( lvl );
        }
        break;
        case DeleteThru:
            _book.deleteThruBid( lvl );
            break;
        case DeleteFrom:
            _book.deleteFromBid( lvl );
            break;
        case Overlay: {
            final int qty = entry.getMdEntrySize();
            if ( qty != Constants.UNSET_INT ) {
                _tmpEntry.set( qty, entry.getMdEntryPx() );
                _book.setBid( lvl, _tmpEntry );
            } else {
                _book.setBidPrice( lvl, entry.getMdEntryPx() );
            }
        }
        break;
        case Unknown:
            break;
        }
    }

    @Override protected final void applyTrade( MDEntry entry ) {
        switch( entry.getMdUpdateAction() ) {
        case New:
        case Change:
            _lastTradeQty = entry.getMdEntrySize();
            _lastTradePrice = entry.getMdEntryPx();

            _totalTradeVol += _lastTradeQty;
            _totalTraded += (_lastTradeQty * _lastTradePrice);
            break;
        case Delete:
            final int tradeQty = entry.getMdEntrySize();
            final double tradePrice = entry.getMdEntryPx();

            _totalTradeVol -= tradeQty;
            _totalTraded -= (tradeQty * tradePrice);
            break;
        case DeleteThru:
        case DeleteFrom:
        case Overlay:
        case Unknown:
            break;
        }
    }

    /**
     * @param snapSeq the seqNum of the snap event
     * @return true if events applied ok with NO gap
     */
    @Override protected final boolean replayQueued( int lastSeqNumProcessed, int snapSeq, EventRecycler entryRecycler ) {

        if ( _rootEnqueue == null ) return true;

        MDEntryImpl nxt = _rootEnqueue;
        MDEntryImpl tmp;

        int nextExpSeq = snapSeq + 1;

        boolean ok = true;

        int cnt = 0;

        while( nxt != null ) {

            ++cnt;
            final int seqNum = nxt.getRepeatSeq();

            tmp = nxt.getNext();
            nxt.setNext( null );

            if ( nxt.getRepeatSeq() < snapSeq ) {
                // ignore old update
            } else if ( seqNum == nextExpSeq ) {
                applyIncrementalEntry( _lastEventSeqNum, nxt );

                ++nextExpSeq;

            } else {
                _errMsg.copy( id() );
                // tag 83 not tag 34
                _errMsg.append( " Gap detected in replay, bookSeqNo=" ).append( snapSeq ).append( ", expSeqNo=" ).append( nextExpSeq ).append( ", got=" ).append( seqNum );

                _log.warn( _errMsg );

                ok = false;
            }

            entryRecycler.recycle( nxt );

            nxt = tmp;
        }

        if ( cnt > LOG_THRESHOLD ) {
            _errMsg.copy( id() );
            _errMsg.append( " large replay , bookSeqNo=" ).append( snapSeq ).append( ", expSeqNo=" ).append( nextExpSeq ).append( ", replayed" ).append( cnt );

            _log.warn( _errMsg );
        }

        _rootEnqueue = _lastEnqueued = null;

        return ok;
    }

    @Override protected void setSeqNums( int msgSeqNum, int rptSeq ) {
        _lastEventSeqNum = msgSeqNum;
        _bookSeqNum      = rptSeq;
    }

    @Override public long getDataSeqNum() {
        return _bookSeqNum;
    }

    @Override public void setDataSeqNum( final long dataSeqNum ) { _bookSeqNum = dataSeqNum; }

    @Override public double getRefPrice() { return _book.getRefPrice(); }

    @Override public int getMsgSeqNum() {
        return _lastEventSeqNum;
    }

    @Override public void setMsgSeqNum( final int seqNum ) {
        _lastEventSeqNum = seqNum;
    }

    public void enqueue( MDEntryImpl nextEntry ) {
        if ( _rootEnqueue == null ) {
            _rootEnqueue = _lastEnqueued = nextEntry;
        } else {
            _lastEnqueued.setNext( nextEntry );
            _lastEnqueued = nextEntry;
        }
    }

    public int getLastTickId() { return (int) _bookSeqNum; }
}
