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

public class TestJSONClassWithDouble extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONClassWithDouble.class, Level.info );

    @Test public void jsonClassWithInfinity() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        RecoverySampleClasses.ClassWithJustDouble c1 = new RecoverySampleClasses.ClassWithJustDouble( Double.POSITIVE_INFINITY );

        ReusableString b = new ReusableString( 512 );

        dataWriter.objectToJson( c1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 2,\n"
                                 + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithJustDouble\",\n"
                                 + "\t\"_dblVal\" : infinity\n"
                                 + "}\n";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object d1 = reader.jsonToObject();
        assertNotNull( d1 );
        assertTrue( d1 instanceof RecoverySampleClasses.ClassWithJustDouble );

        assertNotSame( c1, d1 );
        assertEquals( c1.getDblVal(), ((RecoverySampleClasses.ClassWithJustDouble) d1).getDblVal(), 0.000005 );
    }

    @Test public void jsonClassWithPosInfinity() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        RecoverySampleClasses.ClassWithJustDouble c1 = new RecoverySampleClasses.ClassWithJustDouble( Double.POSITIVE_INFINITY );

        ReusableString b = new ReusableString( 512 );

        dataWriter.objectToJson( c1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 2,\n"
                                 + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithJustDouble\",\n"
                                 + "\t\"_dblVal\" : infinity\n"
                                 + "}\n";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object d1 = reader.jsonToObject();
        assertNotNull( d1 );
        assertTrue( d1 instanceof RecoverySampleClasses.ClassWithJustDouble );

        assertNotSame( c1, d1 );
        assertEquals( c1.getDblVal(), ((RecoverySampleClasses.ClassWithJustDouble) d1).getDblVal(), 0.000005 );
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
