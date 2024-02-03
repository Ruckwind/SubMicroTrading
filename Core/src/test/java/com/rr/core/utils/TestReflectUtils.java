/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.lang.BaseTestCase;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.model.Event;
import com.rr.core.model.ExchangeCode;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.matchers.CapturingMatcher;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;

public class TestReflectUtils extends BaseTestCase {

    public static class TestReflect implements Cloneable {

        private final int            _finalInt  = 12345;
        private       int            _anIntVal  = 123;
        private       long           _aLongVal;
        private       int[]          _anIntArray;
        private       double         _aDoubleVal;
        private       double[][]     _2darr;
        private       String         _aStrVal;
        private       ReusableString _aZStrVal;
        private       ReusableString _aZStrVal2 = new ReusableString();
        private       StringBuilder  _tmpBuf    = new StringBuilder();
        public TestReflect() {
            // nothing
        }

        @Override public Object clone() {
            try {
                TestReflect other = (TestReflect) super.clone();

                ReflectUtils.setMember( other, "_tmpBuf", new StringBuilder() );

                return other;

            } catch( CloneNotSupportedException e ) {
                throw new SMTRuntimeException( "Unable to clone MDBarDecoder " + e.getMessage(), e );
            }
        }

        public double[][] get2darr()                    { return _2darr; }

        public void set2darr( final double[][] a2darr ) { _2darr = a2darr; }

        public double getDoubleVal()                    { return _aDoubleVal; }

        public int getFinalInt()                        { return _finalInt; }

        public int getIntVal()                          { return _anIntVal; }

        public long getLongVal()                        { return _aLongVal; }

        public String getStrVal()                       { return _aStrVal; }

        public StringBuilder getTmpBuf()                { return _tmpBuf; }

        public ReusableString getZStrVal()              { return _aZStrVal; }

        public ReusableString getZStrVal2()             { return _aZStrVal2; }
    }

    private static class TestReflectFinalInt implements Cloneable {

        private final int            _finalInt;
        private       int            _anIntVal  = 123;
        private       long           _aLongVal;
        private       int[]          _anIntArray;
        private       double         _aDoubleVal;
        private       String         _aStrVal;
        private       ReusableString _aZStrVal;
        private       ReusableString _aZStrVal2 = new ReusableString();
        private       StringBuilder  _tmpBuf    = new StringBuilder();
        public TestReflectFinalInt( int finalInt ) {
            _finalInt = finalInt;
        }

        @Override public Object clone() {
            try {
                TestReflect other = (TestReflect) super.clone();

                ReflectUtils.setMember( other, "_tmpBuf", new StringBuilder() );

                return other;

            } catch( CloneNotSupportedException e ) {
                throw new SMTRuntimeException( "Unable to clone MDBarDecoder " + e.getMessage(), e );
            }
        }

        public double getDoubleVal()        { return _aDoubleVal; }

        public int getFinalInt()            { return _finalInt; }

        public int getIntVal()              { return _anIntVal; }

        public long getLongVal()            { return _aLongVal; }

        public String getStrVal()           { return _aStrVal; }

        public StringBuilder getTmpBuf()    { return _tmpBuf; }

        public ReusableString getZStrVal()  { return _aZStrVal; }

        public ReusableString getZStrVal2() { return _aZStrVal2; }
    }

    private static class TestReflectWithEnum extends TestReflect {

        private ExchangeCode   _code;
        private ExchangeCode[] _codes;

        public TestReflectWithEnum( final ExchangeCode code ) {
            _codes      = new ExchangeCode[ 3 ];
            _codes[ 0 ] = ExchangeCode.UNKNOWN;
            _codes[ 1 ] = code;
            _codes[ 2 ] = ExchangeCode.XEUR;
            _code       = code;
        }

        public ExchangeCode getCode() {
            return _code;
        }

        public void setCode( final ExchangeCode code ) {
            _code = code;
        }
    }

    private TestReflectFinalInt _r1 = new TestReflectFinalInt( 12345 );

    public static void reset( final Object mock, final ArgumentCaptor<Event> captor ) {
        Mockito.reset( mock );
        reset( captor );
    }

    public static void reset( final ArgumentCaptor<Event> captor ) {
        CapturingMatcher cm   = ReflectUtils.get( ArgumentCaptor.class, "capturingMatcher", captor );
        List<Object>     list = ReflectUtils.get( CapturingMatcher.class, "arguments", cm );
        list.clear();
        assertEquals( 0, captor.getAllValues().size() );
    }

    @Test
    public void cloneOverridePrivateWithReflect() {

        TestReflect r1 = new TestReflect();
        r1.getTmpBuf().append( "stuff" );
        TestReflect r2 = (TestReflect) r1.clone();

        assertNotSame( r1.getTmpBuf(), r2.getTmpBuf() );
    }

