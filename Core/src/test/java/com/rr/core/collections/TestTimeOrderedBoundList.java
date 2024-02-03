package com.rr.core.collections;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableType;
import com.rr.core.model.BaseEvent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TestTimeOrderedBoundList extends BaseTestCase {

    public static class TstEntry extends BaseEvent<TstEntry> {

        private int _val = -1;

        public TstEntry( final int val )                       { _val = val; }

        @Override public void dump( final ReusableString out ) { out.append( _val ); }

        @Override public ReusableType getReusableType()        { return CoreReusableType.NotReusable; }

        public int getVal()                                    { return _val; }

        public void setVal( final int val )                    { _val = val; }
    }

    @Test public void addToFullSameTS() {

        int size = 5;

        TimeOrderedBoundEventList q = new TimeOrderedBoundEventList( "Q", size, -1 );

        for ( int i = 0; i < size; i++ ) {
            q.add( new TstEntry( i ) );

            assertEquals( i + 1, q.size() );
        }

        for ( int i = 0; i < size; i++ ) {
            TstEntry t = q.removeLast();

            assertEquals( i, t.getVal() );

            assertEquals( size - i - 1, q.size() );
        }
    }

    @Test public void getLast() {

        int size = 5;

        TimeOrderedBoundEventList q = new TimeOrderedBoundEventList( "Q", size, -1 );

        q.add( create( 1, 100 ) );
        q.add( create( 5, 500 ) );
        q.add( create( 2, 200 ) );
        q.add( create( 3, 300 ) );
        q.add( create( 4, 400 ) );

        TstEntry e1 = q.getLast();
        TstEntry e2 = q.getLast();

        assertSame( e1, e2 );

        assertEquals( 1, e1.getVal() );

        removeLast( q, 1 );
        removeLast( q, 2 );
        removeLast( q, 3 );
        removeLast( q, 4 );
        removeLast( q, 5 );
    }

    @Test public void reorder() {

        int size = 5;

        TimeOrderedBoundEventList q = new TimeOrderedBoundEventList( "Q", size, -1 );

        q.add( create( 1, 100 ) );
        q.add( create( 5, 500 ) );
        q.add( create( 2, 200 ) );
        q.add( create( 3, 300 ) );
        q.add( create( 4, 400 ) );

        removeLast( q, 1 );
        removeLast( q, 2 );
        removeLast( q, 3 );
        removeLast( q, 4 );
        removeLast( q, 5 );

    }

    private TstEntry create( final int val, final long ts ) {
        TstEntry e = new TstEntry( val );
        e.setEventTimestamp( ts );
        return e;
    }

    private TstEntry removeLast( final TimeOrderedBoundEventList q, final int expVal ) {
        TstEntry e = q.removeLast();
        assertEquals( expVal, e.getVal() );
        return e;
    }
}
