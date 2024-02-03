/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book;

import com.rr.core.model.Book;
import com.rr.core.model.Instrument;

public interface BookFactory<T extends Book> {

    T create( Instrument inst );

    T create( Instrument inst, int levels );

}
