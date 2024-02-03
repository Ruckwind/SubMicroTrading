/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.template;

public interface TemplateClassRegister {

    /**
     * return the registered template for the name and id
     *
     * @param name
     * @param id
     * @return null if not registered
     */
    Class<? extends FastFixTemplateReader> findReader( String name, int id );

    Class<? extends FastFixTemplateWriter> findWriter( String name, int id );

    void registerReader( Class<? extends FastFixTemplateReader> reader, String name, int id );

    void registerWriter( Class<? extends FastFixTemplateWriter> writer, String name, int id );
}
