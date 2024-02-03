package com.rr.core.model.book;

import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.TLC;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.utils.Utils;

/**
 * BaseLiquidityTrackerImpl - tracks changes to a book
 * <p>
 * note when setting qty at a price level that doesnt count as addedLiquidity
 * only addLiquidity when the qty is changed ... this is because we want to track whats added/removed
 * and not be affected by cleardown / reset / snapshot of book for example
 * <p>
 * Now grows when numLevels = maxLevels
 */
public class LiquidityTrackerImpl implements LiquidityTracker {

    private static final Logger _log = LoggerFactory.create( LiquidityTrackerImpl.class );

    private static final int LVLS_TO_GROW_WHEN_FULL = 10;
    public static long _mismatchExpQtyWA;
    public static long _mismatchTradeGtrRemovedLiqWB;
    private final     boolean                   _isBidSide;
    private           LiquidityDeltaEntryImpl[] _entries;
    private           int                       _maxLevels;
    private           int                       _numLevels;
    private transient double                    _tmpLastPx = Constants.UNSET_DOUBLE; // used to verify in order operations
    private           long                      _badCnt;

    public LiquidityTrackerImpl( final int maxLevels, final boolean isBidSide ) {
        if ( maxLevels == 0 ) _entries = new LiquidityDeltaEntryImpl[ 0 ];
        setMaxLevels( maxLevels );
        _isBidSide = isBidSide;
    }

    @Override public boolean equals( final Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        final LiquidityTrackerImpl that = (LiquidityTrackerImpl) o;

        final int activeLevels = getActiveLevels();
        if ( _isBidSide != that._isBidSide ) return false;
        if ( activeLevels != that.getActiveLevels() ) return false;

        for ( int i = 0; i < activeLevels; i++ ) {
            if ( !_entries[ i ].equals( that._entries[ i ] ) ) return false;
        }

        return true;
    }

    @Override public final String toString() {
        ReusableString s = TLC.instance().pop();
        dump( s );
        String out = s.toString();
        TLC.instance().pushback( s );
        return out;
    }

    @Override public int getMaxLevels() { return _maxLevels; }

    @Override public void setMaxLevels( final int maxLevels ) {
        if ( maxLevels > _maxLevels ) {

            final LiquidityDeltaEntryImpl[] old = _entries;

            _entries = new LiquidityDeltaEntryImpl[ maxLevels ];

            for ( int l = 0; l < _maxLevels; l++ ) {
                _entries[ l ] = old[ l ];
            }

            for ( int l = _maxLevels; l < maxLevels; l++ ) {
                _entries[ l ] = new LiquidityDeltaEntryImpl();
            }

            _maxLevels = maxLevels;
        }
    }

    @Override public void resetLiquidityCounters() {
        for ( LiquidityDeltaEntry e : _entries ) {
            e.resetLiquidityCounters();
        }
    }

    @Override public void resetTradeCounters() {
        for ( LiquidityDeltaEntry e : _entries ) {
            e.resetTradeCounters();
        }
    }

    @Override public int getActiveLevels() { return _numLevels; }

    @Override public void setActiveLevels( final int numActive ) {
        _numLevels = numActive;
    }

    @Override public double getQty( final int lvl ) {
        if ( lvl >= _entries.length ) _entries[ lvl ].getQty();

        return Constants.UNSET_DOUBLE;
    }

