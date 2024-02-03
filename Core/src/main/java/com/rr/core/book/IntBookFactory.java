/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.book;

import com.rr.core.model.Book;

/**
 * ideally this would be a templated interface
 * however to avoid per tick wrapping of int to Integer it is hard coded to int
 *
 * @author Richard Rose
 */
public interface IntBookFactory {

    Book find( int id );

}
