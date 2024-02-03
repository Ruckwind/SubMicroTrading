package com.rr.core.recovery.json;

import com.rr.core.component.SMTSnapshotMember;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Non threadsafe JSONWriter
 * <p>
 * first time a class is used its current definition will be written to the output stream
 *
 * @NOTE make sure you enable access with
 * <p>
 * JSONClassDefinition.resetPermissions( true );
 * <p>
 * before you use the writer then dont forget to resetPermissions after with force set to false
 */
public interface JSONWriter {

    void childObjectToJson( Object obj, JSONClassDefinition jcd, boolean showType, PersistMode mode, boolean writeArrType, boolean overrideCustomCodec ) throws Exception;

    /**
     * exclude nulls
     * use single line JSON, newLine replaced with space
     */
    void enableCompress();

    /**
     * end array ... if showType is true or objectId > 0 then the array is enclosed in an object and that will need ending
     *
     * @param showType
     * @param objectId
     * @throws IOException
     */
    void endArray( boolean showType, int objectId ) throws IOException;

    void endObject() throws IOException;

    JSONClassDefinitionCache getCache();

    /**
     * @return temporary string used by the writer
     */
    ReusableString getWorkStr();

    WriteContext getWriteContext();

    void setWriteContext( WriteContext wctx );

    void handlePrimitive( JSONFieldType f, Object obj, boolean showType ) throws Exception;

    void nextLine() throws IOException;

    /**
     * encode an object using the supplied persistent mode into the writers output stream
     *
     * @param obj
     * @throws Exception (IOException, IllegalAccessException)
     */
    void objectToJson( Object obj ) throws Exception;

    void objectToJson( Object obj, PersistMode mode ) throws Exception;

    /**
     * write a collection of objects, each with its own persistance mode
     */
    void objectsToJson( Collection<SMTSnapshotMember> components ) throws IOException, Exception;

    /**
     * ONLY FOR USE BY CUSTYOM CODEC WHERE OBJECT TYPE IS KNOWN AND CAN BE OMMITTED
     * DOESNT USE REFERENCES OR CUSTOM CODECS
     *
     * @param obj
     * @param jcd
     * @throws Exception
     */
    void rawObjectWrite( Object obj, JSONClassDefinition jcd ) throws Exception;

    void resetState();

    void setEncodeNewLineChar( String newLine );

    void setExcludeNullFields( boolean excludeNullFields );

    void setOutStream( OutputStream outStream );

    void setVerboseSpacing( boolean isEnabled );

    /**
     * start encoding an array ... optionally encloses array within object to set className and/or objectId
     *
     * @param arrLen
     * @param showType
     * @param componentClass
     * @param objectId       - id of object or 0 if not applicable
     * @throws IOException
     */
    void startArray( int arrLen, boolean showType, Class<?> componentClass, int objectId ) throws IOException;

    /**
     * a special startObject to allow different class and id from actual object
     * only required for special export to prod cases
     *
     * @param smtId
     * @param objClass
     * @param objectId
     * @return
     * @throws IOException
     */
    void startCustomObject( String smtId, Class<?> objClass ) throws IOException;

    /**
     * @return true if any fields written ... use this to determine if need to add leading comma as next field
     * @throws IOException
     */
    boolean startObject( Object obj, Integer objectId, boolean writeType, PersistMode mode, JSONClassDefinition jcd ) throws IOException;

    boolean startObject( Object obj, Integer objectId, boolean writeType, PersistMode mode, boolean overrideCustomCodec, JSONClassDefinition jcd ) throws IOException;

    void write( ZString c ) throws IOException;

    /**
     * write character to out stream
     *
     * @param c
     * @throws IOException
     */
    void write( char c ) throws IOException;

    /**
     * write string to outstream .... note if string is a value it should already be wrapped in quotes
     * <p>
     * if value is null then write JSON_NULL
     *
     * @param val
     * @throws IOException
     */
    void write( String val ) throws IOException;

    void writeEndLineDelim() throws IOException;

    void writeSpaces() throws IOException;

    void writeVal( int c ) throws IOException;

    void writeVal( long c ) throws IOException;

    void writeVal( boolean n ) throws IOException;

    void writeVal( double c ) throws IOException;
}
