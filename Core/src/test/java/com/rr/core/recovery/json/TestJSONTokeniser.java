package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableStringOutputStream;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.recovery.RecoverySampleClasses;
import org.junit.Test;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class TestJSONTokeniser extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONTokeniser.class, Level.info );

    @Test public void jsonWriteDouble() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        final double[] doubles = { 0.000000000000000123456789, 1.2, 1.005, 1.0055, 0.0055, 0.00055, 0.0000055, 0.000000555 };

        for ( Double d : doubles ) {

            outStream.reset();

            writer.objectToJson( d, PersistMode.AllFields );

            _log.info( "jsonstr=" + outStream.getBuf().toString() );

            DecimalFormat df = new DecimalFormat( "0", DecimalFormatSymbols.getInstance( Locale.ENGLISH ) );
            df.setMaximumFractionDigits( JSONCommon.MAX_DP_DIGITS );
            String strVal = df.format( d );

            String expPrettyDump = "{ \"@jsonType\" : \"DoubleType\", \"@val\" : " + strVal + " }\n";

            assertEquals( expPrettyDump, outStream.getBuf().toString() );
        }
    }

    @Test public void simpleEmbedded2DArraySmall() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        RecoverySampleClasses.SampleWith2DArray itemA = new RecoverySampleClasses.SampleWith2DArray( 3 );
        itemA.init();

        writer.objectToJson( itemA, PersistMode.AllFields );

        ReusableString rs     = new ReusableString( outStream.getBuf() );
        ReusableString outStr = new ReusableString();

        JSONPrettyDump dump = new JSONPrettyDump();
        dump.prettyDump( rs, outStr );

        final String expStr = "{\n"
                              + "\t\"@jsonId\" : 1,\n"
                              + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleWith2DArray\",\n"
                              + "\t\"_size\" : 3,\n"
                              + "\t\"_arr2D\" : {\n"
                              + "\t\t\"@jsonId\" : 2,\n"
                              + "\t\t\"@arrayOf\" : \"[Lcom.rr.core.recovery.RecoverySampleClasses$SampleInterfaceA;\",\n"
                              + "\t\t\"@val\" : [\n"
                              + "\t\t\t{\n"
                              + "\t\t\t\t\"@jsonId\" : 3,\n"
                              + "\t\t\t\t\"@arrayOf\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleInterfaceA\",\n"
                              + "\t\t\t\t\"@val\" : [\n"
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
                              + "\t\t\t\t\"@val\" : [\n"
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
                              + "\t\t\t\t\"@val\" : [\n"
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
                              + "\t\t\"@val\" : [\n"
                              + "\t\t\t{\n"
                              + "\t\t\t\t\"@jsonId\" : 13,\n"
                              + "\t\t\t\t\"@val\" : [\n"
                              + "\t\t\t\t\t3000\n"
                              + "\t\t\t\t]\n"
                              + "\t\t\t},\n"
                              + "\t\t\t{\n"
                              + "\t\t\t\t\"@jsonId\" : 14,\n"
                              + "\t\t\t\t\"@val\" : [\n"
                              + "\t\t\t\t\t6000,\n"
                              + "\t\t\t\t\t12000\n"
                              + "\t\t\t\t]\n"
                              + "\t\t\t},\n"
                              + "\t\t\t{\n"
                              + "\t\t\t\t\"@jsonId\" : 15,\n"
                              + "\t\t\t\t\"@val\" : [\n"
                              + "\t\t\t\t\t9000,\n"
                              + "\t\t\t\t\t18000,\n"
                              + "\t\t\t\t\t27000\n"
                              + "\t\t\t\t]\n"
                              + "\t\t\t}\n"
                              + "\t\t]\n"
                              + "\t}\n"
                              + "}";

        assertEquals( expStr, outStr.toString() );
    }

    @Test public void simplePrettyClassByteArrayJSON() throws Exception {

        String srcStr = "{\n"
                        + "\t\"@jsonId\" : 1,\n"
                        + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithByteArray\",\n"
                        + "\t\"_byteArrValF\" : {\n"
                        + "\t\t\"@len\" : 2,  \"@val\" : \n"
                        + "\t\t[\n"
                        + "\t\t\t-71,\n"
                        + "\t\t\t21\n"
                        + "\t\t]\n"
                        + "\t},\n"
                        + "\t\"_strValF\" : \"def\",\n"
                        + "\t\"_aByteArrValF\" : {\n"
                        + "\t\t\"@len\" : 2,  \"@val\" : \n"
                        + "\t\t[\n"
                        + "\t\t\t123,\n"
                        + "\t\t\tnull\n"
                        + "\t\t]\n"
                        + "\t}\n"
                        + "}\n";

        ReusableString rs     = new ReusableString( srcStr );
        ReusableString outStr = new ReusableString();

        JSONPrettyDump dump = new JSONPrettyDump();
        dump.prettyDump( rs, outStr );

        final String expStr = "{\n"
                              + "\t\"@jsonId\" : 1,\n"
                              + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithByteArray\",\n"
                              + "\t\"_byteArrValF\" : {\n"
                              + "\t\t\"@len\" : 2,\n"
                              + "\t\t\"@val\" : [\n"
                              + "\t\t\t-71,\n"
                              + "\t\t\t21\n"
                              + "\t\t]\n"
                              + "\t},\n"
                              + "\t\"_strValF\" : \"def\",\n"
                              + "\t\"_aByteArrValF\" : {\n"
                              + "\t\t\"@len\" : 2,\n"
                              + "\t\t\"@val\" : [\n"
                              + "\t\t\t123,\n"
                              + "\t\t\tnull\n"
                              + "\t\t]\n"
                              + "\t}\n"
                              + "}";

        assertEquals( expStr, outStr.toString() );
    }

    @Test public void simplePrettyJSON() throws Exception {

        String srcStr = "{\n"
                        + "\t\"@jsonId\" : 1,\n"
                        + "\t\"@class\" : \"com.rr.core.recovery.DummyPrimitivePersistableItemA\",\n"
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

        ReusableString rs     = new ReusableString( srcStr );
        ReusableString outStr = new ReusableString();

        JSONPrettyDump dump = new JSONPrettyDump();
        dump.prettyDump( rs, outStr );

        final String expStr = "{\n"
                              + "\t\"@jsonId\" : 1,\n"
                              + "\t\"@class\" : \"com.rr.core.recovery.DummyPrimitivePersistableItemA\",\n"
                              + "\t\"_id\" : \"dummyId\",\n"
                              + "\t\"_myBool\" : true,\n"
                              + "\t\"_myInt\" : 123,\n"
                              + "\t\"_myDouble\" : 1.0000000891,\n"
                              + "\t\"_myFloat\" : 2134.56787109375,\n"
                              + "\t\"_myBigDecimal\" : \"123456789.123456789\",\n"
                              + "\t\"_myShort\" : 16112,\n"
                              + "\t\"_myLong\" : 12345678912345,\n"
                              + "\t\"_myString\" : \"someStrVal\"\n"
                              + "}";

        assertEquals( expStr, outStr.toString() );
    }
}
