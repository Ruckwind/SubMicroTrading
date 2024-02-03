package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.Constants;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableStringInputStream;
import com.rr.core.lang.ReusableStringOutputStream;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.recovery.RecoverySampleClasses;
import com.rr.core.recovery.SMTComponentResolver;
import com.rr.core.recovery.json.custom.CollectionJSONCodec;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TestJSONCollection extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONCollection.class, Level.info );

    @Test public void collEmbeddedSharedRefs() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        Collection<RecoverySampleClasses.ClassWithJustRef> list = new ArrayList<>();

        RecoverySampleClasses.ClassWithJustRef r1 = new RecoverySampleClasses.ClassWithJustRef( "TaaaDAAAA" );
        RecoverySampleClasses.ClassWithJustRef r2 = new RecoverySampleClasses.ClassWithJustRef( r1 );
        RecoverySampleClasses.ClassWithJustRef r3 = new RecoverySampleClasses.ClassWithJustRef( r1 );

        list.add( r2 );
        list.add( r3 );
        list.add( r1 );

        final CollectionJSONCodec coder = new CollectionJSONCodec();

        coder.encode( dataWriter, list, 10 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 10,\n"
                                 + "\t\"@class\" : \"java.util.ArrayList\",\n"
                                 + "\t\"@val\" : [\n"
                                 + "\t\t{\n"
                                 + "\t\t\t\"@jsonId\" : 1,\n"
                                 + "\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithJustRef\",\n"
                                 + "\t\t\t\"_ref\" : {\n"
                                 + "\t\t\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithJustRef\",\n"
                                 + "\t\t\t\t\"_ref\" : { \"@jsonType\" : \"StringType\", \"@val\" : \"TaaaDAAAA\" }\n"
                                 + "\t\t\t}\n"
                                 + "\t\t},\n"
                                 + "\t\t{\n"
                                 + "\t\t\t\"@jsonId\" : 3,\n"
                                 + "\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithJustRef\",\n"
                                 + "\t\t\t\"_ref\" : {\"@ref\" : \"2\"}\n"
                                 + "\t\t},\n"
                                 + "\t\t{\"@ref\" : \"2\"}\n"
                                 + "\t]\n"
                                 + "}";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object coll = reader.jsonToObject();

        assertNotNull( coll );
        assertTrue( coll instanceof Collection );
        assertSame( ArrayList.class, coll.getClass() );

        @SuppressWarnings( "unchecked" )
        List<RecoverySampleClasses.ClassWithJustRef> l = (List<RecoverySampleClasses.ClassWithJustRef>) coll;
        assertEquals( 3, l.size() );

        RecoverySampleClasses.ClassWithJustRef e1 = l.get( 0 );
        RecoverySampleClasses.ClassWithJustRef e2 = l.get( 1 );
        RecoverySampleClasses.ClassWithJustRef e3 = l.get( 2 );

        assertNotSame( e1, e2 );

        assertSame( e3, e1.getRef() );
        assertSame( e3, e2.getRef() );
        assertEquals( "TaaaDAAAA", e3.getRef() );
    }

    @Test public void collRefs() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        Collection<double[]> collection = new ArrayList<>();

        double[] val1 = { 12.345, 67.89 };

        collection.add( val1 );
        collection.add( val1 );

        final CollectionJSONCodec coder = new CollectionJSONCodec();

        coder.encode( dataWriter, collection, 100 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 100,\n"
                                 + "\t\"@class\" : \"java.util.ArrayList\",\n"
                                 + "\t\"@val\" : [\n"
                                 + "\t\t{\n"
                                 + "\t\t\t\"@jsonId\" : 1,\n"
                                 + "\t\t\t\"@arrayOf\" : \"double\",\n"
                                 + "\t\t\t\"@val\" : \n"
                                 + "\t\t\t[\n"
                                 + "\t\t\t\t12.345,\n"
                                 + "\t\t\t\t67.89\n"
                                 + "\t\t\t]\n"
                                 + "\t\t},\n"
                                 + "\t\t{\"@ref\" : \"1\"}\n"
                                 + "\t]\n"
                                 + "}";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object coll = reader.jsonToObject();

        assertNotNull( coll );
        assertTrue( coll instanceof Collection );
        assertSame( ArrayList.class, coll.getClass() );

        @SuppressWarnings( "unchecked" )
        List<double[]> l = (List<double[]>) coll;
        assertEquals( 2, l.size() );

        assertSame( l.get( 0 ), l.get( 1 ) );
        assertEquals( val1[ 0 ], l.get( 0 )[ 0 ], Constants.WEIGHT );
        assertEquals( val1[ 1 ], l.get( 0 )[ 1 ], Constants.WEIGHT );
    }

    @Test public void collSingleEntryEncode() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        Collection<String> singleEntryMap = Collections.singletonList( "aValue" );

        final CollectionJSONCodec coder = new CollectionJSONCodec();

        coder.encode( dataWriter, singleEntryMap, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.Collections$SingletonList\",\n"
                                 + "\t\"@val\" : [\n"
                                 + "\t\t\"aValue\"\n"
                                 + "\t]\n"
                                 + "}";

        assertEquals( expPretty, outStreamData.getBuf().toString() );
    }

    @Test public void collThreeEntriesEncode() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        Collection<String> list = new ArrayList<>();

        list.add( "aaa" );
        list.add( "bbb" );
        list.add( "ccc" );

        final CollectionJSONCodec coder = new CollectionJSONCodec();

        coder.encode( dataWriter, list, 1 );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"java.util.ArrayList\",\n"
                                 + "\t\"@val\" : [\n"
                                 + "\t\t\"aaa\",\n"
                                 + "\t\t\"bbb\",\n"
                                 + "\t\t\"ccc\"\n"
                                 + "\t]\n"
                                 + "}";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object coll = reader.jsonToObject();

        assertNotNull( coll );
        assertTrue( coll instanceof Collection );
        assertSame( ArrayList.class, coll.getClass() );

        @SuppressWarnings( "unchecked" )
        List<String> l = (List<String>) coll;
        assertEquals( 3, l.size() );

        assertEquals( "aaa", l.get( 0 ) );
        assertEquals( "bbb", l.get( 1 ) );
        assertEquals( "ccc", l.get( 2 ) );
    }
}
