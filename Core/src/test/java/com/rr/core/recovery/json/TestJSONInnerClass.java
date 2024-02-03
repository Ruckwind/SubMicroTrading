package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableStringInputStream;
import com.rr.core.lang.ReusableStringOutputStream;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.recovery.SMTComponentResolver;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class TestJSONInnerClass extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONInnerClass.class, Level.info );

    public static class SampleOuterA {

        private String      _strValA = "abc";
        private SampleInner _inner;
        public SampleOuterA() { /* for reflection */ }

        public SampleOuterA( final String strValA ) {
            _strValA = strValA;
            _inner   = new SampleInner( "strB" );
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final SampleOuterA that = (SampleOuterA) o;

            if ( _strValA != null ? !_strValA.equals( that._strValA ) : that._strValA != null ) return false;
            if ( _inner != null ? !_inner.equals( that._inner ) : that._inner != null ) return false;

            return true;
        }

        public SampleInner getInner() { return _inner; }

        public String getStrValA()    { return _strValA; }

        public class SampleInner {

            private String _strValInner = "abc";

            public SampleInner()                       { /* for reflection */ }

            public SampleInner( final String strValA ) { _strValInner = strValA; }

            @Override public boolean equals( final Object o ) {
                if ( this == o ) return true;
                if ( o == null || getClass() != o.getClass() ) return false;

                final SampleInner that = (SampleInner) o;

                if ( _strValInner != null ? !_strValInner.equals( that._strValInner ) : that._strValInner != null ) return false;

                return true;
            }

            public String getStrValInner()             { return _strValInner; }
        }
    }

    public static class SampleOuterB {

        public static class SampleInner {

            private String _strValInner = "abc";

            public SampleInner()                       { /* for reflection */ }

            public SampleInner( final String strValA ) { _strValInner = strValA; }

            @Override public boolean equals( final Object o ) {
                if ( this == o ) return true;
                if ( o == null || getClass() != o.getClass() ) return false;

                final SampleInner that = (SampleInner) o;

                if ( _strValInner != null ? !_strValInner.equals( that._strValInner ) : that._strValInner != null ) return false;

                return true;
            }

            public String getStrValInner()             { return _strValInner; }
        }

        private String      _strValA = "abc";
        private SampleInner _inner;

        public SampleOuterB() { /* for reflection */ }

        public SampleOuterB( final String strValA ) {
            _strValA = strValA;
            _inner   = new SampleInner( "strB" );
        }

        @Override public boolean equals( final Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            final SampleOuterB that = (SampleOuterB) o;

            if ( _strValA != null ? !_strValA.equals( that._strValA ) : that._strValA != null ) return false;
            if ( _inner != null ? !_inner.equals( that._inner ) : that._inner != null ) return false;

            return true;
        }

        public SampleInner getInner() { return _inner; }

        public String getStrValA()    { return _strValA; }

    }

    @Test public void innerNonStaticClassJSONTest() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        SampleOuterA src = new SampleOuterA( "z123" );

        dataWriter.objectToJson( src );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"com.rr.core.recovery.json.TestJSONInnerClass$SampleOuterA\",\n"
                                 + "\t\"_strValA\" : \"z123\",\n"
                                 + "\t\"_inner\" : {\n"
                                 + "\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\"@class\" : \"com.rr.core.recovery.json.TestJSONInnerClass$SampleOuterA$SampleInner\",\n"
                                 + "\t\t\"@this$0\" : {\"@ref\" : \"1\"},\n"
                                 + "\t\t\"_strValInner\" : \"strB\"\n"
                                 + "\t}\n"
                                 + "}\n";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object decoded = reader.jsonToObject();
        assertNotNull( decoded );
        assertTrue( decoded instanceof SampleOuterA );

        SampleOuterA out = (SampleOuterA) decoded;

        assertEquals( src, out );
    }

    @Test public void innerStaticClassJSONTest() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        SampleOuterB src = new SampleOuterB( "z123" );

        dataWriter.objectToJson( src );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"com.rr.core.recovery.json.TestJSONInnerClass$SampleOuterB\",\n"
                                 + "\t\"_strValA\" : \"z123\",\n"
                                 + "\t\"_inner\" : {\n"
                                 + "\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\"@class\" : \"com.rr.core.recovery.json.TestJSONInnerClass$SampleOuterB$SampleInner\",\n"
                                 + "\t\t\"_strValInner\" : \"strB\"\n"
                                 + "\t}\n"
                                 + "}\n";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object decoded = reader.jsonToObject();
        assertNotNull( decoded );

        SampleOuterB out = (SampleOuterB) decoded;

        assertEquals( src, out );
    }
}
