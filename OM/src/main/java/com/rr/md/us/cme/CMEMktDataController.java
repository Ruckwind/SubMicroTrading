/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.us.cme;

import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.model.MktDataListener;
import com.rr.core.model.SecurityIDSource;
import com.rr.core.utils.lock.OptimisticReadWriteLock;
import com.rr.inst.InstrumentStore;
import com.rr.md.book.BookFactory;
import com.rr.md.book.MktDataController;
import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.model.generated.internal.events.impl.MDSnapshotFullRefreshImpl;

public final class CMEMktDataController extends MktDataController<CMEBookAdapter> {

    public CMEMktDataController( String id,
                                 String mic,
                                 EventDispatcher inboundDispatcher,
                                 BookFactory<CMEBookAdapter> bookFactory,
                                 MktDataListener<CMEBookAdapter> mktDataListener,
                                 InstrumentStore instrumentStore,
                                 boolean enqueueIncUpdatesOnGap ) {

        super( id, mic, inboundDispatcher, bookFactory, mktDataListener, instrumentStore, enqueueIncUpdatesOnGap );
    }

    @Override
    protected final void handleSnapshot( final MDSnapshotFullRefreshImpl msg ) {

        if ( msg.getSecurityIDSource() != SecurityIDSource.ExchangeSymbol ) {
            return;
        }

        if ( isSubscribed( msg.getSecurityID() ) ) {

            processBookChange( msg.getEventHandler(), msg.getSecurityID(), msg.getSecurityIDSource(), msg.getSecurityExchange() );

            if ( _lastBook != _dummyBook ) {
                final int lastBookSeqNum = _lastBook.getLastTickId();

                if ( msg.getRptSeq() > lastBookSeqNum ) {
                    OptimisticReadWriteLock lock  = _lastBook.getLock();
                    long                    stamp = lock.writeLock();
                    try {
                        if ( _lastBook.applySnapshot( msg, _eventRecycler ) ) {
                            notifyUpdate( _lastBook );      // book updated
                        }
                    } finally {
                        lock.unlockWrite( stamp );
                    }

                } else {
                    // ignore old snapshot
                }
            }
        }
    }

    @Override
    protected final void handleIncrementalRefresh( final MDIncRefreshImpl msg ) {

        MDEntryImpl nextEntry = (MDEntryImpl) msg.getMDEntries();
        MDEntryImpl prevEntry = null;

        while( nextEntry != null ) {
            if ( isSubscribed( msg, nextEntry ) ) {
                if ( processMDIncRefresh( msg, nextEntry ) ) {
                    // ok
                } else if ( shouldQueueEntry( nextEntry ) ) {  // will not enqueue anymore as future entries are processed and book marked dirty
                    nextEntry = extractAndEnqueueEntry( msg, nextEntry, prevEntry );

                    continue;
                }
            }

            prevEntry = nextEntry;
            nextEntry = nextEntry.getNext();
        }

        checkForBookDispatch();

        getEventRecycler().recycle( msg );
    }

    /**
     * @return true if entry processed ok, false if not processed ok
     */
    protected final boolean processMDIncRefresh( final MDIncRefreshImpl msg, final MDEntryImpl entry ) {
        if ( entry.getSecurityIDSource() != SecurityIDSource.ExchangeSymbol ) {
            return true;
        }

        processBookChange( msg.getEventHandler(), entry.getSecurityID(), entry.getSecurityIDSource(), entry.getSecurityExchange() );

        if ( _lastBook == _dummyBook ) return true; // ignore the update

        if ( checkApplyEntry( entry ) ) {
            OptimisticReadWriteLock lock  = _lastBook.getLock();
            long                    stamp = lock.writeLock();
            try {
                _lastBookChanged |= _lastBook.applyIncrementalEntry( msg.getMsgSeqNum(), entry );

                if ( _lastBookChanged ) {
                    _lastBook.setEventTimestamp( msg.getEventTimestamp() );
                }
            } finally {
                lock.unlockWrite( stamp );
            }

            return true;
        }

        return false;
    }

    private boolean checkApplyEntry( final MDEntryImpl entry ) {
        final int bookSeqNum      = _lastBook.getLastTickId();
        final int eventBookSeqNum = entry.getRepeatSeq();

        if ( (bookSeqNum + 1) == eventBookSeqNum || bookSeqNum == 0 ) {
            return true;
        }

        if ( isEnqueueIncUpdatesOnGap() ) {
            return false; // all inc updates enqueued for next snapshot
        }

        // if detect gap mark book as dirty and still apply update as long as not old

        return checkUnexpectedMDEntry( entry );
    }

    private boolean checkUnexpectedMDEntry( final MDEntryImpl entry ) {
        final int bookSeqNum      = _lastBook.getLastTickId();
        final int eventBookSeqNum = entry.getRepeatSeq();

        if ( eventBookSeqNum <= bookSeqNum ) {
            return false;  // dont process
        }

        setLastBookDirty();
        _lastBookChanged = true;                // propogate state change

        _errMsg.copy( "Gap detected on bookId=" );
        _errMsg.append( ", entryBookId=" ).append( entry.getSecurityID() );
        _errMsg.append( ", lastBookId=" ).append( _lastBook.getInstrument().getExchangeSymbol() );
        _errMsg.append( ", lastSeqNum=" ).append( bookSeqNum ).append( ", gapSeqNum=" ).append( eventBookSeqNum );
        _errMsg.append( ", gapOf=" ).append( eventBookSeqNum - bookSeqNum );

        _log.warn( _errMsg );

        return true;
    }

    private MDEntryImpl extractAndEnqueueEntry( final MDIncRefreshImpl msg, MDEntryImpl nextEntry, MDEntryImpl prevEntry ) {
        final MDEntryImpl tmpNextEntry = nextEntry.getNext();

        if ( prevEntry == null ) {                  // move root
            msg.setMDEntries( tmpNextEntry );
        } else {
            prevEntry.setNext( tmpNextEntry );
        }

        nextEntry.setNext( null );
        _lastBook.enqueue( nextEntry );
        nextEntry = tmpNextEntry;
        msg.setNoMDEntries( msg.getNoMDEntries() - 1 );
        return nextEntry;
    }

    private boolean shouldQueueEntry( final MDEntryImpl entry ) {
        final int bookSeqNum      = _lastBook.getLastTickId();
        final int eventBookSeqNum = entry.getRepeatSeq();

        if ( eventBookSeqNum <= bookSeqNum ) {
            return false;  // OLD dont enqueue
        }

        setLastBookDirty();
        _lastBookChanged = true;                // propogate state change

        _errMsg.copy( "Gap detected on bookId=" );
        _errMsg.append( ", entryBookId=" ).append( entry.getSecurityID() );
        _errMsg.append( ", lastBookId=" ).append( _lastBook.getInstrument().getExchangeSymbol() );
        _errMsg.append( ", lastSeqNum=" ).append( bookSeqNum ).append( ", gapSeqNum=" ).append( eventBookSeqNum );
        _errMsg.append( ", gapOf=" ).append( eventBookSeqNum - bookSeqNum );

        _log.warn( _errMsg );

        return true;
    }
}
