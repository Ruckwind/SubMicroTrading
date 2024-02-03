/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.om.events.perf;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ReusableString;
import com.rr.core.utils.Utils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * test the performance difference in alpha order of method name and first / last methods
 *
 * @author Richard Rose
 */
public class PerfTestMethodPosition extends BaseTestCase {

    public interface ShortMethodNames {

        void aa1( int qty );

        void aa2( int qty );

        void aa3( int qty );

        void aa4( int qty );

        void aa5( int qty );

        void aa6( int qty );

        void aa7( int qty );

        void aa8( int qty );

        void aa9( int qty );

        void aaA( int qty );

        void aaB( int qty );

        void aaC( int qty );

        void aaD( int qty );

        void aaE( int qty );

        void aaF( int qty );

        void aaG( int qty );

        void aaH( int qty );

        void aaI( int qty );

        void aaJ( int qty );

        void aaK( int qty );

        void aaL( int qty );

        void aaM( int qty );

        void aaN( int qty );

        void aaO( int qty );

        void aaP( int qty );

        void aaQ( int qty );

        void aaR( int qty );

        void aaS( int qty );

        int getQty();
    }

    public interface LongMethodNames {

        void aaa11111111111111111111( int qty );

        void aaa11111111111111111112( int qty );

        void aaa11111111111111111113( int qty );

        void aaa11111111111111111114( int qty );

        void aaa11111111111111111115( int qty );

        void aaa11111111111111111116( int qty );

        void aaa11111111111111111117( int qty );

        void aaa11111111111111111118( int qty );

        void aaa11111111111111111119( int qty );

        void aaa1111111111111111111A( int qty );

        void aaa1111111111111111111B( int qty );

        void aaa1111111111111111111C( int qty );

        void aaa1111111111111111111D( int qty );

        void aaa1111111111111111111E( int qty );

        void aaa1111111111111111111F( int qty );

        void aaa1111111111111111111G( int qty );

        void aaa1111111111111111111H( int qty );

        void aaa1111111111111111111I( int qty );

        void aaa1111111111111111111J( int qty );

        void aaa1111111111111111111K( int qty );

        void aaa1111111111111111111L( int qty );

        void aaa1111111111111111111M( int qty );

        void aaa1111111111111111111N( int qty );

        void aaa1111111111111111111O( int qty );

        void aaa1111111111111111111P( int qty );

        void aaa1111111111111111111Q( int qty );

        void aaa1111111111111111111R( int qty );

        void aaa1111111111111111111S( int qty );

        int getQty();
    }

    public static class ShortImpl implements ShortMethodNames {

        private int            _qty = 0;
        private ReusableString _clOrdId;

        public ShortImpl( ReusableString clOrdId ) {
            super();
            _clOrdId = clOrdId;
        }

        @Override
        public void aa1( int qty ) { _qty += qty; }

        @Override
        public int getQty() {
            return _qty;
        }

        @Override
        public void aa2( int qty ) { _qty += qty; }

        @Override
        public void aa3( int qty ) { _qty += qty; }

        @Override
        public void aa4( int qty ) { _qty += qty; }

        @Override
        public void aa5( int qty ) { _qty += qty; }

        @Override
        public void aa6( int qty ) { _qty += qty; }

        @Override
        public void aa7( int qty ) { _qty += qty; }

        @Override
        public void aa8( int qty ) { _qty += qty; }

        @Override
        public void aa9( int qty ) { _qty += qty; }

        @Override
        public void aaA( int qty ) { _qty += qty; }

        @Override
        public void aaB( int qty ) { _qty += qty; }

        @Override
        public void aaC( int qty ) { _qty += qty; }

        @Override
        public void aaD( int qty ) { _qty += qty; }

        @Override
        public void aaE( int qty ) { _qty += qty; }

        @Override
        public void aaF( int qty ) { _qty += qty; }

        @Override
        public void aaG( int qty ) { _qty += qty; }

        @Override
        public void aaH( int qty ) { _qty += qty; }

        @Override
        public void aaI( int qty ) { _qty += qty; }

        @Override
        public void aaJ( int qty ) { _qty += qty; }

        @Override
        public void aaK( int qty ) { _qty += qty; }

        @Override
        public void aaL( int qty ) { _qty += qty; }

        @Override
        public void aaM( int qty ) { _qty += qty; }

        @Override
        public void aaN( int qty ) { _qty += qty; }

        @Override
        public void aaO( int qty ) { _qty += qty; }

        @Override
        public void aaP( int qty ) { _qty += qty; }

        @Override
        public void aaQ( int qty ) { _qty += qty; }

        @Override
        public void aaR( int qty ) { _qty += qty; }

        @Override
        public void aaS( int qty ) { _qty += qty; }

        @Override
        public int hashCode() {
            return _clOrdId.hashCode();
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            ShortImpl other = (ShortImpl) obj;
            if ( _clOrdId == null ) {
                return other._clOrdId == null;
            } else return _clOrdId.equals( other._clOrdId );

        }

    }

