package com.rr.core.recovery.json;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZConsumer;
import com.rr.core.lang.ZConsumer2Args;

/**
 * @TODO invoke postJSONDecode ... it should be added to the JSONClassDefinition
 */
public interface JSONReader {

    void checkMatch( String charsToMatch ) throws Exception;

    /**
     * @return the next item on stream which should be an long or null .. for null return Constants.UNSET_LONG
     */
    boolean getBoolean() throws Exception;

    JSONClassDefinitionCache getCache();

    /**
     * @param tmpVal
     * @return the next item on stream which should be an double or null .. for null return Constants.UNSET_DOUBLE
     */
    double getDouble( final ReusableString tmpVal ) throws Exception;

    /**
     * @return the next item on stream which should be an int or null .. for null return Constants.UNSET_INT
     */
    int getInt() throws Exception;

    /**
     * @return the next item on stream which should be an long or null .. for null return Constants.UNSET_LONG
     */
    long getLong() throws Exception;

    Resolver getResolver();

    void getString( ReusableString outStr ) throws Exception;

    JSONInputTokeniser getTokeniser();

    /**
     * @return true if the next object have a null value ... NOT to be used for end of stream detection !
     */
    boolean isNullNext() throws Exception;

    /**
     * consume a top level object from stream
     *
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T jsonToObject() throws Exception;

    String procNumber( byte nextByte ) throws Exception;

    /**
     * consumer a value object from the stream into an already existing child object
     * value may NOT be an array
     * this is a helper method for custom codecs
     *
     * @param jcd  - job class def for class of desty
     * @param dest - an optional known and expected object ... avoids unnecessary instantiation in  custom codecs eg book level
     * @return dest
     * @throws Exception
     */
    Object procRawKnownChildObject( JSONClassDefinition jcd, Object dest ) throws Exception;

    /**
     * consumer a value object from the stream
     *
     * @param clazz optional class
     * @return
     * @throws Exception
     */
    Object procValue( Class<?> clazz ) throws Exception;

    void setFieldHandleMissingRef( Object val, ZConsumer<Object> setter );

    void setFieldHandleMissingRef( Object val1, Object val2, ZConsumer2Args<Object, Object> setter );
}
