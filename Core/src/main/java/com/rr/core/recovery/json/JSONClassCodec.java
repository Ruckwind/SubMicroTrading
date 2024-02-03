package com.rr.core.recovery.json;

import com.rr.core.lang.ReusableString;

/**
 * JSONClassCodec - for class specific encoding / decoding ... must be threadsafe and recommend no state so no synchronisation required
 */
public interface JSONClassCodec {

    /**
     * @return true if the object should of been written, Instruments for example should NOT be written as they preexist
     */
    default boolean checkWritten() { return false; }

    /**
     * decode can be invoked in one of two ways
     * 1) at the start of an object .. where the startObject market, class and jsonId have NOT been read
     * 2) after the class has been read and custom codec identified
     * <p>
     * Note implementations that read a value such as
     * <p>
     * value = reader.procValue( null );
     * <p>
     * Must check if value is an unresolved forward reference
     * <p>
     * if ( value instanceof MissingRef ) {
     * ((MissingRef)value).setResolveFunc( (a) -> map.put( key, a ) );
     * } else {
     * map.put( key, value );
     * }
     *
     * @param reader
     * @param tmpStr    - a string that can be used if necessary to avoid GC while decoding
     * @param postClass - the reader already consumed the @class className and supplied the concrete class
     * @return
     */
    Object decode( JSONReader reader, ReusableString tmpStr, Class<?> postClass, int jsonObjId ) throws Exception;

    void encode( JSONWriter writer, Object val, int jsonObjId ) throws Exception;

    /**
     * @param depth - the level the object is in object tree
     * @return true if the custom codec should be used at top level, false if object should be default encoded field by field
     */
    default boolean useCodec( Object o, WriteContext wctx, boolean isTopLevel ) { return true; }

    /**
     * @return TRUE if references should be used for multiple refs to same object
     */
    boolean useReferences();

    ;
}
