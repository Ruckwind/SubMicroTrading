/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book.l2;

import com.rr.core.model.MktDataListener;
import com.rr.core.model.MktDataWithContext;

public interface ContextualMktDataListener<T extends MktDataWithContext> extends MktDataListener<T> {
    // tag interface
}
