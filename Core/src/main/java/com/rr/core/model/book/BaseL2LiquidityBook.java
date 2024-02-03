/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.BaseEvent;
import com.rr.core.model.Book;
import com.rr.core.model.Instrument;
import com.rr.core.model.LiquidityBook;
import com.rr.core.recovery.json.JSONPrettyDump;
import com.rr.core.utils.Utils;

/**
 * L2 book that tracks changes to liquidity by working out differences between book between atomic operations which invoke the deltaToBook
 * which also snaps the deltaBook into this book.
 */
public abstract class BaseL2LiquidityBook<T extends BaseL2LiquidityBook> extends BaseEvent<T> implements LiquidityBook {

    private static final boolean TREAT_DROPPED_LEVELS_AS_REMOVED = false;
    public static long _mismatchQtyWC;
    public static long _mismatchQtyWD;
    public static long _mismatchQtyWE;
    private static Logger _log = LoggerFactory.create( BaseL2LiquidityBook.class );
    private final           UnsafeL2Book         _book             = new UnsafeL2Book( null, 0 );
    private final           ApiMutatableBook     _tmpBook          = new UnsafeL2Book( null, 0 );
    private final           LiquidityTrackerImpl _bidTracker       = new LiquidityTrackerImpl( 0, true );
    private final           LiquidityTrackerImpl _askTracker       = new LiquidityTrackerImpl( 0, false );
    private final transient BookEntryImpl        _tmpEntry         = new BookEntryImpl();
    private final transient LiquidityDeltaEntry  _tmpDeltaEntry    = new LiquidityDeltaEntryImpl();
    private                 boolean              _snapIncludesLiq  = true;
    private                 boolean              _verifyBook       = false;
    private                 long                 _deltaDurationMS;
    private                 String               _id;
    private                 boolean              _useExtremeVerify = false;
    private                 boolean              _wipeInvalidBook  = true;

    public BaseL2LiquidityBook() {
        super(); // for reflection

        _id = getClass().getSimpleName() + " : ";
    }

    public BaseL2LiquidityBook( Instrument inst, int numBookLevels ) {
        this( inst, numBookLevels, numBookLevels );
    }

    public BaseL2LiquidityBook( Instrument inst, int numBookLevels, int numLiqLevels ) {
        setInstrument( inst );
        _book.setMaxLevels( numBookLevels );
        _bidTracker.setMaxLevels( numLiqLevels );
        _askTracker.setMaxLevels( numLiqLevels );
    }

    @Override public void deltaFromBook( Book latestBook, boolean resetCountersFirst, final Object event ) {

        boolean latestBookValid = latestBook.isValid();

        final boolean pricesValid;

        if ( latestBook instanceof BaseL2Book ) {
            pricesValid = ((BaseL2Book) latestBook).arePricesValid();
        } else pricesValid = latestBookValid;

        if ( _verifyBook && latestBook.getActiveLevels() > 0 ) {
            if ( !pricesValid ) {
                _log.warn( "Book is invalid : " + latestBook.toString() );
            }
        }

        if ( resetCountersFirst ) {
            resetLiquidityCounters();
        }

        if ( _wipeInvalidBook && !pricesValid ) {

            if ( getBook().isValid() ) {// book is invalid, clear it and wait for rebuild
                getBook().reset();
                _bidTracker.reset();
                _askTracker.reset();
            }

        } else {
            if ( _useExtremeVerify && latestBookValid ) {
                _book.snapTo( _tmpBook );
            }

            deltaSide( latestBook, true );
            deltaSide( latestBook, false );

            if ( _useExtremeVerify && latestBookValid ) {
                checkTracker( true, _tmpBook, latestBook, event );
                checkTracker( false, _tmpBook, latestBook, event );
            }

            boolean wasSnapWithLiq = setSnapWithLiquidity( false );

            latestBook.snapTo( this.getBook() );

            setSnapWithLiquidity( wasSnapWithLiq );
        }
    }

