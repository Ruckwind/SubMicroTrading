package com.rr.core.lang;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class Java8Test extends BaseTestCase {

    public double _sum = 1.0;

    @Test public void checkLongCast() {
        long   number    = 499999999000000001L;
        double converted = (double) number;
        System.out.println( number - (long) converted );
    }

    @Test
    public void testMerge() {
        Map<String, Integer> map1 = new HashMap<>();

        doAdd( map1, 1 );
        doAdd( map1, 2 );
        doAdd( map1, 3 );
        doAdd( map1, 4 );

        for ( int i = 0; i < 5000000; i++ ) {
            streamMap( map1 ); // watch the GC
        }

        System.out.println( "sum=" + _sum );
    }

    protected void streamMap( final Map<String, Integer> map1 ) {
        map1.entrySet()
            .stream()
            .sorted( ( e1, e2 ) -> e2.getKey().compareTo( e1.getKey() ) )
            .forEach( e -> _sum = ((_sum + e.getValue()) / _sum) );
    }

    @SuppressWarnings( "Java8MapApi" ) private void doAdd( final Map<String, Integer> map1, int expVal ) {
        String key1 = "K1";
        String key2 = "K2";
        String key3 = "K3";

        Integer old = map1.get( key1 );
        if ( old == null ) {
            map1.put( key1, 1 );
        } else {
            map1.put( key1, old + 1 );
        }

        assertEquals( expVal, (long) map1.get( key1 ) );

        map1.merge( key2, 1, ( a, b ) -> a + b );

        assertEquals( expVal, (long) map1.get( key2 ) );

        map1.merge( key3, 1, Integer::sum );

        assertEquals( expVal, (long) map1.get( key3 ) );
    }
}
