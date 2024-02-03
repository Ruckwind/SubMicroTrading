/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.md.book.l2;

import com.rr.core.dispatch.EventDispatcher;
import com.rr.core.lang.Stopable;
import com.rr.core.lang.ZString;
import com.rr.core.model.Instrument;
import com.rr.core.utils.SMTRuntimeException;
import com.rr.md.book.MutableFixBook;

/**
 * Adapts book change notifications to a message dispatcher
 * <p>
 * Note use of getNextQueueEntry to check if book already queued and if so then conflate
 *
 * @author Richard Rose
 */
public class L2BookDispatchAdapter<T extends MutableFixBook> implements ContextualMktDataListener<T>, Stopable {

    private final EventDispatcher _dispatcher;
    private final String          _id;
    private       int             _conflateCount;

    public L2BookDispatchAdapter( EventDispatcher dispatcher ) {
        this( "anonymous", dispatcher );
    }

    public L2BookDispatchAdapter( String id, EventDispatcher dispatcher ) {
        super();
        _dispatcher = dispatcher;
        _id         = id;
    }

    @Override
    public String id() {
        return _id;
    }

    @Override
    public void marketDataChanged( T book ) {
        if ( book.getNextQueueEntry() == null ) {
            _dispatcher.dispatch( book );
        } else {
            ++_conflateCount;
        }
    }

    @Override public void receive( final ZString subject, final Instrument inst, final Object data, final long timestamp, final long seqNum ) {
        throw new SMTRuntimeException( _id + " cannot publish" );
    }

    @Override
    public void clearMktData() {
        _conflateCount = 0;
    }

    @Override
    public void stop() {
        _dispatcher.setStopping();
    }

    public int getConflateCount() {
        return _conflateCount;
    }
}
