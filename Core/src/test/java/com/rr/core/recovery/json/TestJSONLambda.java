package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableStringInputStream;
import com.rr.core.lang.ReusableStringOutputStream;
import com.rr.core.lang.ZFunction;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.recovery.RecoverySampleClasses.ClassWithJustStringAndLambda;
import com.rr.core.recovery.SMTComponentResolver;
import com.rr.core.utils.ReflectUtils;
import org.junit.Test;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.Arrays;
import java.util.function.Function;

import static org.junit.Assert.*;

public class TestJSONLambda extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONLambda.class, Level.info );

    public interface SimpleTwoMethods {

        String getA( String a );

        String getB( String a );
    }

    public static class SesZFunc implements Function<double[], Double> {

        private double _sesCoef;
        private int    _maxRangeSize;

        public SesZFunc() {
        }

        public SesZFunc( double sesCoef, int maxRangeSize ) { _sesCoef      = sesCoef;
                                                              _maxRangeSize = maxRangeSize;
                                                            }

        @Override public Double apply( final double[] doubles ) {
            if ( doubles == null )
                return 0.0;

            if ( doubles.length < 3 )
                return doubles[ doubles.length - 1 ];

            return 1.0;
        }

        public int getMaxRangeSize() { return _maxRangeSize; }

        public double getSesCoef()   { return _sesCoef; }
    }

    public static class ClassWithStatRef {

        private SesZFunc _startSmoother;

        public ClassWithStatRef() {
        }

        public ClassWithStatRef( final SesZFunc startSmoother ) {
            _startSmoother = startSmoother;
        }
    }

    public static class BadClassWithJustStringAndLambda implements Serializable {

        private String                   _strValA = "abc";
        private Function<String, String> _proc;

        public BadClassWithJustStringAndLambda( final String strValA, final Function<String, String> proc ) {
            _strValA = strValA;
            _proc    = proc;
        }
    }

    public static class FuncRef {

        private String _strValA = "XYZ";

        public FuncRef() {
            // for reflect
        }

        public FuncRef( final String strValA ) {
            _strValA = strValA;
        }

        public String doToUpper( String a ) {
            return _strValA + a.toUpperCase();
        }
    }

    public static class C1 {

        private ZFunction<String, String> _supplier;
        private String                    _s1;

        public C1() { }

        public C1( final String s1 ) {
            _s1 = s1;

            _supplier = makeLambda();
        }

        public SimpleTwoMethods getSupplier() {
            return new SimpleTwoMethods() {

                @Override public String getA( String a ) {
                    return _s1 + "_A" + "_" + a;
                }

                @Override public String getB( String a ) {
                    return _s1 + "_B" + "_" + a;
                }
            };
        }

        public ZFunction<String, String> makeLambda() {
            ZFunction<String, String> func = ( a ) -> _s1 + "_" + a;

            return func;
        }
    }

    public static class C2 {

        private SimpleTwoMethods _supplier;

        public C2()           { }

        public C2( C2 other ) { _supplier = other._supplier; }

        public C2( final SimpleTwoMethods supplier ) {
            _supplier = supplier;
        }

        public String tst( String a ) { return _supplier.getA( a ); }
    }

    @Test public void anonDiffParent() throws Throwable {

        C1 c1  = new C1( "ABC" );
        C2 c2  = new C2( c1.getSupplier() );
        C2 src = new C2( c2 );

        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        dataWriter.objectToJson( src );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"com.rr.core.recovery.json.TestJSONLambda$C2\",\n"
                                 + "\t\"_supplier\" : {\n"
                                 + "\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\"@class\" : \"com.rr.core.recovery.json.TestJSONLambda$C1$1\",\n"
                                 + "\t\t\"@this$0\" : {\n"
                                 + "\t\t\t\"@jsonId\" : 3,\n"
                                 + "\t\t\t\"@class\" : \"com.rr.core.recovery.json.TestJSONLambda$C1\",\n"
                                 + "\t\t\t\"_supplier\" : {\n"
                                 + "\t\t\t\t\"@jsonId\" : 4,\n"
                                 + "\t\t\t\t\"@class\" : \"java.lang.invoke.SerializedLambda\",\n"
                                 + "\t\t\t\t\"capturingClass\" : \"com/rr/core/recovery/json/TestJSONLambda$C1\",\n"
                                 + "\t\t\t\t\"functionalInterfaceClass\" : \"com/rr/core/lang/ZFunction\",\n"
                                 + "\t\t\t\t\"functionalInterfaceMethodName\" : \"apply\",\n"
                                 + "\t\t\t\t\"functionalInterfaceMethodSignature\" : \"(Ljava/lang/Object;)Ljava/lang/Object;\",\n"
                                 + "\t\t\t\t\"implMethodKind\" : 7,\n"
                                 + "\t\t\t\t\"implClass\" : \"com/rr/core/recovery/json/TestJSONLambda$C1\",\n"
                                 + "\t\t\t\t\"implMethodName\" : \"lambda$makeLambda$5d4c661d$1\",\n"
                                 + "\t\t\t\t\"implMethodSignature\" : \"(Ljava/lang/String;)Ljava/lang/String;\",\n"
                                 + "\t\t\t\t\"instantiatedMethodType\" : \"(Ljava/lang/String;)Ljava/lang/String;\",\n"
                                 + "\t\t\t\t\"capturedArgs\" : {\n"
                                 + "\t\t\t\t\t\"@jsonId\" : 5,\n"
                                 + "\t\t\t\t\t\"@arrayOf\" : \"java.lang.Object\",\n"
                                 + "\t\t\t\t\t\"@val\" : \n"
                                 + "\t\t\t\t\t[\n"
                                 + "\t\t\t\t\t\t{\"@ref\" : \"3\"}\n"
                                 + "\t\t\t\t\t\t\n"
                                 + "\t\t\t\t\t]\n"
                                 + "\t\t\t\t}\n"
                                 + "\t\t\t},\n"
                                 + "\t\t\t\"_s1\" : \"ABC\"\n"
                                 + "\t\t}\n"
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
        assertTrue( decoded instanceof C2 );

        C2 out = (C2) decoded;

        assertEquals( "ABC_A_z123", out.tst( "z123" ) );

    }

    @Test public void lambdaCodec() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        ClassWithJustStringAndLambda src = new ClassWithJustStringAndLambda( "z123", ( a ) -> a.toUpperCase() );

        dataWriter.objectToJson( src );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithJustStringAndLambda\",\n"
                                 + "\t\"_strValA\" : \"z123\",\n"
                                 + "\t\"_proc\" : {\n"
                                 + "\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\"@class\" : \"java.lang.invoke.SerializedLambda\",\n"
                                 + "\t\t\"capturingClass\" : \"com/rr/core/recovery/json/TestJSONLambda\",\n"
                                 + "\t\t\"functionalInterfaceClass\" : \"com/rr/core/lang/ZFunction\",\n"
                                 + "\t\t\"functionalInterfaceMethodName\" : \"apply\",\n"
                                 + "\t\t\"functionalInterfaceMethodSignature\" : \"(Ljava/lang/Object;)Ljava/lang/Object;\",\n"
                                 + "\t\t\"implMethodKind\" : 6,\n"
                                 + "\t\t\"implClass\" : \"com/rr/core/recovery/json/TestJSONLambda\",\n"
                                 + "\t\t\"implMethodName\" : \"lambda$lambdaCodec$ec24554e$1\",\n"
                                 + "\t\t\"implMethodSignature\" : \"(Ljava/lang/String;)Ljava/lang/String;\",\n"
                                 + "\t\t\"instantiatedMethodType\" : \"(Ljava/lang/String;)Ljava/lang/String;\",\n"
                                 + "\t\t\"capturedArgs\" : []\n"
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
        assertTrue( decoded instanceof ClassWithJustStringAndLambda );

        ClassWithJustStringAndLambda out = (ClassWithJustStringAndLambda) decoded;

        assertEquals( "ABCz123", out.doStuff( "abc" ) );
    }

    @Test public void manualLambdaSerialisation() throws Throwable {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        ClassWithJustStringAndLambda src = new ClassWithJustStringAndLambda( "z123", ( a ) -> a.toUpperCase() );

        SerializedLambda sl = ReflectUtils.serializeLambda( src.getProc() );

        Class<?> capturingClass                     = ReflectUtils.getClass( sl.getCapturingClass().replace( '/', '.' ) );
        String   functionalInterfaceClass           = sl.getFunctionalInterfaceClass();
        String   functionalInterfaceMethodName      = sl.getFunctionalInterfaceMethodName();
        String   functionalInterfaceMethodSignature = sl.getFunctionalInterfaceMethodSignature();
        int      implMethodKind                     = sl.getImplMethodKind();
        String   implClass                          = sl.getImplClass();
        String   implMethodName                     = sl.getImplMethodName();
        String   implMethodSignature                = sl.getImplMethodSignature();
        String   instantiatedMethodType             = sl.getInstantiatedMethodType();

        SerializedLambda sl2 = new SerializedLambda( capturingClass,
                                                     functionalInterfaceClass,
                                                     functionalInterfaceMethodName,
                                                     functionalInterfaceMethodSignature,
                                                     implMethodKind,
                                                     implClass,
                                                     implMethodName,
                                                     implMethodSignature,
                                                     instantiatedMethodType,
                                                     new Object[ 0 ] );

        Object o = ReflectUtils.deserialiseLambda( sl2 );

        ClassWithJustStringAndLambda out = new ClassWithJustStringAndLambda( "z123", (ZFunction<String, String>) o );

        assertEquals( "ABCz123", out.doStuff( "abc" ) );
    }

    @Test public void methodRefCodec() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        FuncRef fr = new FuncRef( "qwerty" );

        ClassWithJustStringAndLambda src = new ClassWithJustStringAndLambda( "z123", fr::doToUpper );

        dataWriter.objectToJson( src );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithJustStringAndLambda\",\n"
                                 + "\t\"_strValA\" : \"z123\",\n"
                                 + "\t\"_proc\" : {\n"
                                 + "\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\"@class\" : \"java.lang.invoke.SerializedLambda\",\n"
                                 + "\t\t\"capturingClass\" : \"com/rr/core/recovery/json/TestJSONLambda\",\n"
                                 + "\t\t\"functionalInterfaceClass\" : \"com/rr/core/lang/ZFunction\",\n"
                                 + "\t\t\"functionalInterfaceMethodName\" : \"apply\",\n"
                                 + "\t\t\"functionalInterfaceMethodSignature\" : \"(Ljava/lang/Object;)Ljava/lang/Object;\",\n"
                                 + "\t\t\"implMethodKind\" : 5,\n"
                                 + "\t\t\"implClass\" : \"com/rr/core/recovery/json/TestJSONLambda$FuncRef\",\n"
                                 + "\t\t\"implMethodName\" : \"doToUpper\",\n"
                                 + "\t\t\"implMethodSignature\" : \"(Ljava/lang/String;)Ljava/lang/String;\",\n"
                                 + "\t\t\"instantiatedMethodType\" : \"(Ljava/lang/String;)Ljava/lang/String;\",\n"
                                 + "\t\t\"capturedArgs\" : {\n"
                                 + "\t\t\t\"@jsonId\" : 3,\n"
                                 + "\t\t\t\"@arrayOf\" : \"java.lang.Object\",\n"
                                 + "\t\t\t\"@val\" : \n"
                                 + "\t\t\t[\n"
                                 + "\t\t\t\t{\n"
                                 + "\t\t\t\t\t\"@jsonId\" : 4,\n"
                                 + "\t\t\t\t\t\"@class\" : \"com.rr.core.recovery.json.TestJSONLambda$FuncRef\",\n"
                                 + "\t\t\t\t\t\"_strValA\" : \"qwerty\"\n"
                                 + "\t\t\t\t}\n"
                                 + "\t\t\t\t\n"
                                 + "\t\t\t]\n"
                                 + "\t\t}\n"
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
        assertTrue( decoded instanceof ClassWithJustStringAndLambda );

        ClassWithJustStringAndLambda out = (ClassWithJustStringAndLambda) decoded;

        assertEquals( "qwertyABCz123", out.doStuff( "abc" ) );
    }

    @Test public void nonSerialisableLambdaFails() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        BadClassWithJustStringAndLambda src = new BadClassWithJustStringAndLambda( "z123", ( a ) -> a.toUpperCase() );

        try {
            dataWriter.objectToJson( src );

            fail( "expectedException" );
        } catch( JSONException e ) {
            assertTrue( e.getMessage().contains( "lambda is NOT serialisable " ) );
        }
    }

    @Test public void rawLambdaSerialisation() throws Throwable {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        ClassWithJustStringAndLambda src = new ClassWithJustStringAndLambda( "z123", ( a ) -> a.toUpperCase() );

        SerializedLambda sl = ReflectUtils.serializeLambda( src.getProc() );

        Object o = ReflectUtils.deserialiseLambda( sl );

        ClassWithJustStringAndLambda out = new ClassWithJustStringAndLambda( "z123", (ZFunction<String, String>) o );

        assertEquals( "ABCz123", out.doStuff( "abc" ) );
    }

    @Test public void staticMethodRef() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        ClassWithStatRef fr = new ClassWithStatRef( new SesZFunc( 0.3, 10 ) );

        dataWriter.objectToJson( fr );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"com.rr.core.recovery.json.TestJSONLambda$ClassWithStatRef\",\n"
                                 + "\t\"_startSmoother\" : {\n"
                                 + "\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\"@class\" : \"com.rr.core.recovery.json.TestJSONLambda$SesZFunc\",\n"
                                 + "\t\t\"_sesCoef\" : 0.3,\n"
                                 + "\t\t\"_maxRangeSize\" : 10\n"
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
        assertTrue( decoded instanceof ClassWithStatRef );
    }
}
