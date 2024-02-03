package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.*;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.model.ExchangeCode;
import com.rr.core.recovery.RecoverySampleClasses;
import com.rr.core.recovery.SMTComponentResolver;
import com.rr.core.recovery.json.custom.MapJSONCodec;
import com.rr.core.recovery.json.custom.SingletonMap;
import org.junit.Test;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestJSONMap extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONMap.class, Level.info );

    @Test public void mapDoubleNull() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        Map<String, Double> singleEntryMap = Collections.singletonMap( "aKey", Constants.UNSET_DOUBLE );

        final MapJSONCodec encoder = new MapJSONCodec();

        encoder.encode( dataWriter, singleEntryMap, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.Collections$SingletonMap\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : \"aKey\" , \"@val\" : { \"@jsonType\" : \"DoubleType\", \"@val\" : null }}\n"
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
        assertTrue( decodedMap instanceof SingletonMap );

        SingletonMap map = (SingletonMap) decodedMap;
    }

    @Test public void mapMapWithDoubleValAsLong() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        Map<String, Double> singleEntryMap = Collections.singletonMap( "momStrat0-F_FCOLHO1SRB1SFCOL0", 1.0 );

        final MapJSONCodec encoder = new MapJSONCodec();

        encoder.encode( dataWriter, singleEntryMap, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.Collections$SingletonMap\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : \"momStrat0-F_FCOLHO1SRB1SFCOL0\" , \"@val\" : { \"@jsonType\" : \"DoubleType\", \"@val\" : 1 }}\n"
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

    @Test public void mapMapWithEnumKey() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        Map<ExchangeCode, String> singleEntryMap = Collections.singletonMap( ExchangeCode.CHIX, "aValue" );

        final MapJSONCodec encoder = new MapJSONCodec();

        encoder.encode( dataWriter, singleEntryMap, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.Collections$SingletonMap\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : {\n"
                                 + "\t\t\t\"@enum\" : \"com.rr.core.model.ExchangeCode.CHIX\"\n"
                                 + "\t\t} , \"@val\" : \"aValue\"}\n"
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

    @Test public void mapMapWithEnumVal() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        Map<String, ExchangeCode> singleEntryMap = Collections.singletonMap( "aVal", ExchangeCode.CHIX );

        final MapJSONCodec encoder = new MapJSONCodec();

        encoder.encode( dataWriter, singleEntryMap, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.Collections$SingletonMap\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : \"aVal\" , \"@val\" : {\n"
                                 + "\t\t\t\"@enum\" : \"com.rr.core.model.ExchangeCode.CHIX\"\n"
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

    @Test public void mapMapWithMapKey() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        Map<ExchangeCode, String> embeddedMap    = Collections.singletonMap( ExchangeCode.CHIX, "aValue" );
        Map<Map<?, ?>, String>    singleEntryMap = Collections.singletonMap( embeddedMap, "aValue" );

        final MapJSONCodec encoder = new MapJSONCodec();

        encoder.encode( dataWriter, singleEntryMap, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.Collections$SingletonMap\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : {\n"
                                 + "\t\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\t\"@class\" : \"java.util.Collections$SingletonMap\",\n"
                                 + "\t\t\t\"@entrySet\" : \n"
                                 + "\t\t\t[\n"
                                 + "\t\t\t\t{\"@key\" : {\n"
                                 + "\t\t\t\t\t\"@enum\" : \"com.rr.core.model.ExchangeCode.CHIX\"\n"
                                 + "\t\t\t\t} , \"@val\" : \"aValue\"}\n"
                                 + "\t\t\t]\n"
                                 + "\t\t} , \"@val\" : \"aValue\"}\n"
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

    @Test public void mapSingleEntryEncode() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        Map<String, String> singleEntryMap = Collections.singletonMap( "aKey", "aValue" );

        final MapJSONCodec encoder = new MapJSONCodec();

        encoder.encode( dataWriter, singleEntryMap, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.Collections$SingletonMap\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : \"aKey\" , \"@val\" : \"aValue\"}\n"
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

    @Test public void mapSingleEntryEncodeCompoundVal() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        RecoverySampleClasses.ClassWithJustIntAndString val1 = new RecoverySampleClasses.ClassWithJustIntAndString( 12345, "sDummyValue" );

        Map<String, RecoverySampleClasses.ClassWithJustIntAndString> singleEntryMap = Collections.singletonMap( "key1", val1 );

        final MapJSONCodec encoder = new MapJSONCodec();

        encoder.encode( dataWriter, singleEntryMap, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.Collections$SingletonMap\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : \"key1\" , \"@val\" : {\n"
                                 + "\t\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithJustIntAndString\",\n"
                                 + "\t\t\t\"_intValA\" : 12345,\n"
                                 + "\t\t\t\"_strValA\" : \"sDummyValue\"\n"
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

    @Test public void mapSingleEntryEncodeDupValEntries() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        RecoverySampleClasses.ClassWithJustIntAndString val1 = new RecoverySampleClasses.ClassWithJustIntAndString( 12345, "sDummyValue" );

        Map<String, RecoverySampleClasses.ClassWithJustIntAndString> map = new HashMap<>();

        map.put( "key1", val1 );
        map.put( "key2", val1 );
        map.put( "key3", val1 );

        final MapJSONCodec encoder = new MapJSONCodec();

        encoder.encode( dataWriter, map, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.HashMap\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : \"key1\" , \"@val\" : {\n"
                                 + "\t\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithJustIntAndString\",\n"
                                 + "\t\t\t\"_intValA\" : 12345,\n"
                                 + "\t\t\t\"_strValA\" : \"sDummyValue\"\n"
                                 + "\t\t}},\n"
                                 + "\t\t{\"@key\" : \"key2\" , \"@val\" : {\"@ref\" : \"2\"}},\n"
                                 + "\t\t{\"@key\" : \"key3\" , \"@val\" : {\"@ref\" : \"2\"}}\n"
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
        match( map, (Map<?, ?>) decodedMap );
    }

    @Test public void mapSingleEntryEncodeLongKey() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        Map<Long, Short> singleEntryMap = Collections.singletonMap( 123456789012345L, (short) 1025 );

        final MapJSONCodec encoder = new MapJSONCodec();

        encoder.encode( dataWriter, singleEntryMap, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.Collections$SingletonMap\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : { \"@jsonType\" : \"LongType\", \"@val\" : 123456789012345 } , \"@val\" : { \"@jsonType\" : \"ShortType\", \"@val\" : 1025 }}\n"
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

    @Test public void mapSingleMapValWithSubStrings() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        String s = "abc" + '"' + "def" + '"' + "ghi";

        RecoverySampleClasses.ClassWithJustIntAndString val1 = new RecoverySampleClasses.ClassWithJustIntAndString( 12345, s );

        Map<String, RecoverySampleClasses.ClassWithJustIntAndString> singleEntryMap = Collections.singletonMap( "key1", val1 );

        final MapJSONCodec encoder = new MapJSONCodec();

        encoder.encode( dataWriter, singleEntryMap, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.Collections$SingletonMap\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : \"key1\" , \"@val\" : {\n"
                                 + "\t\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithJustIntAndString\",\n"
                                 + "\t\t\t\"_intValA\" : 12345,\n"
                                 + "\t\t\t\"_strValA\" : \"abc'def'ghi\"\n"
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

        val1.setStrValA( "abc'def'ghi" );

        match( singleEntryMap, (Map<?, ?>) decodedMap );
    }

    @Test public void mapThreeEntriesEncode() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        Map<String, String> map = new HashMap<>();

        map.put( "aaa", "111" );
        map.put( "bbb", "222" );
        map.put( "ccc", "333" );

        final MapJSONCodec coder = new MapJSONCodec();

        coder.encode( dataWriter, map, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.HashMap\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : \"aaa\" , \"@val\" : \"111\"},\n"
                                 + "\t\t{\"@key\" : \"ccc\" , \"@val\" : \"333\"},\n"
                                 + "\t\t{\"@key\" : \"bbb\" , \"@val\" : \"222\"}\n"
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
        match( map, (Map<?, ?>) decodedMap );
    }

    @Test public void mapWithBooleanEntries() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        Map<String, Boolean> map = new HashMap<>();

        map.put( "key1", true );
        map.put( "key2", false );
        map.put( "key3", true );

        final MapJSONCodec encoder = new MapJSONCodec();

        encoder.encode( dataWriter, map, 1 );

        final String expPretty = "{\n" + "\t\"@jsonId\" : 1,\n" + "\t\"@class\" : \"java.util.HashMap\",\n" + "\t\"@entrySet\" : \n" + "\t[\n"
                                 + "\t\t{\"@key\" : \"key1\" , \"@val\" : { \"@jsonType\" : \"BooleanType\", \"@val\" : true }},\n"
                                 + "\t\t{\"@key\" : \"key2\" , \"@val\" : { \"@jsonType\" : \"BooleanType\", \"@val\" : false }},\n"
                                 + "\t\t{\"@key\" : \"key3\" , \"@val\" : { \"@jsonType\" : \"BooleanType\", \"@val\" : true }}\n" + "\t]\n" + "}";

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
        match( map, (Map<?, ?>) decodedMap );
    }

    @Test public void mapWithIntEntries() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        Map<String, Integer> map = new HashMap<>();

        map.put( "key1", 10 );
        map.put( "key2", 20 );
        map.put( "key3", 30 );

        final MapJSONCodec encoder = new MapJSONCodec();

        encoder.encode( dataWriter, map, 1 );

        final String expPretty = "{\n" + "\t\"@jsonId\" : 1,\n" + "\t\"@class\" : \"java.util.HashMap\",\n" + "\t\"@entrySet\" : \n" + "\t[\n"
                                 + "\t\t{\"@key\" : \"key1\" , \"@val\" : { \"@jsonType\" : \"IntType\", \"@val\" : 10 }},\n"
                                 + "\t\t{\"@key\" : \"key2\" , \"@val\" : { \"@jsonType\" : \"IntType\", \"@val\" : 20 }},\n"
                                 + "\t\t{\"@key\" : \"key3\" , \"@val\" : { \"@jsonType\" : \"IntType\", \"@val\" : 30 }}\n" + "\t]\n" + "}";

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
        match( map, (Map<?, ?>) decodedMap );
    }

    @Test public void mapWithSharedArrayEntries() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        double[] val1 = { 12.345, 67.89 };

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        Map<String, double[]> map = new HashMap<>();

        map.put( "key1", val1 );
        map.put( "key2", val1 );
        map.put( "key3", val1 );

        final MapJSONCodec encoder = new MapJSONCodec();

        encoder.encode( dataWriter, map, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.HashMap\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : \"key1\" , \"@val\" : {\n"
                                 + "\t\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\t\"@arrayOf\" : \"double\",\n"
                                 + "\t\t\t\"@val\" : \n"
                                 + "\t\t\t[\n"
                                 + "\t\t\t\t12.345,\n"
                                 + "\t\t\t\t67.89\n"
                                 + "\t\t\t]\n"
                                 + "\t\t}},\n"
                                 + "\t\t{\"@key\" : \"key2\" , \"@val\" : {\"@ref\" : \"2\"}},\n"
                                 + "\t\t{\"@key\" : \"key3\" , \"@val\" : {\"@ref\" : \"2\"}}\n"
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
        match( map, (Map<?, ?>) decodedMap );
    }

    @Test public void mapZStringKey() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriteSharedState state      = new JSONWriteSharedStateWithRefs( 1 );
        JSONWriter           dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true, state );

        Map<ZString, ZString> singleEntryMap = Collections.singletonMap( new ViewString( "aKey" ), new ViewString( "aValue" ) );

        final MapJSONCodec encoder = new MapJSONCodec();

        encoder.encode( dataWriter, singleEntryMap, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.Collections$SingletonMap\",\n"
                                 + "\t\"@entrySet\" : \n"
                                 + "\t[\n"
                                 + "\t\t{\"@key\" : { \"@jsonType\" : \"ViewStringType\", \"@val\" : \"aKey\" } , \"@val\" : { \"@jsonType\" : \"ViewStringType\", \"@val\" : \"aValue\" }}\n"
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
