package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.Constants;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestJSONReader extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONReader.class, Level.info );

    @Test public void basicArray() {
        Byte[] arr = { 123, -1 };

        Object[] a2 = (Object[]) arr;

        assertEquals( (byte) 123, a2[ 0 ] );
        assertEquals( (byte) -1, a2[ 1 ] );

        List<Object> l = new ArrayList<>();
        l.add( arr[ 0 ] );
        l.add( arr[ 1 ] );

        l.toArray( a2 );

        assertEquals( (byte) 123, a2[ 0 ] );
        assertEquals( (byte) -1, a2[ 1 ] );
    }

    @Test public void jsonDouble() throws Exception {

        final double[] doubles = { 0.00000001, 1.2, 1.005, 1.0055, 0.0055, 0.00055, 0.0000055, 0.000000555, 123456789.987654321 };

        for ( Double d : doubles ) {

            DecimalFormat df = new DecimalFormat( "0", DecimalFormatSymbols.getInstance( Locale.ENGLISH ) );
            df.setMaximumFractionDigits( JSONCommon.MAX_DP_DIGITS ); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
            String strVal = df.format( d );

            String expPrettyDump = "{ \"@jsonType\" : \"DoubleType\", \"@val\" : " + strVal + " }\n";

            ReusableString           inStr        = new ReusableString( expPrettyDump );
            InputStream              inStrStream  = new ReusableStringInputStream( inStr );
            SMTComponentManager      componentMgr = new SMTComponentManager();
            Resolver                 resolver     = new SMTComponentResolver( componentMgr );
            JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
            JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
            Double                   obj          = reader.jsonToObject();

            ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );
            JSONWriter                 writer    = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );
            writer.objectToJson( obj, PersistMode.AllFields );
            assertEquals( expPrettyDump, outStream.getBuf().toString() );
        }
    }

    @Test public void jsonDoubleInf() throws Exception {

        String expPrettyDump = "{ \"@jsonType\" : \"DoubleType\", \"@val\" :  +infinity  }\n";

        ReusableString           inStr        = new ReusableString( expPrettyDump );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        Double                   obj          = reader.jsonToObject();

        assertEquals( Double.POSITIVE_INFINITY, (double) obj, Constants.TICK_WEIGHT );
        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );
        JSONWriter                 writer    = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );
        writer.objectToJson( Double.POSITIVE_INFINITY, PersistMode.AllFields );
        assertEquals( "{ \"@jsonType\" : \"DoubleType\", \"@val\" : infinity }\n", outStream.getBuf().toString() );
    }

    @Test public void jsonDoubleNaN() throws Exception {

        String expPrettyDump = "{ \"@jsonType\" : \"DoubleType\", \"@val\" : null }\n";

        ReusableString           inStr        = new ReusableString( expPrettyDump );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        Double                   obj          = reader.jsonToObject();

        assertNull( obj );
        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );
        JSONWriter                 writer    = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );
        writer.objectToJson( Double.NaN, PersistMode.AllFields );
        assertEquals( "{ \"@jsonType\" : \"DoubleType\", \"@val\" : null }\n", outStream.getBuf().toString() );
    }

    @Test public void jsonDoubleNegInf() throws Exception {

        String expPrettyDump = "{ \"@jsonType\" : \"DoubleType\", \"@val\" :  -infinity  }\n";

        ReusableString           inStr        = new ReusableString( expPrettyDump );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        Double                   obj          = reader.jsonToObject();

        assertEquals( Double.NEGATIVE_INFINITY, (double) obj, Constants.TICK_WEIGHT );
        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );
        JSONWriter                 writer    = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );
        writer.objectToJson( Double.NEGATIVE_INFINITY, PersistMode.AllFields );
        assertEquals( "{ \"@jsonType\" : \"DoubleType\", \"@val\" : -infinity }\n", outStream.getBuf().toString() );
    }

    @Test public void jsonDoubleNull() throws Exception {

        String expPrettyDump = "{ \"@jsonType\" : \"DoubleType\", \"@val\" : null }\n";

        ReusableString           inStr        = new ReusableString( expPrettyDump );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        Double                   obj          = reader.jsonToObject();

        assertNull( obj );
        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );
        JSONWriter                 writer    = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );
        writer.objectToJson( (Double) null, PersistMode.AllFields );
        assertEquals( "{null}", outStream.getBuf().toString() );
    }

    @Test public void jsonDoublePlusInf() throws Exception {

        String expPrettyDump = "{ \"@jsonType\" : \"DoubleType\", \"@val\" :  infinity  }\n";

        ReusableString           inStr        = new ReusableString( expPrettyDump );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        Double                   obj          = reader.jsonToObject();

        assertEquals( Double.POSITIVE_INFINITY, (double) obj, Constants.TICK_WEIGHT );
        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );
        JSONWriter                 writer    = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );
        writer.objectToJson( Double.POSITIVE_INFINITY, PersistMode.AllFields );
        assertEquals( "{ \"@jsonType\" : \"DoubleType\", \"@val\" : infinity }\n", outStream.getBuf().toString() );
    }

    @Test public void simpleEmbedded2DArraySmall() throws Exception {

        final String expStr = "{\n"
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

        ReusableString                          inStr        = new ReusableString( expStr );
        InputStream                             inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager                     componentMgr = new SMTComponentManager();
        Resolver                                resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache                cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader                              reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        RecoverySampleClasses.SampleWith2DArray obj          = reader.jsonToObject();

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );
        JSONWriter                 writer    = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );
        writer.objectToJson( obj, PersistMode.AllFields );
        assertEquals( expStr, outStream.getBuf().toString() );
    }

    @Test public void simplePrettyClassByteArrayJSON() throws Exception {

        String srcStr = "{\n"
                        + "\t\"@jsonId\" : 1,\n"
                        + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithByteArray\",\n"
                        + "\t\"_byteArrValF\" : {\n"
                        + "\t\t\"@jsonId\" : 2,\n"
                        + "\t\t\"@val\" : \n"
                        + "\t\t[\n"
                        + "\t\t\t-71,\n"
                        + "\t\t\t21\n"
                        + "\t\t]\n"
                        + "\t},\n"
                        + "\t\"_strValF\" : \"def\",\n"
                        + "\t\"_aByteArrValF\" : {\n"
                        + "\t\t\"@jsonId\" : 3,\n"
                        + "\t\t\"@val\" : \n"
                        + "\t\t[\n"
                        + "\t\t\t123,\n"
                        + "\t\t\tnull\n"
                        + "\t\t]\n"
                        + "\t}\n"
                        + "}\n";

        ReusableString                           inStr        = new ReusableString( srcStr );
        InputStream                              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager                      componentMgr = new SMTComponentManager();
        Resolver                                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache                 cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader                               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        RecoverySampleClasses.ClassWithByteArray obj          = reader.jsonToObject();

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );
        JSONWriter                 writer    = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );
        writer.objectToJson( obj, PersistMode.AllFields );
        assertEquals( srcStr, outStream.getBuf().toString() );
    }

    @Test public void simplePrettyJSON() throws Exception {

        String srcStr = "{\n"
                        + "\t\"@jsonId\" : 1,\n"
                        + "\t\"@class\" : \"com.rr.core.recovery.DummyPrimitivePersistableItemA\",\n"
                        + "\t\"@smtId\" : \"dummyId\",\n"
                        + "\t\"_id\" : \"dummyId\",\n"
                        + "\t\"_myBool\" : true,\n"
                        + "\t\"_myInt\" : 123,\n"
                        + "\t\"_myDouble\" : 1.0000000891,\n"
                        + "\t\"_myFloat\" : 2134.56787109375,\n"
                        + "\t\"_myBigDecimal\" : \"123456789.123456789\",\n"
                        + "\t\"_myShort\" : 16112,\n"
                        + "\t\"_myLong\" : 12345678912345,\n"
                        + "\t\"_myString\" : \"someStrVal\"\n"
                        + "}\n";

        String expStr = "{\n"
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

        ReusableString                 inStr        = new ReusableString( srcStr );
        InputStream                    inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager            componentMgr = new SMTComponentManager();
        Resolver                       resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache       cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader                     reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        DummyPrimitivePersistableItemA obj          = reader.jsonToObject();

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );
        JSONWriter                 writer    = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );
        writer.objectToJson( obj, PersistMode.AllFields );
        assertEquals( expStr, outStream.getBuf().toString() );
    }

    @Test public void simpleSMTSnapshotMember() throws Exception {

        String srcStr = "{\n"
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

        ReusableString           inStr        = new ReusableString( srcStr );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        RecoverySampleClasses.SMTComponentWithJustIntAndString obj = reader.jsonToObject();

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );
        JSONWriter                 writer    = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );
        writer.objectToJson( obj, PersistMode.AllFields );
        assertEquals( srcStr, outStream.getBuf().toString() );
    }
}
