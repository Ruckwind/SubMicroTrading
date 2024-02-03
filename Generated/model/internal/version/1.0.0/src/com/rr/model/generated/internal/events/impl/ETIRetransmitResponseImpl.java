package com.rr.model.generated.internal.events.impl;

/*
Copyright 2015 Low Latency Trading Limited
Author Richard Rose
*/

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

public final class ETIRetransmitResponseImpl implements BaseETIResponse, ETIRetransmitResponseWrite, Copyable<ETIRetransmitResponse>, Reusable<ETIRetransmitResponseImpl> {

   // Attrs

    private transient          ETIRetransmitResponseImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    @TimestampMS private long _requestTime = Constants.UNSET_LONG;
    private long _applEndSeqNum = Constants.UNSET_LONG;
    private long _refApplLastSeqNum = Constants.UNSET_LONG;
    private short _applTotalMessageCount = Constants.UNSET_SHORT;
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;


    private int           _flags          = 0;

   // Getters and Setters
    @Override public final long getRequestTime() { return _requestTime; }
    @Override public final void setRequestTime( long val ) { _requestTime = val; }

    @Override public final long getApplEndSeqNum() { return _applEndSeqNum; }
    @Override public final void setApplEndSeqNum( long val ) { _applEndSeqNum = val; }

    @Override public final long getRefApplLastSeqNum() { return _refApplLastSeqNum; }
    @Override public final void setRefApplLastSeqNum( long val ) { _refApplLastSeqNum = val; }

    @Override public final short getApplTotalMessageCount() { return _applTotalMessageCount; }
    @Override public final void setApplTotalMessageCount( short val ) { _applTotalMessageCount = val; }

    @Override public final int getMsgSeqNum() { return _msgSeqNum; }
    @Override public final void setMsgSeqNum( int val ) { _msgSeqNum = val; }

    @Override public final long getEventTimestamp() { return _eventTimestamp; }
    @Override public final void setEventTimestamp( long val ) { _eventTimestamp = val; }


    @Override public final boolean getPossDupFlag() { return isFlagSet( MsgFlag.PossDupFlag ); }
    @Override public final void setPossDupFlag( boolean val ) { setFlag( MsgFlag.PossDupFlag, val ); }

   // Reusable Contract

    @Override
    public final void reset() {
        _requestTime = Constants.UNSET_LONG;
        _applEndSeqNum = Constants.UNSET_LONG;
        _refApplLastSeqNum = Constants.UNSET_LONG;
        _applTotalMessageCount = Constants.UNSET_SHORT;
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.ETIRetransmitResponse;
    }

    @Override
    public final ETIRetransmitResponseImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( ETIRetransmitResponseImpl nxt ) {
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
        out.append( "ETIRetransmitResponseImpl" ).append( ' ' );
        if ( Constants.UNSET_LONG != getRequestTime() && 0 != getRequestTime() ) {
            out.append( ", requestTime=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getRequestTime() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getRequestTime() );
            out.append( " ( " );
            out.append( getRequestTime() ).append( " ) " );
        }
        if ( Constants.UNSET_LONG != getApplEndSeqNum() && 0 != getApplEndSeqNum() )             out.append( ", applEndSeqNum=" ).append( getApplEndSeqNum() );
        if ( Constants.UNSET_LONG != getRefApplLastSeqNum() && 0 != getRefApplLastSeqNum() )             out.append( ", refApplLastSeqNum=" ).append( getRefApplLastSeqNum() );
        if ( Constants.UNSET_SHORT != getApplTotalMessageCount() && 0 != getApplTotalMessageCount() )             out.append( ", applTotalMessageCount=" ).append( getApplTotalMessageCount() );
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

    @Override public final void snapTo( ETIRetransmitResponse dest ) {
        ((ETIRetransmitResponseImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( ETIRetransmitResponse src ) {
        setRequestTime( src.getRequestTime() );
        setApplEndSeqNum( src.getApplEndSeqNum() );
        setRefApplLastSeqNum( src.getRefApplLastSeqNum() );
        setApplTotalMessageCount( src.getApplTotalMessageCount() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( ETIRetransmitResponse src ) {
        setRequestTime( src.getRequestTime() );
        setApplEndSeqNum( src.getApplEndSeqNum() );
        setRefApplLastSeqNum( src.getRefApplLastSeqNum() );
        setApplTotalMessageCount( src.getApplTotalMessageCount() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( ETIRetransmitResponse src ) {
        if ( Constants.UNSET_LONG != src.getRequestTime() ) setRequestTime( src.getRequestTime() );
        if ( Constants.UNSET_LONG != src.getApplEndSeqNum() ) setApplEndSeqNum( src.getApplEndSeqNum() );
        if ( Constants.UNSET_LONG != src.getRefApplLastSeqNum() ) setRefApplLastSeqNum( src.getRefApplLastSeqNum() );
        if ( Constants.UNSET_SHORT != src.getApplTotalMessageCount() ) setApplTotalMessageCount( src.getApplTotalMessageCount() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
