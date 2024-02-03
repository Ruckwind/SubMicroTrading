/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.codec;

import com.rr.core.lang.*;
import com.rr.core.model.BaseEvent;
import com.rr.core.utils.NumberFormatUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public final class IgnoredEvent extends BaseEvent<IgnoredEvent> {

    private static final int DEFAULT_REJECT_FIELD_ENTRIES = 32;

    private static final ZString SEQ_NUM = new ViewString( "34" );
    private static final ZString SRC     = new ViewString( "Src=" );
    private static final ZString DELIM   = new ViewString( ", " );

    private final Map<ReusableString, ReusableString> _map = new LinkedHashMap<>( DEFAULT_REJECT_FIELD_ENTRIES );

    private Throwable _throwable;
    private int       _maxLen;

    public IgnoredEvent( byte[] fixMsg, int offset, int maxIdx ) {
        super();

        _maxLen = maxIdx - offset;

        DecoderUtils.populate( _map, fixMsg, offset, maxIdx );

        ReusableString seqNum = _map.get( SEQ_NUM );

        if ( seqNum != null ) {
            int iSeqNum = NumberFormatUtils.toInteger( seqNum );

            if ( iSeqNum > 0 ) setMsgSeqNum( iSeqNum );
        }
    }

    @Override public void dump( ReusableString out ) {
        if ( getEventHandler() != null ) {
            out.append( SRC );
            out.append( getEventHandler().getComponentId() );
            out.append( DELIM );
        }
        out.append( getMessage() );
        for ( Map.Entry<ReusableString, ReusableString> entry : _map.entrySet() ) {
            out.append( DELIM );
            out.append( entry.getKey() ).append( '=' ).append( entry.getValue() );
        }
    }

    @Override public ReusableType getReusableType() {
        return CoreReusableType.NotReusable;
    }

    @Override public void reset() {
        super.reset();
        _throwable = null;
        _maxLen    = 0;

        TLC.instance().recycleStringMap( _map );
    }

    public ReusableString getFixField( ZString key ) { return _map.get( key ); }

    public int getMaxLen()                           { return _maxLen; }

    public String getMessage() {
        return (_throwable == null) ? null : _throwable.getMessage();
    }

    public int getNumFields()                        { return _map.size(); }

    public Throwable getThrowable() { return _throwable; }
}
