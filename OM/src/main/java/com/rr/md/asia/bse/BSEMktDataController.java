/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.asia.bse;

import com.rr.core.collections.IntToIntHashMap;
import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.ZString;
import com.rr.core.model.*;
import com.rr.core.utils.Utils;
import com.rr.core.utils.lock.OptimisticReadWriteLock;
import com.rr.inst.InstrumentStore;
import com.rr.md.book.BookFactory;
import com.rr.md.book.MktDataController;
import com.rr.model.generated.internal.events.impl.MDEntryImpl;
import com.rr.model.generated.internal.events.impl.MDIncRefreshImpl;
import com.rr.model.generated.internal.events.impl.MDSnapshotFullRefreshImpl;

/**
 * BSE specialisation
 * <p>
 * Note the array and prodGrpMap could be replaced by an IntHolder in the BookContext .... set in the subscribe method
 */
public final class BSEMktDataController extends MktDataController<BSEBookAdapter> {

    private static final long ENQUEUE_DURATION_WARN_NANOS_ = 1000;
    private final int[]              _secGrpSeqNums;            // sequence number for products 0..31   CDX ... 1 to 797 EqD
    private final MDIncRefreshImpl[] _prodGrpPendingChain;
    private final int[]              _chainSize;
    private final int                _prodSeqNumArraySize;
    private final IntToIntHashMap    _prodGrpMap;
    private int _secGrpId;
    private int _curProdSeqNum;
    private boolean _debug = false;

    private boolean _setDirtyFlag = false;

    public BSEMktDataController( String id,
                                 String rec,
                                 EventDispatcher inboundDispatcher,
                                 BookFactory<BSEBookAdapter> bookFactory,
                                 MktDataListener<BSEBookAdapter> mktDataListener,
                                 InstrumentStore instrumentStore,
                                 boolean enqueueIncUpdatesOnGap,
                                 int maxProdGrpArraySize ) {

        super( id, rec, inboundDispatcher, bookFactory, mktDataListener, instrumentStore, enqueueIncUpdatesOnGap );

        _secGrpSeqNums       = new int[ maxProdGrpArraySize ]; // sequence number for products 0..31
        _prodGrpPendingChain = new MDIncRefreshImpl[ maxProdGrpArraySize ];
        _chainSize           = new int[ maxProdGrpArraySize ];

        _prodGrpMap = new IntToIntHashMap( 128, 0.75f );

        _prodSeqNumArraySize = maxProdGrpArraySize;
    }

    @Override
    protected final void handleSnapshot( final MDSnapshotFullRefreshImpl msg ) {

        if ( msg.getSecurityIDSource() != SecurityIDSource.ExchangeSymbol ) {
            return;
        }

        if ( isSubscribed( msg.getSecurityID() ) ) {

            processBookChange( msg.getEventHandler(), msg.getSecurityID(), msg.getSecurityIDSource(), msg.getSecurityExchange() );

            if ( msg.getLastMsgSeqNumProcessed() > _lastBook.getMsgSeqNum() && _secGrpId != 0 ) {
                boolean updated = false;

                OptimisticReadWriteLock lock  = _lastBook.getLock();
                long                    stamp = lock.writeLock();
                try {
                    if ( _lastBook.applySnapshot( msg, _eventRecycler ) ) {
                        updated = true;
                    }

                    //recoverFromSnapshot();
                } finally {
                    lock.unlockWrite( stamp );
                }

                if ( msg.getLastMsgSeqNumProcessed() > _curProdSeqNum ) {
                    _curProdSeqNum = msg.getLastMsgSeqNumProcessed();
                }

                if ( updated ) {

//                    REPLAY ANY PENDING INC UPDATES, RECYCLING ANY OLD
//                    ISSUE IS MdIncRefresh CAN AFFECT MULTIPLE BOOKS !! AND THOSE BOOK SNAPS MAY NOT HAVE ARRIVED YET
//                    SO ONLY REPLAY THE SubElement to the BOOK and RECYCLE THAT ... RECYCLE WHOLE MESSAGE IF ALL SUBELEMS FOR SNAP BOOK
//
//                    MAYBE REMOVE SNAPSHOT CODE FROM BOOKS ?
//
//                    ADD UPPER BOUND TO NUMBER OF PACKETS TO BE HELD PENDING RECOVERY

                    notifyUpdate( _lastBook );      // book updated
                }
            } else {
                // ignore old snapshot
            }
        }
    }