    @Override public int getActiveLiqLevels() {
        int bidLvls = _bidTracker.getActiveLevels();
        int askLvls = _askTracker.getActiveLevels();
        return Math.max( bidLvls, askLvls );
    }

    @Override public boolean getAskLiqEntry( int lvl, LiquidityDeltaEntry dest ) {

        _askTracker.getEntry( lvl, dest );

        return Utils.hasVal( dest.getPrice() );
    }

    @Override public LiquidityTracker getAskTracker() { return _askTracker; }

    @Override public boolean getBidLiqEntry( int lvl, LiquidityDeltaEntry dest ) {

        _bidTracker.getEntry( lvl, dest );

        return Utils.hasVal( dest.getPrice() );
    }

    @Override public LiquidityTracker getBidTracker() { return _bidTracker; }

    @Override public ApiMutatableBook getBook()                  { return _book; }

    @Override public void setBook( Book latestBook ) {

        boolean latestBookValid = latestBook.isValid();

        final boolean pricesValid;

        if ( latestBook instanceof BaseL2Book ) {
            pricesValid = ((BaseL2Book) latestBook).arePricesValid();
        } else pricesValid = latestBook.isValid();

        if ( _verifyBook && latestBook.getActiveLevels() > 0 ) {
            if ( !pricesValid ) {
                _log.warn( "Book is invalid : " + latestBook.toString() );
            }
        }

        if ( _wipeInvalidBook && !pricesValid ) {

            if ( getBook().isValid() ) {
                getBook().reset();
                _bidTracker.reset();
                _askTracker.reset();
            }

        } else { // book is invalid, clear it and wait for rebuild
            if ( _useExtremeVerify && latestBookValid ) {
                _book.snapTo( _tmpBook );
            }

            boolean wasSnapWithLiq = setSnapWithLiquidity( false );

            latestBook.snapTo( this.getBook() );

            setSnapWithLiquidity( wasSnapWithLiq );
        }
    }

    @Override public long getDeltaDurationMS()                       { return _deltaDurationMS; }

    @Override public void setDeltaDurationMS( long deltaDurationMS ) { _deltaDurationMS = deltaDurationMS; }

    @Override public boolean isBookIdentical( LiquidityBook o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        BaseL2LiquidityBook that = (BaseL2LiquidityBook) o;

        if ( !_book.isBookIdentical( that._book ) ) return false;

        if ( _snapIncludesLiq != that._snapIncludesLiq ) return false;
        if ( !_bidTracker.equals( that._bidTracker ) ) return false;
        return _askTracker.equals( that._askTracker );
    }

    @Override public boolean isSnapIncludesLiq() { return _snapIncludesLiq; }

    @Override public void postWrite() {
        moveQtyToPrevQty();
        resetTradeCounters();
        resetLiquidityCounters();
        removeInactiveLevels();
    }

    @Override public void removeInactiveLevels() {
        _bidTracker.removeInactiveLevels();
        _askTracker.removeInactiveLevels();
    }

    @Override public void removeLvlsWithNoAction() {
        _bidTracker.removeLvlsWithNoAction();
        _askTracker.removeLvlsWithNoAction();
    }

    @Override public void resetLiquidityCounters() {
        _bidTracker.resetLiquidityCounters();
        _askTracker.resetLiquidityCounters();
    }

    @Override public void resetTrackers() {
        _bidTracker.reset();
        _askTracker.reset();
    }

    @Override public void resetTradeCounters() {
        _bidTracker.resetTradeCounters();
        _askTracker.resetTradeCounters();
    }

    @Override public void set( final Instrument inst, final int numBookLevels, final int numDeltaLevels ) {
        setInstrument( inst );

        _book.setMaxLevels( numBookLevels );

        _bidTracker.setMaxLevels( numDeltaLevels );
        _askTracker.setMaxLevels( numDeltaLevels );
    }

    @Override public boolean setSnapWithLiquidity( boolean snapIncludesLiq ) {
        boolean prvVal = _snapIncludesLiq;
        _snapIncludesLiq = snapIncludesLiq;
        return prvVal;
    }

