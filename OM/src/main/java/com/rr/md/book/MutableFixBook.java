/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book;

import com.rr.core.model.Event;
import com.rr.core.model.book.MutableBook;
import com.rr.md.book.l2.FixBook;

public interface MutableFixBook extends FixBook, MutableBook, Event {
    // 
}