    /**
     * handle the INcrementalRefresh : RECYCLE if not enqueued in book
     */
    @Override
    protected final void handleIncrementalRefresh( final MDIncRefreshImpl msg ) {

        MDEntryImpl nextEntry = (MDEntryImpl) msg.getMDEntries();

        if ( isSubscribed( msg, nextEntry ) ) {
            if ( nextEntry.getSecurityIDSource() == SecurityIDSource.ExchangeSymbol ) {

                ZString curSecId = null;

                while( nextEntry != null ) {
                    curSecId = nextEntry.getSecurityID();

                    processBookChange( msg.getEventHandler(), curSecId, nextEntry.getSecurityIDSource(), nextEntry.getSecurityExchange() );

                    if ( _secGrpId != 0 ) break;

                    if ( _debug ) {
                        _logMsg.copy( "MDINC SKIP entry : [" ).append( msg.getMsgSeqNum() )
                               .append( " / start " ).append( "] " )
                               .append( ", secGrpId=" ).append( _secGrpId )
                               .append( " book=" ).append( curSecId )
                               .append( " " ).append( _lastBook.getInstrument().getSecurityDesc() )
                        ;

                        _log.info( _logMsg );
                    }

                    nextEntry = nextEntry.getNext();
                }

                if ( _secGrpId != 0 ) {
                    if ( checkApplyEntry( msg ) ) {

                        doApplyIncrementalUpdate( msg, nextEntry, curSecId, true );

                    } else if ( shouldQueueEntry( msg ) ) {

                        enqueueUpdate( msg );

                        return; // DONT RECYCLE MESSAGE
                    }
                }
            }
        }

        checkForBookDispatch();

        getEventRecycler().recycle( msg );
    }

    @Override
    protected final void processBookChange( final EventHandler src, final ZString securityID, final SecurityIDSource idSrc, final ExchangeCode securityExchange ) {
        if ( securityID.equals( getLastSecurityId() ) ) {
            return;
        }

        if ( _debug ) {
            _logMsg.copy( "changeBook prevBook=" ).append( getLastSecurityId() ).append( ", secGrp=" ).append( _secGrpId ).append( ", curSecNum=" ).append( _curProdSeqNum );
            _log.info( _logMsg );
        }

        storeLatestProdSeqNumBeforeBookChange();

        super.processBookChange( src, securityID, idSrc, securityExchange );

        _secGrpId = ((ExchangeInstrument) _lastBook.getInstrument()).getSecurityGroupId();

        getProdSeqNumForNewBook();

        if ( _debug ) {
            _logMsg.copy( "changeBook newBook=" ).append( securityID ).append( ", secGrp=" ).append( _secGrpId ).append( ", curSecNum=" ).append( _curProdSeqNum );
            _log.info( _logMsg );
        }
    }

    public final boolean isDebug() {
        return _debug;
    }

    public final void setDebug( boolean debug ) {
        _debug = debug;
    }

    private boolean checkApplyEntry( final MDIncRefreshImpl msg ) {
        if ( !isMarkDirtyEnabled() ) _setDirtyFlag = false;
        final int productSeqNum = msg.getMsgSeqNum();

        if ( (_curProdSeqNum + 1) == productSeqNum || _curProdSeqNum == 0 ) {
            return true;
        }

        if ( isEnqueueIncUpdatesOnGap() ) {
            return false; // all inc updates enqueued for next snapshot
        }

        // if detect gap mark book as dirty and still apply update as long as not old

        if ( productSeqNum <= _curProdSeqNum ) {
            return false;  // dont process old seqnum
        }

        if ( isMarkDirtyEnabled() ) _setDirtyFlag = true;
        _lastBookChanged = true;                // propogate state change

        _errMsg.copy( "Force Update and Mark Dirty, Gap detected on " ).append( _lastBook.getInstrument().getSecurityDesc() ).append( ", bookId=" ).append( _lastBook.getInstrument().getExchangeSymbol() );
        _errMsg.append( ", lastSeqNum=" ).append( _curProdSeqNum ).append( ", gapSeqNum=" ).append( productSeqNum );
        _errMsg.append( ", gapOf=" ).append( productSeqNum - _curProdSeqNum );

        _log.warn( _errMsg );

        return true;
    }

