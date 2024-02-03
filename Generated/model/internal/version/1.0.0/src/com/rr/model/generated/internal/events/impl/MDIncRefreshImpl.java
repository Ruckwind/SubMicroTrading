package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

import com.rr.model.generated.internal.events.interfaces.MDEntry;
import com.rr.core.utils.Utils;
import com.rr.core.lang.*;
import com.rr.core.model.*;
import com.rr.core.annotations.*;
import com.rr.model.internal.type.*;
import com.rr.model.generated.internal.core.ModelReusableTypes;
import com.rr.model.generated.internal.core.SizeType;
import com.rr.model.generated.internal.core.EventIds;
import com.rr.model.generated.internal.events.interfaces.*;

@SuppressWarnings( { "unused", "override"  })

public final class MDIncRefreshImpl implements BaseMDResponse, MDIncRefreshWrite, Copyable<MDIncRefresh>, Reusable<MDIncRefreshImpl> {

   // Attrs

    private transient          MDIncRefreshImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    private long _received = Constants.UNSET_LONG;
    private int _noMDEntries = Constants.UNSET_INT;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;

    private MDEntry _MDEntries;

    private int           _flags          = 0;

   // Getters and Setters
    @Override public final long getReceived() { return _received; }
    @Override public final void setReceived( long val ) { _received = val; }

    @Override public final int getNoMDEntries() { return _noMDEntries; }
    @Override public final void setNoMDEntries( int val ) { _noMDEntries = val; }

    @Override public final MDEntry getMDEntries() { return _MDEntries; }
    @Override public final void setMDEntries( MDEntry val ) { _MDEntries = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _received = Constants.UNSET_LONG;
        _noMDEntries = Constants.UNSET_INT;
        _MDEntries = null;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.MDIncRefresh;
    }

    @Override
    public final MDIncRefreshImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( MDIncRefreshImpl nxt ) {
        _next = nxt;
    }

    @Override
    public final void detachQueue() {
        _nextMessage = null;
    }

    @Override
    public final Event getNextQueueEntry() {
        return _nextMessage;
    }

    @Override
    public final void attachQueue( Event nxt ) {
        _nextMessage = nxt;
    }

    @Override
    public final EventHandler getEventHandler() {
        return _messageHandler;
    }

    @Override
    public final void setEventHandler( EventHandler handler ) {
        _messageHandler = handler;
    }


   // Helper methods
    @Override
    public void setFlag( MsgFlag flag, boolean isOn ) {
        _flags = MsgFlag.setFlag( _flags, flag, isOn );
    }

    @Override
    public boolean isFlagSet( MsgFlag flag ) {
        return MsgFlag.isOn( _flags, flag );
    }

    @Override
    public int getFlags() {
        return _flags;
    }

    @Override
    public String toString() {
        ReusableString buf = TLC.instance().pop();
        dump( buf );
        String rs = buf.toString();
        TLC.instance().pushback( buf );
        return rs;
    }

    @Override
    public final void dump( final ReusableString out ) {
        out.append( "MDIncRefreshImpl" ).append( ' ' );
        if ( Constants.UNSET_LONG != getReceived() && 0 != getReceived() )             out.append( ", received=" ).append( getReceived() );
        if ( Constants.UNSET_INT != getNoMDEntries() && 0 != getNoMDEntries() )             out.append( ", noMDEntries=" ).append( getNoMDEntries() );

        MDEntryImpl tPtrMDEntries = (MDEntryImpl) getMDEntries();
        int tIdxMDEntries=0;

        while( tPtrMDEntries != null ) {
            out.append( " {#" ).append( ++tIdxMDEntries ).append( "} " );
            tPtrMDEntries.dump( out );
            tPtrMDEntries = tPtrMDEntries.getNext();
        }

        if ( Constants.UNSET_INT != getMsgSeqNum() && 0 != getMsgSeqNum() )             out.append( ", msgSeqNum=" ).append( getMsgSeqNum() );
        out.append( ", possDupFlag=" ).append( getPossDupFlag() );
        if ( Constants.UNSET_LONG != getEventTimestamp() && 0 != getEventTimestamp() ) {
            out.append( ", eventTimestamp=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getEventTimestamp() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getEventTimestamp() );
            out.append( " ( " );
            out.append( getEventTimestamp() ).append( " ) " );
        }
    }

    @Override public final void snapTo( MDIncRefresh dest ) {
        ((MDIncRefreshImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( MDIncRefresh src ) {
        setReceived( src.getReceived() );
        setNoMDEntries( src.getNoMDEntries() );
        MDEntryImpl tSrcPtrMDEntries = (MDEntryImpl) src.getMDEntries();
        MDEntryImpl tNewPtrMDEntries = null;
        while( tSrcPtrMDEntries != null ) {
            if ( tNewPtrMDEntries == null ) {
                tNewPtrMDEntries = new MDEntryImpl();
                setMDEntries( tNewPtrMDEntries );
            } else {
                tNewPtrMDEntries.setNext( new MDEntryImpl() );
                tNewPtrMDEntries = tNewPtrMDEntries.getNext();
            }
            tNewPtrMDEntries.deepCopyFrom( tSrcPtrMDEntries );
            tSrcPtrMDEntries = tSrcPtrMDEntries.getNext();
        }
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( MDIncRefresh src ) {
        setReceived( src.getReceived() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( MDIncRefresh src ) {
        if ( Constants.UNSET_LONG != src.getReceived() ) setReceived( src.getReceived() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