    @Override public void setUseExtremeVerify( final boolean useExtremeVerify ) {
        _useExtremeVerify = useExtremeVerify;
    }

    @Override public void dump( ReusableString dest ) {
        try {

            dest.append( id() );

            Instrument instrument = _book.getInstrument();

            if ( instrument != null ) {
                dest.append( instrument.id() );
            }

            dest.append( "\n" );

            _bidTracker.dump( dest );
            _askTracker.dump( dest );

            _book.dump( dest );

        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    @Override public Instrument getInstrument()                  { return _book.getInstrument(); }

    @Override public void setInstrument( final Instrument instrument ) {
        if ( instrument != null && instrument != _book.getInstrument() ) {
            _book.setInstrument( instrument );

            String id = "LiqBook : ";

            id += instrument.id();

            _id = id;
        }
    }

    @Override public String id()                                     { return _id; }

    @Override public long getDataSeqNum()                        { return _book.getDataSeqNum(); }

    @Override public void setDataSeqNum( final long dataSeqNum ) { _book.setDataSeqNum( dataSeqNum ); }

    @Override public boolean isValid()                           { return _book.isValid(); }

    @Override public void setDirty( final boolean isDirty )      { _book.setDirty( isDirty ); }

    @Override public void reset() {
        super.reset();
        _book.reset();
        resetTrackers();
    }

    @Override public void snapTo( LiquidityBook dest ) {
        dest.setInstrument( _book.getInstrument() );
        _book.snapTo( dest.getBook() );

        if ( _snapIncludesLiq ) {
            _bidTracker.snapTo( dest.getBidTracker() );
            _askTracker.snapTo( dest.getAskTracker() );
        }
    }

    public boolean isWipeInvalidBook()                                        { return _wipeInvalidBook; }

    @Override public void setWipeInvalidBook( final boolean wipeInvalidBook ) { _wipeInvalidBook = wipeInvalidBook; }

    @Override public boolean verifyEndOfBlock() {
        if ( !_book.arePricesValid() ) {
            return false;
        }

        boolean bidTrackerValid = _bidTracker.verifyEndOfBlock();
        boolean askTrackerValid = _askTracker.verifyEndOfBlock();

        return bidTrackerValid && askTrackerValid;
    }

    @Override public boolean verifyPostOp() {
        if ( !_book.arePricesValid() ) {
            return false;
        }

        boolean bidTrackerValid = _bidTracker.verifyPostOp();
        boolean askTrackerValid = _askTracker.verifyPostOp();

        return bidTrackerValid && askTrackerValid;
    }

    private boolean checkTracker( final boolean isBidSide, final Book beforeBook, final Book latestBook, final Object event ) {
        boolean ok = true;

        LiquidityTrackerImpl t = (isBidSide) ? _bidTracker : _askTracker;

        int tLvls = t.getActiveLevels();

        int bLatestLvls = latestBook.getActiveLevels();

        for ( int i = 0; ok && i < tLvls; ++i ) {
            t.getEntry( i, _tmpDeltaEntry );

            boolean found = false;

            for ( int j = 0; ok && j < bLatestLvls; j++ ) {

                if ( isBidSide ) {
                    latestBook.getBidEntry( j, _tmpEntry );
                } else {
                    latestBook.getAskEntry( j, _tmpEntry );
                }

                if ( Utils.compare( _tmpDeltaEntry.getPrice(), _tmpEntry.getPrice() ) == 0 ) {
                    found = true;

                    double origQty = _tmpDeltaEntry.getPrevQty();

                    if ( Utils.hasVal( origQty ) ) {
                        double expQty = origQty + _tmpDeltaEntry.getAddedLiquidity() - _tmpDeltaEntry.getRemovedLiquidity();

                        boolean qtyMatchesNewQty = (Utils.compare( _tmpDeltaEntry.getQty(), expQty ) == 0);

                        if ( !qtyMatchesNewQty ) {
                            _log.info( "POST checkTracker mismatch WC#" + (++_mismatchQtyWC) + " on EXPECTED qty (A), bidSide=" + isBidSide + ", i=" + i + ", j=" + j +
                                       ", liqEntry=" + _tmpDeltaEntry.toString() + ", prvQty=" + origQty + ", expQty=" + expQty );
                        }
                    }

                    boolean qtySame = (Utils.compare( _tmpDeltaEntry.getQty(), _tmpEntry.getQty() ) == 0);

                    if ( qtySame ) {
                        ok = true;

                        break;

                    } else if ( Utils.isZero( _tmpDeltaEntry.getQty() + _tmpDeltaEntry.getTradedQty() ) ) {

                        _log.info(
                                "POST checkTracker mismatch WD#" + (++_mismatchQtyWD) + " on qty (B), bidSide=" + isBidSide + ", i=" + i + ", j=" + j + ", liqEntry=" + _tmpDeltaEntry.toString() + ", bookEntry=" + _tmpEntry.toString() );

                        postErrLog( beforeBook, latestBook, event );

                        ok = false;
                    }
                }
            }

            if ( ok && !found ) { // price level not in book, check zero qty
                if ( latestBook.getMaxLevels() > 1 && Utils.hasNonZeroVal( _tmpDeltaEntry.getQty() ) ) {
                    _log.info( "POST checkTracker mismatch WE#" + (++_mismatchQtyWE) + " on qty (C), bidSide=" + isBidSide + ", i=" + i + ", liqEntry=" + _tmpDeltaEntry.toString() + ", bookEntry=" + _tmpEntry.toString() );
                    postErrLog( beforeBook, latestBook, event );
                    ok = false;
                }
            }
        }

        if ( !ok ) {
            _log.info( "checkTracker mismatch" );
        }

        return ok;
    }

    private void deltaSide( Book latestBook, boolean isBidSide ) {

        BookEntryImpl[]      entries = (isBidSide) ? _book.getBids() : _book.getAsks();
        LiquidityTrackerImpl tracker = (isBidSide) ? _bidTracker : _askTracker;

        int newLvls = latestBook.getActiveLevels();

        int prevLvls = _book.getActiveLevels();

        int prvIdx       = 0;
        int liqPrevIndex = 0; // stores the index from operation on previous book entry
        int liqNewIndex  = 0;  // stores the index from operation on new book entry
        int latestIdx    = 0;

        BookEntryImpl prevEntry = null;
        BookEntryImpl newEntry  = _tmpEntry;

        boolean getNextNewLvl = true;
        boolean getNextPrvLvl = true;

        boolean prevNullPx = false;
        boolean newNullPx  = false;

        //         while( prvIdx < prevLvls && latestIdx < newLvls ) {

        while( !prevNullPx && !newNullPx ) { // have a prev px and new px to process

            if ( getNextPrvLvl ) {
                if ( prvIdx < prevLvls ) {
                    prevEntry     = entries[ prvIdx++ ];
                    getNextPrvLvl = false;
                    prevNullPx    = Utils.isNull( prevEntry.getPrice() );
                } else {
                    prevNullPx = true;
                }
            }

            if ( getNextNewLvl ) {
                if ( latestIdx < newLvls ) {
                    newEntryFetcher( latestBook, isBidSide, latestIdx++, newEntry );
                    getNextNewLvl = false;
                    newNullPx     = Utils.isNull( newEntry.getPrice() );
                } else {
                    newNullPx = true;
                }
            }

            if ( prevNullPx || newNullPx ) { // will be no more matching prices

                break;

            } else {

                int cmpPx = Utils.compare( newEntry.getPrice(), prevEntry.getPrice() );

                double  newQty        = newEntry.getQty();
                boolean newQtyNonZero = Utils.hasNonZeroVal( newQty );

                if ( cmpPx == 0 ) { // price exists in new book, update qty then set flags to iterate to next in both prev and new lists

                    if ( newQtyNonZero ) {
                        liqPrevIndex = liqNewIndex = tracker.liquidityChange( liqNewIndex, newQty, newEntry.getPrice() );
                    }

                    getNextPrvLvl = true;
                    getNextNewLvl = true;

                } else if ( shouldInsertNewEntry( isBidSide, cmpPx ) ) { // new price level in book

                    if ( newQtyNonZero ) {
                        liqNewIndex = tracker.liquidityChange( liqNewIndex, newQty, newEntry.getPrice() );
                    }

                    getNextNewLvl = true;

                } else { // prev entry doesnt have entry in new book

                    if ( Utils.hasNonZeroVal( prevEntry.getQty() ) ) {
                        liqPrevIndex = tracker.liquidityChange( liqPrevIndex, 0, prevEntry.getPrice() );
                    }

                    getNextPrvLvl = true;
                }
            }
        }

        if ( prevNullPx ) { // end of prev entries
            if ( !newNullPx ) {
                if ( !getNextNewLvl ) {
                    if ( Utils.hasNonZeroVal( newEntry.getQty() ) ) {
                        liqNewIndex = tracker.liquidityChange( liqNewIndex, newEntry.getQty(), newEntry.getPrice() );
                    }
                }

                while( latestIdx < newLvls ) {

                    newEntryFetcher( latestBook, isBidSide, latestIdx++, newEntry );
                    newNullPx = Utils.isNull( newEntry.getPrice() );

                    if ( newNullPx ) { // no more prices

                        break;

                    } else {

                        if ( Utils.hasNonZeroVal( newEntry.getQty() ) ) {
                            liqNewIndex = tracker.liquidityChange( liqNewIndex, newEntry.getQty(), newEntry.getPrice() );
                        }
                    }
                }
            }
        }

        if ( newNullPx ) { // end of new entries
            double updatedQty = (TREAT_DROPPED_LEVELS_AS_REMOVED) ? 0 : Constants.UNSET_DOUBLE;

            if ( !prevNullPx ) {
                if ( !getNextPrvLvl ) {
                    if ( Utils.hasNonZeroVal( prevEntry.getQty() ) ) {
                        liqPrevIndex = tracker.liquidityChange( liqPrevIndex, updatedQty, prevEntry.getPrice() );
                    }
                }

                while( prvIdx < prevLvls ) {

                    prevEntry  = entries[ prvIdx++ ];
                    prevNullPx = Utils.isNull( prevEntry.getPrice() );

                    if ( prevNullPx ) { // no more prices

                        break;

                    } else {

                        if ( Utils.hasNonZeroVal( prevEntry.getQty() ) ) {
                            liqPrevIndex = tracker.liquidityChange( liqPrevIndex, updatedQty, prevEntry.getPrice() );
                        }
                    }
                }
            }
        }
    }

    private void moveQtyToPrevQty() {
        _bidTracker.moveQtyToPrevQty();
        _askTracker.moveQtyToPrevQty();
    }

    private void newEntryFetcher( Book latestBook, boolean isBidSide, int idx, BookEntryImpl newEntry ) {
        if ( isBidSide ) {
            latestBook.getBidEntry( idx, newEntry );
        } else {
            latestBook.getAskEntry( idx, newEntry );
        }
    }

    private void postErrLog( final Book beforeBook, final Book latestBook, final Object event ) {
        if ( beforeBook != null ) {
            _log.info( "\nBEFORE: " + beforeBook.toString() );
        }
        if ( latestBook != null ) {
            _log.info( "\nPOST: " + latestBook.toString() );
        }
        if ( event != null ) {
            ReusableString msg = TLC.strPop();
            msg.copy( "\nEVENT: " ).chain( () -> JSONPrettyDump.objToJSON( event, msg ) );
            _log.info( msg );
            TLC.strPush( msg );
        }
    }

    private boolean shouldInsertNewEntry( boolean isBidSide, int cmp ) {
        if ( isBidSide ) {
            return cmp > 0;
        }

        return cmp < 0;
    }
}
