/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.lang.ReusableString;
import com.rr.core.model.Book;
import com.rr.core.model.Instrument;
import com.rr.core.utils.Utils;

/**
 * common L2 book code
 */
public abstract class BaseL2Book extends BaseFixedSizeBook {

    private static final int MAX_LEVELS = 10;

    private BookEntryImpl[] _bids;
    private BookEntryImpl[] _asks;
    private BookEntryImpl[] _tmp;

    private int _maxIdx;
    private int _errors;

    public BaseL2Book() { // for reflective construction
        super();

        _bids = _asks = _tmp = null;

        _maxIdx = -1;
    }

    public BaseL2Book( Instrument instrument, int maxLevels ) {
        super( instrument, maxLevels );

        setMaxLevels( maxLevels );
    }

    @Override public void dump( final ReusableString dest ) {
        try {
            dest.append( "Book [err=" ).append( _errors ).append( "] " );

            if ( _instrument != null ) {
                dest.append( _instrument.id() );
            }

            dest.append( "\n" );

            for ( int l = 0; l < _numLevels; ++l ) {
                final BookEntryImpl bid = _bids[ l ];
                final BookEntryImpl ask = _asks[ l ];

                dest.append( "[L" ).append( l ).append( "]  " );
                dest.append( (bid.isDirty()) ? "D " : "  " );
                dest.append( bid.getQty() ).append( " x " ).append( bid.getPrice() ).append( "  :  " );
                dest.append( ask.getPrice() ).append( " x " ).append( ask.getQty() );
                dest.append( (ask.isDirty()) ? " D" : "  " );
                dest.append( "\n" );
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    @Override public double getRefPrice() { return (_bids[ 0 ].getPrice() + _asks[ 0 ].getPrice()) / 2.0; }

    @Override
    public final boolean isValid() {
        if ( _numLevels == 0 ) return false;

        final BookEntryImpl bid = _bids[ 0 ];
        final BookEntryImpl ask = _asks[ 0 ];

        /**
         * the bid can be less than the ask for spread instruments
         * could check inst type and if its not a spread verify, but for now disable
         */
        return ((bid.isValid()) && (ask.isValid()));
    }

    @Override
    public void snapTo( final ApiMutatableBook dest ) {
        int startLevels = dest.getActiveLevels();
        dest.setNumLevels( _numLevels );
        int lvl = 0;
        if ( _numLevels > 0 ) {

            while( lvl < _numLevels ) {
                final BookEntryImpl bid = _bids[ lvl ];
                final BookEntryImpl ask = _asks[ lvl ];
                dest.setLevel( lvl, bid, ask );
                ++lvl;
            }
        }
        if ( lvl < startLevels ) {
            dest.deleteFrom( lvl );
        }
        dest.setEventTimestamp( getEventTimestamp() );
        dest.setDataSeqNum( getDataSeqNum() );
        dest.setInstrument( _instrument );
    }

    @Override
    public final int getActiveLevels() {
        return _numLevels;
    }

    @Override
    public final boolean getAskEntry( final int lvl, final BookLevelEntry dest ) {
        if ( lvl < 0 || lvl > _maxIdx ) return false;

        final BookEntryImpl entry = _asks[ lvl ];

        dest.set( entry.getQty(), entry.getPrice() );

        return true;
    }

    @Override
    public final boolean getBidEntry( final int lvl, final BookLevelEntry dest ) {
        if ( lvl < 0 || lvl > _maxIdx ) return false;

        final BookEntryImpl entry = _bids[ lvl ];

        dest.set( entry.getQty(), entry.getPrice() );

        return true;
    }

    @Override public Level getLevel()     { return Level.L2; }

    @Override
    public final boolean getLevel( final int lvl, final DoubleSidedBookEntry dest ) {
        if ( lvl < 0 || lvl > _maxIdx ) return false;

        final BookEntryImpl bid = _bids[ lvl ];
        final BookEntryImpl ask = _asks[ lvl ];

        dest.set( bid, ask );

        return true;
    }

    @Override
    public void reset() {
        clear( _asks );
        clear( _bids );
        setNumLevels( 0 );
    }

    @Override
    public final void setDirty( boolean isDirty ) {
        dirty( _asks, isDirty );
        dirty( _bids, isDirty );
    }

    @Override
    public final void setLevel( final int lvl,
                                final int bidNumOrders, final double bidQty, final double bidPrice, final boolean bidIsDirty,
                                final int askNumOrders, final double askQty, final double askPrice, final boolean askIsDirty ) {
        if ( lvl < 0 || lvl > _maxIdx ) return;

        if ( lvl >= _numLevels ) setNumLevels( lvl + 1 );

        final BookEntryImpl bid = _bids[ lvl ];
        final BookEntryImpl ask = _asks[ lvl ];

        ++_ticks;

        bid.set( bidNumOrders, bidQty, bidPrice, bidIsDirty );
        ask.set( askNumOrders, askQty, askPrice, askIsDirty );
    }

    @Override
    public final void setLevel( final int lvl,
                                final double bidQty, final double bidPrice, final boolean bidIsDirty,
                                final double askQty, final double askPrice, final boolean askIsDirty ) {
        if ( lvl < 0 || lvl > _maxIdx ) return;

        if ( lvl >= _numLevels ) setNumLevels( lvl + 1 );

        final BookEntryImpl bid = _bids[ lvl ];
        final BookEntryImpl ask = _asks[ lvl ];

        ++_ticks;

        bid.set( bidQty, bidPrice, bidIsDirty );
        ask.set( askQty, askPrice, askIsDirty );
    }

    @Override
    public final void setLevel( final int lvl, final BookEntryImpl newBid, final BookEntryImpl newAsk ) {
        if ( lvl < 0 || lvl > _maxIdx ) return;

        if ( lvl >= _numLevels ) setNumLevels( lvl + 1 );

        final BookEntryImpl bid = _bids[ lvl ];
        final BookEntryImpl ask = _asks[ lvl ];

        ++_ticks;

        bid.set( newBid );
        ask.set( newAsk );
    }

    @Override
    public final void setNumLevels( int lvl ) {

        if ( lvl == _numLevels ) return;

        if ( lvl < 0 ) {
            lvl = 0;
        } else if ( lvl > _maxLevels ) {
            if ( lvl > MAX_LEVELS ) {
                lvl = MAX_LEVELS;
            }
            setMaxLevels( lvl );
        }

        boolean clean = lvl < _numLevels;

        if ( clean ) {
            deleteFrom( _asks, lvl, _numLevels );
            deleteFrom( _bids, lvl, _numLevels );
        }

        _numLevels = lvl;
    }

    @Override
    public final void setBid( final int lvl, final BookLevelEntry entry ) {
        if ( lvl < 0 || lvl > _maxIdx ) return;

        if ( lvl >= _numLevels ) setNumLevels( lvl + 1 );

        final BookEntryImpl bid = _bids[ lvl ];

        ++_ticks;

        bid.set( entry );
    }

    @Override
    public final void setBidQty( final int lvl, final double qty ) {
        if ( lvl < 0 || lvl > _maxIdx ) return;

        final BookEntryImpl bid = _bids[ lvl ];

        ++_ticks;

        bid.setQty( qty );
    }

    @Override
    public final void setBidPrice( final int lvl, final double price ) {
        if ( lvl < 0 || lvl > _maxIdx ) return;

        final BookEntryImpl bid = _bids[ lvl ];

        ++_ticks;

        bid.setPrice( price );
    }

    @Override public final void setBidNumOrders( final int lvl, final int numOrders ) {
        if ( lvl < 0 || lvl > _maxIdx ) return;

        final BookEntryImpl bid = _bids[ lvl ];

        bid.setNumOrders( numOrders );
    }

    @Override
    public final void insertBid( final int lvl, final BookLevelEntry entry ) {
        insert( _bids, lvl );
        setBid( lvl, entry );
    }

    @Override
    public final void deleteBid( final int lvl ) {
        delete( _bids, lvl );
        checkActiveLvlsAfterDelete();
    }

    @Override
    public final void setBidDirty( final boolean isDirty ) {
        dirty( _bids, isDirty );
    }

    @Override
    public final void setAsk( final int lvl, final BookLevelEntry entry ) {
        if ( lvl < 0 || lvl > _maxIdx ) return;

        if ( lvl >= _numLevels ) setNumLevels( lvl + 1 );

        final BookEntryImpl ask = _asks[ lvl ];

        ++_ticks;

        ask.set( entry );
    }

    @Override
    public final void setAskQty( final int lvl, final double qty ) {
        if ( lvl < 0 || lvl > _maxIdx ) return;

        final BookEntryImpl ask = _asks[ lvl ];

        ++_ticks;

        ask.setQty( qty );
    }

    @Override
    public final void setAskPrice( final int lvl, final double price ) {
        if ( lvl < 0 || lvl > _maxIdx ) return;

        final BookEntryImpl ask = _asks[ lvl ];

        ++_ticks;

        ask.setPrice( price );
    }

    @Override public final void setAskNumOrders( final int lvl, final int numOrders ) {
        if ( lvl < 0 || lvl > _maxIdx ) return;

        final BookEntryImpl ask = _asks[ lvl ];

        ask.setNumOrders( numOrders );
    }

    @Override
    public final void insertAsk( final int lvl, final BookLevelEntry entry ) {
        insert( _asks, lvl );
        setAsk( lvl, entry );
    }

    @Override
    public final void deleteAsk( final int lvl ) {
        delete( _asks, lvl );
        checkActiveLvlsAfterDelete();
    }

    @Override
    public final void setAskDirty( final boolean isDirty ) {
        dirty( _asks, isDirty );
    }

    @Override public final void deleteFrom( final int lvl ) {
        deleteFrom( _asks, lvl );
        deleteFrom( _bids, lvl );
        checkActiveLvlsAfterDelete();
    }

    @Override
    public final void deleteThruBid( final int lvl ) {
        deleteThru( _bids, lvl );
    }

    @Override
    public final void deleteFromBid( final int lvl ) {
        deleteFrom( _bids, lvl );
        checkActiveLvlsAfterDelete();
    }

    @Override
    public final void deleteThruAsk( final int lvl ) {
        deleteThru( _asks, lvl );
    }

    @Override
    public final void deleteFromAsk( final int lvl ) {
        deleteFrom( _asks, lvl );
        checkActiveLvlsAfterDelete();
    }

    /**
     * ensure can hold max levels ... wipes data if arrays grown
     *
     * @param maxLevels
     */
    @Override public void setMaxLevels( final int maxLevels ) {
        int curLevels = (_bids == null) ? 0 : _bids.length;

        if ( maxLevels > curLevels ) {

            BookEntryImpl[] oldBids = _bids;
            BookEntryImpl[] oldAsks = _asks;

            _bids = new BookEntryImpl[ maxLevels ];
            _asks = new BookEntryImpl[ maxLevels ];
            _tmp  = new BookEntryImpl[ maxLevels ]; // required for deleteThru op

            for ( int l = 0; l < curLevels; l++ ) {
                _bids[ l ] = oldBids[ l ];
                _asks[ l ] = oldAsks[ l ];
                _tmp[ l ]  = null;
            }

            for ( int l = curLevels; l < maxLevels; l++ ) {
                _bids[ l ] = new BookEntryImpl();
                _asks[ l ] = new BookEntryImpl();
                _tmp[ l ]  = null;
            }
        }

        _maxLevels = maxLevels;
        _maxIdx    = maxLevels - 1;
    }

    @Override public void setLevel( final int lvl, final DoubleSidedBookEntryImpl ds ) {
        if ( lvl < 0 || lvl > _maxIdx ) return;

        if ( lvl >= _numLevels ) setNumLevels( lvl + 1 );

        final BookEntryImpl bid = _bids[ lvl ];
        final BookEntryImpl ask = _asks[ lvl ];

        ++_ticks;

        bid.set( ds.getNumBuyOrders(), ds.getBidQty(), ds.getBidPx(), ds.isBidIsDirty() );
        ask.set( ds.getNumSellOrders(), ds.getAskQty(), ds.getAskPx(), ds.isAskIsDirty() );
    }

    public boolean arePricesValid() {

        if ( _numLevels <= 1 ) return true;

        int idx = 1;

        double p1 = _bids[ 0 ]._price;
        for ( ; idx < _numLevels; idx++ ) {
            double p2 = _bids[ idx ]._price;

            if ( Utils.hasVal( p1 ) && Utils.hasVal( p2 ) && !_bids[ idx ].isDirty() ) {
                int cmpPrice = Utils.compare( p2, p1 );

                if ( cmpPrice >= 0 ) {
                    return false;
                }

                p1 = p2;
            }
        }

        idx = 1;
        p1  = _asks[ 0 ]._price;
        for ( ; idx < _numLevels; idx++ ) {
            double p2 = _asks[ idx ]._price;

            if ( Utils.hasVal( p1 ) && Utils.hasVal( p2 ) && !_asks[ idx ].isDirty() ) {
                int cmpPrice = Utils.compare( p2, p1 );

                if ( cmpPrice <= 0 ) {
                    return false;
                }

                p1 = p2;
            }
        }

        for ( idx = _numLevels; idx < _maxLevels; idx++ ) {
            double bid = _bids[ idx ]._price;
            double ask = _asks[ idx ]._price;

            if ( Utils.hasVal( bid ) || Utils.hasVal( ask ) ) {
                return false;
            }
        }

        return true;
    }

    public void checkActiveLvlsAfterDelete() {
        while( _numLevels > 0 ) {
            int idx = _numLevels - 1;

            final boolean haveBid = Utils.hasVal( _bids[ idx ].getPrice() );
            final boolean haveAsk = Utils.hasVal( _asks[ idx ].getPrice() );

            if ( haveBid || haveAsk ) {
                break;
            }

            _numLevels = idx;
        }
//
//        int idx = _numLevels;
//        while( idx < _maxLevels ) {
//
//            final BookEntryImpl bid     = _bids[idx];
//            final boolean       haveBid = Utils.hasVal( bid.getPrice() );
//            final BookEntryImpl ask     = _asks[idx];
//            final boolean       haveAsk = Utils.hasVal( ask.getPrice() );
//
//            if ( haveBid || haveAsk ) {
//
//                if ( haveAsk ) {
//                    ask.clear();
//                }
//
//                if ( haveAsk ) {
//                    bid.clear();
//                }
//
//            } else {
//                break;
//            }
//
//            ++idx;
//        }
    }

    public int getAskLevels() {
        int levels = 0;

        for ( int l = 0; l < _numLevels; ++l ) {
            final BookEntryImpl entry = _asks[ l ];

            if ( Utils.isNull( entry.getPrice() ) ) {
                break;
            }

            ++levels;
        }

        return levels;
    }

    public int getBidLevels() {
        int levels = 0;

        for ( int l = 0; l < _numLevels; ++l ) {
            final BookEntryImpl entry = _bids[ l ];

            if ( Utils.isNull( entry.getPrice() ) ) {
                break;
            }

            ++levels;
        }

        return levels;
    }

    public int getMaxIdx() { return _maxIdx; }

    public boolean isBookIdentical( final Book o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        final BaseL2Book that = (BaseL2Book) o;

        int activeLevels = getActiveLevels();
        if ( activeLevels != that.getActiveLevels() ) return false;

        for ( int i = 0; i < activeLevels; i++ ) {
            if ( !_bids[ i ].equals( that._bids[ i ] ) ) return false;
            if ( !_asks[ i ].equals( that._asks[ i ] ) ) return false;
        }

        return true;
    }

    /**
     * @return true if the book is invalid
     */
    public boolean markDirtyIfInvalid() {

        if ( _maxIdx < 0 ) return true;

        int    idx = 1;
        double p1  = _bids[ 0 ]._price;
        for ( ; idx < _numLevels; idx++ ) {
            final BookEntryImpl bid = _bids[ idx ];
            double              p2  = bid._price;

            if ( Utils.hasVal( p1 ) && Utils.hasVal( p2 ) && !bid.isDirty() ) {
                int cmpPrice = Utils.compare( p2, p1 );

                if ( cmpPrice >= 0 ) {
                    clear( _asks );
                    clear( _bids );
                    setNumLevels( 0 );
                    ++_errors;
                    return true;
                } else {
                    p1 = p2;
                }
            }
        }

        idx = 1;
        p1  = _asks[ 0 ]._price;
        for ( ; idx < _numLevels; idx++ ) {
            final BookEntryImpl ask = _asks[ idx ];
            double              p2  = ask._price;

            if ( Utils.hasVal( p1 ) && Utils.hasVal( p2 ) && !ask.isDirty() ) {
                int cmpPrice = Utils.compare( p2, p1 );

                if ( cmpPrice <= 0 ) {
                    clear( _asks );
                    clear( _bids );
                    setNumLevels( 0 );
                    ++_errors;
                    return true;
                } else {
                    p1 = p2;
                }
            }
        }

        return false;
    }

    protected BookEntryImpl[] getAsks() { return _asks; }

    protected BookEntryImpl[] getBids() { return _bids; }

    protected BookEntryImpl[] getTmp()  { return _tmp; }

    private void clear( final BookEntryImpl[] entries ) {
        for ( int l = 0; l <= _maxIdx; l++ ) {
            entries[ l ].clear();
        }
    }

    private void delete( final BookEntryImpl[] entries, final int lvl ) {
        if ( lvl < 0 || lvl > _maxIdx ) return;

        ++_ticks;

        final BookEntryImpl removed = entries[ lvl ];
        for ( int l = lvl; l < _maxIdx; ++l ) {
            entries[ l ] = entries[ l + 1 ];
        }

        entries[ _maxIdx ] = removed;
        removed.clear();
    }

    private void deleteFrom( final BookEntryImpl[] entries, final int fromLvl, final int maxLvl ) {
        ++_ticks;
        for ( int l = fromLvl; l < maxLvl; l++ ) {
            entries[ l ].clear();
        }
    }

    private void deleteFrom( final BookEntryImpl[] entries, final int fromLvl ) {
        ++_ticks;
        for ( int l = fromLvl; l <= _maxIdx; l++ ) {
            entries[ l ].clear();
        }
    }

    private void deleteThru( final BookEntryImpl[] entries, final int lvl ) {
        if ( lvl < 0 || lvl > _maxIdx ) return;

        ++_ticks;

        /**
         * L1 L2 L3 L4 L5 L6 L7 L8 E1 E2
         *
         * deleteFrom idx=2 to idx=0  .. ie levelsToMove=7
         *
         * L4 L5 L6 L7 L8 E1 E2 L1 L2 L3 .... L1/L2/L3 cleared
         */

        // backup the entries at top of book that are being erased
        for ( int l = 0; l <= lvl; l++ ) {
            final BookEntryImpl removed = entries[ l ];
            removed.clear();
            _tmp[ l ] = removed;
        }

        // shift up entries
        int destIdx = 0;
        for ( int l = lvl + 1; l <= _maxIdx; l++ ) {
            entries[ destIdx++ ] = entries[ l ];
        }

        // put back removed entries at bottom of book
        for ( int l = 0; l <= lvl; l++ ) {
            entries[ destIdx++ ] = _tmp[ l ];
        }
    }

    private void dirty( final BookEntryImpl[] entries, boolean isDirty ) {
        ++_ticks;
        for ( int l = 0; l <= _maxIdx; l++ ) {
            entries[ l ].setDirty( isDirty );
        }
    }

    private void insert( final BookEntryImpl[] entries, final int lvl ) {
        if ( lvl < 0 || lvl >= _maxIdx ) return; // note upper bounds check is gtr OR equal too

        final BookEntryImpl last = entries[ _maxIdx ];
        for ( int l = _maxIdx; l > lvl; --l ) {
            entries[ l ] = entries[ l - 1 ];
        }

        entries[ lvl ] = last;

        // dont need reset the inserted entry as it will be set post this operation
    }

    private void removeInactiveLevels( BookEntryImpl[] entries ) {
        final int levels = _numLevels;

        int lvl = 0;

        if ( levels > 0 ) {

            while( lvl < _numLevels ) {

                final BookEntryImpl entry = entries[ lvl ];

                final boolean missingPrice = Utils.isNull( entry.getPrice() );

                if ( missingPrice ) {

                    int nextIdx = lvl + 1;

                    boolean swapped = false;

                    while( nextIdx < _numLevels ) {
                        final BookEntryImpl nextEntry = entries[ nextIdx ];

                        if ( Utils.hasVal( nextEntry.getPrice() ) ) {
                            if ( Utils.hasNonZeroVal( nextEntry.getQty() ) ) {
                                entries[ lvl ]     = nextEntry;
                                entries[ nextIdx ] = entry;
                                                     swapped = true;
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
                }

                ++lvl;
            }
        }
    }
}
