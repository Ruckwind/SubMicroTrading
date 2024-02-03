package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.datarec.JSONDataRecord;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableStringInputStream;
import com.rr.core.lang.ReusableStringOutputStream;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.recovery.SMTComponentResolver;
import com.rr.core.recovery.json.custom.JSONDataRecordCodec;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class TestJSONDataRecord extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONDataRecord.class, Level.info );

    @Test public void dataDouble() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        JSONDataRecord record = new JSONDataRecord();

        record.add( "key4", -425147808028867D );
        record.add( "key1", 123.45 );
        record.add( "key2", -8.0 );
        record.add( "key2", 0.0000123D );
        record.add( "key2", 0.00001234D );
        record.add( "key2", 0.000012345D );
        record.add( "key3", 0.219645 );

        final JSONDataRecordCodec coder = new JSONDataRecordCodec();

        coder.encode( dataWriter, record, 100 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 100,\n"
                                 + "\t\"@class\" : \"com.rr.core.datarec.JSONDataRecord\",\n"
                                 + "\t\"key4\" : -425147808028867, \"key1\" : 123.45, \"key2\" : -8.0, \"key2\" : 0.0000123, \"key2\" : 0.00001234, \"key2\" : 0.00001234, \"key3\" : 0.219645\n"
                                 + "}";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object rec = reader.jsonToObject();

        assertNotNull( rec );
        assertTrue( rec instanceof JSONDataRecord );

        @SuppressWarnings( "unchecked" )
        JSONDataRecord jrec = (JSONDataRecord) rec;

        assertEquals( record, jrec );
    }

    @Test public void dataNestedDataPointRec() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        JSONDataRecord record = new JSONDataRecord();

        record.add( "key1", 123.45 );
        record.add( "key2", "ze String" );
        record.addNestedRecord( "key3" );
        record.add( "key4", "ze first nest" );
        record.addNestedRecord( "key5" );
        record.add( "key6", "ze second nest" );
        record.endNestedRecord();
        record.endNestedRecord();

        final JSONDataRecordCodec coder = new JSONDataRecordCodec();

        coder.encode( dataWriter, record, 100 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 100,\n"
                                 + "\t\"@class\" : \"com.rr.core.datarec.JSONDataRecord\",\n"
                                 + "\t\"key1\" : 123.45, \"key2\" : \"ze String\", \"key3\" : { \"key4\" : \"ze first nest\", \"key5\" : { \"key6\" : \"ze second nest\" } }\n"
                                 + "}";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object rec = reader.jsonToObject();

        assertNotNull( rec );
        assertTrue( rec instanceof JSONDataRecord );

        @SuppressWarnings( "unchecked" )
        JSONDataRecord jrec = (JSONDataRecord) rec;

        assertEquals( record, jrec );
    }

    @Test public void dataPointFail1() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        JSONDataRecord record = new JSONDataRecord();

        record.add( "key1", Constants.UNSET_DOUBLE );
        record.add( "key2", Constants.UNSET_DOUBLE );

        final JSONDataRecordCodec coder = new JSONDataRecordCodec();

        coder.encode( dataWriter, record, 100 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 100,\n"
                                 + "\t\"@class\" : \"com.rr.core.datarec.JSONDataRecord\"\t\n"
                                 + "}";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object rec = reader.jsonToObject();

        assertNotNull( rec );
        assertTrue( rec instanceof JSONDataRecord );

        @SuppressWarnings( "unchecked" )
        JSONDataRecord jrec = (JSONDataRecord) rec;

        assertEquals( record, jrec );
    }

    @Test public void dataPointRec() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        JSONDataRecord record = new JSONDataRecord();

        record.add( "key1", 123.45 );
        record.add( "key2", "ze String" );

        final JSONDataRecordCodec coder = new JSONDataRecordCodec();

        coder.encode( dataWriter, record, 100 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 100,\n"
                                 + "\t\"@class\" : \"com.rr.core.datarec.JSONDataRecord\",\n"
                                 + "\t\"key1\" : 123.45, \"key2\" : \"ze String\"\n"
                                 + "}";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object rec = reader.jsonToObject();

        assertNotNull( rec );
        assertTrue( rec instanceof JSONDataRecord );

        @SuppressWarnings( "unchecked" )
        JSONDataRecord jrec = (JSONDataRecord) rec;

        assertEquals( record, jrec );
    }

    @Test public void dataPointReset() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        JSONDataRecord record = new JSONDataRecord();

        record.add( "key1", 123.45 );

        record.reset();

        record.add( "key2", "ze String" );

        final JSONDataRecordCodec coder = new JSONDataRecordCodec();

        coder.encode( dataWriter, record, 100 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 100,\n"
                                 + "\t\"@class\" : \"com.rr.core.datarec.JSONDataRecord\",\n"
                                 + "\t\"key2\" : \"ze String\"\n"
                                 + "}";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object rec = reader.jsonToObject();

        assertNotNull( rec );
        assertTrue( rec instanceof JSONDataRecord );

        @SuppressWarnings( "unchecked" )
        JSONDataRecord jrec = (JSONDataRecord) rec;

        assertEquals( record, jrec );
    }

    @Test public void testJSONEmptyStr() {
        String r = JSONUtils.objectToJSON( "" );

        assertEquals( "{ \"@jsonType\" : \"StringType\", \"@val\" : \"\" }\n", r );
    }
}
