/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.book;

import com.rr.core.collections.IntHashMap;
import com.rr.core.factories.Factory;
import com.rr.core.model.Book;

/**
 * Non thread safe BookFactory using int as key
 *
 * @author Richard Rose
 */
public final class MapIntBookFactory implements IntBookFactory {

    private final IntHashMap<Book>       _map;
    private final Factory<Integer, Book> _bookFactory;

    public MapIntBookFactory( int numSymbols, Factory<Integer, Book> bookFactory ) {
        _map         = new IntHashMap<>( numSymbols, 0.75f );
        _bookFactory = bookFactory;
    }

    @Override
    public Book find( int id ) {
        Book book = _map.get( id );

        if ( book == null ) {
            book = _bookFactory.create( id );

            _map.put( id, book );
        }

        return book;
    }

}
