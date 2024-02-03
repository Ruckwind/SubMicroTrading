package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.*;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.model.ExchangeCode;
import com.rr.core.recovery.RecoverySampleClasses;
import com.rr.core.recovery.SMTComponentResolver;
import com.rr.core.recovery.json.custom.EnumMapJSONCodec;
import com.rr.core.recovery.json.custom.MapJSONCodec;
import com.rr.core.recovery.json.custom.SingletonMap;
import org.junit.Test;

import java.io.InputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestJSONEnumMap extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONEnumMap.class, Level.info );

    @Test public void mapMapWithEnumKey() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        EnumMap<ExchangeCode, String> singleEntryMap = new EnumMap<>( ExchangeCode.class );

        singleEntryMap.put( ExchangeCode.XCME, "A CME thing" );

        final EnumMapJSONCodec encoder = new EnumMapJSONCodec();

        encoder.encode( dataWriter, singleEntryMap, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.EnumMap\",\n"
                                 + "\t\"enumClass\" : \"com.rr.core.model.ExchangeCode\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : {\n"
                                 + "\t\t\t\"@enum\" : \"com.rr.core.model.ExchangeCode.XCME\"\n"
                                 + "\t\t} , \"@val\" : \"A CME thing\"}\n"
                                 + "\t]\n"
                                 + "}";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object decodedMap = reader.jsonToObject();
        assertNotNull( decodedMap );
        assertTrue( decodedMap instanceof Map );

        match( singleEntryMap, (Map<?, ?>) decodedMap );
    }

    @Test public void mapMapWithEnumKeyAndEmptyMap() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        EnumMap<ExchangeCode, HashMap> singleEntryMap = new EnumMap<>( ExchangeCode.class );

        singleEntryMap.put( ExchangeCode.XCME, new HashMap<>() );

        final EnumMapJSONCodec encoder = new EnumMapJSONCodec();

        encoder.encode( dataWriter, singleEntryMap, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.EnumMap\",\n"
                                 + "\t\"enumClass\" : \"com.rr.core.model.ExchangeCode\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : {\n"
                                 + "\t\t\t\"@enum\" : \"com.rr.core.model.ExchangeCode.XCME\"\n"
                                 + "\t\t} , \"@val\" : {\n"
                                 + "\t\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\t\"@class\" : \"java.util.HashMap\",\n"
                                 + "\t\t\t\"@entrySet\" : \n"
                                 + "\t\t\t[\n"
                                 + "\t\t\t]\n"
                                 + "\t\t}}\n"
                                 + "\t]\n"
                                 + "}";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object decodedMap = reader.jsonToObject();
        assertNotNull( decodedMap );
        assertTrue( decodedMap instanceof Map );

        match( singleEntryMap, (Map<?, ?>) decodedMap );
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
                if ( resVal.getClass().isArray() ) {
                    if ( resVal.getClass().getName().equals( "[D" ) ) {
                        assertArrayEquals( (double[]) expVal, (double[]) resVal, Constants.TICK_WEIGHT );
                    } else {
                        assertArrayEquals( (Object[]) expVal, (Object[]) resVal );
                    }
                } else {
                    assertEquals( expVal, resVal );
                }
            }
        }
    }
}
