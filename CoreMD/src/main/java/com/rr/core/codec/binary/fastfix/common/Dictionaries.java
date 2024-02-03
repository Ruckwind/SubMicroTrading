/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec.binary.fastfix.common;

import com.rr.core.codec.binary.fastfix.msgdict.DictComponentFactory;

import java.util.HashMap;
import java.util.Map;

public class Dictionaries {

    private final Map<String, ComponentFactory> _dictionaries = new HashMap<>();

    public synchronized ComponentFactory getFullDictComponentFactory( String dictionaryId ) {
        ComponentFactory dict = _dictionaries.get( dictionaryId );

        if ( dict == null ) {
            dict = new DictComponentFactory();

            _dictionaries.put( dictionaryId, dict );
        }

        return dict;
    }

    public synchronized ComponentFactory getMsgTypeDictComponentFactory( String dictionaryId ) {
        ComponentFactory dict = _dictionaries.get( dictionaryId );

        if ( dict == null ) {
            dict = new DictComponentFactory();

            _dictionaries.put( dictionaryId, dict );
        }

        return dict;
    }
}
