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

public final class ETIRetransmitOrderEventsResponseImpl implements BaseETIResponse, ETIRetransmitOrderEventsResponseWrite, Copyable<ETIRetransmitOrderEventsResponse>, Reusable<ETIRetransmitOrderEventsResponseImpl> {

   // Attrs

    private transient          ETIRetransmitOrderEventsResponseImpl _next = null;
    private transient volatile Event        _nextMessage    = null;
    private transient          EventHandler _messageHandler = null;
    @TimestampMS private long _requestTime = Constants.UNSET_LONG;
    private short _applTotalMessageCount = Constants.UNSET_SHORT;
    private final ReusableString _applEndMsgID = new ReusableString( SizeType.ETI_APP_MSG_ID_LENGTH.getSize() );
    private final ReusableString _refApplLastMsgID = new ReusableString( SizeType.ETI_APP_MSG_ID_LENGTH.getSize() );
    private int _msgSeqNum = Constants.UNSET_INT;
    @TimestampMS private long _eventTimestamp = Constants.UNSET_LONG;


    private int           _flags          = 0;

   // Getters and Setters
    @Override public final long getRequestTime() { return _requestTime; }
    @Override public final void setRequestTime( long val ) { _requestTime = val; }

    @Override public final short getApplTotalMessageCount() { return _applTotalMessageCount; }
    @Override public final void setApplTotalMessageCount( short val ) { _applTotalMessageCount = val; }

    @Override public final ViewString getApplEndMsgID() { return _applEndMsgID; }

    @Override public final void setApplEndMsgID( byte[] buf, int offset, int len ) { _applEndMsgID.setValue( buf, offset, len ); }
    @Override public final ReusableString getApplEndMsgIDForUpdate() { return _applEndMsgID; }

    @Override public final ViewString getRefApplLastMsgID() { return _refApplLastMsgID; }

    @Override public final void setRefApplLastMsgID( byte[] buf, int offset, int len ) { _refApplLastMsgID.setValue( buf, offset, len ); }
    @Override public final ReusableString getRefApplLastMsgIDForUpdate() { return _refApplLastMsgID; }

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
        _applTotalMessageCount = Constants.UNSET_SHORT;
        _applEndMsgID.reset();
        _refApplLastMsgID.reset();
        _msgSeqNum = Constants.UNSET_INT;
        _eventTimestamp = Constants.UNSET_LONG;
        _flags = 0;
        _next = null;
        _nextMessage = null;
        _messageHandler = null;
    }

    @Override
    public final ReusableType getReusableType() {
        return ModelReusableTypes.ETIRetransmitOrderEventsResponse;
    }

    @Override
    public final ETIRetransmitOrderEventsResponseImpl getNext() {
        return _next;
    }

    @Override
    public final void setNext( ETIRetransmitOrderEventsResponseImpl nxt ) {
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
        out.append( "ETIRetransmitOrderEventsResponseImpl" ).append( ' ' );
        if ( Constants.UNSET_LONG != getRequestTime() && 0 != getRequestTime() ) {
            out.append( ", requestTime=" );
            TimeUtilsFactory.safeTimeUtils().unixTimeToLocalTimestamp( out, getRequestTime() );
            out.append( " / " );
            TimeUtilsFactory.safeTimeUtils().unixTimeToUTCTimestamp( out, getRequestTime() );
            out.append( " ( " );
            out.append( getRequestTime() ).append( " ) " );
        }
        if ( Constants.UNSET_SHORT != getApplTotalMessageCount() && 0 != getApplTotalMessageCount() )             out.append( ", applTotalMessageCount=" ).append( getApplTotalMessageCount() );
        if ( getApplEndMsgID().length() > 0 )             out.append( ", applEndMsgID=" ).append( getApplEndMsgID() );
        if ( getRefApplLastMsgID().length() > 0 )             out.append( ", refApplLastMsgID=" ).append( getRefApplLastMsgID() );
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

    @Override public final void snapTo( ETIRetransmitOrderEventsResponse dest ) {
        ((ETIRetransmitOrderEventsResponseImpl)dest).deepCopyFrom( this );
    }

    /** DEEP copy all members ... INCLUDING subEvents : WARNING CREATES NEW OBJECTS SO MONITOR FOR GC */
    @Override public final void deepCopyFrom( ETIRetransmitOrderEventsResponse src ) {
        setRequestTime( src.getRequestTime() );
        setApplTotalMessageCount( src.getApplTotalMessageCount() );
        getApplEndMsgIDForUpdate().copy( src.getApplEndMsgID() );
        getRefApplLastMsgIDForUpdate().copy( src.getRefApplLastMsgID() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowCopyFrom( ETIRetransmitOrderEventsResponse src ) {
        setRequestTime( src.getRequestTime() );
        setApplTotalMessageCount( src.getApplTotalMessageCount() );
        getApplEndMsgIDForUpdate().copy( src.getApplEndMsgID() );
        getRefApplLastMsgIDForUpdate().copy( src.getRefApplLastMsgID() );
        setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        setEventTimestamp( src.getEventTimestamp() );
    }

    /** shallow copy all primitive members ... EXCLUDING subEvents */
    @Override public final void shallowMergeFrom( ETIRetransmitOrderEventsResponse src ) {
        if ( Constants.UNSET_LONG != src.getRequestTime() ) setRequestTime( src.getRequestTime() );
        if ( Constants.UNSET_SHORT != src.getApplTotalMessageCount() ) setApplTotalMessageCount( src.getApplTotalMessageCount() );
        if ( src.getApplEndMsgID().length() > 0 ) getApplEndMsgIDForUpdate().copy( src.getApplEndMsgID() );
        if ( src.getRefApplLastMsgID().length() > 0 ) getRefApplLastMsgIDForUpdate().copy( src.getRefApplLastMsgID() );
        if ( Constants.UNSET_INT != src.getMsgSeqNum() ) setMsgSeqNum( src.getMsgSeqNum() );
        setPossDupFlag( src.getPossDupFlag() );
        if ( Constants.UNSET_LONG != src.getEventTimestamp() ) setEventTimestamp( src.getEventTimestamp() );
    }

}
