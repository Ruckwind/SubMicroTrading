/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book;

import com.rr.core.model.Book;
import com.rr.core.model.MktDataListener;

/**
 * specialisation of BookListener for subscribers that only use subscribe upto 32 books.
 * <p>
 * BookSubscriber used by the listener must maintain fixed indexed set of books per subscriber
 *
 * @author Richard Rose
 */
public interface SmallBookListener<T extends Book> extends MktDataListener<T> {

    /**
     * notification that 1 or more books have changed, each changed book identified by a bit
     * in the bookChangeBitSet
     *
     * @param bookChangeBitSet
     * @WARN only use where the subscription list is static and setup before market data processing
     * does not guarentee thread safety for changes by the subscriber to its subscription list
     */
    void changed( int bookChangeBitSet );
}
