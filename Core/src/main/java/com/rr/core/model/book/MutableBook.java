/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.model.Book;
import com.rr.core.model.InstRefDataWrite;
import com.rr.core.model.Instrument;

/**
 * Book can be threadsafe or non threadsafe
 *
 * @author Richard Rose
 */
public interface MutableBook extends Book, InstRefDataWrite<Instrument> {
    // tag interface
}
