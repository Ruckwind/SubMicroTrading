/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model.book;

import com.rr.core.lang.CoreReusableType;
import com.rr.core.lang.ReusableType;
import com.rr.core.model.BaseEvent;
import com.rr.core.model.BookContext;
import com.rr.core.model.Instrument;

/**
 * for use when only read and write to book on same thread
 *
 * @author Richard Rose
 */
public abstract class AbstractBook extends BaseEvent<AbstractBook> implements MutableBook {

    protected Instrument _instrument;
    protected long _ticks;
    private String _id;
    private long _dataSeqNum; // book seq num (if used)

    private transient ListenerMktDataContextWrapper<ApiMutatableBook, BookContext> _context;

    public AbstractBook() { // for reflective construction
        _instrument = null;
        _id         = null;
    }

    public AbstractBook( Instrument instrument ) {
        super();
        setMsgSeqNum( 0 );
        setInstrument( instrument );
    }

    @Override public final ListenerMktDataContextWrapper<ApiMutatableBook, BookContext> getContextWrapper() {
        return _context;
    }

    @Override final public ListenerMktDataContextWrapper<ApiMutatableBook, BookContext> setContextWrapper( ListenerMktDataContextWrapper<ApiMutatableBook, BookContext> context ) {
        ListenerMktDataContextWrapper<ApiMutatableBook, BookContext> prev = _context;
        _context = context;
        return prev;
    }

    @Override public final long getDataSeqNum() { return _dataSeqNum; }

    @Override public final void setDataSeqNum( final long dataSeqNum ) { _dataSeqNum = dataSeqNum; }

    @Override public final Instrument getInstrument() {
        return _instrument;
    }

    @Override public String id() { return _id; }

    @Override public abstract void setDirty( final boolean isDirty );

    @Override public final long getTickCount() {
        return _ticks;
    }

    @Override public void setInstrument( final Instrument instrument ) {
        _instrument = instrument;

        String id = "Book : ";

        if ( instrument != null ) id += _instrument.id();

        _id = id;

        setContextWrapper( null );
    }

    @Override public ReusableType getReusableType() {
        return CoreReusableType.NotReusable;
    }
}
