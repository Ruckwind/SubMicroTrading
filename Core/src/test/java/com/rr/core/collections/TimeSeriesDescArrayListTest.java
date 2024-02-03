package com.rr.core.collections;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.Constants;
import com.rr.core.model.PointInTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.*;

public class TimeSeriesDescArrayListTest extends BaseTestCase {

    private static final class DummyVersionable implements PointInTime {

        private final String _data;
        private final long   _startTimeStamp;

        public DummyVersionable( final long startTimeStamp, String data ) {
            _startTimeStamp = startTimeStamp;
            _data           = data;
        }

        @Override public long getEventTimestamp() { return _startTimeStamp; }

        @Override public String toString() {
            return "DummyVersionable{" +
                   "_data='" + _data + '\'' +
                   ", _startTimeStamp=" + _startTimeStamp +
                   '}';
        }
    }

    private TimeSeriesDescArrayList<DummyVersionable> _list = new TimeSeriesDescArrayList<>();
    private ArrayList<DummyVersionable>               _copy = new ArrayList<>();

    @Test public void testGetAtA() {
        addTimes( 100 );
        assertEquals( _copy.get( 0 ), _list.getAt( 100 ) );
        assertEquals( _copy.get( 0 ), _list.getAt( 101 ) );
        assertNull( _list.getAt( 99 ) );
    }

    @Test public void testGetAtB() {
        addTimes( 100, 101 );
        assertEquals( _copy.get( 0 ), _list.getAt( 100 ) );
        assertEquals( _copy.get( 1 ), _list.getAt( 101 ) );
        assertEquals( _copy.get( 1 ), _list.getAt( 102 ) );
        assertNull( _list.getAt( 99 ) );
    }

    @Test public void testGetAtC() {
        addTimes( 100, 101, 102 );
        assertEquals( _copy.get( 0 ), _list.getAt( 100 ) );
        assertEquals( _copy.get( 1 ), _list.getAt( 101 ) );
        assertEquals( _copy.get( 2 ), _list.getAt( 102 ) );
        assertNull( _list.getAt( 99 ) );
    }

    @Test public void testGetAtD() {
        addTimes( 100, 103, 109 );
        assertEquals( _copy.get( 0 ), _list.getAt( 100 ) );
        assertEquals( _copy.get( 0 ), _list.getAt( 102 ) );

        assertEquals( _copy.get( 1 ), _list.getAt( 103 ) );
        assertEquals( _copy.get( 1 ), _list.getAt( 108 ) );

        assertEquals( _copy.get( 2 ), _list.getAt( 109 ) );
        assertEquals( _copy.get( 2 ), _list.getAt( 110 ) );
    }

    @Test public void testGetAtE() {
        addTimes( 100, 103, 109, 110, 121 );
        assertEquals( _copy.get( 0 ), _list.getAt( 100 ) );
        assertEquals( _copy.get( 0 ), _list.getAt( 102 ) );

        assertEquals( _copy.get( 1 ), _list.getAt( 103 ) );
        assertEquals( _copy.get( 1 ), _list.getAt( 108 ) );

        assertEquals( _copy.get( 2 ), _list.getAt( 109 ) );

        assertEquals( _copy.get( 3 ), _list.getAt( 110 ) );
        assertEquals( _copy.get( 3 ), _list.getAt( 120 ) );

        assertEquals( _copy.get( 4 ), _list.getAt( 121 ) );
        assertEquals( _copy.get( 4 ), _list.getAt( 122 ) );

        assertNull( _list.getAt( 99 ) );
    }

    @Test public void testIncrementalNewActiveVersions() {

        DummyVersionable dv1 = createEntry( 1000, Constants.UNSET_LONG, "AAA" );
        DummyVersionable dv2 = createEntry( 2000, Constants.UNSET_LONG, "BBB" );
        DummyVersionable dv3 = createEntry( 3000, Constants.UNSET_LONG, "CCC" );

        _list.add( dv1 );
        _list.add( dv2 );
        _list.add( dv3 );

        assertSame( dv3, _list.latest() );

        Iterator<DummyVersionable> dvi = _list.iterator();

        expNext( dvi, dv3 );
        expNext( dvi, dv2 );
        expNext( dvi, dv1 );

        assertFalse( dvi.hasNext() );

        dvi = _list.oldestToNewestIterator();

        expNext( dvi, dv1 );
        expNext( dvi, dv2 );
        expNext( dvi, dv3 );

        assertFalse( dvi.hasNext() );
    }

    private void addTimes( int... times ) {
        for ( int t : times ) {
            final DummyVersionable entry = createEntry( t, Constants.UNSET_LONG, "S" + t );
            _list.add( entry );
            _copy.add( entry );
        }
    }

    private DummyVersionable createEntry( long startTime, long endTime, final String data ) {
        DummyVersionable dv = new DummyVersionable( startTime, data );
        return dv;
    }

    private void expNext( final Iterator<DummyVersionable> dvi, final DummyVersionable exp ) {
        assertTrue( dvi.hasNext() );
        DummyVersionable dv = dvi.next();
        assertSame( exp, dv );
    }
}
