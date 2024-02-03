package com.rr.core.recovery.json;

import com.rr.core.component.SMTComponentManager;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ReusableStringInputStream;
import com.rr.core.lang.ReusableStringOutputStream;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;
import com.rr.core.recovery.RecoverySampleClasses.ClassWithClassAndField;
import com.rr.core.recovery.SMTComponentResolver;
import com.rr.core.utils.ReflectUtils;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class TestJSONClassField extends BaseJSONTest {

    private static final Logger _log = ConsoleFactory.console( TestJSONClassField.class, Level.info );

    @Test public void javaClazzAndField() throws Exception {
        ReusableStringOutputStream outStreamData = new ReusableStringOutputStream( 1024 );

        Class<?> clazz = JSONWriterImpl.class;

        JSONWriter dataWriter = new JSONWriterImpl( outStreamData, _cache, new SMTComponentManager(), true );

        final Field field = ReflectUtils.getMember( clazz, "_writeState" );

        ClassWithClassAndField src = new ClassWithClassAndField( 123, clazz, field );

        dataWriter.objectToJson( src );

        final String expPretty = "{\n"
                                 + "\t\"@jsonId\" : 1,\n"
                                 + "\t\"@class\" : \"com.rr.core.recovery.RecoverySampleClasses$ClassWithClassAndField\",\n"
                                 + "\t\"_intValA\" : 123,\n"
                                 + "\t\"_someClass\" : \"com.rr.core.recovery.json.JSONWriterImpl\"\n"
                                 + "}\n";

        assertEquals( expPretty, outStreamData.getBuf().toString() );

        ReusableString           inStr        = new ReusableString( expPretty );
        InputStream              inStrStream  = new ReusableStringInputStream( inStr );
        SMTComponentManager      componentMgr = new SMTComponentManager();
        Resolver                 resolver     = new SMTComponentResolver( componentMgr );
        JSONClassDefinitionCache cache        = new JSONClassDefinitionCache( _ctx );
        JSONReader               reader       = new JSONReaderImpl( inStrStream, resolver, cache );

        Object decoded = reader.jsonToObject();
        assertNotNull( decoded );
        assertTrue( decoded instanceof ClassWithClassAndField );

        ClassWithClassAndField out = (ClassWithClassAndField) decoded;

        assertSame( clazz, out.getSomeClass() );
    }
}
