/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.fastfix.meta;

import com.rr.core.collections.IntHashMap;
import com.rr.core.collections.IntMap;

import java.util.Iterator;

public class MetaTemplates {

    private IntMap<MetaTemplate> _templates = new IntHashMap<>( 128, 0.75f );

    public void add( MetaTemplate template ) {
        _templates.put( template.getId(), template );
    }

    public MetaTemplate getTemplate( int id ) {
        return _templates.get( id );
    }

    public int size() {
        return _templates.size();
    }

    public Iterator<Integer> templateIterator() {
        return _templates.keys().iterator();
    }
}
