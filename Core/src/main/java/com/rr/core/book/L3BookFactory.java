/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.book;

import com.rr.core.factories.FactoryCache;
import com.rr.core.lang.ZString;
import com.rr.core.model.Book;

/**
 * @author Richard Rose
 */
public interface L3BookFactory extends FactoryCache<ZString, Book> {

    @Override Book getItem( ZString id );

}