    @Override public void removeLvlsWithNoAction() {
        final int levels = _numLevels;

        int lvl     = 0;
        int lastLvl = 0;

        if ( levels > 0 ) {

            while( lvl < _numLevels ) {

                final LiquidityDeltaEntryImpl entry = _entries[ lvl ];

                final boolean missingVals = !entry.isActive();

                if ( missingVals ) {

                    entry.clear();

                    int nextIdx = lvl + 1;

                    boolean swapped = false;

                    while( nextIdx < _numLevels ) {
                        final LiquidityDeltaEntryImpl nextEntry = _entries[ nextIdx ];

                        if ( Utils.hasVal( nextEntry.getPrice() ) ) {
                            if ( Utils.hasNonZeroVal( nextEntry.getQty() ) ) {
                                lastLvl             = lvl + 1;
                                _entries[ lvl ]     = nextEntry;
                                _entries[ nextIdx ] = entry;
                                swapped             = true;
                                break;
                            } else {
                                nextEntry.clear();
                            }
                        }

                        ++nextIdx;
                    }

                    if ( !swapped ) { // nothing left to swap .. ie no more entries with a qty set
                        break;
                    }
                } else {
                    lastLvl = lvl + 1;
                }

                ++lvl;
            }
        }

        setActiveLevels( lastLvl );

        if ( !verifyPostOp() ) {
            _log.warn( "LiquidityTrackerImpl.removeLvlsWithNoAction changed lvl from " + levels + " to " + lastLvl + " resulting in invalid tracker : " + this.toString() );
        }
    }

    @Override public int countActive() {
        int levels = _maxLevels;

        int lvl = 0;

        if ( levels > 0 ) {

            while( lvl < _maxLevels ) {

                if ( Utils.hasVal( _entries[ lvl ].getPrice() ) ) {

                    ++lvl;

                } else {
                    break;
                }
            }
        }

        return lvl;
    }

    @Override public boolean hasPrice( final int lvl ) {
        if ( lvl >= _entries.length ) return false;

        return Utils.hasVal( _entries[ lvl ].getPrice() );
    }

    @Override public double getPrice( final int lvl ) {
        if ( lvl >= _entries.length ) _entries[ lvl ].getPrice();

        return Constants.UNSET_DOUBLE;
    }

    @Override public void removeInactiveLevels() {
        final int levels = _numLevels;

        int lvl     = 0;
        int lastLvl = 0;

        if ( levels > 0 ) {

            while( lvl < _numLevels ) {

                final LiquidityDeltaEntryImpl entry = _entries[ lvl ];

                final boolean missingPrice = Utils.isNull( entry.getPrice() );
                final boolean missingQty   = Utils.isZero( entry.getQty() );

                if ( missingPrice || missingQty ) {

                    entry.clear();

                    int nextIdx = lvl + 1;

                    boolean swapped = false;

                    while( nextIdx < _numLevels ) {
                        final LiquidityDeltaEntryImpl nextEntry = _entries[ nextIdx ];

                        if ( Utils.hasVal( nextEntry.getPrice() ) ) {
                            if ( Utils.hasNonZeroVal( nextEntry.getQty() ) ) {
                                lastLvl             = lvl + 1;
                                _entries[ lvl ]     = nextEntry;
                                _entries[ nextIdx ] = entry;
                                swapped             = true;
                                break;
                            } else {
                                nextEntry.clear();
                            }
                        }

                        ++nextIdx;
                    }

                    if ( !swapped ) { // nothing left to swap .. ie no more entries with a qty set
                        break;
                    }
                } else {
                    lastLvl = lvl + 1;
                }

                ++lvl;
            }
        }

        setActiveLevels( lastLvl );

        if ( !verifyPostOp() ) {
            _log.warn( "LiquidityTrackerImpl.removeInactiveLevels changed lvl from " + levels + " to " + lastLvl + " resulting in invalid tracker : " + this.toString() );
        }
    }

