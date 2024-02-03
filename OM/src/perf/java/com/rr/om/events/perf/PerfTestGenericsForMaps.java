/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.utils.Utils;
import com.rr.model.generated.internal.events.impl.ClientNewOrderSingleImpl;
import com.rr.om.events.perf.generics.maps.*;
import org.junit.Test;

/**
 * test to determine overhead of using generics in map
 */
public class PerfTestGenericsForMaps extends BaseTestCase {

    private int _dontOpt = 0;

    @Test
    public void testGenerics() {

        int runs          = 5;
        int size          = 100000;
        int numIterations = 1000;

        doRun( runs, size, numIterations );
    }

    private void doRun( int runs, int size, int numIterations ) {
        for ( int idx = 0; idx < runs; idx++ ) {

            long expFinal    = testExpFinal( size, numIterations, false );
            long genMap      = testGenMap( size, numIterations, false );
            long genMapFinal = testGenMapFinal( size, numIterations, false );
            long exp         = testExp( size, numIterations, false );
            long expWrapper  = testWrapper( size, numIterations, false );

            System.out.println( "Run " + idx + " GET generic=" + genMap + ", genFinalAccessor=" + genMapFinal +
                                ", explicit=" + exp + ", expFinalAccessor=" + expFinal + ", wrapped=" + expWrapper );
        }

        for ( int idx = 0; idx < runs; idx++ ) {

            long expFinal    = testExpFinal( size, numIterations, true );
            long genMap      = testGenMap( size, numIterations, true );
            long genMapFinal = testGenMapFinal( size, numIterations, true );
            long exp         = testExp( size, numIterations, true );
            long expWrapper  = testWrapper( size, numIterations, true );

            System.out.println( "Run " + idx + " SET generic=" + genMap + ", genFinalAccessor=" + genMapFinal +
                                ", explicit=" + exp + ", expFinalAccessor=" + expFinal + ", wrapped=" + expWrapper );
        }
    }

    private long testExp( int size, int iterations, boolean isWrite ) {

        DummyMapExplicit map = new DummyMapExplicit( size );

        for ( int i = 0; i < size; i++ ) {
            map.set( new ReusableString( "key" + i ), null, i );
        }

        ReusableString match = map.getKey( 10 );

        Utils.invokeGC();

        int cnt = _dontOpt;

        long startTime = ClockFactory.get().currentTimeMillis();

        if ( isWrite ) {
            for ( int j = 0; j < iterations; ++j ) {
                for ( int i = 0; i < size; ++i ) {
                    map.set( match, null, i );
                }
            }
        } else {
            for ( int j = 0; j < iterations; ++j ) {
                for ( int i = 0; i < size; ++i ) {
                    ReusableString key = map.getKey( i );
                    if ( match == key ) {
                        cnt++;
                    }
                }
            }
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        _dontOpt = cnt;

        return endTime - startTime;
    }

    private long testExpFinal( int size, int iterations, boolean isWrite ) {

        DummyMapExplicitFinalMod map = new DummyMapExplicitFinalMod( size );

        for ( int i = 0; i < size; i++ ) {
            map.set( new ReusableString( "key" + i ), null, i );
        }

        ReusableString match = map.getKey( 10 );

        Utils.invokeGC();

        int cnt = _dontOpt;

        long startTime = ClockFactory.get().currentTimeMillis();

        if ( isWrite ) {
            for ( int j = 0; j < iterations; ++j ) {
                for ( int i = 0; i < size; ++i ) {
                    map.set( match, null, i );
                }
            }
        } else {
            for ( int j = 0; j < iterations; ++j ) {
                for ( int i = 0; i < size; ++i ) {
                    ReusableString key = map.getKey( i );
                    if ( match == key ) {
                        cnt++;
                    }
                }
            }
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        _dontOpt = cnt;

        return endTime - startTime;
    }

    private long testGenMap( int size, int iterations, boolean isWrite ) {

        DummyMapWithGenerics<ReusableString, ClientNewOrderSingleImpl> map = new DummyMapWithGenerics<>( size );

        for ( int i = 0; i < size; i++ ) {
            map.set( new ReusableString( "key" + i ), null, i );
        }

        ReusableString match = map.getKey( 10 );

        Utils.invokeGC();

        int cnt = _dontOpt;

        long startTime = ClockFactory.get().currentTimeMillis();

        if ( isWrite ) {
            for ( int j = 0; j < iterations; ++j ) {
                for ( int i = 0; i < size; ++i ) {
                    map.set( match, null, i );
                }
            }
        } else {
            for ( int j = 0; j < iterations; ++j ) {
                for ( int i = 0; i < size; ++i ) {
                    ReusableString key = map.getKey( i );
                    if ( match == key ) {
                        cnt++;
                    }
                }
            }
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        _dontOpt = cnt;

        return endTime - startTime;
    }

    private long testGenMapFinal( int size, int iterations, boolean isWrite ) {

        DummyMapWithGenericsAndFinalMod<ReusableString, ClientNewOrderSingleImpl> map = new DummyMapWithGenericsAndFinalMod<>( size );

        for ( int i = 0; i < size; i++ ) {
            map.set( new ReusableString( "key" + i ), null, i );
        }

        ReusableString match = map.getKey( 10 );

        Utils.invokeGC();

        int cnt = _dontOpt;

        long startTime = ClockFactory.get().currentTimeMillis();

        if ( isWrite ) {
            for ( int j = 0; j < iterations; ++j ) {
                for ( int i = 0; i < size; ++i ) {
                    map.set( match, null, i );
                }
            }
        } else {
            for ( int j = 0; j < iterations; ++j ) {
                for ( int i = 0; i < size; ++i ) {
                    ReusableString key = map.getKey( i );
                    if ( match == key ) {
                        cnt++;
                    }
                }
            }
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        _dontOpt = cnt;

        return endTime - startTime;
    }

    private long testWrapper( int size, int iterations, boolean isWrite ) {

        DummyMapExplicitWrapperToGenerics map = new DummyMapExplicitWrapperToGenerics( size );

        for ( int i = 0; i < size; i++ ) {
            map.set( new ReusableString( "key" + i ), null, i );
        }

        ReusableString match = map.getKey( 10 );

        Utils.invokeGC();

        int cnt = _dontOpt;

        long startTime = ClockFactory.get().currentTimeMillis();

        if ( isWrite ) {
            for ( int j = 0; j < iterations; ++j ) {
                for ( int i = 0; i < size; ++i ) {
                    map.set( match, null, i );
                }
            }
        } else {
            for ( int j = 0; j < iterations; ++j ) {
                for ( int i = 0; i < size; ++i ) {
                    ReusableString key = map.getKey( i );
                    if ( match == key ) {
                        cnt++;
                    }
                }
            }
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        _dontOpt = cnt;

        return endTime - startTime;
    }
}
