/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.model.NoArgsFactory;

public class NoOpsFactoryImpl<T> implements NoArgsFactory<T> {

    private Class<T> _class;

    public NoOpsFactoryImpl( String className ) {
        _class = ReflectUtils.getClass( className );
    }

    public NoOpsFactoryImpl( Class<T> clazz ) {
        _class = clazz;
    }

    @Override public T create() {
        try {
            return _class.newInstance();
        } catch( Exception e ) {
            throw new SMTRuntimeException( "Unable to instantiate " + _class.getName() + " : " + e.getMessage(), e );
        }
    }
}
