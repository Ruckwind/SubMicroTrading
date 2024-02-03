package com.rr.md.book.l3;

import com.rr.core.lang.ReusableString;
import com.rr.core.model.Book;

import java.util.concurrent.ConcurrentHashMap;

public class L3BookStats {

    private static L3BookStats _instance = new L3BookStats();

    private static final class Stats {

        long _maxClosingActiveOrders;
        long _maxDailyActiveOrders;
        long _maxBuyLevels;
        long _maxSellLevels;
    }
    private ConcurrentHashMap<Book, Stats> _bookStats = new ConcurrentHashMap<>( 128 );

    public static L3BookStats instance() { return _instance; }

    public void add( final Book book, final int lastActiveOrders, final int maxActiveOrders, final int buyLevels, final int sellLevels ) {
        Stats stats = _bookStats.computeIfAbsent( book, ( k ) -> new Stats() );

        if ( lastActiveOrders > stats._maxClosingActiveOrders ) stats._maxClosingActiveOrders = lastActiveOrders;
        if ( maxActiveOrders > stats._maxDailyActiveOrders ) stats._maxDailyActiveOrders = maxActiveOrders;
        if ( buyLevels > stats._maxBuyLevels ) stats._maxBuyLevels = buyLevels;
        if ( lastActiveOrders > stats._maxSellLevels ) stats._maxSellLevels = sellLevels;
    }

    public void dump( ReusableString dest ) {

        Stats max       = new Stats();
        Stats aggregate = new Stats();
        Stats avg       = new Stats();

        for ( Stats stat : _bookStats.values() ) {
            if ( stat._maxClosingActiveOrders > max._maxClosingActiveOrders ) max._maxClosingActiveOrders = stat._maxClosingActiveOrders;
            if ( stat._maxDailyActiveOrders > max._maxDailyActiveOrders ) max._maxDailyActiveOrders = stat._maxDailyActiveOrders;
            if ( stat._maxBuyLevels > max._maxBuyLevels ) max._maxBuyLevels = stat._maxBuyLevels;
            if ( stat._maxSellLevels > max._maxSellLevels ) max._maxSellLevels = stat._maxSellLevels;

            aggregate._maxClosingActiveOrders += stat._maxClosingActiveOrders;
            aggregate._maxDailyActiveOrders += stat._maxDailyActiveOrders;
            aggregate._maxBuyLevels += stat._maxBuyLevels;
            aggregate._maxSellLevels += stat._maxSellLevels;
        }

        final int books = _bookStats.size();

        if ( books > 0 ) {
            avg._maxClosingActiveOrders = aggregate._maxClosingActiveOrders / books;
            avg._maxDailyActiveOrders   = aggregate._maxDailyActiveOrders / books;
            avg._maxBuyLevels           = aggregate._maxBuyLevels / books;
            avg._maxSellLevels          = aggregate._maxSellLevels / books;
        }

        dest.append( "L3BookStats numBooks=" ).append( books ).append( ", allStats [avg/max] " )
            .append( ":  maxOrdersActiveDuringDay[" ).append( avg._maxDailyActiveOrders ).append( '/' ).append( max._maxDailyActiveOrders )
            .append( "], maxOrdersActiveAtEOD[" ).append( avg._maxClosingActiveOrders ).append( '/' ).append( max._maxClosingActiveOrders )
            .append( "], buyLevels [" ).append( avg._maxBuyLevels ).append( '/' ).append( max._maxBuyLevels )
            .append( "], sellLevels [" ).append( avg._maxSellLevels ).append( '/' ).append( max._maxSellLevels ).append( ']' );
    }

    public void reset() {
        _bookStats.clear();
    }
}
