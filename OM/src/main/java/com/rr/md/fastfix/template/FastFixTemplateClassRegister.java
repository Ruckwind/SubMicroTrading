/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.template;

import com.rr.core.collections.IntHashMap;

public class FastFixTemplateClassRegister implements TemplateClassRegister {

    private final IntHashMap<Class<? extends FastFixTemplateReader>> _readers = new IntHashMap<>( 128, 0.75f );
    private final IntHashMap<Class<? extends FastFixTemplateWriter>> _writers = new IntHashMap<>( 128, 0.75f );

    @Override
    public synchronized Class<? extends FastFixTemplateReader> findReader( String name, int id ) {
        return _readers.get( id );
    }

    @Override
    public synchronized Class<? extends FastFixTemplateWriter> findWriter( String name, int id ) {
        return _writers.get( id );
    }

    @Override
    public synchronized void registerReader( Class<? extends FastFixTemplateReader> reader, String name, int id ) {
        _readers.put( id, reader );
    }

    @Override
    public synchronized void registerWriter( Class<? extends FastFixTemplateWriter> writer, String name, int id ) {
        _writers.put( id, writer );
    }
}