    public static class LongImpl implements LongMethodNames {

        private int            _qty = 0;
        private ReusableString _clOrdId;

        public LongImpl( ReusableString clOrdId ) {
            super();
            _clOrdId = clOrdId;
        }

        @Override
        public void aaa11111111111111111111( int qty ) { _qty += qty; }

        @Override
        public int getQty() {
            return _qty;
        }

        @Override
        public void aaa11111111111111111112( int qty ) { _qty += qty; }

        @Override
        public void aaa11111111111111111113( int qty ) { _qty += qty; }

        @Override
        public void aaa11111111111111111114( int qty ) { _qty += qty; }

        @Override
        public void aaa11111111111111111115( int qty ) { _qty += qty; }

        @Override
        public void aaa11111111111111111116( int qty ) { _qty += qty; }

        @Override
        public void aaa11111111111111111117( int qty ) { _qty += qty; }

        @Override
        public void aaa11111111111111111118( int qty ) { _qty += qty; }

        @Override
        public void aaa11111111111111111119( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111A( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111B( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111C( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111D( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111E( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111F( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111G( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111H( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111I( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111J( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111K( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111L( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111M( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111N( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111O( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111P( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111Q( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111R( int qty ) { _qty += qty; }

        @Override
        public void aaa1111111111111111111S( int qty ) { _qty += qty; }

        @Override
        public int hashCode() {
            return _clOrdId.hashCode();
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            LongImpl other = (LongImpl) obj;
            if ( _clOrdId == null ) {
                return other._clOrdId == null;
            } else return _clOrdId.equals( other._clOrdId );
        }
    }
    private int _dontOpt;

    @Test
    public void testFirstMethod() {

        int runs       = 5;
        int size       = 1000000;
        int iterations = 100;

        doRun( runs, size, iterations );
    }

    private void doRun( int runs, int size, int iterations ) {
        for ( int idx = 0; idx < runs; idx++ ) {

            long sh = testShort( size, iterations, true );
            long ln = testLong( size, iterations, true );

            System.out.println( "Run " + idx + " short=" + sh + ", long=" + ln + ", firstMethod=TRUE" );

            sh = testShort( size, iterations, false );
            ln = testLong( size, iterations, false );

            System.out.println( "Run " + idx + " short=" + sh + ", long=" + ln + ", firstMethod=FALSE" );
        }
    }

    private long testLong( int size, int iterations, boolean firstMethod ) {

        Map<ReusableString, LongImpl> map = new HashMap<>( size );

        for ( int i = 0; i < size; ++i ) {
            ReusableString key = new ReusableString( "SOMEKEY" + (10000000 + i) );
            map.put( key, new LongImpl( key ) );
        }

        ReusableString[] keys = map.keySet().toArray( new ReusableString[ size ] );

        Utils.invokeGC();

        int cnt = _dontOpt;

        long startTime = ClockFactory.get().currentTimeMillis();

        if ( firstMethod ) {
            for ( int i = 0; i < iterations; ++i ) {
                int max = keys.length;

                for ( int j = 0; j < max; ++j ) {
                    LongImpl order = map.get( keys[ j ] );

                    order.aaa11111111111111111111( j );
                }
            }

        } else {
            for ( int i = 0; i < iterations; ++i ) {
                int max = keys.length;

                for ( int j = 0; j < max; ++j ) {
                    LongImpl order = map.get( keys[ j ] );

                    order.aaa1111111111111111111S( j );
                }
            }
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        _dontOpt = cnt;

        return endTime - startTime;
    }

    private long testShort( int size, int iterations, boolean firstMethod ) {

        Map<ReusableString, ShortImpl> map = new HashMap<>( size );

        for ( int i = 0; i < size; ++i ) {
            ReusableString key = new ReusableString( "SOMEKEY" + (10000000 + i) );
            map.put( key, new ShortImpl( key ) );
        }

        ReusableString[] keys = map.keySet().toArray( new ReusableString[ size ] );

        Utils.invokeGC();

        int cnt = _dontOpt;

        long startTime = ClockFactory.get().currentTimeMillis();

        if ( firstMethod ) {
            for ( int i = 0; i < iterations; ++i ) {
                int max = keys.length;

                for ( int j = 0; j < max; ++j ) {
                    ShortImpl order = map.get( keys[ j ] );

                    order.aa1( j );
                }
            }

        } else {
            for ( int i = 0; i < iterations; ++i ) {
                int max = keys.length;

                for ( int j = 0; j < max; ++j ) {
                    ShortImpl order = map.get( keys[ j ] );

                    order.aaS( j );
                }
            }
        }

        long endTime = ClockFactory.get().currentTimeMillis();

        _dontOpt = cnt;

        return endTime - startTime;
    }
}
