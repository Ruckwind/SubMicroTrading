/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.model;

import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;
import com.rr.core.utils.SMTRuntimeException;

/**
 * Immutable Reference Data Event Interface - represent data event related to single instrument
 * <p>
 * Used to wrap an immutable data object which should not change ... it is copied .. so publisher should clone if required
 * <p>
 * Not for very high event rates as NOT recycled
 * <p>
 * Requires no locking
 */

public class ImmutableInstRefDataEventImpl<T> extends ImmutableBaseEvent implements InstRefDataEvent<T> {

    private final ZString    _subject;
    private final T          _data;
    private final Instrument _inst;

    /**
     * @param subject
     * @param data      should be immutable !
     * @param inst
     * @param timestamp
     * @param seqNum
     */
    public ImmutableInstRefDataEventImpl( final ZString subject, final T data, final Instrument inst, final long timestamp, final long seqNum ) {
        super();
        _subject = subject;
        _data    = data;
        _inst    = inst;

        setEventTimestamp( timestamp );
        setMsgSeqNum( (int) seqNum );
    }

    @Override public void dump( final ReusableString out ) {
        out.append( "InstRefDataEventImpl " ).append( _subject ).append( ", inst=" ).append( _inst.id() ).append( ", data=" );
        if ( _data instanceof Dumpable ) {
            ((Dumpable) _data).dump( out );
        } else {
            out.append( _data.toString() );
        }
    }

    @Override public long getDataSeqNum() { return getMsgSeqNum(); }

    @Override public Instrument getInstrument() { return _inst; }

    @Override public void setInstrument( final Instrument instrument ) {
        if ( instrument != _inst ) throw new SMTRuntimeException( "ImmutableInstRefDataEventImpl cannot change instrument" );
    }

    @Override public ZString getSubject()       { return _subject; }

    @Override public T getData()                { return _data; }
}