    @Test
    public void shallowClone() {
        TestReflect r1 = new TestReflect();
        TestReflect r2 = new TestReflect();

        Set<Field> mems = ReflectUtils.getMembers( r1 );

        for ( Field f : mems ) {
            if ( !Modifier.isFinal( f.getModifiers() ) ) {
                if ( !f.getName().equals( "_2darr" ) ) {
                    ReflectUtils.setMemberFromString( r1, f, "5" );
                }
            }
        }

        ReflectUtils.shallowCopy( r2, r1, mems );

        assertEquals( r1.getZStrVal(), r2.getZStrVal() );
        assertEquals( r1.getZStrVal2(), r2.getZStrVal2() );
        assertNotSame( r1.getZStrVal(), r2.getZStrVal() );
        assertNotSame( r1.getZStrVal2(), r2.getZStrVal2() );
    }

    @Test
    public void shallowCloneEnum() throws NoSuchFieldException {
        TestReflectWithEnum r1 = new TestReflectWithEnum( ExchangeCode.XAMS );
        TestReflectWithEnum r2 = new TestReflectWithEnum( ExchangeCode.DUMMY );

        Set<Field> mems = ReflectUtils.getMembers( r1 );

        ReflectUtils.shallowCopy( r2, r1, mems );

        assertSame( r1.getCode(), r2.getCode() );

        Field f = ReflectUtils.getMember( TestReflectWithEnum.class, "_codes" );
        ReflectUtils.setMemberFromString( r2, f, "XCME,XBRU,XAMS" );
        assertEquals( 3, r2._codes.length );
        assertSame( ExchangeCode.XCME, r2._codes[ 0 ] );
        assertSame( ExchangeCode.XBRU, r2._codes[ 1 ] );
        assertSame( ExchangeCode.XAMS, r2._codes[ 2 ] );
    }

    @Test
    public void testIntArray() throws IllegalAccessException {
        TestReflect r1 = new TestReflect();

        int[] intArr = { 12, 24, 36 };

        Field f = ReflectUtils.getMember( TestReflect.class, "_anIntArray" );

        ReflectUtils.setMember( r1, f, intArr );

        f.setAccessible( true );

        int[] arr = (int[]) f.get( r1 );

        int len = arr.length;

        assertEquals( 3, len );

        int i1 = arr[ 0 ];
        int i2 = arr[ 1 ];
        int i3 = arr[ 2 ];

        assertEquals( 12, i1 );
        assertEquals( 24, i2 );
        assertEquals( 36, i3 );

    }

    @Test
    public void testReflectSet2DArray() {

        TestReflect r1 = new TestReflect();

        double v[][] = { { 10, 11, 12, 13 }, { 20, 21, 22, 23 }, { 30, 31, 32, 33 } };

        ReflectUtils.setMember( r1, "_2darr", v );

        double z[][] = r1.get2darr();

        assertTrue( Arrays.deepEquals( v, z ) );
    }

    @Test
    public void testReflectSetFinalInt() throws IllegalAccessException {

        ReflectUtils.setMember( _r1, "_finalInt", 54321 );

        Field f = ReflectUtils.getMember( _r1.getClass(), "_finalInt" );

        f.setAccessible( true );

        assertEquals( 54321, f.get( _r1 ) );

        assertEquals( 54321, _r1.getFinalInt() );
    }

    @Test
    public void testReflectSetNonFinalInt() {

        TestReflect r1 = new TestReflect();

        ReflectUtils.setMember( r1, "_anIntVal", 321 );

        assertEquals( 321, r1.getIntVal() );
    }

    @Test
    public void testReflectUtils() throws ClassNotFoundException, IllegalAccessException {
        TestReflect r1 = new TestReflect();

        ZString startStr1 = r1.getZStrVal();  // null as unset
        ZString startStr2 = r1.getZStrVal2(); // empty ReusableString

        Set<Field> mems = ReflectUtils.getMembers( r1 );
        assertEquals( 10, mems.size() );

        double v[][] = { { 10, 11, 12, 13 }, { 20, 21, 22, 23 }, { 30, 31, 32, 33 } };

        for ( Field f : mems ) {
            if ( f.getName().equals( "_2darr" ) ) {
                ReflectUtils.setMemberFromString( r1, f, "{{10,11,12,13}, {20,21,22,23}, {30,31,32,33}}" );
            } else if ( !Modifier.isFinal( f.getModifiers() ) ) {
                ReflectUtils.setMemberFromString( r1, f, "5" );
            }
        }

        assertEquals( 5, r1.getIntVal() );
        assertEquals( 5, r1.getLongVal() );
        assertEquals( 5.0, r1.getDoubleVal(), 0.0000005 );
        assertEquals( "5", r1.getStrVal() );
        assertEquals( new ReusableString( "5" ), r1.getZStrVal() );
        assertEquals( new ReusableString( "5" ), r1.getZStrVal2() );

        assertNull( startStr1 );
        assertSame( startStr2, r1.getZStrVal2() );

        double z[][] = r1.get2darr();

        assertTrue( Arrays.deepEquals( v, z ) );
    }
}
