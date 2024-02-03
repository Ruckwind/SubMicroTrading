package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableStringInputStream;
import com.rr.core.lang.ReusableStringOutputStream;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.recovery.DummyPrimitivePersistableItemA;
import com.rr.core.recovery.RecoverySampleClasses;
import com.rr.core.recovery.SMTComponentResolver;
import org.junit.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import static org.junit.Assert.*;

public class TestJSONWrite extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONWrite.class, Level.info );

    public static class EmbeddedTZ {

        public TimeZone _tz;
    }

    @Test public void checkDeltaSnapMember() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        RecoverySampleClasses.SMTComponentWithJustIntAndString c1 = new RecoverySampleClasses.SMTComponentWithJustIntAndString( "sampleComponent" );

        ReusableString b = new ReusableString( 512 );

        for ( int i = 0; i < 15; i++ ) {
            String istr = String.format( "%02d", i );

            b.append( "I" + istr + "abcdefghijklmnopqrstuvwxyz0123456789\n" );
        }

        c1.setStrValA( b.toString() );

        writer.objectToJson( c1, PersistMode.OnlyFieldsWithPersistAnnotation );

        String expJSONStr = "{\n"
                            + "\t\"@jsonId\" : 1,\n"
                            + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$SMTComponentWithJustIntAndString\",\n"
                            + "\t\"@smtId\" : \"I00abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I01abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I02abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I03abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I04abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I05abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I06abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I07abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I08abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I09abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I10abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I11abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I12abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I13abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I14abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "\",\n"
                            + "\t\"@pMode\" : \"OnlyFieldsWithPersistAnnotation\"\n"
                            + "}\n";

        assertEquals( expJSONStr, outStream.getBuf().toString() );
    }

    @Test public void checkSimplePrimitiveUsingCompleteReflection() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        DummyPrimitivePersistableItemA itemA = new DummyPrimitivePersistableItemA( "dummyId", true, 123, 1.0000000891d, 2134.5678f, new BigDecimal( "123456789.123456789" ),
                                                                                   (short) 16112, 12345678912345l, "someStrVal" );

        writer.objectToJson( itemA, PersistMode.AllFields );
    }

    @Test public void checkStaticAllFields() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        RecoverySampleClasses.SMTComponentWithJustIntAndString c1 = new RecoverySampleClasses.SMTComponentWithJustIntAndString( "sampleComponent" );

        ReusableString b = new ReusableString( 512 );

        for ( int i = 0; i < 15; i++ ) {
            String istr = String.format( "%02d", i );

            b.append( "I" + istr + "abcdefghijklmnopqrstuvwxyz0123456789\n" );
        }

        c1.setStrValA( b.toString() );

        writer.objectToJson( c1, PersistMode.AllFields );

        String expJSONStr = "{\n"
                            + "\t\"@jsonId\" : 1,\n"
                            + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$SMTComponentWithJustIntAndString\",\n"
                            + "\t\"@smtId\" : \"I00abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I01abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I02abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I03abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I04abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I05abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I06abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I07abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I08abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I09abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I10abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I11abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I12abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I13abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I14abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "\",\n"
                            + "\t\"_intValA\" : 123,\n"
                            + "\t\"_strValA\" : \"I00abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I01abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I02abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I03abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I04abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I05abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I06abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I07abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I08abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I09abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I10abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I11abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I12abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I13abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "I14abcdefghijklmnopqrstuvwxyz0123456789\n"
                            + "\"\n"
                            + "}\n";

        assertEquals( expJSONStr, outStream.getBuf().toString() );
    }

    @Test public void embeddedMap() throws Exception {
        Map<String, String> map = new HashMap<>();

        map.put( "aaa", "111" );
        map.put( "bbb", "222" );
        map.put( "ccc", "333" );

        RecoverySampleClasses.ClassWithMapAndInt v1 = new RecoverySampleClasses.ClassWithMapAndInt( 123, map );
        RecoverySampleClasses.ClassWithMapAndInt v2 = new RecoverySampleClasses.ClassWithMapAndInt( 456, map );

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        List<RecoverySampleClasses.ClassWithMapAndInt> vals = Arrays.asList( v1, v2 );

        writer.objectToJson( vals, PersistMode.AllFields );

        String expJSONStr = "{\n"
                            + "\t\"@jsonId\" : 1,\n"
                            + "\t\"@class\" : \"java.util.Arrays$ArrayList\",\n"
                            + "\t\"@val\" : [\n"
                            + "\t\t{\n"
                            + "\t\t\t\"@jsonId\" : 2,\n"
                            + "\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithMapAndInt\",\n"
                            + "\t\t\t\"_map\" : {\n"
                            + "\t\t\t\t\"@jsonId\" : 3,\n"
                            + "\t\t\t\t\"@class\" : \"java.util.HashMap\",\n"
                            + "\t\t\t\t\"@entrySet\" : \n"
                            + "\t\t\t\t[\n"
                            + "\t\t\t\t\t{\"@key\" : \"aaa\" , \"@val\" : \"111\"},\n"
                            + "\t\t\t\t\t{\"@key\" : \"ccc\" , \"@val\" : \"333\"},\n"
                            + "\t\t\t\t\t{\"@key\" : \"bbb\" , \"@val\" : \"222\"}\n"
                            + "\t\t\t\t]\n"
                            + "\t\t\t},\n"
                            + "\t\t\t\"_intVal\" : 123\n"
                            + "\t\t},\n"
                            + "\t\t{\n"
                            + "\t\t\t\"@jsonId\" : 4,\n"
                            + "\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithMapAndInt\",\n"
                            + "\t\t\t\"_map\" : {\"@ref\" : \"3\"},\n"
                            + "\t\t\t\"_intVal\" : 456\n"
                            + "\t\t}\n"
                            + "\t]\n"
                            + "}\n";

        assertEquals( expJSONStr, outStream.getBuf().toString() );
    }

    @Test public void jsonTZField() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        Class<?> clazz = JSONWriterImpl.class;

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        TimeZone t1 = TimeZone.getTimeZone( "America/Chicago" );

        EmbeddedTZ src = new EmbeddedTZ();

        src._tz = t1;

        dataWriter.objectToJson( src );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"com.rr.core.recovery.json.TestJSONWrite$EmbeddedTZ\",\n"
                                 + "\t\"_tz\" : \"America/Chicago\"\n"
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
        assertTrue( decoded instanceof EmbeddedTZ );

        EmbeddedTZ out = (EmbeddedTZ) decoded;

        assertEquals( src._tz, out._tz );
    }

    @Test public void jsonWriteDouble() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        final double[] doubles = { 123456789012345.987654321d, 0.0123456789d, 0.000000000000000123456789d, 1.2, 1.005, 1.0055, 0.0055, 0.00055, 0.0000055d, 0.000000555d, 12345.987654321012345d };

        for ( Double d : doubles ) {

            outStream.reset();

            writer.objectToJson( d, PersistMode.AllFields );

            _log.info( "" );
            _log.info( "ACTUALjsonstr=" + outStream.getBuf().toString() );

            DecimalFormat df = new DecimalFormat( "0", DecimalFormatSymbols.getInstance( Locale.ENGLISH ) );
            df.setMaximumFractionDigits( JSONCommon.MAX_DP_DIGITS );
            String strVal = df.format( d );

            String expPrettyDump = "{ \"@jsonType\" : \"DoubleType\", \"@val\" : " + strVal + " }\n";

            _log.info( "EXPECTjsonstr=" + expPrettyDump );

            assertEquals( expPrettyDump, outStream.getBuf().toString() );
        }
    }

    @Test public void simpleEmbedded2DArraySmall() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        RecoverySampleClasses.SampleWith2DArray itemA = new RecoverySampleClasses.SampleWith2DArray( 3 );
        itemA.init();

        writer.objectToJson( itemA, PersistMode.AllFields );

        String expPrettyDump = "{\n"
                               + "\t\"@jsonId\" : 1,\n"
                               + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleWith2DArray\",\n"
                               + "\t\"_size\" : 3,\n"
                               + "\t\"_arr2D\" : {\n"
                               + "\t\t\"@jsonId\" : 2,\n"
                               + "\t\t\"@arrayOf\" : \"[Lcom.rr.core.recovery.RecoverySampleClasses$SampleInterfaceA;\",\n"
                               + "\t\t\"@val\" : \n"
                               + "\t\t[\n"
                               + "\t\t\t{\n"
                               + "\t\t\t\t\"@jsonId\" : 3,\n"
                               + "\t\t\t\t\"@arrayOf\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleInterfaceA\",\n"
                               + "\t\t\t\t\"@val\" : \n"
                               + "\t\t\t\t[\n"
                               + "\t\t\t\t\t{\n"
                               + "\t\t\t\t\t\t\"@jsonId\" : 4,\n"
                               + "\t\t\t\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleInterfaceAImpl\",\n"
                               + "\t\t\t\t\t\t\"_covariance\" : 0.12345,\n"
                               + "\t\t\t\t\t\t\"_timeSpan\" : 3000,\n"
                               + "\t\t\t\t\t\t\"_paused\" : true\n"
                               + "\t\t\t\t\t}\n"
                               + "\t\t\t\t]\n"
                               + "\t\t\t},\n"
                               + "\t\t\t{\n"
                               + "\t\t\t\t\"@jsonId\" : 5,\n"
                               + "\t\t\t\t\"@arrayOf\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleInterfaceA\",\n"
                               + "\t\t\t\t\"@val\" : \n"
                               + "\t\t\t\t[\n"
                               + "\t\t\t\t\t{\n"
                               + "\t\t\t\t\t\t\"@jsonId\" : 6,\n"
                               + "\t\t\t\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleInterfaceAImpl\",\n"
                               + "\t\t\t\t\t\t\"_covariance\" : 0.2469,\n"
                               + "\t\t\t\t\t\t\"_timeSpan\" : 6000,\n"
                               + "\t\t\t\t\t\t\"_paused\" : true\n"
                               + "\t\t\t\t\t},\n"
                               + "\t\t\t\t\t{\n"
                               + "\t\t\t\t\t\t\"@jsonId\" : 7,\n"
                               + "\t\t\t\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleInterfaceAImpl\",\n"
                               + "\t\t\t\t\t\t\"_covariance\" : 0.4938,\n"
                               + "\t\t\t\t\t\t\"_timeSpan\" : 12000,\n"
                               + "\t\t\t\t\t\t\"_paused\" : true\n"
                               + "\t\t\t\t\t}\n"
                               + "\t\t\t\t]\n"
                               + "\t\t\t},\n"
                               + "\t\t\t{\n"
                               + "\t\t\t\t\"@jsonId\" : 8,\n"
                               + "\t\t\t\t\"@arrayOf\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleInterfaceA\",\n"
                               + "\t\t\t\t\"@val\" : \n"
                               + "\t\t\t\t[\n"
                               + "\t\t\t\t\t{\n"
                               + "\t\t\t\t\t\t\"@jsonId\" : 9,\n"
                               + "\t\t\t\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleInterfaceAImpl\",\n"
                               + "\t\t\t\t\t\t\"_covariance\" : 0.37035,\n"
                               + "\t\t\t\t\t\t\"_timeSpan\" : 9000,\n"
                               + "\t\t\t\t\t\t\"_paused\" : true\n"
                               + "\t\t\t\t\t},\n"
                               + "\t\t\t\t\t{\n"
                               + "\t\t\t\t\t\t\"@jsonId\" : 10,\n"
                               + "\t\t\t\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleInterfaceAImpl\",\n"
                               + "\t\t\t\t\t\t\"_covariance\" : 0.7407,\n"
                               + "\t\t\t\t\t\t\"_timeSpan\" : 18000,\n"
                               + "\t\t\t\t\t\t\"_paused\" : true\n"
                               + "\t\t\t\t\t},\n"
                               + "\t\t\t\t\t{\n"
                               + "\t\t\t\t\t\t\"@jsonId\" : 11,\n"
                               + "\t\t\t\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleInterfaceAImpl\",\n"
                               + "\t\t\t\t\t\t\"_covariance\" : 1.11105,\n"
                               + "\t\t\t\t\t\t\"_timeSpan\" : 27000,\n"
                               + "\t\t\t\t\t\t\"_paused\" : true\n"
                               + "\t\t\t\t\t}\n"
                               + "\t\t\t\t]\n"
                               + "\t\t\t}\n"
                               + "\t\t]\n"
                               + "\t},\n"
                               + "\t\"_arr2DInt\" : {\n"
                               + "\t\t\"@jsonId\" : 12,\n"
                               + "\t\t\"@val\" : \n"
                               + "\t\t[\n"
                               + "\t\t\t{\n"
                               + "\t\t\t\t\"@jsonId\" : 13,\n"
                               + "\t\t\t\t\"@val\" : \n"
                               + "\t\t\t\t[\n"
                               + "\t\t\t\t\t3000\n"
                               + "\t\t\t\t]\n"
                               + "\t\t\t},\n"
                               + "\t\t\t{\n"
                               + "\t\t\t\t\"@jsonId\" : 14,\n"
                               + "\t\t\t\t\"@val\" : \n"
                               + "\t\t\t\t[\n"
                               + "\t\t\t\t\t6000,\n"
                               + "\t\t\t\t\t12000\n"
                               + "\t\t\t\t]\n"
                               + "\t\t\t},\n"
                               + "\t\t\t{\n"
                               + "\t\t\t\t\"@jsonId\" : 15,\n"
                               + "\t\t\t\t\"@val\" : \n"
                               + "\t\t\t\t[\n"
                               + "\t\t\t\t\t9000,\n"
                               + "\t\t\t\t\t18000,\n"
                               + "\t\t\t\t\t27000\n"
                               + "\t\t\t\t]\n"
                               + "\t\t\t}\n"
                               + "\t\t]\n"
                               + "\t}\n"
                               + "}\n";

        assertEquals( expPrettyDump, outStream.getBuf().toString() );
    }

    @Test public void simplePrettyClassByteArrayJSON() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        RecoverySampleClasses.ClassWithByteArray itemA = new RecoverySampleClasses.ClassWithByteArray();

        writer.objectToJson( itemA, PersistMode.AllFields );

        String expJSONStr = "{\n"
                            + "\t\"@jsonId\" : 1,\n"
                            + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithByteArray\",\n"
                            + "\t\"_byteArrValF\" : {\n"
                            + "\t\t\"@jsonId\" : 2,\n"
                            + "\t\t\"@val\" : \n"
                            + "\t\t[\n"
                            + "\t\t\t-71,\n"
                            + "\t\t\t21,\n"
                            + "\t\t\t-1\n"
                            + "\t\t]\n"
                            + "\t},\n"
                            + "\t\"_strValF\" : \"def\",\n"
                            + "\t\"_aByteArrValF\" : {\n"
                            + "\t\t\"@jsonId\" : 3,\n"
                            + "\t\t\"@val\" : \n"
                            + "\t\t[\n"
                            + "\t\t\t123,\n"
                            + "\t\t\t-1,\n"
                            + "\t\t\t23\n"
                            + "\t\t]\n"
                            + "\t}\n"
                            + "}\n";

        assertEquals( expJSONStr, outStream.getBuf().toString() );
    }

    @Test public void simplePrettyJSON() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        DummyPrimitivePersistableItemA itemA = new DummyPrimitivePersistableItemA( "dummyId", true, 123, 1.0000000891d, 2134.5678f, new BigDecimal( "123456789.123456789" ),
                                                                                   (short) 16112, 12345678912345l, "someStrVal" );

        writer.objectToJson( itemA, PersistMode.AllFields );

        String expJSONStr = "{\n"
                            + "\t\"@jsonId\" : 1,\n"
                            + "\t\"@class\" : \"com.rr.core.recovery.DummyPrimitivePersistableItemA\",\n"
                            + "\t\"@smtId\" : \"dummyId\",\n"
                            + "\t\"_id\" : \"dummyId\",\n"
                            + "\t\"_myBool\" : true,\n"
                            + "\t\"_myInt\" : 123,\n"
                            + "\t\"_myDouble\" : 1.00000009,\n"
                            + "\t\"_myFloat\" : 2134.56787109,\n"
                            + "\t\"_myBigDecimal\" : \"123456789.123456789\",\n"
                            + "\t\"_myShort\" : 16112,\n"
                            + "\t\"_myLong\" : 12345678912345,\n"
                            + "\t\"_myString\" : \"someStrVal\"\n"
                            + "}\n";

        assertEquals( expJSONStr, outStream.getBuf().toString() );
    }
}
