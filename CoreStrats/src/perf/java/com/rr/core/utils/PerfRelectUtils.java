/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.lang.*;
import com.rr.core.logger.Logger;
import com.rr.core.logger.LoggerFactory;
import com.rr.core.model.BaseReusableEvent;
import com.rr.core.model.ExchangeCode;
import com.rr.core.pool.PoolFactory;
import com.rr.core.pool.Recycler;
import com.rr.core.pool.SuperpoolManager;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class PerfRelectUtils extends BaseTestCase {

    private static final Logger _log = LoggerFactory.create( PerfRelectUtils.class );

    public static class TestReflect extends BaseReusableEvent<TestReflect> {

        private static ReusableType _testType = new SingularReusableType( "PerfTestType", ReusableCategoryEnum.Test );
        private int            _anIntVal;
        private long           _aLongVal;
        private double         _aDoubleVal;
        private String         _aStrVal;
        private ReusableString _aZStrVal;
        private ReusableString _aZStrVal2 = new ReusableString();
        private ExchangeCode   _code;
        public TestReflect() { super(); }

        @Override public ReusableType getReusableType() {
            return _testType;
        }

        public int getAnIntVal()                                   { return _anIntVal; }

        public void setAnIntVal( final int anIntVal )              { _anIntVal = anIntVal; }

        public ExchangeCode getCode()                              { return _code; }

        public void setCode( final ExchangeCode code )             { _code = code; }

        public double getaDoubleVal()                              { return _aDoubleVal; }

        public void setaDoubleVal( final double aDoubleVal )       { _aDoubleVal = aDoubleVal; }

        public long getaLongVal()                                  { return _aLongVal; }

        public void setaLongVal( final long aLongVal )             { _aLongVal = aLongVal; }

        public String getaStrVal()                                 { return _aStrVal; }

        public void setaStrVal( final String aStrVal )             { _aStrVal = aStrVal; }

        public ReusableString getaZStrVal()                        { return _aZStrVal; }

        public void setaZStrVal( final ReusableString aZStrVal )   { _aZStrVal = aZStrVal; }

        public ReusableString getaZStrVal2()                       { return _aZStrVal2; }

        public void setaZStrVal2( final ReusableString aZStrVal2 ) { _aZStrVal2 = aZStrVal2; }
    }

    private final PoolFactory<TestReflect> _factory       = SuperpoolManager.instance().getPoolFactory( TestReflect.class );
    private final Recycler<TestReflect>    _recycler      = SuperpoolManager.instance().getRecycler( TestReflect.class );
    private final Set<Field>               _mems          = ReflectUtils.getMembers( TestReflect.class );
    private final ExchangeCode[]           _exchangeCodes = ExchangeCode.values();
    private final ReusableString           _tmpStr        = new ReusableString();

    @Test
    public void testReflectUtils() {

        int testRuns       = 5;
        int size           = 10000000;            // 10 million iterations
        int workingSetSize = 10000;     // across 10K objects

        for ( int i = 0; i < testRuns; i++ ) {
            runTest( "Direct ", i, size, workingSetSize, ( a, b ) -> doDirect( a, b ) );
            runTest( "Reflect", i, size, workingSetSize, ( a, b ) -> doReflect( a, b ) );
        }
    }

    private void doDirect( final TestReflect src, final TestReflect dest ) {
        dest.setAnIntVal( src.getAnIntVal() );
        dest.setaLongVal( src.getaLongVal() );
        dest.setaDoubleVal( src.getaDoubleVal() );
        dest.setaStrVal( src.getaStrVal() );
        dest.setaZStrVal( src.getaZStrVal() );
        dest.setaZStrVal2( src.getaZStrVal2() );
        dest.setCode( src.getCode() );
    }

    private void doReflect( final TestReflect src, final TestReflect dest ) {
        ReflectUtils.shallowCopy( dest, src, _mems );
    }

    private void init( final TestReflect obj, ReusableString v, int id ) {
        obj.setAnIntVal( 1234567 );
        obj.setaLongVal( 123456789012345L );
        obj.setaDoubleVal( 1234567.8901234 );
        obj.setaStrVal( "abcdefghijk" );
        obj.setaZStrVal( v );
        obj.setaZStrVal2( v );
        obj.setCode( _exchangeCodes[ id % _exchangeCodes.length ] );
    }

    private void runTest( String descript, final int testRun, final int size, final int workingSetSize, BiConsumer<TestReflect, TestReflect> consumer ) {
        int batchSets = size / workingSetSize;

        long[]      _stats       = new long[ batchSets ];
        Percentiles _percentiles = new Percentiles( _stats, TimeUnit.NANOSECONDS );

        long runStart = System.nanoTime();

        for ( int batch = 0; batch < batchSets; batch++ ) {
            TestReflect root = _factory.get();
            TestReflect prev = root;

            _tmpStr.copy( "ZeTempString" ).append( batch );

            init( root, _tmpStr, batch );

            long start = System.nanoTime();

            for ( int i = 0; i < workingSetSize; i++ ) {
                TestReflect next = _factory.get();

                consumer.accept( prev, next );

                next.setNext( root.getNext() );
                root.setNext( next );
                prev = next;
            }

            while( root != null ) {
                TestReflect tmp = root;
                root = root.getNext();
                tmp.setNext( null );
                _recycler.recycle( tmp );
            }

            long end      = System.nanoTime();
            long duration = Math.abs( end - start );

            _stats[ batch ] = duration;
        }

        long   runEnd        = System.nanoTime();
        double duartionNano  = Math.abs( runEnd - runStart );
        double durationMicro = duartionNano / 1000;

        double rate  = ((batchSets * size) / durationMicro) * 1000000;
        double aveNS = duartionNano / (batchSets * size);

//        final ReusableString out = new ReusableString( descript ).append( " testRun=" ).append( testRun ).append( " -> " );
//        _percentiles.recalc( _stats, _stats.length );
//        _percentiles.logStats( out );
//        _log.info( out );

        _log.info( descript + " testRun=" + testRun + " eventRate=" + String.format( "%,.2f", rate ) + ", aveEventTimeNanos=" + String.format( "%,.2f", aveNS ) );
    }
}
