package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableStringInputStream;
import com.rr.core.lang.ReusableStringOutputStream;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.recovery.ExportContainer;
import com.rr.core.recovery.RecoverySampleClasses;
import com.rr.core.recovery.SMTComponentResolver;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestJSONExportSample extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONExportSample.class, Level.info );

    @Test public void mapJSONCustomExport() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        RecoverySampleClasses.SampleBTExportComponent c1 = new RecoverySampleClasses.SampleBTExportComponent( "sampleComponent", "aStrRef" );

        ReusableString b = new ReusableString( 512 );

        String strExportVal = "aaabbbcccddd";

        for ( int i = 0; i < 15; i++ ) {
            String istr = String.format( "%02d", i );

            b.append( "I" + istr + "abcdefghijklmnopqrstuvwxyz0123456789\n" );
        }

        RecoverySampleClasses.SampleContainer[] sampleContainers = new RecoverySampleClasses.SampleContainer[ 3 ];

        sampleContainers[ 0 ] = makeContainerEntry( 1.2, 1.5, 2.1 );
        sampleContainers[ 1 ] = makeContainerEntry( 1.5, 2.1, 2.5 );
        sampleContainers[ 2 ] = makeContainerEntry( 3.1, 3.6, 4.1 );

        c1.setStrValA( b.toString() );
        c1.setStrForBTExport( strExportVal );
        c1.setSampleContainerArr( sampleContainers );

        ExportContainer ec = new ExportContainer();
        ec.setIdOfExportComponent( "sampleId" );
        c1.exportData( ec );

        dataWriter.objectToJson( ec );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 2,\n"
                                 + "\t\"@class\" : \"com.rr.core.recovery.ExportContainer\",\n"
                                 + "\t\"_idOfExportComponent\" : \"sampleId\",\n"
                                 + "\t\"_exportVals\" : {\n"
                                 + "\t\t\"@jsonId\" : 3,\n"
                                 + "\t\t\"@class\" : \"java.util.LinkedHashMap\",\n"
                                 + "\t\t\"@entrySet\" : \n"
                                 + "\t\t[\n"
                                 + "\t\t\t{\"@key\" : \"dblVal\" , \"@val\" : { \"@jsonType\" : \"DoubleType\", \"@val\" : null }},\n"
                                 + "\t\t\t{\"@key\" : \"myStr\" , \"@val\" : \"aaabbbcccddd\"},\n"
                                 + "\t\t\t{\"@key\" : \"myContainerArr\" , \"@val\" : {\n"
                                 + "\t\t\t\t\"@jsonId\" : 4,\n"
                                 + "\t\t\t\t\"@arrayOf\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleContainer\",\n"
                                 + "\t\t\t\t\"@val\" : \n"
                                 + "\t\t\t\t[\n"
                                 + "\t\t\t\t\t{\n"
                                 + "\t\t\t\t\t\t\"@jsonId\" : 5,\n"
                                 + "\t\t\t\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleContainer\",\n"
                                 + "\t\t\t\t\t\t\"_aMap\" : {\n"
                                 + "\t\t\t\t\t\t\t\"@jsonId\" : 6,\n"
                                 + "\t\t\t\t\t\t\t\"@class\" : \"java.util.HashMap\",\n"
                                 + "\t\t\t\t\t\t\t\"@entrySet\" : \n"
                                 + "\t\t\t\t\t\t\t[\n"
                                 + "\t\t\t\t\t\t\t\t{\"@key\" : \"K3\" , \"@val\" : { \"@jsonType\" : \"DoubleType\", \"@val\" : 2.1 }},\n"
                                 + "\t\t\t\t\t\t\t\t{\"@key\" : \"K1\" , \"@val\" : { \"@jsonType\" : \"DoubleType\", \"@val\" : 1.2 }},\n"
                                 + "\t\t\t\t\t\t\t\t{\"@key\" : \"K2\" , \"@val\" : { \"@jsonType\" : \"DoubleType\", \"@val\" : 1.5 }}\n"
                                 + "\t\t\t\t\t\t\t]\n"
                                 + "\t\t\t\t\t\t},\n"
                                 + "\t\t\t\t\t\t\"_dvals\" : {\n"
                                 + "\t\t\t\t\t\t\t\"@jsonId\" : 7,\n"
                                 + "\t\t\t\t\t\t\t\"@val\" : \n"
                                 + "\t\t\t\t\t\t\t[\n"
                                 + "\t\t\t\t\t\t\t\t1.2,\n"
                                 + "\t\t\t\t\t\t\t\t1.5,\n"
                                 + "\t\t\t\t\t\t\t\t2.1\n"
                                 + "\t\t\t\t\t\t\t]\n"
                                 + "\t\t\t\t\t\t}\n"
                                 + "\t\t\t\t\t},\t\t\t\t\t{\n"
                                 + "\t\t\t\t\t\t\"@jsonId\" : 8,\n"
                                 + "\t\t\t\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleContainer\",\n"
                                 + "\t\t\t\t\t\t\"_aMap\" : {\n"
                                 + "\t\t\t\t\t\t\t\"@jsonId\" : 9,\n"
                                 + "\t\t\t\t\t\t\t\"@class\" : \"java.util.HashMap\",\n"
                                 + "\t\t\t\t\t\t\t\"@entrySet\" : \n"
                                 + "\t\t\t\t\t\t\t[\n"
                                 + "\t\t\t\t\t\t\t\t{\"@key\" : \"K3\" , \"@val\" : { \"@jsonType\" : \"DoubleType\", \"@val\" : 2.5 }},\n"
                                 + "\t\t\t\t\t\t\t\t{\"@key\" : \"K1\" , \"@val\" : { \"@jsonType\" : \"DoubleType\", \"@val\" : 1.5 }},\n"
                                 + "\t\t\t\t\t\t\t\t{\"@key\" : \"K2\" , \"@val\" : { \"@jsonType\" : \"DoubleType\", \"@val\" : 2.1 }}\n"
                                 + "\t\t\t\t\t\t\t]\n"
                                 + "\t\t\t\t\t\t},\n"
                                 + "\t\t\t\t\t\t\"_dvals\" : {\n"
                                 + "\t\t\t\t\t\t\t\"@jsonId\" : 10,\n"
                                 + "\t\t\t\t\t\t\t\"@val\" : \n"
                                 + "\t\t\t\t\t\t\t[\n"
                                 + "\t\t\t\t\t\t\t\t1.5,\n"
                                 + "\t\t\t\t\t\t\t\t2.1,\n"
                                 + "\t\t\t\t\t\t\t\t2.5\n"
                                 + "\t\t\t\t\t\t\t]\n"
                                 + "\t\t\t\t\t\t}\n"
                                 + "\t\t\t\t\t},\t\t\t\t\t{\n"
                                 + "\t\t\t\t\t\t\"@jsonId\" : 11,\n"
                                 + "\t\t\t\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$SampleContainer\",\n"
                                 + "\t\t\t\t\t\t\"_aMap\" : {\n"
                                 + "\t\t\t\t\t\t\t\"@jsonId\" : 12,\n"
                                 + "\t\t\t\t\t\t\t\"@class\" : \"java.util.HashMap\",\n"
                                 + "\t\t\t\t\t\t\t\"@entrySet\" : \n"
                                 + "\t\t\t\t\t\t\t[\n"
                                 + "\t\t\t\t\t\t\t\t{\"@key\" : \"K3\" , \"@val\" : { \"@jsonType\" : \"DoubleType\", \"@val\" : 4.1 }},\n"
                                 + "\t\t\t\t\t\t\t\t{\"@key\" : \"K1\" , \"@val\" : { \"@jsonType\" : \"DoubleType\", \"@val\" : 3.1 }},\n"
                                 + "\t\t\t\t\t\t\t\t{\"@key\" : \"K2\" , \"@val\" : { \"@jsonType\" : \"DoubleType\", \"@val\" : 3.6 }}\n"
                                 + "\t\t\t\t\t\t\t]\n"
                                 + "\t\t\t\t\t\t},\n"
                                 + "\t\t\t\t\t\t\"_dvals\" : {\n"
                                 + "\t\t\t\t\t\t\t\"@jsonId\" : 13,\n"
                                 + "\t\t\t\t\t\t\t\"@val\" : \n"
                                 + "\t\t\t\t\t\t\t[\n"
                                 + "\t\t\t\t\t\t\t\t3.1,\n"
                                 + "\t\t\t\t\t\t\t\t3.6,\n"
                                 + "\t\t\t\t\t\t\t\t4.1\n"
                                 + "\t\t\t\t\t\t\t]\n"
                                 + "\t\t\t\t\t\t}\n"
                                 + "\t\t\t\t\t}\n"
                                 + "\t\t\t\t\t\n"
                                 + "\t\t\t\t]\n"
                                 + "\t\t\t}}\n"
                                 + "\t\t]\n"
                                 + "\t}\n"
                                 + "}\n";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object decodedExportContainer = reader.jsonToObject();
        assertNotNull( decodedExportContainer );
        assertTrue( decodedExportContainer instanceof ExportContainer );

        assertNotSame( ec, decodedExportContainer );

        RecoverySampleClasses.SampleBTExportComponent c2 = new RecoverySampleClasses.SampleBTExportComponent();
        c2.importData( ec, 0 );

        RecoverySampleClasses.SampleContainer[] decodedSampleContainers = c2.getSampleContainerArr();

        assertEquals( c1.getStrForBTExport(), c2.getStrForBTExport() );
        assertEquals( sampleContainers[ 0 ], c2.getSampleContainerArr()[ 0 ] );
        assertEquals( sampleContainers[ 1 ], c2.getSampleContainerArr()[ 1 ] );
        assertEquals( sampleContainers[ 2 ], c2.getSampleContainerArr()[ 2 ] );
    }

    private RecoverySampleClasses.SampleContainer makeContainerEntry( final double v, final double v1, final double v2 ) {

        Map<String, Object> map   = new HashMap<>( 3 );
        double[]            dvals = { v, v1, v2 };

        map.put( "K1", v );
        map.put( "K2", v1 );
        map.put( "K3", v2 );

        RecoverySampleClasses.SampleContainer c = new RecoverySampleClasses.SampleContainer( map, dvals );

        return c;
    }

    private void match( final Map<?, ?> expectedMap, final Map<?, ?> resultMap ) {
        assertEquals( expectedMap.size(), resultMap.size() );

        for ( Map.Entry<?, ?> e : expectedMap.entrySet() ) {
            Object key    = e.getKey();
            Object expVal = e.getValue();

            Object resVal = resultMap.get( key );

            if ( expVal == null ) {
                assertNull( resVal );
            } else {
                assertEquals( expVal, resVal );
            }
        }
    }
}