    private void doApplyIncrementalUpdate( final MDIncRefreshImpl msg, MDEntryImpl nextEntry, ZString curSecId, boolean isDirty ) {
        long stamp = _lastBook.getLock().writeLock();

        try {
            _curProdSeqNum = msg.getMsgSeqNum();

            if ( _setDirtyFlag ) _lastBook.setDirty( isDirty );

            int entry    = 0;
            int startGrp = _secGrpId;

            while( nextEntry != null ) {
                if ( nextEntry.getSecurityIDSource() == SecurityIDSource.ExchangeSymbol ) {

                    final ZString nextSecId = nextEntry.getSecurityID();

                    if ( !nextSecId.equals( curSecId ) ) {
                        stampChangedBook( msg );

                        _lastBook.getLock().unlockWrite( stamp );

                        processBookChange( msg.getEventHandler(), nextSecId, nextEntry.getSecurityIDSource(), nextEntry.getSecurityExchange() );

                        stamp = _lastBook.getLock().writeLock();

                        if ( _setDirtyFlag ) _lastBook.setDirty( isDirty );

                        curSecId = nextSecId;

                        if ( startGrp != _secGrpId && _secGrpId != 0 ) {
                            _logMsg.copy( "WARNING: increment update has multiple productGroups, msgSeqNum=[" ).append( msg.getMsgSeqNum() )
                                   .append( " / " ).append( ++entry ).append( "] " )
                                   .append( ", firstSecGrpId=" ).append( startGrp )
                                   .append( ", secGrpId=" ).append( _secGrpId )
                                   .append( " book=" ).append( curSecId )
                                   .append( " " ).append( _lastBook.getInstrument().getSecurityDesc() )
                            ;

                            _log.info( _logMsg );
                        }
                    }

                    if ( _secGrpId != 0 ) {
                        if ( _debug ) {
                            _logMsg.copy( "MDINC entry : [" ).append( msg.getMsgSeqNum() )
                                   .append( " / " ).append( ++entry ).append( "] " )
                                   .append( ", secGrpId=" ).append( _secGrpId )
                                   .append( " book=" ).append( curSecId )
                                   .append( " " ).append( _lastBook.getInstrument().getSecurityDesc() )
                            ;

                            _log.info( _logMsg );
                        }

                        _lastBookChanged |= _lastBook.applyIncrementalEntry( msg.getMsgSeqNum(), nextEntry );
                    } else {
                        if ( _debug ) {
                            _logMsg.copy( "MDINC SKIP entry : [" ).append( msg.getMsgSeqNum() )
                                   .append( " / " ).append( ++entry ).append( "] " )
                                   .append( ", secGrpId=" ).append( _secGrpId )
                                   .append( " book=" ).append( curSecId )
                                   .append( " " ).append( _lastBook.getInstrument().getSecurityDesc() )
                            ;

                            _log.info( _logMsg );
                        }
                    }
                }

                nextEntry = nextEntry.getNext();
            }

            stampChangedBook( msg );

        } finally {
            _lastBook.getLock().unlockWrite( stamp );
        }
    }

    // enqueue in asc order as thats order replay requires ... check to see if enqueued update fills gap
    // @WARNING N+1 function COULD BE ISSUE FOR BIG GAPS
    @SuppressWarnings( "null" )
    private void enqueueUpdate( MDIncRefreshImpl msg ) {

        final long startNanos = Utils.nanoTime();

        msg.detachQueue();

        MDIncRefreshImpl prv  = null;
        MDIncRefreshImpl next = _prodGrpPendingChain[ _secGrpId ];

        if ( next == null ) {
            _prodGrpPendingChain[ _secGrpId ] = msg;
            return;
        }

        int diff = 0;

        int     nextExpected = _curProdSeqNum + 1;
        boolean noGaps       = true;

        while( next != null ) {
            final int nextMsgSeqNum = next.getMsgSeqNum();
            diff = msg.getMsgSeqNum() - nextMsgSeqNum;

            if ( diff <= 0 ) break;

            if ( nextExpected != nextMsgSeqNum ) {
                noGaps = false;
            }

            prv  = next;
            next = next.getNext();
            ++nextExpected;
        }

        int curProdSeqNum = _curProdSeqNum;

        if ( diff == 0 ) {
            // duplicate
            getEventRecycler().recycle( msg );
        }

        if ( diff < 0 ) {
            msg.setNext( next );
            if ( prv == null ) {
                _prodGrpPendingChain[ _secGrpId ] = msg;
            } else {
                prv.setNext( msg );
            }

            if ( noGaps ) { // check for chain being restored
                while( msg != null ) {
                    final int nextMsgSeqNum = msg.getMsgSeqNum();

                    if ( nextExpected != nextMsgSeqNum ) {
                        noGaps = false;
                        break;
                    }

                    msg = msg.getNext();
                    ++nextExpected;
                }

                if ( noGaps ) {
                    recoverGap();
                }
            }
            return;
        }

        if ( diff > 0 ) {
            // got to end of chain, next is null, set prv to msg
            prv.setNext( msg );

            if ( noGaps ) {
                recoverGap();
            }
        }

        final long durationNanos = Math.abs( Utils.nanoTime() - startNanos );

        if ( noGaps ) {
            int gap = _curProdSeqNum - curProdSeqNum;

            _logMsg.copy( "ENQUEUE REPLAY Recovered gap processing took " ).append( durationNanos ).append( " nanos for prodGrp=" ).append( _secGrpId )
                   .append( ", msgSeqNum=" ).append( msg.getMsgSeqNum() )
                   .append( ", gap=" ).append( gap );

            _log.info( _logMsg );
        } else {
            final int chainSize = _chainSize[ _secGrpId ] + 1;
            _chainSize[ _secGrpId ] = chainSize;

            if ( chainSize > getMaxEnqueueIncUpdatesOnGap() ) {

                _logMsg.copy( "ENQUEUE FORCE REPLAY, Exceeded max enqueue size of " ).append( getMaxEnqueueIncUpdatesOnGap() ).append( ", duration " )
                       .append( durationNanos ).append( " nanos for prodGrp=" ).append( _secGrpId )
                       .append( ", curSeqNum=" ).append( _curProdSeqNum )
                       .append( ", newSeqNum=" ).append( msg.getMsgSeqNum() );
                _log.warn( _logMsg );

                recoverGap();

            } else {
                if ( durationNanos > ENQUEUE_DURATION_WARN_NANOS_ ) {
                    _logMsg.copy( "ENQUEUE for gap processing took " ).append( durationNanos ).append( " nanos for prodGrp=" ).append( _secGrpId )
                           .append( ", curSeqNum=" ).append( _curProdSeqNum )
                           .append( ", msgSeqNum=" ).append( msg.getMsgSeqNum() );
                    _log.warn( _logMsg );
                } else {
                    if ( _debug ) {
                        _logMsg.copy( "ENQUEUE due to GAP " ).append( " prodGrp=" ).append( _secGrpId )
                               .append( ", curSeqNum=" ).append( _curProdSeqNum )
                               .append( ", msgSeqNum=" ).append( msg.getMsgSeqNum() );
                        _log.info( _logMsg );
                    }
                }
            }
        }
    }

