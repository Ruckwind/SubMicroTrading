/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common;

import com.rr.core.codec.binary.fastfix.fulldict.entry.DictEntry;

import java.util.Collection;

public interface ComponentFactory {

    Collection<DictEntry> getDictEntries();

    DictEntry getPrevFieldValInt32Wrapper( String name, int initVal );

    DictEntry getPrevFieldValInt64Wrapper( String name, long initVal );

    /**
     * return a dictionary type appropriate holder for value (eg used to store previous value)
     */
    DictEntry getPrevFieldValWrapper( String name, FieldDataType type, String initVal );

    <T extends FieldReader, V> T getReader( Class<T> fieldClass, Object... args );

    <T extends FieldWriter, V> T getWriter( Class<T> fieldClass, Object... args );
}