    @Override public final void dump( final ReusableString dest ) {
        dest.append( "numLvl=" ).append( _numLevels ).append( ", max=" ).append( _maxLevels ).append( "\n" );

        try {

            int levels = _numLevels;

            int lvl = 0;

            if ( levels > 0 ) {

                while( lvl < _numLevels ) {

                    final LiquidityDeltaEntryImpl entry = _entries[ lvl ];
                    if ( Utils.hasVal( entry.getPrice() ) ) {
                        if ( _isBidSide ) {
                            dest.append( "[BID LL " );
                        } else {
                            dest.append( "[ASK LL " );
                        }

                        dest.append( lvl ).append( "]  " );

                        dest.append( entry.getQty() ).append( " x " ).append( entry.getPrice() ).append( "  :  A=" );
                        dest.append( entry.getAddedLiquidity() ).append( ", D=" ).append( entry.getRemovedLiquidity() ).append( ", T=" ).append( entry.getTradedQty() );
                        dest.append( "\n" );
                    } else {
                        break;
                    }

                    ++lvl;
                }
            }

        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    @Override public boolean verifyPostOp() {
        int levels = _numLevels;

        if ( levels > 0 ) {

            LiquidityDeltaEntryImpl entry = _entries[ 0 ];

            if ( !verifyPostOpEntry( entry ) ) return false;

            double curPrice = entry.getPrice();

            int lvl = 1;

            while( lvl < _numLevels ) {

                entry = _entries[ lvl ];

                if ( !verifyPostOpEntry( entry ) ) return false;

                int cmpPrice = Utils.compare( entry.getPrice(), curPrice );

                if ( cmpPrice == 0 ) {
                    return false; // duplicate price level
                }

                if ( outOfOrder( cmpPrice ) ) {
                    return false;
                }

                curPrice = entry.getPrice();

                ++lvl;
            }
        }

        return true;
    }

    @Override public boolean verifyEndOfBlock() {
        int levels = _numLevels;

        if ( levels > 0 ) {

            LiquidityDeltaEntryImpl entry = _entries[ 0 ];

            if ( !verifyEndOfBlockEntry( entry ) ) return false;

            double curPrice = entry.getPrice();

            int lvl = 1;

            while( lvl < _numLevels ) {

                entry = _entries[ lvl ];

                if ( !verifyEndOfBlockEntry( entry ) ) return false;

                int cmpPrice = Utils.compare( entry.getPrice(), curPrice );

                if ( cmpPrice == 0 ) {
                    return false; // duplicate price level
                }

                if ( outOfOrder( cmpPrice ) ) {
                    return false;
                }

                curPrice = entry.getPrice();

                ++lvl;
            }
        }

        return true;
    }

    @Override public boolean isBidSide() { return _isBidSide; }

    @Override public int liquidityChange( final int startIndex, final double latestQty, final double price ) {

        DBG_CHECK(); // check before

        int idx = doLiquidityChange( startIndex, latestQty, price );

        DBG_CHECK(); // check after

        return idx < _maxLevels ? idx : Constants.UNSET_INT;
    }

    @Override public void traded( final double tradeQty, final double price ) {
        DBG_CHECK();

        doTraded( tradeQty, price );

        DBG_CHECK();
    }

    @Override public void getEntry( final int level, final LiquidityDeltaEntry that ) {
        if ( level < _entries.length ) {
            that.setLiquidityEntry( _entries[ level ] );
        } else {
            that.clear();
        }
    }

    @Override public void reset() {
        for ( LiquidityDeltaEntry e : _entries ) {
            e.clear();
        }
        setActiveLevels( 0 );
    }

    @Override public void reset( int lvl ) {
        if ( lvl < _entries.length ) {
            _entries[ lvl ].clear();
        }
    }

    @Override public void snapTo( final LiquidityTracker dest ) {
        int levels = _numLevels;

        final int destMaxLevels = dest.getMaxLevels();
        if ( levels > destMaxLevels ) {
            dest.setMaxLevels( levels );
        }

        if ( levels > 0 || dest.getActiveLevels() > 0 ) {
            int lvl = 0;

            dest.setActiveLevels( _numLevels );

            while( lvl < _numLevels ) {
                dest.set( lvl, _entries[ lvl ] );
                ++lvl;
            }

            while( lvl < destMaxLevels ) {
                if ( !dest.hasPrice( lvl ) ) {
                    break; // no need to do any more
                }
                dest.reset( lvl );
                ++lvl;
            }
        }
    }

    @Override public void set( final int lvl, final LiquidityDeltaEntry src ) {
        if ( lvl >= _maxLevels ) {
            setMaxLevels( _maxLevels + LVLS_TO_GROW_WHEN_FULL );
        }
        _entries[ lvl ].setLiquidityEntry( src );
        if ( lvl >= _numLevels ) {
            int orig = _numLevels;
            setActiveLevels( lvl + 1 );
            if ( !verifyPostOp() ) {
                _log.warn( "LiquidityTrackerImpl set changed lvl from " + orig + " to " + (lvl + 1) + ", entry=" + src.toString() + " resulting in invalid tracker : " + this.toString() );
            }
        }
    }

    public void moveQtyToPrevQty() {
        for ( LiquidityDeltaEntry e : _entries ) {
            e.setPrevQty( e.getQty() );
        }
    }

    private void DBG_CHECK() {

        if ( _entries.length == 0 ) return;

        int    idx = 1;
        double p1  = _entries[ 0 ]._price;
        for ( ; idx < _numLevels; idx++ ) {
            double p2 = _entries[ idx ]._price;

            if ( Utils.hasVal( p1 ) && Utils.hasVal( p2 ) ) {
                int cmpPrice = Utils.compare( p2, p1 );

                if ( outOfOrder( cmpPrice ) ) {
                    _log.warn( "LiquidityTrackerImpl.DBG_CHECK tracker OUT OF ORDER " + this.toString() );

                    ++_badCnt;
                }
            }

            if ( Utils.isNull( p2 ) ) {
                _log.warn( "LiquidityTrackerImpl.DBG_CHECK tracker NULL entry at idx=" + idx + ", numLvls=" + _numLevels + " : " + this.toString() );

                ++_badCnt;
            }
        }
    }

    private int doLiquidityChange( int startIndex, final double latestQty, final double price ) {

        if ( Utils.isNull( price ) ) return Constants.UNSET_INT;

        int idx = (Utils.isNull( startIndex ) || startIndex < 0) ? 0 : startIndex;

        if ( startIndex == 0 || Utils.isNull( _tmpLastPx ) ) {
            _tmpLastPx = price;
        } else {
            int cmpPrice = Utils.compare( price, _tmpLastPx );

            if ( outOfOrder( cmpPrice ) ) {
                idx = 0; // out of order start a
            }
        }

        if ( _numLevels >= _maxLevels ) {
            setMaxLevels( _maxLevels + LVLS_TO_GROW_WHEN_FULL );
        }

        LiquidityDeltaEntryImpl e = null;

        double  prevQty   = 0;
        boolean qtyIsNull = Utils.isNull( latestQty );
        boolean hasQty    = Utils.hasNonZeroVal( latestQty );
        double  newQty    = (qtyIsNull) ? 0 : latestQty;

        for ( ; idx < _numLevels; idx++ ) {
            e = _entries[ idx ];

            if ( Utils.isNull( e.getPrice() ) ) { // no entries

                if ( !hasQty ) { // price didnt exist in ladded and as has no qty dont add it
                    return startIndex;
                }

                e.clear();
                e.set( newQty, price );

                setActiveLevels( idx + 1 );

                break;

            } else {
                int cmpPrice = Utils.compare( price, e.getPrice() );

                if ( cmpPrice == 0 ) {

                    prevQty = e.getQty(); // qty has changed .. changing to zero is valid

                    break; // FOUND PRICE

                }
                if ( shouldSkip( cmpPrice ) ) { // price less than in current level ... go to next entry in list

                    e = null;

                    continue;
                }

                if ( !hasQty ) { // price didnt exist in ladded and as has no qty dont add it
                    return startIndex;
                }

                e = insertAt( idx, newQty, price );

                break;
            }
        }

        if ( idx == _numLevels ) {
            e = _entries[ _numLevels ];
            e.clear();
            e.set( newQty, price );
            setActiveLevels( idx + 1 );
        }

        if ( e != null ) {
            int    cmpQty  = 0;
            double diffQty = 0;

            if ( !qtyIsNull ) {
                cmpQty  = Double.compare( latestQty, prevQty );
                diffQty = Math.abs( latestQty - prevQty );

                if ( cmpQty == 0 ) {
                    // unchanged
                } else if ( cmpQty < 0 ) {
                    e.setRemovedLiquidity( e.getRemovedLiquidity() + diffQty );
                } else {
                    e.setAddedLiquidity( e.getAddedLiquidity() + diffQty );
                }

                e.setQty( newQty );
            } else { // this will only happen when prices dropping off the book

                e.clear();

                // shuffle up entries
                int maxIdx = _numLevels - 2;
                for ( int i = idx; i <= maxIdx; ++i ) {
                    _entries[ i ] = _entries[ i + 1 ];
                }

                _entries[ maxIdx + 1 ] = e;

                setActiveLevels( _numLevels - 1 );

                --idx; // repoint to last good idx
            }
        }

        return idx < _maxLevels ? idx : Constants.UNSET_INT;
    }

    private void doTraded( final double tradeQty, final double price ) {

        if ( Utils.isNull( price ) || Utils.isNullOrZero( tradeQty ) ) return;

        LiquidityDeltaEntryImpl e = null;

        if ( _numLevels >= _maxLevels ) {
            setMaxLevels( _maxLevels + LVLS_TO_GROW_WHEN_FULL );
        }

        for ( int idx = 0; idx < _maxLevels; idx++ ) {
            e = _entries[ idx ];

            if ( Utils.isNull( e.getPrice() ) ) { // no entries

                e.clear();
                e.set( 0, price );
                setActiveLevels( idx + 1 );

                break;

            } else {
                int cmpPrice = Utils.compare( price, e.getPrice() );

                if ( cmpPrice == 0 ) {

                    break; // FOUND PRICE

                }
                if ( shouldSkip( cmpPrice ) ) { // price less than in current level ... go to next entry in list

                    e = null;

                    continue;
                }

                e = insertAt( idx, 0, price );

                break;
            }
        }

        if ( e != null ) {
//            double origQty = e.getQty();
//
//            if ( Utils.isNull( origQty ) ) {
//                origQty = 0;
//            }
//
//            double adjustedQty = origQty - tradeQty;
//
//            if ( adjustedQty < 0 ) {
//                adjustedQty = 0;
//            }

            e.setTradedQty( e.getTradedQty() + tradeQty );

            // DONT ADJUST QTY DOWN BY TRADED QTY
            //
            // e.setQty( adjustedQty );

            e.setNumTrades( e.getNumTrades() + 1 );
        }
    }

    private LiquidityDeltaEntryImpl insertAt( final int idx, final double currentQty, final double price ) {

        LiquidityDeltaEntryImpl lastEntry = _entries[ _numLevels ];

        int t = _numLevels;
        while( t > idx ) {
            _entries[ t ] = _entries[ --t ];
        }

        ++_numLevels;

        _entries[ idx ] = lastEntry;

        lastEntry.clear();
        lastEntry.set( currentQty, price );

        return lastEntry;
    }

    private boolean outOfOrder( final int cmpPrice ) {
        if ( isBidSide() ) {
            return cmpPrice > 0;
        }

        return cmpPrice < 0;
    }

    private boolean shouldSkip( final int cmpPrice ) {
        if ( isBidSide() ) {
            return cmpPrice < 0;
        }

        return cmpPrice > 0;
    }

    private boolean verifyEndOfBlockEntry( final LiquidityDeltaEntryImpl entry ) {
        if ( Utils.isNull( entry.getPrice() ) ) return false;

        double origQty = entry.getPrevQty();

        if ( Utils.hasVal( origQty ) ) {
            double newQty  = entry.getQty();
            double added   = entry.getAddedLiquidity();
            double removed = entry.getRemovedLiquidity();

            double expQty  = origQty + added - removed;
            double qtyDiff = Math.abs( newQty - expQty );

            if ( qtyDiff > Constants.TICK_WEIGHT ) {
                _log.info( "Mismatch WA#" + (++_mismatchExpQtyWA) + " on liqEntry expQty=" + expQty + ", but got " + newQty + ", absDiff=" + qtyDiff + ", entry=" + entry.toString() );

                return false;
            }

            double traded = entry.getTradedQty();

            if ( traded > removed ) {
                _log.info( "Mismatch WB#" + (++_mismatchTradeGtrRemovedLiqWB) + " on liqEntry traded=" + traded + ", but removed=" + removed + ", entry=" + entry.toString() );

                return false;
            }
        }

        return true;
    }

    private boolean verifyPostOpEntry( final LiquidityDeltaEntryImpl entry ) {
        if ( Utils.isNull( entry.getPrice() ) ) return false;

        return true;
    }
}