    private void getProdSeqNumForNewBook() {
        if ( _secGrpId < _prodSeqNumArraySize ) {
            _curProdSeqNum = _secGrpSeqNums[ _secGrpId ];
        } else {
            _curProdSeqNum = _prodGrpMap.get( _secGrpId );
        }
    }

    private void recover( MDIncRefreshImpl msg ) {
        MDEntryImpl nextEntry = (MDEntryImpl) msg.getMDEntries();

        ZString curSecId = nextEntry.getSecurityID();

        processBookChange( msg.getEventHandler(), curSecId, nextEntry.getSecurityIDSource(), nextEntry.getSecurityExchange() );

        doApplyIncrementalUpdate( msg, nextEntry, curSecId, false );

        checkForBookDispatch();

        getEventRecycler().recycle( msg );
    }

    /**
     * incremental updates have recovered the GAP
     */
    private void recoverGap() {
        if ( isMarkDirtyEnabled() ) _setDirtyFlag = true; // force mark dirtyflag reset

        MDIncRefreshImpl cur = _prodGrpPendingChain[ _secGrpId ];
        MDIncRefreshImpl next;

        while( cur != null ) {
            next = cur.getNext();
            cur.detachQueue();
            recover( cur );
            cur = next;
        }

        if ( isMarkDirtyEnabled() ) _setDirtyFlag = false;

        _prodGrpPendingChain[ _secGrpId ] = null;
        _chainSize[ _secGrpId ]           = 0;
    }

    private boolean shouldQueueEntry( final MDIncRefreshImpl msg ) {
        final int bookSeqNum      = _lastBook.getMsgSeqNum();
        final int eventBookSeqNum = msg.getMsgSeqNum();

        if ( eventBookSeqNum <= bookSeqNum ) {
            return false;  // OLD dont enqueue
        }

        setLastBookDirty();
        _lastBookChanged = true;                // propogate state change

        _errMsg.copy( "Holdup update as Gap detected, lastBookId=" ).append( _lastBook.getInstrument().getExchangeSymbol() );
        _errMsg.append( ", msgSeqNum=" ).append( msg.getMsgSeqNum() );
        _errMsg.append( ", lastSeqNum=" ).append( bookSeqNum ).append( ", gapSeqNum=" ).append( eventBookSeqNum );
        _errMsg.append( ", gapOf=" ).append( eventBookSeqNum - bookSeqNum );

        _log.warn( _errMsg );

        return true;
    }

    private void stampChangedBook( final MDIncRefreshImpl msg ) {
        if ( _lastBookChanged ) {
            _lastBook.setMsgSeqNum( msg.getMsgSeqNum() );
            _lastBook.setEventTimestamp( msg.getEventTimestamp() );
        }
    }

    private void storeLatestProdSeqNumBeforeBookChange() {
        if ( _curProdSeqNum > 0 ) {
            if ( _secGrpId < _prodSeqNumArraySize ) {
                _secGrpSeqNums[ _secGrpId ] = _curProdSeqNum;
            } else {
                _prodGrpMap.put( _secGrpId, _curProdSeqNum );
            }
        }
    }

}
