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
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class TestJSONArray extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONArray.class, Level.info );

    private static class DblArrWrapper {

        private double[] _stratsFitnesses;

        public DblArrWrapper( final double[] stratsFitnesses ) {
            _stratsFitnesses = stratsFitnesses;
        }
    }

    @Test public void doubleAr1() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        double[] nodes = new double[ 1 ];

        nodes[ 0 ] = -1;

        writer.objectToJson( nodes, PersistMode.AllFields );

        String expJSONStr = "{\n"
                            + "\t\"@jsonId\" : 1,\n"
                            + "\t\"@arrayOf\" : \"double\",\n"
                            + "\t\"@val\" : \n"
                            + "\t[\n"
                            + "\t\t-1\n"
                            + "\t]\n"
                            + "}\n";

        assertEquals( expJSONStr, outStream.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( outStream.getBuf().toString() );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        Object                   decNodes     = reader.jsonToObject();

        assertArrayEquals( nodes, (double[]) decNodes, Constants.TICK_WEIGHT );
    }

    @Test public void doubleAr2() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        double[] nodes = new double[ 1 ];

        nodes[ 0 ] = Constants.UNSET_DOUBLE;

        writer.objectToJson( nodes, PersistMode.AllFields );

        String expJSONStr = "{\n"
                            + "\t\"@jsonId\" : 1,\n"
                            + "\t\"@arrayOf\" : \"double\",\n"
                            + "\t\"@val\" : \n"
                            + "\t[\n"
                            + "\t\tnull\n"
                            + "\t]\n"
                            + "}\n";

        assertEquals( expJSONStr, outStream.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( outStream.getBuf().toString() );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        Object                   decNodes     = reader.jsonToObject();

        assertArrayEquals( nodes, (double[]) decNodes, Constants.TICK_WEIGHT );
    }

    @Test public void doubleArEmpty() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        double[] nodes = new double[ 0 ];

        writer.objectToJson( nodes, PersistMode.AllFields );

        String expJSONStr = "{\n"
                            + "\t\"@jsonId\" : 1,\n"
                            + "\t\"@arrayOf\" : \"double\",\n"
                            + "\t\"@val\" : \n"
                            + "\t[\n"
                            + "\t]\n"
                            + "}\n";

        assertEquals( expJSONStr, outStream.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( outStream.getBuf().toString() );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        Object                   decNodes     = reader.jsonToObject();

        assertArrayEquals( nodes, (double[]) decNodes, Constants.TICK_WEIGHT );
    }

    @Test public void doubleArNegInf() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        double[] nodes = { -0.08596152, Double.NEGATIVE_INFINITY, -0.05200846 };

        writer.objectToJson( nodes, PersistMode.AllFields );

        String expJSONStr = "{\n"
                            + "\t\"@jsonId\" : 1,\n"
                            + "\t\"@arrayOf\" : \"double\",\n"
                            + "\t\"@val\" : \n"
                            + "\t[\n"
                            + "\t\t-0.08596152,\n"
                            + "\t\t-infinity,\n"
                            + "\t\t-0.05200846\n"
                            + "\t]\n"
                            + "}\n";

        assertEquals( expJSONStr, outStream.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( outStream.getBuf().toString() );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        Object                   decNodes     = reader.jsonToObject();

        assertArrayEquals( nodes, (double[]) decNodes, Constants.TICK_WEIGHT );
    }

    @Test public void topArrayDoubleInClass() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        RecoverySampleClasses.ClassWithDoubleArray itemA = new RecoverySampleClasses.ClassWithDoubleArray();

        writer.objectToJson( itemA, PersistMode.AllFields );

        String expJSONStr = "{\n"
                            + "\t\"@jsonId\" : 1,\n"
                            + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithDoubleArray\",\n"
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
                            + "\t\"_aDblArrValF\" : {\n"
                            + "\t\t\"@jsonId\" : 3,\n"
                            + "\t\t\"@val\" : \n"
                            + "\t\t[\n"
                            + "\t\t\t123.456,\n"
                            + "\t\t\t-1.234,\n"
                            + "\t\t\t23.987\n"
                            + "\t\t]\n"
                            + "\t}\n"
                            + "}\n";

        assertEquals( expJSONStr, outStream.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( outStream.getBuf().toString() );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        Object                   obj          = reader.jsonToObject();

        assertArrayEquals( itemA.getDblArrVal(), ((RecoverySampleClasses.ClassWithDoubleArray) obj).getDblArrVal(), Constants.TICK_WEIGHT );
    }

    @Test public void topArrayDoubleInClassExcludeNull() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        RecoverySampleClasses.ClassWithDoubleArray itemA = new RecoverySampleClasses.ClassWithDoubleArray();

        writer.setExcludeNullFields( true );
        writer.objectToJson( itemA, PersistMode.AllFields );

        String expJSONStr = "{\n"
                            + "\t\"@jsonId\" : 1,\n"
                            + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithDoubleArray\",\n"
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
                            + "\t\"_aDblArrValF\" : {\n"
                            + "\t\t\"@jsonId\" : 3,\n"
                            + "\t\t\"@val\" : \n"
                            + "\t\t[\n"
                            + "\t\t\t123.456,\n"
                            + "\t\t\t-1.234,\n"
                            + "\t\t\t23.987\n"
                            + "\t\t]\n"
                            + "\t}\n"
                            + "}\n";

        assertEquals( expJSONStr, outStream.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( outStream.getBuf().toString() );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        Object                   obj          = reader.jsonToObject();

        assertArrayEquals( itemA.getDblArrVal(), ((RecoverySampleClasses.ClassWithDoubleArray) obj).getDblArrVal(), Constants.TICK_WEIGHT );
    }

    @Test public void topLevelArray() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        RecoverySampleClasses.ClassWithByteArray   itemA = new RecoverySampleClasses.ClassWithByteArray();
        RecoverySampleClasses.ClassWithByteArray   itemB = new RecoverySampleClasses.ClassWithByteArray();
        RecoverySampleClasses.ClassWithByteArray[] arr   = { itemA, itemB };

        writer.objectToJson( arr, PersistMode.AllFields );

        String expJSONStr = "{\n"
                            + "\t\"@jsonId\" : 1,\n"
                            + "\t\"@arrayOf\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithByteArray\",\n"
                            + "\t\"@val\" : \n"
                            + "\t[\n"
                            + "\t\t{\n"
                            + "\t\t\t\"@jsonId\" : 2,\n"
                            + "\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithByteArray\",\n"
                            + "\t\t\t\"_byteArrValF\" : {\n"
                            + "\t\t\t\t\"@jsonId\" : 3,\n"
                            + "\t\t\t\t\"@val\" : \n"
                            + "\t\t\t\t[\n"
                            + "\t\t\t\t\t-71,\n"
                            + "\t\t\t\t\t21,\n"
                            + "\t\t\t\t\t-1\n"
                            + "\t\t\t\t]\n"
                            + "\t\t\t},\n"
                            + "\t\t\t\"_strValF\" : \"def\",\n"
                            + "\t\t\t\"_aByteArrValF\" : {\n"
                            + "\t\t\t\t\"@jsonId\" : 4,\n"
                            + "\t\t\t\t\"@val\" : \n"
                            + "\t\t\t\t[\n"
                            + "\t\t\t\t\t123,\n"
                            + "\t\t\t\t\t-1,\n"
                            + "\t\t\t\t\t23\n"
                            + "\t\t\t\t]\n"
                            + "\t\t\t}\n"
                            + "\t\t},\t\t{\n"
                            + "\t\t\t\"@jsonId\" : 5,\n"
                            + "\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithByteArray\",\n"
                            + "\t\t\t\"_byteArrValF\" : {\n"
                            + "\t\t\t\t\"@jsonId\" : 6,\n"
                            + "\t\t\t\t\"@val\" : \n"
                            + "\t\t\t\t[\n"
                            + "\t\t\t\t\t-71,\n"
                            + "\t\t\t\t\t21,\n"
                            + "\t\t\t\t\t-1\n"
                            + "\t\t\t\t]\n"
                            + "\t\t\t},\n"
                            + "\t\t\t\"_strValF\" : \"def\",\n"
                            + "\t\t\t\"_aByteArrValF\" : {\n"
                            + "\t\t\t\t\"@jsonId\" : 7,\n"
                            + "\t\t\t\t\"@val\" : \n"
                            + "\t\t\t\t[\n"
                            + "\t\t\t\t\t123,\n"
                            + "\t\t\t\t\t-1,\n"
                            + "\t\t\t\t\t23\n"
                            + "\t\t\t\t]\n"
                            + "\t\t\t}\n"
                            + "\t\t}\n"
                            + "\t\t\n"
                            + "\t]\n"
                            + "}\n";

        assertEquals( expJSONStr, outStream.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( outStream.getBuf().toString() );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        Object                   obj          = reader.jsonToObject();

        assertArrayEquals( arr, (RecoverySampleClasses.ClassWithByteArray[]) obj );
    }

    @Test public void topLevelArrayElemRefs() throws Exception {

        ReusableStringOutputStream outStream = new ReusableStringOutputStream( 1024 );

        JSONWriter writer = new JSONWriterImpl( outStream, _cache, new SMTComponentManager(), true );

        RecoverySampleClasses.ClassWithByteArray   itemA = new RecoverySampleClasses.ClassWithByteArray();
        RecoverySampleClasses.ClassWithByteArray[] arr   = { itemA, itemA };

        writer.objectToJson( arr, PersistMode.AllFields );

        String expJSONStr = "{\n"
                            + "\t\"@jsonId\" : 1,\n"
                            + "\t\"@arrayOf\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithByteArray\",\n"
                            + "\t\"@val\" : \n"
                            + "\t[\n"
                            + "\t\t{\n"
                            + "\t\t\t\"@jsonId\" : 2,\n"
                            + "\t\t\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithByteArray\",\n"
                            + "\t\t\t\"_byteArrValF\" : {\n"
                            + "\t\t\t\t\"@jsonId\" : 3,\n"
                            + "\t\t\t\t\"@val\" : \n"
                            + "\t\t\t\t[\n"
                            + "\t\t\t\t\t-71,\n"
                            + "\t\t\t\t\t21,\n"
                            + "\t\t\t\t\t-1\n"
                            + "\t\t\t\t]\n"
                            + "\t\t\t},\n"
                            + "\t\t\t\"_strValF\" : \"def\",\n"
                            + "\t\t\t\"_aByteArrValF\" : {\n"
                            + "\t\t\t\t\"@jsonId\" : 4,\n"
                            + "\t\t\t\t\"@val\" : \n"
                            + "\t\t\t\t[\n"
                            + "\t\t\t\t\t123,\n"
                            + "\t\t\t\t\t-1,\n"
                            + "\t\t\t\t\t23\n"
                            + "\t\t\t\t]\n"
                            + "\t\t\t}\n"
                            + "\t\t},\t\t{\"@ref\" : \"2\"}\n"
                            + "\t\t\n"
                            + "\t]\n"
                            + "}\n";

        assertEquals( expJSONStr, outStream.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( outStream.getBuf().toString() );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );
        Object                   obj          = reader.jsonToObject();

        RecoverySampleClasses.ClassWithByteArray[] decodedArr = (RecoverySampleClasses.ClassWithByteArray[]) obj;
        assertArrayEquals( arr, decodedArr );

        assertSame( decodedArr[ 0 ], decodedArr[ 1 ] );
    }

}
