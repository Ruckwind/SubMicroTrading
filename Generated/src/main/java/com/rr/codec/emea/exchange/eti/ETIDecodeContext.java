/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.codec.emea.exchange.eti;

import com.rr.core.collections.IntToLongHashMap;
import com.rr.core.lang.ReusableString;
import com.rr.core.lang.ZString;

public final class ETIDecodeContext {

    // context required for exec report seq num uniqueness

    private final ReusableString _lastApplMsgID = new ReusableString( 16 );
    private final ReusableString _parentClOrdId = new ReusableString();
    private final IntToLongHashMap _seqNumToMktClOrdId;
    private       int            _lastPartitionID;

    public ETIDecodeContext() {
        this( 1000 );
    }

    public ETIDecodeContext( int expectedRequests ) {
        _seqNumToMktClOrdId = new IntToLongHashMap( expectedRequests, 0.75f );
    }

    public ZString getLastApplMsgID() {
        return _lastApplMsgID;
    }

    public void setLastApplMsgID( ZString lastApplMsgID ) {
        _lastApplMsgID.copy( lastApplMsgID );
    }

    public int getLastPartitionID() {
        return _lastPartitionID;
    }

    public void setLastPartitionID( int lastPartitionID ) {
        _lastPartitionID = lastPartitionID;
    }

    public IntToLongHashMap getMapSeqNumClOrdId() {
        return _seqNumToMktClOrdId;
    }

    public ZString getParentClOrdId() {
        return _parentClOrdId;
    }

    public void setParentClOrdId( ZString val ) {
        _parentClOrdId.copy( val );
    }

    public ReusableString getParentClOrdIdForUpdate() {
        return _parentClOrdId;
    }

    public boolean hasValue() {
        return _lastApplMsgID.length() > 0;
    }

    public void reset() {
        _lastApplMsgID.reset();
        _lastPartitionID = 0;
    }
}
