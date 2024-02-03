package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.ReusableStringOutputStream;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.recovery.RecoverySampleClasses;
import com.rr.core.recovery.json.custom.CustomJSONCodecs;
import com.rr.core.utils.FileUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestJSONClassDefinition extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONClassDefinition.class, Level.info );

    @Test public void schemaJSONClassDefFieldEntryExamplePretty() throws Exception {

        ReusableStringOutputStream outStreamSchema = new ReusableStringOutputStream( 1024 );

        JSONWriter schemaWriter = new JSONWriterImpl( outStreamSchema, _cache, new SMTComponentManager(), true );

        final JSONClassDefinition def = _cache.getDefinition( JSONClassDefinition.FieldEntry.class );
        schemaWriter.objectToJson( def.getEntries()[ 0 ], PersistMode.AllFields );

        _log.info( "jsonstr=" + outStreamSchema.getBuf().toString() );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"com.rr.core.recovery.json.JSONClassDefinition$FieldEntry\",\n"
                                 + "\t\"_field\" : {\n"
                                 + "\t\t\"fieldClass\" : \"com.rr.core.recovery.json.JSONClassDefinition$FieldEntry\",\n"
                                 + "\t\t\"fieldName\" : \"_field\"\n"
                                 + "\t},\n"
                                 + "\t\"_fieldType\" : {\n"
                                 + "\t\t\"@enum\" : \"com.rr.core.recovery.json.JSONFieldType.Object\"\n"
                                 + "\t},\n"
                                 + "\t\"_fieldClass\" : \"java.lang.reflect.Field\",\n"
                                 + "\t\"_isArray\" : false,\n"
                                 + "\t\"_objectDef\" : null,\n"
                                 + "\t\"_hasPersistAnnotation\" : true,\n"
                                 + "\t\"_hadPermission\" : false,\n"
                                 + "\t\"_customCodec\" : {\n"
                                 + "\t\t\"@jsonId\" : 2,\n"
                                 + "\t\t\"@class\" : \"com.rr.core.recovery.json.custom.CustomJSONCodecs$ReflectFieldJSONCodec\"\n"
                                 + "\t},\n"
                                 + "\t\"_isForceSMTRef\" : false,\n"
                                 + "\t\"_isEncodeAsStringTimestamp\" : false,\n"
                                 + "\t\"_depth\" : 0,\n"
                                 + "\t\"_shadowed\" : false,\n"
                                 + "\t\"_isExportable\" : true\n"
                                 + "}\n";

        assertEquals( expPretty, outStreamSchema.getBuf().toString() );
    }

    @Test public void schemaJSONClassDefinitionPretty() throws Exception {

        System.setProperty( "line.separator", "\n" );

        ReusableStringOutputStream outStreamSchema = new ReusableStringOutputStream( 1024 );

        JSONWriter schemaWriter = new JSONWriterImpl( outStreamSchema, _cache, new SMTComponentManager(), true );

        schemaWriter.objectToJson( _cache.getDefinition( JSONClassDefinition.class ), PersistMode.AllFields );

        _log.info( "jsonstr=" + outStreamSchema.getBuf().toString() );

        final String expPretty = FileUtils.fileToString( "core/schemaJSONClassDefinitionPretty.json" );

        final String resultStr = outStreamSchema.getBuf().toString();

//        for( int idx=0 ; idx < expPretty.length() ; ++idx ) {
//            char e = expPretty.charAt( idx );
//            char r = resultStr.charAt( idx );
//
//            if ( e != r ) {
//                _log.info( "idx " + idx + " expected " + (int)e + " got " + (int) r );
//            }
//        }

        assertEquals( expPretty.trim(), resultStr.trim() );
    }

    @Test
    public void testEnum() {
        JSONClassDefinition def = _cache.getDefinition( RecoverySampleClasses.ClassWithEnum.class );

        JSONClassDefinition.FieldEntry[] entries = def.getEntries();

        assertEquals( 1, entries.length );

        final JSONClassDefinition.FieldEntry e = entries[ 0 ];

        assertSame( JSONFieldType.Enum, e.getFieldType() );
        assertNotNull( e.getCustomCodec() );
        assertTrue( e.getCustomCodec() instanceof CustomJSONCodecs.SpecificEnumJSONCodec );

        check( e, "_enumF", null, e.getCustomCodec() );
    }

    @Test
    public void testObjectArray() {
        JSONClassDefinition defMain = _cache.getDefinition( RecoverySampleClasses.ClassWithCompArray.class );
        JSONClassDefinition defSub  = _cache.getDefinition( RecoverySampleClasses.ClassWithJustInt[].class );

        assertFalse( defMain.isArray() );
        assertTrue( defSub.isArray() );

        JSONClassDefinition.FieldEntry[] entries = defSub.getComponentJCD().getEntries();

        assertEquals( 1, entries.length );

        check( entries[ 0 ], "_intVal", null, null );
    }

    @Test
    public void testObjectArrayOfFinalsWithPersistTags() {
        JSONClassDefinition defFieldEntry = _cache.getDefinition( JSONClassDefinition.FieldEntry[].class );

        JSONClassDefinition.FieldEntry[] entries = defFieldEntry.getComponentJCD().getEntries();

        assertEquals( 13, entries.length );
    }

    @Test
    public void testSimpleArrayIntClass() {
        JSONClassDefinition def = _cache.getDefinition( RecoverySampleClasses.ClassWithIntArray.class );

        JSONClassDefinition.FieldEntry[] entries = def.getEntries();

        assertEquals( 2, entries.length );

        check( entries[ 0 ], "_intValC", null, null );
        check( entries[ 1 ], "_strValC", null, null );
    }

    @Test
    public void testSimpleArrayStringClass() {
        JSONClassDefinition def = _cache.getDefinition( RecoverySampleClasses.ClassWithStringArray.class );

        JSONClassDefinition.FieldEntry[] entries = def.getEntries();

        assertEquals( 2, entries.length );

        check( entries[ 0 ], "_intValB", null, null );
        check( entries[ 1 ], "_strValB", null, null );
    }

    @Test
    public void testSimpleByteArrayIntClass() {
        JSONClassDefinition def = _cache.getDefinition( RecoverySampleClasses.ClassWithByteArray.class );

        JSONClassDefinition.FieldEntry[] entries = def.getEntries();

        assertEquals( 3, entries.length );

        check( entries[ 0 ], "_byteArrValF", null, null );
        check( entries[ 1 ], "_strValF", null, null );
        check( entries[ 2 ], "_aByteArrValF", null, null );
    }

    @Test
    public void testSimpleClassDefOfClassDef() {
        JSONClassDefinition def           = _cache.getDefinition( JSONClassDefinition.class );
        JSONClassDefinition defFieldEntry = _cache.getDefinition( JSONClassDefinition.FieldEntry[].class );

        JSONClassDefinition.FieldEntry[] entries = def.getEntries();

        assertEquals( 9, entries.length );

        check( entries[ 0 ], "_targetClass", null, _ctx.getJSONCustomCodecs().get( Class.class ) );
        check( entries[ 1 ], "_entries", defFieldEntry, null );
        check( entries[ 3 ], "_isArray", null, null );
        check( entries[ 4 ], "_componentJCD", def, null );
        check( entries[ 5 ], "_classDefId", null, null );
    }

    @Test
    public void testSimpleIntAndStrClass() {
        JSONClassDefinition def = _cache.getDefinition( RecoverySampleClasses.ClassWithJustIntAndString.class );

        JSONClassDefinition.FieldEntry[] entries = def.getEntries();

        assertEquals( 2, entries.length );

        check( entries[ 0 ], "_intValA", null, null );
        check( entries[ 1 ], "_strValA", null, null );
    }

    @Test
    public void testSimpleIntClass() {
        JSONClassDefinition def = _cache.getDefinition( RecoverySampleClasses.ClassWithJustInt.class );

        JSONClassDefinition.FieldEntry[] entries = def.getEntries();

        assertEquals( 1, entries.length );

        check( entries[ 0 ], "_intVal", null, null );
    }

    @Test
    public void testSimpleRef() {
        JSONClassDefinition defSub  = _cache.getDefinition( RecoverySampleClasses.ClassWithJustIntAndString.class );
        JSONClassDefinition defMain = _cache.getDefinition( RecoverySampleClasses.ClassWithRef.class );

        JSONClassDefinition.FieldEntry[] entries = defMain.getEntries();

        assertEquals( 3, entries.length );

        check( entries[ 0 ], "_intValD", null, null );
        check( entries[ 1 ], "_intAndStrD", defSub, null );
        check( entries[ 2 ], "_strValD", null, null );
    }

    @Test
    public void testSimpleRefArray() {
        JSONClassDefinition defSubArr = _cache.getDefinition( RecoverySampleClasses.ClassWithJustInt[].class );
        JSONClassDefinition defMain   = _cache.getDefinition( RecoverySampleClasses.ClassWithCompArray.class );

        JSONClassDefinition.FieldEntry[] entries = defMain.getEntries();

        assertEquals( 3, entries.length );

        check( entries[ 0 ], "_intValE", null, null );
        check( entries[ 1 ], "_intAndStrE", defSubArr, null );
        check( entries[ 2 ], "_strValE", null, null );
    }

    private void check( final JSONClassDefinition.FieldEntry entry, final String expFieldName, final Object expObjDef, final Object expCustomCodec ) {
        assertEquals( expFieldName, entry.getField().getName() );
        assertSame( expObjDef, entry.getObjectDef() );
        assertSame( expCustomCodec, entry.getCustomCodec() );
    }
}
